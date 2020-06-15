package agentCenter;

import java.io.File;
import java.io.FileInputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
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

@Singleton
@Startup
@Remote(AgentCenterRest.class)
@Path("/connection")
public class AgentCenterBean implements AgentCenterRest {
	
	private String nodeAddr;
	private String nodeName;
	private String master = null;
	private List<String> connections = new ArrayList<String>();
	
	@EJB
	private AgentManager agentManager;
	
	@PostConstruct
	private void init() {
		try {
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			ObjectName http = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");
			this.nodeAddr = (String) mBeanServer.getAttribute(http, "boundAddress");
			this.nodeName = System.getProperty("jboss.node.name") + ":8080";
			
			File f = FileUtils.getFile(AgentCenterRest.class, "", "connections.properties");
			FileInputStream fis = new FileInputStream(f);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();
			this.master = properties.getProperty("master");
			if (master != null && !master.equals("")) {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = client.target("http://" + master + "projekat-war/rest/connection");
				AgentCenterRest rest = rtarget.proxy(AgentCenterRest.class);
				this.connections = rest.newConnection(this.nodeName);
				this.connections.remove(this.nodeName);
				this.connections.add(this.master);
				System.out.println("****SLAVE NODE ADDED****");
			} else {
				System.out.println("****MASTER NODE ADDED****");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<String> newConnection(String connection) {
		for (String c : connections) {
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget rtarget = client.target("http://" + c + "/projekat-war/rest/connection");
			AgentCenterRest rest = rtarget.proxy(AgentCenterRest.class);
			rest.addConnection(connection);
		}
		connections.add(connection);
		
		return connections;
	}

	@Override
	public void addConnection(String connection) {
		connections.add(connection);
	}
	
}
