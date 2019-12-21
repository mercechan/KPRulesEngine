package org.kp.rulesengine.resolver;

public class Rule {
	private Long id;
	private String rule_activationgroup;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRule_activationgroup() {
		return rule_activationgroup;
	}
	public void setRule_activationgroup(String rule_activationgroup) {
		this.rule_activationgroup = rule_activationgroup;
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
	public String getRule_name() {
		return rule_name;
	}
	public void setRule_name(String rule_name) {
		this.rule_name = rule_name;
	}
	public String getRule_salience() {
		return rule_salience;
	}
	public void setRule_salience(String rule_salience) {
		this.rule_salience = rule_salience;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	public RuleSet getRule_set() {
		return rule_set;
	}
	public void setRule_set(RuleSet rule_set) {
		this.rule_set = rule_set;
	}
	private String rule_cond;
	private String rule_cons;
	private String rule_name;
	private String rule_salience;
	private String createdAt;
	private String updatedAt;
	private RuleSet rule_set;
}
