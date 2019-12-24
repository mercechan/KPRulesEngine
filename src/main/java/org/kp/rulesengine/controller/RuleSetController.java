package org.kp.rulesengine.controller;

import org.kp.rulesengine.repository.RuleSetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.validation.Valid;

import org.kp.rulesengine.exception.ResourceNotFoundException;
import org.kp.rulesengine.model.RuleSets;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;


@RestController
public class RuleSetController {

	@Autowired
	private RuleSetsRepository ruleSetRepository;
	
    @GetMapping("/rulesets")
    public Page<RuleSets> getAllRuleSets(Pageable pageable) {
        return ruleSetRepository.findAll(pageable);
    }	
	
    @GetMapping("/rulesets/{ruleSetId}")
    public RuleSets getOneRuleSetById(@PathVariable Long ruleSetId){
    	RuleSets result = null;
    	RuleSets rs = new RuleSets();
    	rs.setId(1L);    	
    	Example<RuleSets> example = Example.of(rs);
    	Optional<RuleSets> actual = ruleSetRepository.findOne(example);
    	if(actual.isPresent()){
    		result = actual.get();
    	}else{
    		result = new RuleSets();
    	}
    	return result;
    }
    
    @GetMapping("/rulesets/names/{ruleSetName}")
    public RuleSets getOneRuleSetByName(@PathVariable String ruleSetName){
    	RuleSets result = null;

		RuleSets rs = new RuleSets();
		rs.setName(ruleSetName);   	
		Example<RuleSets> example = Example.of(rs);
		Optional<RuleSets> actual = ruleSetRepository.findOne(example);
		if(actual.isPresent()){
			result = actual.get();
		}else{
			result = new RuleSets();
		}

    	return result;
    }    
    
    @PostMapping("/rulesets")
    public RuleSets createRuleSet(@Valid @RequestBody RuleSets ruleSet) {
        return ruleSetRepository.save(ruleSet);
    }	
    
    
    @PutMapping("/rulesets/{ruleSetId}")
    public RuleSets updateRuleSet(@PathVariable Long ruleSetId, @Valid @RequestBody RuleSets ruleSetRequest) {
        return ruleSetRepository.findById(ruleSetId).map(template -> {
        	template.setContent(ruleSetRequest.getContent());
        	template.setName(ruleSetRequest.getName());
        	template.setPackage_name(ruleSetRequest.getPackage_name());
            return ruleSetRepository.save(template);
        }).orElseThrow(() -> new ResourceNotFoundException("ruleSetId " + ruleSetId + " not found"));
    }    
    
    @DeleteMapping("/rulesets/{ruleSetId}")
    public ResponseEntity<?> deleteRuleSet(@PathVariable Long ruleSetId) {
        return ruleSetRepository.findById(ruleSetId).map(template -> {
        	ruleSetRepository.delete(template);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("ruleSetId " + ruleSetId + " not found"));
    }    
    
}
