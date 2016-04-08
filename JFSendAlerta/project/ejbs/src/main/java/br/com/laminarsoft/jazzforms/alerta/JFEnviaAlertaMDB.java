package br.com.laminarsoft.jazzforms.alerta;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

import br.com.laminarsoft.jazzforms.logging.types.InfoEnvioAlertaMSG;



/**
 * Message-Driven Bean implementation class for: TratamentoErrosSubscriber
 */
@MessageDriven(name = "JFEnviaAlertaMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/envioAlertaQ"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
@Pool("error-model-max-pool")
public class JFEnviaAlertaMDB implements MessageListener {

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
			LOGGER = Logger.getLogger("br.com.laminarsoft.jazzforms.alerta");	
		}
		if (PROPS == null) {
			PROPS = new Properties();
			try {
				PROPS.load(this.getClass().getResourceAsStream("/config.properties"));
			} catch (IOException e) {
				LOGGER.error("Erro ao carregar arquivo com configura��es: " + e.getMessage());
				return;
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		try {
			
			if (message != null && message instanceof ObjectMessage && ((ObjectMessage)message).getObject() instanceof InfoEnvioAlertaMSG) {
				InfoEnvioAlertaMSG destinatario = ((InfoEnvioAlertaMSG)((ObjectMessage)message).getObject());
				
			} else {
				LOGGER.error("A mensagem ser� descartada, pois n�o � do formato: br.com.laminarsoft.jazzforms.logging.types.InfoEnvioRelatorioMSG");
			}
		} catch (JMSException e) {
			LOGGER.error("Erro na recupera��o da mensagem enviada: " + e.getMessage());
		} catch(Exception e) {
			LOGGER.error("Erro ao montar relatorio: " + e.getMessage());
		}
	}

	
}
