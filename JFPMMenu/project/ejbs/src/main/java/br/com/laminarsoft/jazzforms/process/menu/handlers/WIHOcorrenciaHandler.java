package br.com.laminarsoft.jazzforms.process.menu.handlers;

import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;

import br.com.laminarsoft.jazzit.processo.limpatrilho.type.JFMensagemAndamentoPedido;
import br.com.laminarsoft.jazzit.processo.limpatrilho.type.JFMensagemOcorrencia;
import br.com.laminarsoft.jazzit.processo.limpatrilho.type.JFMensagemPedido;


public class WIHOcorrenciaHandler implements WorkItemHandler {

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		
	}
	
	
}
