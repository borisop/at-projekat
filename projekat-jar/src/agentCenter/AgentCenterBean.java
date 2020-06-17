package agentCenter;

import java.io.File;
import java.io.FileInputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.ws.rs.Path;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agent.AgentManager;
import model.AID;
import model.MasterResponse;

@Singleton
@Startup
@Remote(AgentCenterRest.class)
@Path("/connection")
public class AgentCenterBean implements AgentCenterRest {
	
	private String nodeAddr;
	private String nodeName;
	private String master = null;
	private List<String> connections = new ArrayList<String>();
	private Set<AID> allRunningAgents = new HashSet<AID>();
	
	@EJB
	private AgentManager agentManager;
	
	@PostConstruct
	private void init() {
		try {
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			ObjectName http = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");
			this.nodeAddr = (String) mBeanServer.getAttribute(http, "boundAddress");
//			this.nodeName = System.getProperty("jboss.node.name") + ":8080";
			this.nodeName = this.nodeAddr + ":8080";
			System.out.println("****THIS NODE -> " + this.nodeName + "****");
			
			File f = FileUtils.getFile(AgentCenterRest.class, "", "connections.properties");
			FileInputStream fis = new FileInputStream(f);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();
			this.master = properties.getProperty("master");
			System.out.println("****MASTER NODE -> " + this.master + "****");
			
			if (master != null && !master.equals("")) {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = client.target("http://" + master + "/projekat-war/rest/connection");
				AgentCenterRest rest = rtarget.proxy(AgentCenterRest.class);
//				this.connections = rest.newConnection(this.nodeName);
				MasterResponse mr = rest.newConnection(this.nodeName);
				this.connections = mr.getConnections();
				this.allRunningAgents = mr.getRunningAgents();
				updateRunningAgents(allRunningAgents);
				this.connections.remove(this.nodeName);
				this.connections.add(this.master);
				System.out.println("****SLAVE NODE ADDED****");
			} else {
				master = nodeName;
				System.out.println("****MASTER NODE ADDED****");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public MasterResponse newConnection(String connection) {
		System.out.println("****ADDING NEW SLAVE NODE****"); 
		
		List<AID> masterRunningAgents = agentManager.getRunningAgents();
		Set<AID> masterSet = new HashSet<AID>(masterRunningAgents);
		allRunningAgents.addAll(masterSet);
		
		for (String c : connections) {
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget rtarget = client.target("http://" + c + "/projekat-war/rest/connection");
			AgentCenterRest rest = rtarget.proxy(AgentCenterRest.class);
			List<AID> cRunningAgents = rest.addConnection(connection);
			Set<AID> set = new HashSet<AID>(cRunningAgents);
			allRunningAgents.addAll(set);
		}
		connections.add(connection);
		
		System.out.println("****SENDING RUNNING AGENTS TO " + connection + "****");
		System.out.println(allRunningAgents); 

		MasterResponse mr = new MasterResponse();
		mr.setConnections(connections);
		mr.setRunningAgents(allRunningAgents);

		return mr;
	}

	@Override
	public List<AID> addConnection(String connection) {
		connections.add(connection);
		System.out.println("****SLAVE NODE -> " + connection + "****");
		
		return agentManager.getRunningAgents();
	}
	
	@Override
	public void addNewRunningAgent(AID runningAgent) {
		System.out.println("****SENDING NEWLY RUN AGENT TO OTHER NODES****");
		
		String agentOrigin = runningAgent.getAgentCenter().getAddress();
		/*
		 * Ako se agent pokrene na masteru obavesti sve ostale slave-ove da ga dodaju, 
		 * a ako se agent pokrene na slave-u obavesti master da obavesti ostale slave-ove da ga dodaju
		*/
		if (!connections.isEmpty()) {
			if (nodeName.equals(master)) {		
				for (String c : connections) {
					if (!c.equals(agentOrigin)) { 
						ResteasyClient client = new ResteasyClientBuilder().build();
						ResteasyWebTarget rtarget = client.target("http://" + c + "/projekat-war/rest/connection");
						AgentCenterRest rest = rtarget.proxy(AgentCenterRest.class);
						rest.addNewRunningAgent(runningAgent);
					}
				}
				allRunningAgents.add(runningAgent);
				if (!agentOrigin.equals(master)) {
					agentManager.addRunningAgentFromAnotherHostToCache(runningAgent);
				}
			} else if(!agentOrigin.equals(nodeName)) {
				System.out.println("****NEW AGENT RUNNING ON OTHRE NODE****");
				agentManager.addRunningAgentFromAnotherHostToCache(runningAgent);
				allRunningAgents.add(runningAgent);
			} else {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = client.target("http://" + master + "/projekat-war/rest/connection");
				AgentCenterRest rest = rtarget.proxy(AgentCenterRest.class);
				rest.addNewRunningAgent(runningAgent);
				allRunningAgents.add(runningAgent);
			}
		} else {
			allRunningAgents.add(runningAgent);
		}
	}

	@Override
	public void removeRunningAgent(AID runningAgent) {
		System.out.println("****SENDING STOPPED AGENT TO OTHER NODES****");
		
		String agentOrigin = runningAgent.getAgentCenter().getAddress();
		/*
		 * Ako se agent zaustavi na masteru obavesti sve ostale slave-ove da ga uklone, 
		 * a ako se agent zaustavi na slave-u obavesti master da obavesti ostale slave-ove da ga uklone
		*/
		if (!connections.isEmpty()) {
			if (nodeName.equals(master)) {		
				for (String c : connections) {
					if (!c.equals(agentOrigin)) { 
						ResteasyClient client = new ResteasyClientBuilder().build();
						ResteasyWebTarget rtarget = client.target("http://" + c + "/projekat-war/rest/connection");
						AgentCenterRest rest = rtarget.proxy(AgentCenterRest.class);
						rest.removeRunningAgent(runningAgent);
					}
				}
				removeAgent(runningAgent);
				if (!agentOrigin.equals(master)) {
					agentManager.removeRunningAgentFromAnotherHostFromCache(runningAgent);
				}
			} else if(!agentOrigin.equals(nodeName)) {
				System.out.println("****AGENT STOPPED ON OTHRE NODE****");
				agentManager.removeRunningAgentFromAnotherHostFromCache(runningAgent);
				removeAgent(runningAgent);
			} else {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = client.target("http://" + master + "/projekat-war/rest/connection");
				AgentCenterRest rest = rtarget.proxy(AgentCenterRest.class);
				rest.removeRunningAgent(runningAgent);
				removeAgent(runningAgent);
			}
		} else {
			removeAgent(runningAgent);
		}
	}
	/*
	 * ako se masteru javi da se neki slave gasi, master javlja ostalim slave-ovima da ga izbrisu
	 */
	@Override
	public void removeConnection(String alias) {
		if (master.equals(nodeName)) {
			System.out.println("****SLAVE NODE SHUTTING DOWN****");
			connections.remove(alias);
			for (String c :  connections) {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = client.target("http://" + c + "/projekat-war/rest/connection");
				AgentCenterRest rest = rtarget.proxy(AgentCenterRest.class);
				rest.removeConnection(alias);
			}
		} else {
			System.out.println("****OTHER SLAVE NODE SHUTTING DOWN****");
			connections.remove(alias);
		}
	}
	
	/*
	 * pre gasenja javi master node-u da se gasis 
	 */
	
	@PreDestroy
	public void terminate() {
		System.out.println("****NODE SHUTTING DOWN****");
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget rtarget = client.target("http://" + master + "/projekat-war/rest/connection");
		AgentCenterRest rest = rtarget.proxy(AgentCenterRest.class);
		rest.removeConnection(nodeName);
	}

	public void updateRunningAgents(Set<AID> runningAgents) {
		allRunningAgents = runningAgents;
		for (AID aid : runningAgents) {
			agentManager.addRunningAgentFromAnotherHostToCache(aid);
		}
	}
	
	public void removeAgent(AID aid) {
		AID agentAid = null;
		for (AID a : allRunningAgents) {
			if (aid.getAgentCenter().getAlias().equals(a.getAgentCenter().getAlias()) && 
					aid.getAgentCenter().getAddress().equals(a.getAgentCenter().getAddress()) &&
					aid.getAgentType().getName().equals(a.getAgentType().getName()) &&
					aid.getAgentType().getModule().equals(a.getAgentType().getModule())) {
				agentAid = a;
				break;
			}
		}
		
		allRunningAgents.remove(agentAid);
	}
	
}
