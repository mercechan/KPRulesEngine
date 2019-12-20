package org.kp.rulesengine.controller;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.type.FactType;
import org.kie.internal.utils.KieHelper;
import org.kp.rulesengine.model.RuleSets;
import org.kp.rulesengine.model.Rules;
import org.kp.rulesengine.repository.RuleSetsRepository;
import org.kp.rulesengine.repository.RulesRepository;
import org.kp.rulesengine.utility.DroolsUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateExecutionController {
    @Autowired
    private RulesRepository rulesRepository;
	@Autowired
	private RuleSetsRepository ruleSetRepository;
	
   @GetMapping("/template/{ruleSetId}/execute")
    public void getAllRulesByRuleSetId(@PathVariable (value = "ruleSetId") Long ruleSetId,
                                                Pageable pageable)  
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
    	
    	DroolsUtility.addListenersToKieSession(kieSession);
    	
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
		String message = (String) outputType.get(outParam, "message");
		System.out.println(message);
    	kieSession.dispose();
    	// end executing rules
    }
	
}
