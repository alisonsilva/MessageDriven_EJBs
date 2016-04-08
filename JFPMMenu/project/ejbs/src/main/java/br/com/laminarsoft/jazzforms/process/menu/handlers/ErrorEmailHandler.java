package br.com.laminarsoft.jazzforms.process.menu.handlers;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;

import br.com.laminarsoft.jazzforms.jbpm.archetypes.ErrorMessage;
import br.com.laminarsoft.jazzforms.process.menu.JFPMMenuInMDB;

public class ErrorEmailHandler implements WorkItemHandler {

	@Resource(name="java:jboss/mail/Gmail")
	private Session mailSession;
	
	
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		boolean erroCriandoSessao = false;
		Map<String, Object> results = new HashMap<String, Object>();
		if (mailSession == null) {
			try {
				Context initCtx = new InitialContext();
				mailSession = (Session) initCtx.lookup("java:jboss/mail/Gmail");
			} catch (NamingException e) {
				results.put("msgSaida", "erro ao enviar email");
				JFPMMenuInMDB.LOGGER.error("erro ao enviar email: ", e);
				erroCriandoSessao = true;
			}
		}
		if (!erroCriandoSessao) {
			ErrorMessage mensagem = (ErrorMessage) workItem
					.getParameter("mensagem");
			JFPMMenuInMDB.LOGGER
					.info("Mensagem recebida para envio de email: " + mensagem);
			String from = (String) workItem.getParameter("from");
			String to = (String) workItem.getParameter("to");
			String subject = (String) workItem.getParameter("subject");
			try {
				MimeMessage m = new MimeMessage(mailSession);
				Address fromMail = new InternetAddress(from);
				Address[] toMail = new InternetAddress[] { new InternetAddress(
						to) };

				m.setFrom(fromMail);
				m.setRecipients(Message.RecipientType.TO, toMail);
				m.setSubject(subject);
				m.setSentDate(new java.util.Date());

				String msgErroMail = new String();
				for (String msgErro : mensagem.messages) {
					msgErroMail += msgErro;
					msgErroMail += "\n";
				}

				m.setContent(msgErroMail, "text/plain");
				Transport.send(m);

				results.put("msgSaida", "sucesso");
			} catch (AddressException e) {
				results.put("msgSaida", "erro ao enviar email");
				JFPMMenuInMDB.LOGGER.error(
						"erro ao enviar email: ", e);
			} catch (MessagingException e) {
				results.put("msgSaida", "erro ao enviar email");
				JFPMMenuInMDB.LOGGER.error(
						"erro ao enviar email: ", e);
			}
		}
		manager.completeWorkItem(workItem.getId(), results);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		
	}

	
}
