package br.com.laminarsoft.jazzforms.logging;

import java.io.IOException;
import java.util.Properties;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.drools.runtime.StatefulKnowledgeSession;
import org.jboss.ejb3.annotation.Pool;
import org.jboss.logging.Logger;

import br.com.laminarsoft.jazzforms.logging.types.MobileLoggingMSG;


/**
 * Message-Driven Bean implementation class for: TratamentoErrosSubscriber
 */
@MessageDriven(name = "JFLoggingMobileMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/loggingMobileQ"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
@Pool("error-model-max-pool")
public class JFLoggingMobileMDB implements MessageListener {

	/**
	 * The internal ksession where the processes will run.
	 */
	protected static StatefulKnowledgeSession session;
	public static Logger LOGGER;
	public static Properties PROPS;
	

	/**
	 * Default constructor.
	 */

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		if (LOGGER == null) {
			LOGGER = Logger.getLogger("br.com.laminarsoft.jazzforms.negocio");	
		}
		if (PROPS == null) {
			PROPS = new Properties();
			try {
				PROPS.load(this.getClass().getResourceAsStream("/config.properties"));
			} catch (IOException e) {
				LOGGER.error("Erro ao carregar arquivo com configurações: " + e.getMessage());
				return;
			}
		}
		
		try {
			if (message != null && message instanceof ObjectMessage && ((ObjectMessage)message).getObject() instanceof MobileLoggingMSG) {
				MobileLoggingMSG pedido = ((MobileLoggingMSG)((ObjectMessage)message).getObject());
				LOGGER.info(pedido.toString());
			} else {
				LOGGER.error("A mensagem será descartada, pois não é do formato: br.com.laminarsoft.jazzforms.logging.types.MobileLoggingMSG");
			}
		} catch (JMSException e) {
			LOGGER.error("Erro na recuperação da mensagem enviada: " + e.getMessage());
		}
	}

	
}
