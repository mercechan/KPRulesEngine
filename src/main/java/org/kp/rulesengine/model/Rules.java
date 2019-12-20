package org.kp.rulesengine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Entity
@Table(name = "rules")
public class Rules extends AuditModel {

	private static final long serialVersionUID = 8075480913072799924L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RuleSets getRuleSet() {
		return ruleSet;
	}

	public void setRuleSet(RuleSets ruleSet) {
		this.ruleSet = ruleSet;
	}

	@NotNull
    @Size(max = 255)
    @Column(unique = true, name = "rule_name")
    private String rule_name;    
    
    @Column(nullable = true, name = "rule_cond")
    @Size(max = 1000)
    private String rule_cond;	
	
    public String getRule_name() {
		return rule_name;
	}

	public void setRule_name(String rule_name) {
		this.rule_name = rule_name;
	}

	public String getRule_cond() {
		return rule_cond;
	}

	public void setRule_cond(String rule_cond) {
		this.rule_cond = rule_cond;
	}

	public String getRule_cons() {
		return rule_cons;
	}

	public void setRule_cons(String rule_cons) {
		this.rule_cons = rule_cons;
	}

	public String getRule_salience() {
		return rule_salience;
	}

	public void setRule_salience(String rule_salience) {
		this.rule_salience = rule_salience;
	}

	public String getRule_activationgroup() {
		return rule_activationgroup;
	}

	public void setRule_activationgroup(String rule_activationgroup) {
		this.rule_activationgroup = rule_activationgroup;
	}

	@Column(nullable = true, name = "rule_cons")
    @Size(max = 1000)
    private String rule_cons;	
	
    @NotNull
    @Column(name = "rule_salience")
    @Size(max = 255)
	private String rule_salience;
    
    @NotNull
    @Size(max = 255)
    @Column(name = "rule_activationgroup")
    private String rule_activationgroup;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_set_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private RuleSets ruleSet;
    
    @Override
    public String toString() {
    	return String.format(
    			"[rule_name]: %s [rule_cond]: %s [rule_cons]: %s [rule_activationgroup]: %s [rule_salience]: %s [ruleSet]: %s", 
    			rule_name, rule_cond, rule_cons, rule_activationgroup, rule_salience, ruleSet);
    }
}
