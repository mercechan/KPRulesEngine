package org.kp.rulesengine.business;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class KPRuleTemplate {
	private List<Map<String, Object>> ruleAttributes;
	private InputStream templateStream;
	
	public KPRuleTemplate(List<Map<String, Object>> ruleAttributes, InputStream templateStream){
		this.ruleAttributes = ruleAttributes;
		this.templateStream = templateStream;
	}

	public List<Map<String, Object>> getRuleAttributes() {
		return ruleAttributes;
	}

	public InputStream getTemplateStream() {
		return templateStream;
	}
}
