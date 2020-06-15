package jndi;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import client.Node;

public abstract class ContextFactory {
	
	private static final Logger logger = Logger.getLogger(ContextFactory.class.getName());
	private static Context context;
	private static Context remoteContext;
	
	static {
		try {
			Hashtable<String, Object> jndiProps = new Hashtable<>();
			jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			context = new InitialContext(jndiProps);
		} catch(NamingException ne) {
			logger.log(Level.SEVERE, "Context initialization error!", ne);
		}
	}
	
	public static Context get(Node remote) {
		if (remote != Node.LOCAL) {
			try {
				if (remoteContext == null || !remoteContext.getEnvironment().get(Context.PROVIDER_URL).toString().equals("http-remoting://" + remote.host + ":" + remote.port)) {
					Hashtable<String, Object> jndiProps = new Hashtable<>();
					jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
					jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
					jndiProps.put(Context.PROVIDER_URL, "http-remoting://" + remote.host + ":" + remote.port);
					remoteContext = new InitialContext(jndiProps);
				}
			} catch (NamingException ne) {
				ne.printStackTrace();
				remoteContext = null;
			}
			return remoteContext;
		}
		return context;
	}
}
