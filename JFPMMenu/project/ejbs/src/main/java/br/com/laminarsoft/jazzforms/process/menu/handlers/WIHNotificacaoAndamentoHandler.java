package br.com.laminarsoft.jazzforms.process.menu.handlers;

import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;

import br.com.laminarsoft.jazzforms.jbpm.controller.WebServiceController;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoMensagem;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.MensagemVO;
import br.com.laminarsoft.jazzit.processo.limpatrilho.type.JFMensagemAndamentoPedido;
import br.com.laminarsoft.jazzit.processo.limpatrilho.type.JFMensagemOcorrencia;
import br.com.laminarsoft.jazzit.processo.limpatrilho.type.JFMensagemPedido;



public class WIHNotificacaoAndamentoHandler implements WorkItemHandler {
	private static Logger LOGGER;
	private static Properties PROP;
	
	private static final String USUARIO = "jbpm";
	private static final String SENHA = "123455";

	public static JFMensagemOcorrencia montaOcorrenciaEntradaPedido() {
		
		return new JFMensagemOcorrencia();
	}
	
	public static JFMensagemAndamentoPedido montaMensagemPedidoEmAndamento() {
		return new JFMensagemAndamentoPedido();
	}	
	
	public JFMensagemPedido handleOcorrencia(JFMensagemPedido mensagem) {
		
		return mensagem;
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		if (LOGGER == null) {
			LOGGER = LogManager.getLogger("br.com.laminarsoft.jazzforms.process.jfformout");	
		}	
		JFMensagemPedido pedido = null;
		if(workItem.getParameter("pedido") != null && workItem.getParameter("pedido") instanceof JFMensagemPedido) {
			pedido = (JFMensagemPedido)workItem.getParameter("pedido");
		}
		JFMensagemAndamentoPedido msgAndamentoPeidido = null;
		if(workItem.getParameter("mensagemAndamento") != null && workItem.getParameter("mensagemAndamento") instanceof JFMensagemAndamentoPedido) {
			msgAndamentoPeidido = (JFMensagemAndamentoPedido)workItem.getParameter("mensagemAndamento");
		}
		String usuariosDestinatarios = "";
		if(workItem.getParameter("usuarios destinatarios") != null) {
			usuariosDestinatarios = (String)workItem.getParameter("usuarios destinatarios");
		}
		String gruposDestinatarios = "";
		if(workItem.getParameter("grupos destinatarios") != null) {
			gruposDestinatarios = (String)workItem.getParameter("grupos destinatarios");
		}
		String assunto = null;
		String conteudo = null;
		if(workItem.getParameter("assunto") != null) {
			assunto = (String)workItem.getParameter("assunto");
		}
		if(workItem.getParameter("conteudo") != null) {
			conteudo = (String)workItem.getParameter("conteudo");
		}
		
		if(pedido == null) {
			LOGGER.error("O pedido deve ser passado para a notifica��o de andamento");
		} else if(msgAndamentoPeidido == null && (assunto == null || conteudo == null)) {
			LOGGER.error("O assunto e o conte�do da mensagem devem ser definidos");
		} else {
			String destinatarios = "";
			if(msgAndamentoPeidido != null) {
				destinatarios = msgAndamentoPeidido.destinatario + ";";
			}
			destinatarios += usuariosDestinatarios;
			
			MensagemVO mensagem = new MensagemVO();
			mensagem.conteudo = conteudo;
			mensagem.data = new Date();
			mensagem.titulo = assunto;
			mensagem.remetenteUID = "jbpm";
			
			if(destinatarios != null && destinatarios.length() > 0) {
				StringTokenizer strtok = new StringTokenizer(destinatarios, ";");
				while(strtok.hasMoreTokens()) {
					String usr = strtok.nextToken();
					mensagem.destinatariosUids.add(usr);
				}
			}

			destinatarios = "";
			if(msgAndamentoPeidido != null) {
				destinatarios = msgAndamentoPeidido.destinatario + ";";
			}
			destinatarios += gruposDestinatarios;
			
			if(destinatarios != null && destinatarios.length() > 0) {
				StringTokenizer strtok = new StringTokenizer(destinatarios, ";");
				while(strtok.hasMoreElements()) {
					String grp = strtok.nextToken();
					mensagem.grupos.add(grp);
				}
			}
			WebServiceController controller = WebServiceController.getInstance(LOGGER);
			InfoRetornoMensagem info = controller.novaMensagem(mensagem, USUARIO, SENHA);
			if(info.codigo != 0) {
				LOGGER.error("Erro ao enviar nova mensagem para o bpm: " + info.mensagem);
			} else {
				LOGGER.info("Mensagem enviada com sucesso");
			}
		}
		
		manager.completeWorkItem(workItem.getId(), null);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		
		manager.abortWorkItem(workItem.getId());
	}
	
	
}
