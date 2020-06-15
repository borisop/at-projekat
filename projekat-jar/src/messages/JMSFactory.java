package messages;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.Session;

import model.Agent;

@Singleton
@LocalBean
public class JMSFactory {
//	private Logger LOG = LoggerFactory.getLogger(JMSFactory.class);
	private Connection connection;
	@Resource(lookup = "java:jboss/exported/jms/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;
	@Resource(lookup = "java:jboss/exported/jms/queue/projekat")
	private Queue defaultQueue;
//	@Resource(lookup = "java:jboss/exported/jms/queue/projekat")
//	private Queue testQueue;
	
	@PostConstruct
	public void postConstruct() {
		try {
			connection = connectionFactory.createConnection("guest", "guest.guest.1");
			connection.setClientID(Agent.PROJEKAT_MODULE);
		} catch (JMSException jmse) {
			throw new IllegalStateException(jmse);
		}
	}
	
	@PreDestroy
	public void preDestroy() {
		try {
			connection.close();
		} catch (JMSException jmse) {
//			LOG.warn("Exception while closing the JMS connection.", jmse);
			System.out.println("Exception while closing the JMS connection.");
		}
	}
	
	public Session getSession() {
		try {
			return connection.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
		} catch (JMSException jmse) {
			throw new IllegalStateException(jmse);
		}
	}
	
	public MessageProducer getDefaultProducer(Session session) {
		try {
			return session.createProducer(defaultQueue);
		} catch (JMSException jmse) {
			throw new IllegalStateException(jmse);
		}
	}
	
//	public MessageProducer getTestProducer(Session session) {
//		try {
//			return session.createProducer(testQueue);
//		} catch (JMSException jmse) {
//			throw new IllegalStateException(jmse);
//		}
//	}
	
}
