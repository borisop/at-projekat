package model;

import javax.ejb.Remove;

import agent.AgentManager;
import client.Node;
import jndi.ObjectFactory;
import messages.MessageManager;

public abstract class BaseAgent implements Agent {

	private static final long serialVersionUID = 1L;
	protected AID myAid;
	private AgentManager agentManager;
	private MessageManager messageManager;
	
	@Override
	public void init(AID aid) {
		myAid = aid;
	}
	
	@Override
	@Remove
	public void stop() {
		try {
			onTerminate();
		} catch (Exception e) {
			System.out.println("Error on terminate.");
			e.printStackTrace();
		}
	}
	
	protected void onTerminate() {
		
	}

	@Override
	public void handleMessage(ACLMessage aclMessage) {
		if (filter(aclMessage)) {
			try {
				onMessage(aclMessage);
			} catch (Exception e) {
				System.out.println("Error while delivering message: " + aclMessage);
				e.printStackTrace();
			}
		}
	}
	
	protected boolean filter(ACLMessage aclMessage) {
		return true;
	}
	
	protected abstract void onMessage(ACLMessage aclMessage);
	
	protected void onInit() {
		
	}

	@Override
	public AID getAID() {
		return myAid;
	}
	
	protected AgentManager agentManager() {
		if (agentManager == null) {
			agentManager = ObjectFactory.getAgentManager(Node.LOCAL);
		}
		return agentManager;
	}
	
	protected MessageManager messageManager() {
		if (messageManager == null) {
			messageManager = ObjectFactory.getMessageManager(Node.LOCAL);
		}
		return messageManager;
	}
	
}
