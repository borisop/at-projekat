package agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.NamingException;

import org.infinispan.Cache;

import appCache.GlobalCache;
import client.Node;
import jndi.JndiTreeParser;
import jndi.ObjectFactory;
import model.AID;
import model.Agent;
import model.AgentCenter;
import model.AgentType;

@Stateless
@Remote(AgentManager.class)
@LocalBean
public class AgentManagerBean implements AgentManager{

	private static final long serialVersionUID = 1L;
	
	private Cache<AID, Agent> agents;
	
	@EJB
	private JndiTreeParser jndiTreeParser;
	
	@Override
	public List<AgentType> getAvailableAgentTypes() {
		try {
			return jndiTreeParser.parse();
		} catch (NamingException ne) {
			throw new IllegalStateException(ne);
		}
	}

	@Override
	public AID startServerAgent(AgentType type, String runtimeName) {
		String host = System.getProperty("jboss.node.name");
		
		if (host == null) {
//			host = type.
		}
		AgentCenter agentCenter = new AgentCenter(runtimeName, host);
		AID aid = new AID(agentCenter, type);
		startServerAgent(aid, true);
		
		return aid;
	}
	
	public void startServerAgent(AID aid, boolean replace) {
		if (getCache().containsKey(aid)) {
			if(!replace) {
				throw new IllegalStateException("Agent already running: " + aid);
			}
			stopAgent(aid);
//			if (args == null || args.get("noUIUpdate", "").equals("")) {
//				LoggerUtil.logAgent(aid, SocketMessageType.REMOVE);
//			}
		}
		Agent agent = null;
		try {
			agent = ObjectFactory.lookup(getAgentLookup(aid.getAgentType(), true), Agent.class, Node.LOCAL);
		} catch (IllegalStateException ise) {
			agent = ObjectFactory.lookup(getAgentLookup(aid.getAgentType(), false), Agent.class, Node.LOCAL);
		}
		initAgent(agent, aid);
		System.out.println("Agent " + aid.getAgentCenter().getAlias() + " started.");
	}
	
	public void stopAgent(AID aid) {
//		Agent agent = getCache().get(aid);
		Cache<AID, Agent> cache = getCache();
		Set<AID> aids = cache.keySet();
		Agent agent = null;
		AID agentAid = null;
		for (AID a : aids) {
			if (aid.getAgentCenter().getAlias().equals(a.getAgentCenter().getAlias()) && 
					aid.getAgentCenter().getAddress().equals(a.getAgentCenter().getAddress()) &&
					aid.getAgentType().getName().equals(a.getAgentType().getName()) &&
					aid.getAgentType().getModule().equals(a.getAgentType().getModule())) {
				agent = cache.get(a);
				agentAid = a;
				break;
			}
		}
		
		if (agent != null) {
			agent.stop();
//			getCache().remove(aid);
			getCache().remove(agentAid);
			System.out.println("Stopped agent: " + aid);
//			LoggerUtil.log("Stopped agent: " + aid, true);
//			LoggerUtil.logAgent(aid, SocketMessageType.REMOVE);
		}
	}
	
	public void initAgent(Agent agent, AID aid) {
		getCache().put(aid, agent);
		agent.init(aid);
	}

	@Override
	public List<AID> getRunningAgents() {
		Set<AID> set = getCache().keySet();
		
		if (set.size() > 0) {
			try {
				AID aid = set.iterator().next();
				try {
					ObjectFactory.lookup(getAgentLookup(aid.getAgentType(), true), Agent.class, Node.LOCAL);
				} catch (Exception ex) {
					ObjectFactory.lookup(getAgentLookup(aid.getAgentType(), false), Agent.class, Node.LOCAL);
				}
			} catch (Exception e) {
				getCache().clear();
				return new ArrayList<AID>();
			}
		}
		return new ArrayList<AID>(set);
	}
	
	private Cache<AID, Agent> getCache() {
		if (agents == null) {
			agents = GlobalCache.get().getRunningAgents();
		}
		
		return agents;
	}
	
	private String getAgentLookup(AgentType agentType, boolean stateful) {
		if (inEar(agentType)) {
			if (stateful) {
				return String.format("ejb:%s//%s!%s?stateful", agentType.getModule(), agentType.getName(), Agent.class.getName());
			} else {
				return String.format("ejb:%s//%s!%s", agentType.getModule(), agentType.getName(), Agent.class.getName());
			}
		} else {
			if (stateful) {
				return String.format("ejb:%s//%s!%s?stateful", agentType.getModule(), agentType.getName(), Agent.class.getName());
			} else {
				return String.format("ejb:%s//%s!%s", agentType.getModule(), agentType.getName(), Agent.class.getName());
			}
		}
	}
	
	private boolean inEar(AgentType agentType) {
		if (agentType.getModule().contains("/")) {
			return true;
		}
		
		return false;
	}
	
	public Agent getAgentReference(AID aid) {
		return getCache().get(aid);
	}
}
