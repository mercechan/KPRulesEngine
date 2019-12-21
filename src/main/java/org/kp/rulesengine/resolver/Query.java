package org.kp.rulesengine.resolver;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Global;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;
import org.kp.rulesengine.model.RuleSets;
import org.kp.rulesengine.model.Rules;
import org.kp.rulesengine.repository.RuleSetsRepository;
import org.kp.rulesengine.repository.RulesRepository;
import org.kp.rulesengine.utility.DroolsUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

@Component
public class Query implements GraphQLQueryResolver{
	private static final Logger logger = LoggerFactory.getLogger(Query.class);
    @Autowired
    private RulesRepository rulesRepository;
	@Autowired
	private RuleSetsRepository ruleSetRepository;
	
	public List<RuleSet> getAllRuleSets(int count)
	{
		logger.info("getAllRuleSets called with count: {}",count);
		List<RuleSets> rss = ruleSetRepository.findAll().stream().limit(count).collect(Collectors.toList());
		List<RuleSet> results =new ArrayList<>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

		// 			Page request [number: 0, size 20, sort: UNSORTED]
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
			   List<String> flds = new ArrayList<>();
			   for(FactField field: fields){
				   flds.add(String.format("[fieldName]: %s, [fieldType]: %s", field.getName(),field.getType().getName()));
			   }
			   f.setFields(flds);
			   inserts.add(f);
		   }// end for fact type in a package
		   ruleset.setInserts(inserts);
		   results.add(ruleset);
		} // end for each ruleset from database

		return results;
	}
}
