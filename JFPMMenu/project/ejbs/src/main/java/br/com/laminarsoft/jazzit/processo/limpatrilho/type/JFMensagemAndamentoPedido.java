package br.com.laminarsoft.jazzit.processo.limpatrilho.type;

import java.io.Serializable;

@SuppressWarnings("all")
public class JFMensagemAndamentoPedido implements Serializable {
	public String destinatario = "";
	public String assunto = "";
	public String conteudo = "";
	
	public JFMensagemPedido pedido = null;
	
	public JFMensagemAndamentoPedido(){}
	
	public JFMensagemAndamentoPedido(String destinatario, String assunto, String contedudo, JFMensagemPedido pedido) {
		this.destinatario = destinatario;
		this.assunto = assunto;
		this.conteudo = conteudo;
		this.pedido = pedido;
	}
}
