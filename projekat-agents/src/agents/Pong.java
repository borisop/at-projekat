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
public class Pong extends BaseAgent {

	private static final long serialVersionUID = 1L;
	
	private int counter;
	
	
	
	@Override
	protected void onInit() {
		// TODO Auto-generated method stub
		super.onInit();
		counter = 0;
	}



	@Override
	protected void onMessage(ACLMessage aclMessage) {
		if (aclMessage.getPerformative() == Performative.REQUEST) {
			AgentCenter agentCenter = new AgentCenter(aclMessage.getSender().getAgentCenter().getAlias(), aclMessage.getSender().getAgentCenter().getAddress());
			AgentType agentType = new AgentType(Ping.class.getSimpleName(), Agent.PROJEKAT_MODULE);
			AID pongAid  = new AID(agentCenter, agentType);
			ACLMessage reply = new ACLMessage(Performative.INFORM);
			reply.setSender(myAid);
			List<AID> recievers = new ArrayList<AID>();
			recievers.add(aclMessage.getSender());
			reply.setRecievers(recievers);
			++counter;
			reply.setContent("Counter:" +  counter);
			messageManager().post(reply);
		} else if (aclMessage.getPerformative() == Performative.INFORM) {
			ACLMessage msgFromPong = aclMessage;
		}
	}

}
