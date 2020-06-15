package jndi;

import javax.naming.NamingException;

import agent.AgentManager;
import agent.AgentManagerBean;
import client.ClientBean;
import client.ClientRest;
import client.Node;
import messages.JMSFactory;
import messages.MessageManager;
import messages.MessageManagerBean;
import model.Agent;

public abstract class ObjectFactory {
	public static final String AgentManagerLookup = "ejb:" + Agent.PROJEKAT_EAR + "/" + Agent.PROJEKAT_MODULE + "//" + AgentManagerBean.class.getSimpleName() + "!" + AgentManager.class.getName();
	public static final String MessageManagerLookup = "ejb:" + Agent.PROJEKAT_EAR + "/" + Agent.PROJEKAT_MODULE + "//" + MessageManagerBean.class.getSimpleName() + "!" + MessageManager.class.getName();
	public static final String WebClientManagerLookup = "ejb:" + Agent.PROJEKAT_EAR + "/" + Agent.PROJEKAT_MODULE + "//" + ClientBean.class.getSimpleName() + "!" + ClientRest.class.getName() + "?stateful";
	public static final String JMSFactoryLookup = "java:app/" + Agent.PROJEKAT_MODULE + "/" + JMSFactory.class.getSimpleName();
	
	public static AgentManager getAgentManager(Node remote) {
		return lookup(AgentManagerLookup, AgentManager.class, remote);
	}
	
	public static MessageManager getMessageManager(Node remote) {
		return lookup(MessageManagerLookup, MessageManager.class, remote);
	}
	
	public static ClientRest getWebClientManager() {
		return lookup(WebClientManagerLookup, ClientBean.class, Node.LOCAL);
	}
	
	public static JMSFactory getJMSFactory() {
		return lookup(JMSFactoryLookup, JMSFactory.class, Node.LOCAL);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T lookup(String name, Class<T> c, Node remote) {
		try {
			return (T) ContextFactory.get(remote).lookup(name);
		} catch (NamingException ne) {
			throw new IllegalStateException("Failed to lookup " + name, ne);
		}
	}
}
