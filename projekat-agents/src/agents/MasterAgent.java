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
public class MasterAgent extends BaseAgent {
	
	private static final long serialVersionUID = 1L;
	private String nodeName;
	
	@Override
	protected void onInit() {
		super.onInit();
		nodeName = System.getProperty("jboss.node.name");
		System.out.println("****MASTER AGENT CREATED****");
		System.out.println("Agent is on " + nodeName);
	}

	@Override
	protected void onMessage(ACLMessage aclMessage) {
		System.out.println("****MASTER AGENT RECIEVED MESSAGE****");
		
		if (aclMessage.getPerformative() == Performative.REQUEST) {
			AgentCenter agentCenter = new AgentCenter(aclMessage.getSender().getAgentCenter().getAlias(), aclMessage.getSender().getAgentCenter().getAddress());
			AgentType agentType = new AgentType(SeekerAgent.class.getSimpleName(), Agent.PROJEKAT_MODULE);
			AID seekerAid  = new AID(agentCenter, agentType);
			ACLMessage msgToSeeker = new ACLMessage(Performative.REQUEST);
			msgToSeeker.setSender(myAid);
			msgToSeeker.setContent(aclMessage.getContent());
			List<AID> recievers = new ArrayList<AID>();
			recievers.add(seekerAid);
			msgToSeeker.setRecievers(recievers);
			messageManager().post(msgToSeeker);
		}
	}
	
}
