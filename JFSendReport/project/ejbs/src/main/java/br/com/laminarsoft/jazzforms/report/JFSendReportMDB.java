package br.com.laminarsoft.jazzforms.report;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRProperties;

import org.drools.runtime.StatefulKnowledgeSession;
import org.jboss.ejb3.annotation.Pool;
import org.jboss.logging.Logger;

import br.com.laminarsoft.jazzforms.logging.types.InfoEnvioRelatorioMSG;



/**
 * Message-Driven Bean implementation class for: TratamentoErrosSubscriber
 */
@MessageDriven(name = "JFSendReportMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/reportQ"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
@Pool("error-model-max-pool")
public class JFSendReportMDB implements MessageListener {

	/**
	 * The internal ksession where the processes will run.
	 */
	protected static StatefulKnowledgeSession session;
	public static Logger LOGGER;
	public static Properties PROPS;
	
	public static Map<String, JasperReport> reports = new HashMap<String, JasperReport>();
	public static JasperPrint jasperPring;

	/**
	 * Default constructor.
	 */

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		if (LOGGER == null) {
			LOGGER = Logger.getLogger("br.com.laminarsoft.jazzforms.report");	
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		try {
			
			if (message != null && message instanceof ObjectMessage && ((ObjectMessage)message).getObject() instanceof InfoEnvioRelatorioMSG) {
				InfoEnvioRelatorioMSG relatorio = ((InfoEnvioRelatorioMSG)((ObjectMessage)message).getObject());
				if(reports.get(relatorio.nomeRelatorio) == null) {
					String caminhoJasper = PROPS.getProperty("caminho.jasper");
					JRProperties.setProperty(JRProperties.COMPILER_CLASSPATH, JRProperties.getProperty(JRProperties.COMPILER_CLASSPATH) 
							+ ";" + caminhoJasper + "jasperreports-6.0.3.jar"
							+ ";" + caminhoJasper + "jasperreports-functions-6.0.3.jar"
							+ ";" + caminhoJasper + "jasperreports-fonts-6.0.3.jar"); 
					InputStream is = this.getClass().getResourceAsStream("/" + relatorio.nomeRelatorio + ".jrxml");
					JasperReport report = JasperCompileManager.compileReport(is);
					reports.put(relatorio.nomeRelatorio, report);
				}
				String jndiName = PROPS.getProperty("connection.jndi."+ relatorio.nomeRelatorio);
				
				InitialContext iniCtx = new InitialContext();
				DataSource ds = (DataSource)iniCtx.lookup(jndiName);
				Connection conn = ds.getConnection();
				
			    JasperPrint jasperPrint = JasperFillManager.fillReport(reports.get(relatorio.nomeRelatorio), relatorio.parametrosPesquisa, conn);
			    byte[] pdfFile = JasperExportManager.exportReportToPdf(jasperPrint);
			    
			    String smtpAutenticado = PROPS.getProperty("smtp.autenticacao");
			    String smtpServer = PROPS.getProperty("smtp.server");
			    String smtpPort = PROPS.getProperty("smtp.port");
			    String smtpUser = PROPS.getProperty("usuario.smtp");
			    String smtpSenha = PROPS.getProperty("senha.smtp");
			    
			    Properties prps = new Properties();
			    prps.put("mail.smtp.starttls.enable", true); // added this line
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
			    
			    Session session = Session.getDefaultInstance(prps, auth);
			    
			    MimeMessage mimeMessage = new MimeMessage(session);
			    
			    mimeMessage.setFrom(new InternetAddress(PROPS.getProperty("from")));
			    mimeMessage.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(relatorio.destinatario));
			    mimeMessage.setSubject(relatorio.assunto);
			    
			    BodyPart msgBodyPart = new MimeBodyPart();
			    msgBodyPart.setText(relatorio.conteudo);

			    Multipart multipart = new MimeMultipart();
			    multipart.addBodyPart(msgBodyPart);
			    
			    msgBodyPart = new MimeBodyPart();
			    javax.activation.DataSource dts = new ByteArrayDataSource(pdfFile, "application/pdf");
			    msgBodyPart.setDataHandler(new DataHandler(dts));
			    msgBodyPart.setFileName(relatorio.nomeRelatorio + "_" + sdf.format(new Date()) + ".pdf");
			    multipart.addBodyPart(msgBodyPart);
			    mimeMessage.setContent(multipart);
			    
			    Transport.send(mimeMessage);
			    
			} else {
				LOGGER.error("A mensagem será descartada, pois não é do formato: br.com.laminarsoft.jazzforms.logging.types.InfoEnvioRelatorioMSG");
			}
		} catch (JMSException e) {
			LOGGER.error("Erro na recuperação da mensagem enviada: " + e.getMessage());
		} catch (JRException e) {
			LOGGER.error("Erro ao compilar arquivo de modelo jasper: " + e.getMessage());
		} catch(NamingException e) {
			LOGGER.error("Erro ao localizar data source: " + e.getMessage());
		} catch(SQLException e) {
			LOGGER.error("Erro ao recuperar conexão: " + e.getMessage());
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
