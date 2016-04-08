package br.com.laminarsoft.jazzit.processo.limpatrilho.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import br.com.laminarsoft.jazzforms.process.connector.jfformout.handler.IJazzProcessOutParameters;

@SuppressWarnings("all")
public class JFMensagemPedido implements Serializable, IJazzProcessOutParameters {
	public String idUsuario = "";
	public String dhEnvioPedido = "";
	public String idProcesso = "";
	public String idSessao = "";
	public Map<String, String> itensPedidos = new HashMap<String, String>();
	
	
	@Override
	public Map<String, String> getValores() {
		return itensPedidos;
	}


	@Override
	public String getSessionId() {
		return idSessao;
	}


	@Override
	public String getProcessId() {
		return idProcesso;
	}	
	
	
}
