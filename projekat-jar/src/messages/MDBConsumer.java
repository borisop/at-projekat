package messages;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import agent.AgentManagerBean;
import model.ACLMessage;
import model.AID;
import model.Agent;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/projekat")
})
public class MDBConsumer implements MessageListener {
	
	@EJB
	private AgentManagerBean agentManager;

	@Override
	public void onMessage(Message arg0) {
		try {
			processMessage(arg0);
		} catch (JMSException jmse) {
			System.out.println("Cannot process incoming message.");
		}
	}
	
	private void processMessage(Message msg) throws JMSException {
		ACLMessage aclMessage = (ACLMessage) ((ObjectMessage) msg).getObject();
		AID aid = getAid(msg, aclMessage);
		deliverMessage(aclMessage, aid);
	}
	
	private AID getAid(Message msg, ACLMessage acl) throws JMSException {
		int i = msg.getIntProperty("AIDIndex");
		return acl.getRecievers().get(i);
	}
	
	private void deliverMessage(ACLMessage msg, AID aid) {
		Agent agent = agentManager.getAgentReference(aid);
		if (agent != null) {
			agent.handleMessage(msg);
		} else {
			System.out.println("No such agent: " + aid.getAgentCenter().getAlias());
		}
	}
}
