package agents;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateful;

import model.ACLMessage;
import model.AID;
import model.Agent;
import model.AgentCenter;
import model.AgentType;
import model.BaseAgent;
import model.Performative;

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
		
		if (aclMessage.getPerformative() == Performative.REQUEST) {
			AgentCenter agentCenter = new AgentCenter(aclMessage.getContent(), aclMessage.getSender().getAgentCenter().getAddress());
			AgentType agentType = new AgentType(GathererAgent.class.getSimpleName(), Agent.PROJEKAT_MODULE);
			AID gathererAid  = new AID(agentCenter, agentType);
			ACLMessage msgToGatherer = new ACLMessage(Performative.REQUEST);
			msgToGatherer.setSender(myAid);
			List<AID> recievers = new ArrayList<AID>();
			recievers.add(gathererAid);
			msgToGatherer.setRecievers(recievers);
			messageManager().post(msgToGatherer);
		}
		
	}

}
