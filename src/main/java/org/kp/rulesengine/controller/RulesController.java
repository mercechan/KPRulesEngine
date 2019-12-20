package org.kp.rulesengine.controller;

import javax.validation.Valid;

import org.kp.rulesengine.exception.ResourceNotFoundException;
import org.kp.rulesengine.model.Rules;
import org.kp.rulesengine.repository.RuleSetsRepository;
import org.kp.rulesengine.repository.RulesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;



@RestController
public class RulesController {

    @Autowired
    private RulesRepository rulesRepository;
    
	@Autowired
	private RuleSetsRepository ruleSetRepository;
	
    @GetMapping("/ruleSets/{ruleSetId}/rules")
    public Page<Rules> getAllRulesByRuleSetId(@PathVariable (value = "ruleSetId") Long ruleSetId,
                                                Pageable pageable) {
        return rulesRepository.findByRuleSetId(ruleSetId, pageable);
    }	
	
    
    @PostMapping("/ruleSets/{ruleSetId}/rules")
    public Rules createRule(@PathVariable (value = "ruleSetId") Long ruleSetId,
                                 @Valid @RequestBody Rules rule) {
        return ruleSetRepository.findById(ruleSetId).map(template -> {
            rule.setRuleSet(template);
            return rulesRepository.save(rule);
        }).orElseThrow(() -> new ResourceNotFoundException("ruleSetId " + ruleSetId + " not found"));
    }    
    
    
    @PutMapping("/ruleSets/{ruleSetId}/rules/{ruleId}")
    public Rules updateRule(@PathVariable (value = "ruleSetId") Long ruleSetId,
                                 @PathVariable (value = "ruleId") Long ruleId,
                                 @Valid @RequestBody Rules rule) {
        if(!ruleSetRepository.existsById(ruleSetId)) {
            throw new ResourceNotFoundException("ruleSetId " + ruleSetId + " not found");
        }

        return rulesRepository.findById(ruleId).map(r -> {
        	r.setRule_name(rule.getRule_name());
        	r.setRule_salience(rule.getRule_salience());
        	r.setRule_activationgroup(rule.getRule_activationgroup());
        	r.setRule_cond(rule.getRule_cond());
        	r.setRule_cons(rule.getRule_cons());
        	
            return rulesRepository.save(r);
        }).orElseThrow(() -> new ResourceNotFoundException("ruleId " + ruleId + "not found"));
    }    
    
    
    @DeleteMapping("/ruleSets/{ruleSetId}/rules/{ruleId}")
    public ResponseEntity<?> deleteComment(@PathVariable (value = "ruleSetId") Long ruleSetId,
                              @PathVariable (value = "ruleId") Long ruleId) {
        return rulesRepository.findByIdAndRuleSetId(ruleId, ruleSetId).map(r -> {
        	rulesRepository.delete(r);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("Rule not found with id " + ruleId + " and ruleSetId " + ruleSetId));
    }    
    
    
    
    
}
