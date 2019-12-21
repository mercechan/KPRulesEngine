package org.kp.rulesengine.resolver;

import java.util.List;

public class RuleSet {
	private Long id;
	private String name;
	private String package_name;
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
	public String getPackage_name() {
		return package_name;
	}
	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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

	private String content;
	public List<FactType> getInserts() {
		return inserts;
	}
	public void setInserts(List<FactType> inserts) {
		this.inserts = inserts;
	}
	public List<FactType> getGlobals() {
		return globals;
	}
	public void setGlobals(List<FactType> globals) {
		this.globals = globals;
	}



	private String createdAt;
	private String updatedAt;
	private List<FactType> inserts;
	private List<FactType> globals;
}
