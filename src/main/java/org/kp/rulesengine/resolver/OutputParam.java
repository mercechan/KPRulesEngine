package org.kp.rulesengine.resolver;

import java.util.List;

public class OutputParam {
private String package_name;
private List<Field> fields;
public String getPackage_name() {
	return package_name;
}
public void setPackage_name(String package_name) {
	this.package_name = package_name;
}
public List<Field> getFields() {
	return fields;
}
public void setFields(List<Field> fields) {
	this.fields = fields;
}

}
