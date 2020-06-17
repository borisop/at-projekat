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
public class Ping extends BaseAgent {

	private static final long serialVersionUID = 1L;

	@Override
	protected void onMessage(ACLMessage aclMessage) {
		if (aclMessage.getPerformative() == Performative.REQUEST) {
			AgentCenter agentCenter = new AgentCenter(aclMessage.getSender().getAgentCenter().getAlias(), aclMessage.getSender().getAgentCenter().getAddress());
			AgentType agentType = new AgentType(Pong.class.getSimpleName(), Agent.PROJEKAT_MODULE);
			AID pongAid  = new AID(agentCenter, agentType);
			ACLMessage msgToPong = new ACLMessage(Performative.REQUEST);
			msgToPong.setSender(myAid);
			List<AID> recievers = new ArrayList<AID>();
			recievers.add(pongAid);
			msgToPong.setRecievers(recievers);
			messageManager().post(msgToPong);
		} else if (aclMessage.getPerformative() == Performative.INFORM) {
			System.out.println("****MESSAGE FROM PONG: " + aclMessage.getContent() + "****");
		}
	}

}
