package org.kp.rulesengine.utility;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DroolsUtility {
	private static final Logger logger = LoggerFactory.getLogger(DroolsUtility.class);
	
	public static void addListenersToKieSession(KieSession kieSession)
	{
		addAgendaEventListenerToSession(kieSession);
		addRuleRuntimeEventListenerToSession(kieSession);
		addProcessEventListenerToSession(kieSession);
	}

	private static void addAgendaEventListenerToSession(KieSession kieSession)
	{
		kieSession.addEventListener(new AgendaEventListener() {
			
			public void matchCreated(MatchCreatedEvent event) {
				// TODO Auto-generated method stub
			    logger.info("matchCreated: " + 
			            event.getMatch());
			}
			
			public void matchCancelled(MatchCancelledEvent event) {
				// TODO Auto-generated method stub
			    logger.info("matchCancelled: " + 
			            event.getMatch());
				
			}
			
			public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
				// TODO Auto-generated method stub
			    logger.info("beforeRuleFlowGroupDeactivated: " + 
			            event.getRuleFlowGroup());
				
			}
			
			public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
				// TODO Auto-generated method stub
			    logger.info("beforeRuleFlowGroupActivated: " + 
			            event.getRuleFlowGroup());
				
			}
			
			public void beforeMatchFired(BeforeMatchFiredEvent event) {
				// TODO Auto-generated method stub
			    logger.info("beforeMatchFired: " + 
			            event.getMatch());
			}
			
			public void agendaGroupPushed(AgendaGroupPushedEvent event) {
				// TODO Auto-generated method stub
			    logger.info("agendaGroupPushed: " + 
			            event.getAgendaGroup());
				
			}
			
			public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
				// TODO Auto-generated method stub
			    logger.info("agendaGroupPopped: " + 
			            event.getAgendaGroup());
				
			}
			
			public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
				// TODO Auto-generated method stub
			    logger.info("afterRuleFlowGroupDeactivated: " + 
			            event.getRuleFlowGroup());
				
			}
			
			public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
				// TODO Auto-generated method stub
			    logger.info("afterRuleFlowGroupActivated: " + 
			            event.getRuleFlowGroup());
				
			}
			
			public void afterMatchFired(AfterMatchFiredEvent event) {
				// TODO Auto-generated method stub
			    logger.info("afterMatchFired: " + 
			            event.getMatch());
				
			}
		});
	}
	
	private static void addRuleRuntimeEventListenerToSession(KieSession kieSession)
	{
		kieSession.addEventListener(new RuleRuntimeEventListener() {
			
			public void objectUpdated(ObjectUpdatedEvent event) {
				// TODO Auto-generated method stub
			    logger.info("objectUpdated: " + 
			            event.getRule());
				
			}
			
			public void objectInserted(ObjectInsertedEvent event) {
				// TODO Auto-generated method stub
			    logger.info("objectInserted: " + 
			            event.getObject());
				
			}
			
			public void objectDeleted(ObjectDeletedEvent event) {
				// TODO Auto-generated method stub
			    logger.info("objectDeleted: " + 
			            event.getRule());
				
			}
		});		
	}

	private static void addProcessEventListenerToSession(KieSession kieSession)
	{
		kieSession.addEventListener(new ProcessEventListener() {
			
			public void beforeVariableChanged(ProcessVariableChangedEvent event) {
				// TODO Auto-generated method stub
			    logger.info("beforeVariableChanged: " + 
			            "old: " + event.getOldValue() + "; new: "
			            + event.getNewValue());
				
			}
			
			public void beforeProcessStarted(ProcessStartedEvent event) {
				// TODO Auto-generated method stub
			    logger.info("beforeProcessStarted: " + event.getProcessInstance());
				
			}
			
			public void beforeProcessCompleted(ProcessCompletedEvent event) {
				// TODO Auto-generated method stub
			    logger.info("beforeProcessCompleted: " + event.getProcessInstance());
				
			}
			
			public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
				// TODO Auto-generated method stub
			    logger.info("beforeNodeTriggered: " + event.getNodeInstance());				
			}
			
			public void beforeNodeLeft(ProcessNodeLeftEvent event) {
				// TODO Auto-generated method stub
				logger.info("beforeNodeLeft: " + event.getNodeInstance());		
				
			}
			
			public void afterVariableChanged(ProcessVariableChangedEvent event) {
				// TODO Auto-generated method stub
				logger.info("afterVariableChanged: " + event.getVariableId());	
				
			}
			
			public void afterProcessStarted(ProcessStartedEvent event) {
				// TODO Auto-generated method stub
				logger.info("afterProcessStarted: " + event.getProcessInstance());
			}
			
			public void afterProcessCompleted(ProcessCompletedEvent event) {
				// TODO Auto-generated method stub
				logger.info("afterProcessCompleted: " + event.getProcessInstance());
				
			}
			
			public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
				// TODO Auto-generated method stub
				logger.info("afterNodeTriggered: " + event.getNodeInstance());	
			}
			
			public void afterNodeLeft(ProcessNodeLeftEvent event) {
				// TODO Auto-generated method stub
				logger.info("afterNodeLeft: " + event.getNodeInstance());	
			}
		});		
	}

	public static KieSession createKieSession(List<Map<String, Object>> ruleAttributes, InputStream templateStream)
	{
		KieBase kieBase = createKieBase(ruleAttributes, templateStream);
		return kieBase.newKieSession();
	}
	
	public static KieBase createKieBase(List<Map<String, Object>> ruleAttributes, InputStream templateStream)
	{
		ObjectDataCompiler compiler = new ObjectDataCompiler();
    	String generatedDRL = compiler.compile(ruleAttributes, templateStream);

    	logger.info(generatedDRL);
    	KieServices kieServices = KieServices.Factory.get();
    	KieHelper kieHelper = new KieHelper();

    	//multiple such resoures/rules can be added
    	byte[] b1 = generatedDRL.getBytes();
    	Resource resource1 = kieServices.getResources().newByteArrayResource(b1);
    	kieHelper.addResource(resource1, ResourceType.DRL);
    	//multiple such resoures/rules can be added
    	
    	KieBase kieBase = kieHelper.build();    			
    	return kieBase;
	}

}
