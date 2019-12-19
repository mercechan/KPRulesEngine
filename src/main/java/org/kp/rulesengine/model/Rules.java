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
@Table(name = "Rules")
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Integer getSalience() {
		return salience;
	}

	public void setSalience(Integer salience) {
		this.salience = salience;
	}

	public String getActivationGroup() {
		return activationGroup;
	}

	public void setActivationGroup(String activationGroup) {
		this.activationGroup = activationGroup;
	}

	public RuleSets getRuleSet() {
		return ruleSet;
	}

	public void setRuleSet(RuleSets ruleSet) {
		this.ruleSet = ruleSet;
	}

	@NotNull
    @Size(max = 255)
    @Column(unique = true, name = "name")
    private String name;    
    
    @Column(nullable = true, name = "rule_cond")
    @Size(max = 1000)
    private String rule_cond;	
	
    @Column(nullable = true, name = "rule_cons")
    @Size(max = 1000)
    private String rule_cons;	
	
    @NotNull
    @Column(name = "salience")
	private Integer salience;
    
    @NotNull
    @Size(max = 255)
    @Column(name = "activationGroup")
    private String activationGroup;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ruleSetId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private RuleSets ruleSet;
}
