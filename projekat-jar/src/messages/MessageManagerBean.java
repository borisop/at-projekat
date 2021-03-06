package messages;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import model.ACLMessage;
import model.AID;
import model.Performative;

@Stateless
@Remote(MessageManager.class)
@LocalBean
public class MessageManagerBean implements MessageManager {
	
	@EJB
	private JMSFactory jmsFactory;
	private Session session;
	private MessageProducer defaultProducer;
	private MessageProducer wsProducer;
	
	
	@PostConstruct
	public void postConstruct() {
		session = jmsFactory.getSession();
		defaultProducer = jmsFactory.getDefaultProducer(session);
		session = jmsFactory.getSession();
		wsProducer = jmsFactory.getWsProducer(session);
	}
	
	@PreDestroy
	public void preDestroy() {
		try {
			session.close();
		} catch (JMSException jmse) {
			
		}
	}
	
	@Override
	public List<String> getPerformatives() {
		final Performative[] arr = Performative.values();
		List<String> list = new ArrayList<>(arr.length);
		for (Performative p : arr) {
			list.add(p.toString());
		}
		
		return list;
	}

	@Override
	public void post(ACLMessage message) {
		for (int i = 0; i < message.getRecievers().size(); i++) {
			if (message.getRecievers().get(i) == null) {
				throw new IllegalArgumentException("AID cannot be null.");
			}
			postToReciever(message, i);
		}
		
	}
	
	@Override
	public void post(String message) {
		try {
			ObjectMessage jmsMsg = session.createObjectMessage(message);
			getWsProducer().send(jmsMsg);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void postToReciever(ACLMessage msg, int index) {
		AID aid = msg.getRecievers().get(index);
		try {
			ObjectMessage jmsMsg = session.createObjectMessage(msg);
			setupJmsMsg(jmsMsg, aid, index);
			getProducer(msg).send(jmsMsg);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void setupJmsMsg(ObjectMessage jmsMsg, AID aid, int index) throws JMSException {
//		jmsMsg.setStringProperty("JMSXGroupID", aid.getStr());
		jmsMsg.setIntProperty("AIDIndex", index);
		jmsMsg.setStringProperty("_HQ_DUPL_ID", UUID.randomUUID().toString());
	}
	
	private MessageProducer getProducer(ACLMessage msg) {
//		if (MessageManager.REPLY_WITH_TEST.equals(msg.inReplyTo)) {
//			return getTestProducer();
//		}
		
		return defaultProducer;
	}
	
	
	private MessageProducer getWsProducer() {
//		if (wsProducer == null) {
//			wsProducer = factory.getTestProducer(session);
//		}
		return wsProducer;
	}
}
