package org.kp.rulesengine.resolver;

public class Character {
	private Long id;
	private String name;
	private String ssn;
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
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	
	@Override
	public String toString() {
		return String.format("[id]: %s, [name]: %s, [ssn]: %s", 
				id, name, ssn);
	}
}
