package org.kp.rulesengine.resolver;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.apache.poi.util.StringUtil;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Global;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.kp.rulesengine.model.RuleSets;
import org.kp.rulesengine.model.Rules;
import org.kp.rulesengine.repository.RuleSetsRepository;
import org.kp.rulesengine.repository.RulesRepository;
import org.kp.rulesengine.utility.DroolsUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import io.micrometer.core.instrument.util.StringUtils;

@Component
public class KPRulesEngineQuery implements GraphQLQueryResolver{
	private static final Logger logger = LoggerFactory.getLogger(KPRulesEngineQuery.class);
    @Autowired
    private RulesRepository rulesRepository;
	@Autowired
	private RuleSetsRepository ruleSetRepository;
	
	public List<RuleSet> GetAllRuleSets(int count)
	{
		return getListOfRuleSets(count,"",0L);
	}

	public RuleSet GetRuleSetByName(String ruleSetName){
		return getListOfRuleSets(0,ruleSetName,0L).get(0);
	}
	
	public RuleSet GetRuleSetById(Long ruleSetId){
		return getListOfRuleSets(0,"",ruleSetId).get(0);
	}
	
	private List<RuleSet> getListOfRuleSets(int count, String ruleSetName, Long ruleSetId)
	{
		logger.info("getAllRuleSets called with count: {}",count);
		List<RuleSets> rss = null;
		RuleSets result = null;
		
		if(count != 0){
			rss = ruleSetRepository.findAll().stream().limit(count).collect(Collectors.toList());	
		}else if(!StringUtils.isEmpty(ruleSetName)){
			RuleSets rs = new RuleSets();
			rs.setName(ruleSetName);   	
			Example<RuleSets> example = Example.of(rs);
			Optional<RuleSets> actual = ruleSetRepository.findOne(example);
			if(actual.isPresent()){
				result = actual.get();
			}else{
				result = new RuleSets();
			}
			if(rss == null) rss = new ArrayList<>();
			rss.add(result);
		}else if(StringUtils.isEmpty(ruleSetName) && ruleSetId != 0){
			RuleSets rs = new RuleSets();
			rs.setId(ruleSetId);   	
			Example<RuleSets> example = Example.of(rs);
			Optional<RuleSets> actual = ruleSetRepository.findOne(example);
			if(actual.isPresent()){
				result = actual.get();
			}else{
				result = new RuleSets();
			}
			if(rss == null) rss = new ArrayList<>();
			rss.add(result);			
		}
		
		List<RuleSet> results =new ArrayList<>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		// Page request [number: 0, size 20, sort: UNSORTED]
		@SuppressWarnings("deprecation")
		Pageable pageable = new PageRequest(0,20);
		
		for(RuleSets rs: rss)
		{
	    	Page<Rules> pageOfRules = rulesRepository.findByRuleSetId(rs.getId(),pageable);
	    	List<Rules> listOfRules = pageOfRules.getContent();
			List<Map<String, Object>> ruleAttributes = new ArrayList<>();
			Map<String, Object> rule1 = new HashMap<>();
			for (Rules r: listOfRules)
			{
				if (rs == null) rs = r.getRuleSet();
				// below are standard specification of rule attributes
				rule1.put("rule_name", r.getRule_name());
				rule1.put("rule_salience", r.getRule_salience());
				rule1.put("rule_activationgroup", r.getRule_activationgroup());
				rule1.put("rule_cond", r.getRule_cond());
				rule1.put("rule_cons", r.getRule_cons());
			}
			ruleAttributes.add(rule1);
	    	InputStream templateStream = new ByteArrayInputStream(rs.getContent().getBytes());
	    	KieBase kieBase = DroolsUtility.createKieBase(ruleAttributes, templateStream);					

			RuleSet ruleset = new RuleSet();
			
			// normal ruleset fields
			ruleset.setId(rs.getId());
			ruleset.setName(rs.getName());
			ruleset.setPackage_name(rs.getPackage_name());
			ruleset.setContent(rs.getContent());
			ruleset.setCreatedAt(dateFormat.format(rs.getCreatedAt()));
			ruleset.setUpdatedAt(dateFormat.format(rs.getUpdatedAt()));
			// end normal ruleset fields
			
			// grab globals
			KiePackage pkg = kieBase.getKiePackage(rs.getPackage_name());
			Collection<Global> gls = pkg.getGlobalVariables();
			List<org.kp.rulesengine.resolver.FactType> globals = new ArrayList<>();
			for (Global gl : gls){
				org.kp.rulesengine.resolver.FactType f = new org.kp.rulesengine.resolver.FactType();
				f.setPackage_name(gl.getType());
				f.setVariable_name(gl.getName());
				globals.add(f);
			} // end for globals in a package
			ruleset.setGlobals(globals);
		   
		   // grab fact types
		   Collection<FactType> fts = pkg.getFactTypes();
		   List<org.kp.rulesengine.resolver.FactType> inserts = new ArrayList<>();
		   for(FactType ft: fts)
		   {
			   org.kp.rulesengine.resolver.FactType f = new org.kp.rulesengine.resolver.FactType();
			   f.setPackage_name(ft.getName());
			   f.setVariable_name(ft.getSimpleName());
			   // grab fields for a given fact type
			   List<FactField> fields = ft.getFields();
			   List<Field> flds = new ArrayList<>();
			   for(FactField field: fields){
				   Field kpf = new Field();
				   kpf.setName(field.getName());
				   kpf.setType(field.getType().getName());
				   flds.add(kpf);
			   }
			   f.setFields(flds);
			   inserts.add(f);
		   }// end for fact type in a package
		   ruleset.setInserts(inserts);
		   results.add(ruleset);
		} // end for each ruleset from database

		return results;		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<OutputParam> FireAllRulesForRuleSet(
			Long ruleSetId, 
			List<InputFactType> inserts,
			List<InputFactType> globals,
			String outputParameter_pkg_name) 
	{
		List<OutputParam> results = new ArrayList<>();
		
		Pageable pageable = new PageRequest(0,20);
    	Page<org.kp.rulesengine.model.Rules> pageOfRules = rulesRepository.findByRuleSetId(ruleSetId, pageable);
    	List<org.kp.rulesengine.model.Rules> listOfRules = pageOfRules.getContent();		
		
    	org.kp.rulesengine.model.RuleSets rs = null;
		List<Map<String, Object>> ruleAttributes = new ArrayList<>();
		
		for (org.kp.rulesengine.model.Rules r: listOfRules)
		{
			Map<String, Object> rule1 = new HashMap<>();
			if (rs == null) rs = r.getRuleSet();
			rule1.put("rule_name", r.getRule_name());
			rule1.put("rule_salience", r.getRule_salience());
			rule1.put("rule_activationgroup", r.getRule_activationgroup());
			rule1.put("rule_cond", r.getRule_cond());
			rule1.put("rule_cons", r.getRule_cons());
			ruleAttributes.add(rule1);
		}

    	InputStream templateStream = new ByteArrayInputStream(rs.getContent().getBytes());
    	KieBase kieBase = DroolsUtility.createKieBase(ruleAttributes, templateStream);
    	KieSession kieSession = kieBase.newKieSession();

       	try{
       		DroolsUtility.addListenersToKieSession(kieSession);
       		
       		// process inserts
       		for(InputFactType f: inserts)
       		{
       			Object droolsObject = createDroolsObject(f.getPackage_name(),f.getClass_name(),kieBase,f);
       			kieSession.insert(droolsObject);	
       		}
       		
       		// process globals
       		for(InputFactType f: globals)
       		{
       			Object droolsObject = createDroolsObject(f.getPackage_name(),f.getClass_name(),kieBase,f);
       			kieSession.setGlobal(f.getVariable_name(), droolsObject);       			
       		}
       		
       		kieSession.fireAllRules();
       		
       		// Object result = searchForOutputInKieSession(outputParameter_pkg_name, kieSession);
       		List<Object> outs = searchForOutputParametersInKieSession(outputParameter_pkg_name, kieSession);

       		outs.forEach(result -> {
	    		if(result != null)
	    		{
	    			OutputParam out = new OutputParam();
	    			out.setPackage_name(outputParameter_pkg_name);
	    			java.lang.reflect.Field[] fs = result.getClass().getDeclaredFields();
	    			List<Field> outFields = new ArrayList<>();
	    			for(java.lang.reflect.Field f: fs)
	    			{
	    				Class<?> targetType = f.getType();
	    				Object objectValue;
						try {
							objectValue = targetType.newInstance();
						} catch (InstantiationException | IllegalAccessException e) {
							e.printStackTrace();
							throw new RuntimeException(e);						}
	    				f.setAccessible(true);
	    				try {
							objectValue = f.get(result);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
							throw new RuntimeException(e);						} 
	    				Field ff = new Field();
	    				ff.setName(f.getName());
	    				ff.setType(f.getType().getName());
	    				
	    				if ("java.util.List".equals(f.getType()) &&
	    						objectValue instanceof java.util.List)
	    				{
	    					StringBuilder sb = new StringBuilder();
	    					// assuming list contains all primitive types
	    					// like string, in, etc
	    					((java.util.List) objectValue).forEach(c -> sb.append(c));
	    					ff.setValue(sb.toString());
	        				logger.info("[fieldname:] {}, [fieldvalue]: {}, [fieldtype]: {}",
	        						f.getName(), sb.toString(), f.getType().getName());
	    				}else{
	    					ff.setValue(objectValue.toString());
	        				logger.info("[fieldname:] {}, [fieldvalue]: {}, [fieldtype]: {}",
	        						f.getName(), objectValue, f.getType().getName());
	    				}
	    				
	    				outFields.add(ff);
	    			}
	    			out.setFields(outFields);
	    			results.add(out);
	    		}	
       		});
       	} 
       	catch(Exception e)
       	{
			e.printStackTrace();
			throw new RuntimeException(e);       		
       	}
       	finally{
       		kieSession.dispose();
       	}
    	
		return results;
	}

   private List<Object> searchForOutputParametersInKieSession(String packageName, KieSession kieSession)
   {
	   	List<Object> results = new ArrayList<>();
		ObjectFilter payPassFilter = new ObjectFilter() {
		    @Override
		    public boolean accept(Object object) {
		    	return packageName.equals(object.getClass().getName());
		    }
		};

	    for(Object obj: kieSession.getObjects(payPassFilter))
	    {
	    	results.add(obj);
	    }

	    return results;
   }
   
	private Object createDroolsObject(String packageName, String className, KieBase kieBase, 
			InputFactType factType)
	{
		FactType serverType = kieBase
				.getFactType(packageName, className);
		if(serverType == null) throw new RuntimeException(String.format(
				"FactType %s.%s not found.", 
				packageName,className));
				
		Object debianServer = null;
		
		try {
			debianServer = serverType.newInstance();
			
		} catch (InstantiationException e) 
		{
			logger.error("the class {} on {} package has no constructor",className, packageName);
			throw new RuntimeException(String.format("the class %s on %s package has no constructor",className, packageName),e);
		} catch (IllegalAccessException e) 
		{
			logger.error("unable to access the class {} on {} package",className, packageName);
			throw new RuntimeException(String.format("unable to access the class %s on %s package",className, packageName),e);
		}
		
		for (org.kp.rulesengine.resolver.Field f: factType.getFields())
		{
			if("java.lang.String".equals(f.getType()))
			{
				serverType.set(debianServer, f.getName(), f.getValue());
				
			}else if("java.lang.Long".equals(f.getType())){
				try{
					Long num = Long.parseLong(f.getValue());
					serverType.set(debianServer, f.getName(), num);
					
				}catch (java.lang.NumberFormatException nex){
					throw new RuntimeException(nex);
				}
			}else if("java.util.List".equals(f.getType()))
			{
				List<String> lstr = new ArrayList<>();
				String[] strArray = f.getValue().split(",");
				for(String s: strArray){
					lstr.add(s);
				}
				serverType.set(debianServer, f.getName(), lstr);
			}else if("int".equals(f.getType())){
				try{
					Integer num = Integer.parseInt(f.getValue());
					serverType.set(debianServer, f.getName(), num);
				}catch (java.lang.NumberFormatException nex){
					throw new RuntimeException(nex);
				}
			}else if("java.sql.Date".equals(f.getType())){
				// java.sql.Date
				java.sql.Date curDate = 
						getDate(f.getValue(), "yyyy-MM-dd hh:mm:ss");
				serverType.set(debianServer, f.getName(), curDate);
				
			}else if("java.util.Date".equals(f.getType())){
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				try {
					serverType.set(debianServer, f.getName(), dateFormat.parse(f.getValue()));
				} catch (ParseException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
		
		return debianServer;
	}
	
	private java.sql.Date getDate(String sDate, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		Date date;
		try {
			date = df.parse(sDate);
			return new java.sql.Date(date.getTime());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}	
	
	public List<org.kp.rulesengine.resolver.Rule> GetRulesByRuleSetName(String ruleSetName)
	{
		Pageable pageable = new PageRequest(0,20);
    	Page<org.kp.rulesengine.model.Rules> pageOfRules = rulesRepository.findByRuleSetName(ruleSetName, pageable);
    	List<org.kp.rulesengine.model.Rules> listOfRules = pageOfRules.getContent();		
		
    	List<org.kp.rulesengine.resolver.Rule> results = new ArrayList<>();
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	
    	if (listOfRules != null && listOfRules.size() > 0){
    		for(org.kp.rulesengine.model.Rules r: listOfRules)
    		{
    			org.kp.rulesengine.resolver.RuleSet tmpRs = new org.kp.rulesengine.resolver.RuleSet();
    			org.kp.rulesengine.resolver.Rule tmp = new org.kp.rulesengine.resolver.Rule();
    			tmp.setCreatedAt(df.format(r.getCreatedAt()));
    			tmp.setUpdatedAt(df.format(r.getUpdatedAt()));
    			tmp.setId(r.getId());
    			tmp.setRule_activationgroup(r.getRule_activationgroup());
    			tmp.setRule_cond(r.getRule_cond());
    			tmp.setRule_cons(r.getRule_cons());
    			tmp.setRule_name(r.getRule_name());
    			tmp.setRule_salience(r.getRule_salience());
    			
    			// inner RuleSet object
    			tmpRs.setContent(r.getRuleSet().getContent());
    			tmpRs.setCreatedAt(df.format(r.getRuleSet().getCreatedAt()));
    			tmpRs.setUpdatedAt(df.format(r.getRuleSet().getUpdatedAt()));
    			tmpRs.setId(r.getRuleSet().getId());
    			tmpRs.setName(r.getRuleSet().getName());
    			tmpRs.setPackage_name(r.getRuleSet().getPackage_name());
    			tmp.setRule_set(tmpRs);
    			// end inner ruleset object
    			
    			results.add(tmp);
    		}
    	}
    	return results;
	}
	
}























