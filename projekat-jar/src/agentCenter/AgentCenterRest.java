package agentCenter;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.AID;
import model.MasterResponse;

public interface AgentCenterRest {
	
	@POST
	@Path("/node")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public MasterResponse newConnection(String connection);
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<AID> addConnection(String connection);
	
	@POST
	@Path("/agents/running")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addNewRunningAgent(AID runningAgent);
	
	@POST
	@Path("/agents/running/stop")
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeRunningAgent(AID runningAgent);
	
	@DELETE
	@Path("/node/{alias}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeConnection(@PathParam ("alias") String alias);
}
