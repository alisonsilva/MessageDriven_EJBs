package br.com.laminarsoft.jazzit.processo.limpatrilho.type;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("all")
public class JFMensagemOcorrencia implements Serializable {
	public JFMensagemPedido pedido = null;
	public int codigoMensagem = -1;
	public Date dhOcorrencia = new Date();
	public String conteudoMensagem = "";
	
	public JFMensagemOcorrencia(){}
	
	public JFMensagemOcorrencia(int codigo, String conteudo, JFMensagemPedido pedido) {
		this.codigoMensagem = codigo;
		this.conteudoMensagem = conteudo;
		this.pedido = pedido;
	}
}
