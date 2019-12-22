package org.kp.rulesengine.resolver;

public class Field {
	private String name;
	private String type;
	private String value;
	
	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	@Override
	public String toString() {
		return String.format("[fieldname]: %s, [fieldtype]: %s", 
				name, type);
	}

}
