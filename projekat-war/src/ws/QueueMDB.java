package ws;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import model.ACLMessage;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/projekat-ws")
})
public class QueueMDB implements MessageListener {
	
	@EJB
	WSEndPoint ws;
	
	@Override
	public void onMessage(Message arg0) {
		try {
			Object msg = ((ObjectMessage) arg0).getObject();
			if (msg instanceof ACLMessage) {
				ACLMessage aclMessage = (ACLMessage) ((ObjectMessage) arg0).getObject();
			} else {
				System.out.println("****WS HANDLING RECIEVED TEXT MESSAGE****");
				String tmsg = (String) ((ObjectMessage) arg0).getObject();
				ws.echoTextMessage(tmsg);
			}
		} catch (JMSException jmse) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void processMessage(Message msg) throws JMSException {
		TextMessage tmsg = (TextMessage) msg;
	}

}
