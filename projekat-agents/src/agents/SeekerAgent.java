package agents;

import javax.ejb.Remote;
import javax.ejb.Stateful;

import model.ACLMessage;
import model.Agent;
import model.BaseAgent;

@Stateful
@Remote(Agent.class)
public class SeekerAgent extends BaseAgent {

	private static final long serialVersionUID = 1L;
	private String nodeName;
	
	@Override
	protected void onInit() {
		super.onInit();
		nodeName = System.getProperty("jboss.node.name");
		System.out.println("****SEEKER AGENT CREATED****");
		System.out.println("Agent is on " + nodeName);
	}
	
	@Override
	protected void onMessage(ACLMessage aclMessage) {
		System.out.println("****SEEKER AGENT RECIEVED MESSAGE****");
	}

}
