package org.kp.rulesengine.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

import org.kp.rulesengine.model.RuleSets;
import org.kp.rulesengine.model.Rules;
import org.kp.rulesengine.repository.RuleSetsRepository;
import org.kp.rulesengine.repository.RulesRepository;
import org.kp.rulesengine.utility.DroolsUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.kie.api.definition.rule.Global;
import org.kie.api.definition.type.FactField;


import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;


@RestController
public class TemplateExecutionController {
	private static final Logger logger = LoggerFactory.getLogger(TemplateExecutionController.class);
    @Autowired
    private RulesRepository rulesRepository;
	@Autowired
	private RuleSetsRepository ruleSetRepository;
	
   @GetMapping("/template/{ruleSetId}/execute")
    public void getAllRulesByRuleSetId(@PathVariable (value = "ruleSetId") Long ruleSetId,
                                                Pageable pageable) throws InstantiationException, IllegalAccessException  
   {
    	Page<Rules> pageOfRules = rulesRepository.findByRuleSetId(ruleSetId, pageable);
    	List<Rules> listOfRules = pageOfRules.getContent();
    	
		System.out.println();
		System.out.println();
		System.out.println();
    	listOfRules.stream().forEach(
    			(c) -> System.out.println(c)
    			
    			);
		System.out.println();
		System.out.println();
		System.out.println();
		
		RuleSets rs = null;
		List<Map<String, Object>> ruleAttributes = new ArrayList<>();
		Map<String, Object> rule1 = new HashMap<>();
		for (Rules r: listOfRules)
		{
			if (rs == null) rs = r.getRuleSet();
			rule1.put("rule_name", r.getRule_name());
			rule1.put("rule_salience", r.getRule_salience());
			rule1.put("rule_activationgroup", r.getRule_activationgroup());
			rule1.put("rule_cond", r.getRule_cond());
			rule1.put("rule_cons", r.getRule_cons());
		}
		ruleAttributes.add(rule1);
    	
    	InputStream templateStream = new ByteArrayInputStream(rs.getContent().getBytes());
    	KieBase kieBase = DroolsUtility.createKieBase(ruleAttributes, templateStream);
    	KieSession kieSession = kieBase.newKieSession();
    	
    	try{
        	DroolsUtility.addListenersToKieSession(kieSession);

        	// display metadata before executing the rules
        	displayMetaDataforKieBase(kieBase);
        	// end display metadatra before executing the rules
        	
        	// execute rules on kieSession
    		FactType serverType = kieBase
    				.getFactType("com.rhc.drools", "Server");
    		if(serverType == null) throw new RuntimeException("FactType com.rhc.drools.Server not found.");
    				
    		Object debianServer = null;
    		try {
    			debianServer = serverType.newInstance();
    		} catch (InstantiationException e) {
    			System.err.println("the class Server on com.rhc.drools package hasn't a constructor");
    		} catch (IllegalAccessException e) {
    			System.err.println("unable to access the class Server on com.rhc.drools package");
    		}
    		serverType.set(debianServer, "name", "server001");
    		serverType.set(debianServer, "processors", 1);
    		serverType.set(debianServer, "memory", 123);
    		serverType.set(debianServer, "diskSpace", 123);
    		serverType.set(debianServer, "cpuUsage", 1);
    		kieSession.insert(debianServer);
    		
    		FactType outputType = kieBase
    				.getFactType("com.rhc.drools", "OutParameter");
    		if(outputType == null) throw new RuntimeException("FactType com.rhc.drools.OutParameter not found.");
    		
    		Object outParam = null;
    		try {
    			outParam = outputType.newInstance();
    		} catch (InstantiationException e) {
    			System.err.println("the class OutParameter on com.rhc.drools package hasn't a constructor");
    		} catch (IllegalAccessException e) {
    			System.err.println("unable to access the class OutParameter on com.rhc.drools package");
    		}
    		
    		kieSession.setGlobal("out", outParam);
    		kieSession.fireAllRules();
    		
    		Object o = searchForOutputInKieSession("com.rhc.drools.OutParameter",kieSession);
    		
    		if(o != null){
    			java.lang.reflect.Field[] fs = o.getClass().getDeclaredFields();
    			for(java.lang.reflect.Field f: fs)
    			{
    				Class<?> targetType = f.getType();
    				Object objectValue = targetType.newInstance();
    				f.setAccessible(true);
    				objectValue = f.get(o); 
    				logger.info("[fieldname:] {}, [fieldvalue]: {}, [fieldtype]: {}",
    						f.getName(), objectValue, f.getType().getName());
    			}
    		}
    		
    		String message = (String) outputType.get(outParam, "message");
    		System.out.println(message);
        	// end executing rules    		
    	}
    	finally{
    		kieSession.dispose();
    	}

    }
   
   
   private Object searchForOutputInKieSession(String packageName, KieSession kieSession)
   {
	    ObjectFilter payPassFilter = new ObjectFilter() {
	        @Override
	        public boolean accept(Object object) {
	        	return packageName.equals(object.getClass().getName());
	        }
	    };

	    for (FactHandle handle : kieSession.getFactHandles(payPassFilter)) {
	        return kieSession.getObject(handle);
	    }
	    return null;
   }
   
   private void displayMetaDataforKieBase(KieBase kieBase)
   {
	   /*
	    * Fact type is essentially a input parameter and or an output parameter
	    * [package name]: com.rhc.drools
	    * [fact type name]: com.rhc.drools.Server, [fact type simple name]: Server
	    * [fact type name]: com.rhc.drools.OutParameter, [fact type simple name]: OutParameter
	    * [global name]: out, [global type]: com.rhc.drools.OutParameter
	    */
	   
	   
	   // display packages
	   Collection<KiePackage> pkgs = kieBase.getKiePackages();
	   for(KiePackage pkg : pkgs)
	   {
		   
		   Collection<org.kie.api.definition.rule.Rule> rules = pkg.getRules();
		   for (org.kie.api.definition.rule.Rule r: rules) {
			   Map<String, Object> ruleMetaDataMap = r.getMetaData();
			   
			   StringBuilder sb = new StringBuilder("Rule name: " + r.getName());
			   
			    if (ruleMetaDataMap.size() > 0) {
			        sb.append("\n  With [" + ruleMetaDataMap.size() + "] meta-data:");
			        for (String key : ruleMetaDataMap.keySet()) {
			            sb.append("\n    key=" + key + ", value="
			                    + ruleMetaDataMap.get(key));
			        }
			    }
			    logger.debug(sb.toString());
		   }
		   
		   logger.info("[package name]: {}", pkg.getName());

		   // display globals
		   Collection<Global> gls = pkg.getGlobalVariables();
		   for (Global gl : gls){
			   logger.info("[global name]: {}, [global type]: {}",gl.getName(), gl.getType());
		   }
		   
		   // display fact types
		   Collection<FactType> fts = pkg.getFactTypes();
		   for(FactType ft: fts){
			   if(isFacttypeInGlobal(ft,gls)){
				   logger.info("[fact type name(no insert, found in globals)]: {}, [fact type simple name]: {}",ft.getName(), ft.getSimpleName());   
			   }else{
				   logger.info("[fact type name(do insert)]: {}, [fact type simple name]: {}",ft.getName(), ft.getSimpleName());
			   }
			   
			   List<FactField> fields = ft.getFields();
			   for(FactField field: fields){
				   logger.info("\t\t\t [field]: {}, [field type]: {}", field.getName(), field.getType().getName());
			   }
		   }
	   }
   }
   
   private boolean isFacttypeInGlobal(FactType fact, Collection<Global> globals){
	   boolean found = false;
	   
	   for(Global g: globals){
		   if(g.getType().equalsIgnoreCase(fact.getName())){
			   return true;
		   }
	   }
	   return found;
   }
	
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
}
