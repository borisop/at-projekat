package agents;

import javax.ejb.Remote;
import javax.ejb.Stateful;

import model.ACLMessage;
import model.Agent;
import model.BaseAgent;
import model.Performative;
import webcrawler.Spider;

@Stateful
@Remote(Agent.class)
public class GathererAgent extends BaseAgent {

	private static final long serialVersionUID = 1L;
	private String nodeName;
	
	@Override
	protected void onInit() {
		super.onInit();
		nodeName = System.getProperty("jboss.node.name");
		System.out.println("****GATHERER AGENT CREATED****");
		System.out.println("Agent is on " + nodeName);
	}
	
	@Override
	protected void onMessage(ACLMessage aclMessage) {
		System.out.println("****GATHERER AGENT RECIEVED MESSAGE****");
		
		if (aclMessage.getPerformative() == Performative.REQUEST) {
			Spider spider = new Spider();
			spider.search("http://arstechnica.com/", "computer");
		}
		
	}
 
}
