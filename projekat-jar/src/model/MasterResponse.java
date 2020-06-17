package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MasterResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private List<String> connections = new ArrayList<String>();
	private Set<AID> runningAgents = new HashSet<AID>();
	
	public MasterResponse() {
		
	}
	
	public List<String> getConnections() {
		return connections;
	}
	public void setConnections(List<String> connections) {
		this.connections = connections;
	}

	public Set<AID> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(Set<AID> runningAgents) {
		this.runningAgents = runningAgents;
	}
	
}
