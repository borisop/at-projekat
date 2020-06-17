package client;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import agent.AgentManager;
import messages.MessageManager;
import model.ACLMessage;
import model.AID;
import model.AgentType;

@Stateless
@LocalBean
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Remote(ClientRest.class)
@Path("/managers")
public class ClientBean implements ClientRest{
	
	@EJB
	AgentManager agentManager;
	
	@EJB
	MessageManager messageManager;
	
	@Override
	public List<AgentType> getAvailableAgentTypes() {
		return agentManager.getAvailableAgentTypes();
	}

	@Override
	public List<AID> getRunningAgents() {
		return agentManager.getRunningAgents();
	}

	@Override
	public AID startAgent(AgentType type, String name) {
		messageManager.post("AGENT_STARTED");
		return agentManager.startServerAgent(type, name);
	}

	@Override
	public void stopAgent(AID aid) {
		messageManager.post("AGENT_STOPPED");
		agentManager.stopAgent(aid);
	}

	@Override
	public void sendACLMessage(ACLMessage aclMessage) {
		messageManager.post(aclMessage);
	}

	@Override
	public List<String> getAllPerformatives() {
		return messageManager.getPerformatives();
	}

	
}
