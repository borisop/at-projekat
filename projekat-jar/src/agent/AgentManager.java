package agent;

import java.io.Serializable;
import java.util.List;

import model.AID;
import model.AgentType;

public interface AgentManager extends Serializable {
	
	public List<AgentType> getAvailableAgentTypes();
	
	public List<AID> getRunningAgents(); 
	
	public AID startServerAgent(AgentType type, String runtimeName);
	
	public void stopAgent(AID aid);
	
}
