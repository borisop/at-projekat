package jndi;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;

import model.Agent;
import model.AgentType;

@Stateless
@LocalBean
public class JndiTreeParser {
	
	private static final String INTF = "!" + Agent.class.getName();
	private static final String EXP = "java:jboss/exported/";
	private Context context;

	//	private Set<Class<? extendsXjafAgent>>ignored;
	
	@PostConstruct
	public void postConstruct() {
		context = ContextFactory.get(null);
//		ignored = new HashSet<>();
	}
	
	public List<AgentType> parse() throws NamingException {
		List<AgentType> result = new ArrayList<>();
		NamingEnumeration<NameClassPair> moduleList = context.list(EXP);
		
		while (moduleList.hasMore()) {
			NameClassPair ncp = moduleList.next();
			String module = ncp.getName();
			processModule("", module, result);
		}

		System.out.println("****JNDI PARSED****");
		return result;
	}
	
	private void processModule(String parentModule, String module, List<AgentType> result) throws NamingException {
		NamingEnumeration<NameClassPair> agentList;
		
		if (parentModule.equals("")) {
			agentList = context.list(EXP + "/" + module);
		} else {
			try {
				agentList = context.list(EXP + "/" + parentModule + "/" + module);
			} catch (NotContextException nce) {
				return;
			}
		}
		
		while (agentList.hasMore()) {
			NameClassPair ncp = agentList.next();
			String ejbName = ncp.getName();
			
			if (ejbName.contains("!")) {
				AgentType agType = parseEjbNameIfValid(parentModule, module, ejbName);
				if (agType != null) {
					result.add(agType);
				}
			} else {
				processModule(module, ejbName, result);
			}
		}
	}
	
	private AgentType parseEjbNameIfValid(String parentModule, String module, String ejbName) {
		if (ejbName != null && ejbName.endsWith(INTF)) {
			return parseEjbName(parentModule, module, ejbName);
		}
		return null;
	}
	
	private AgentType parseEjbName(String parentModule, String module, String ejbName) {
		ejbName = extractAgentName(ejbName);
		
//		if (!ignored.contains(ejbName)) {
//			String path;	
//			if(parentModule.equals("")) {
//				path = String.format("/%s/agents/xjaf", module);
//			} else {
//				path = String.format("/%s/%s/agents/xjaf", parentModule, module);
//				return new AgentType(parentModule + "/" + module, ejbName, path);
//			}
//		}
//		return null;
		return new AgentType(ejbName, parentModule + "/" + module);
	}
	
	private String extractAgentName(String ejbName) {
		int n = ejbName.lastIndexOf(INTF);
		return ejbName.substring(0, n);
	}
}
