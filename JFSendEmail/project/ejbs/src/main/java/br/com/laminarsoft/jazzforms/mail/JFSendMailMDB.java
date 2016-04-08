package br.com.laminarsoft.jazzforms.mail;

import java.io.IOException;
import java.util.Properties;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.drools.core.util.StringUtils;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jboss.ejb3.annotation.Pool;
import org.jboss.logging.Logger;

import br.com.laminarsoft.jazzforms.logging.types.InfoEnvioMailMSG;

import com.sun.mail.util.MailSSLSocketFactory;



/**
 * Message-Driven Bean implementation class for: TratamentoErrosSubscriber
 */
@MessageDriven(name = "JFSendMailMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/mailQ"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
@Pool("error-model-max-pool")
public class JFSendMailMDB implements MessageListener {

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
			LOGGER = Logger.getLogger("br.com.laminarsoft.jazzforms.mail");	
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
			
			if (message != null && message instanceof ObjectMessage && ((ObjectMessage)message).getObject() instanceof InfoEnvioMailMSG) {
				InfoEnvioMailMSG mail = ((InfoEnvioMailMSG)((ObjectMessage)message).getObject());

			    String smtpAutenticado = PROPS.getProperty("smtp.autenticacao");
			    String smtpServer = PROPS.getProperty("smtp.server");
			    String smtpPort = PROPS.getProperty("smtp.port");
			    String smtpUser = PROPS.getProperty("usuario.smtp");
			    String smtpSenha = PROPS.getProperty("senha.smtp");
			    
			    Properties prps = new Properties();
//			    prps.put("mail.smtp.starttls.enable", true); // added this line
			    SMTPAuthenticator auth = null;
			    
			    if (smtpAutenticado.equalsIgnoreCase("true")) {
					prps.put("mail.smtp.auth", true);
					prps.put("mail.smtp.user", smtpUser);
					prps.put("mail.smtp.password", smtpSenha);
					auth = new SMTPAuthenticator();
				} else {
					prps.put("mail.smtp.auth", false);
				}
				prps.put("mail.smtp.host", smtpServer);
			    prps.put("mail.smtp.port", smtpPort);
//			    prps.put("mail.smtps.ssl.trust", "*");
//			    prps.put("mail.imaps.ssl.trust", "*");
//			    
//			    MailSSLSocketFactory socketFactory= new MailSSLSocketFactory();
//			    socketFactory.setTrustedHosts(new String[] { smtpServer});
//			    socketFactory.setTrustAllHosts(true); 
//			    prps.put("mail.smtps.socketFactory", socketFactory);
			    
			    Session session = Session.getDefaultInstance(prps, auth);
			    
			    MimeMessage mimeMessage = new MimeMessage(session);
			    
			    String from = mail.remetente;
			    if(StringUtils.isEmpty(from)) {
			    	from = PROPS.getProperty("from");
			    }
			    
			    mimeMessage.setFrom(new InternetAddress(from));
			    mimeMessage.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(mail.destinatario));
			    mimeMessage.setSubject(mail.assunto);
			    mimeMessage.setContent(mail.conteudo, "text/html");

			    
//			    BodyPart msgBodyPart = new MimeBodyPart();
//			    msgBodyPart.setText(mail.conteudo);
//
//			    Multipart multipart = new MimeMultipart();
//			    multipart.addBodyPart(msgBodyPart);
//			    
//			    msgBodyPart = new MimeBodyPart();
//			    multipart.addBodyPart(msgBodyPart);
//			    mimeMessage.setContent(multipart);
			    
			    Transport.send(mimeMessage);
			    
			} else {
				LOGGER.error("A mensagem será descartada, pois não é do formato: br.com.laminarsoft.jazzforms.logging.types.InfoEnvioRelatorioMSG");
			}
		} catch (JMSException e) {
			LOGGER.error("Erro na recuperação da mensagem enviada: " + e.getMessage());
		} catch(MessagingException e) {
			LOGGER.error("Erro ao montar mensagem: " + e.getMessage());
		} catch(Exception e) {
			LOGGER.error("Erro ao montar relatorio: " + e.getMessage());
		}
	}
	
	private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
           String username = PROPS.getProperty("usuario.smtp");
           String password = PROPS.getProperty("senha.smtp");
           return new PasswordAuthentication(username, password);
        }
    }	
	
}
