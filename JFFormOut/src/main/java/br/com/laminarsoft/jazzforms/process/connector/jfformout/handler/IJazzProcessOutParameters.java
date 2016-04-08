package br.com.laminarsoft.jazzforms.process.connector.jfformout.handler;

import java.util.Map;

public interface IJazzProcessOutParameters {
	public Map<String, String> getValores();
	public String getSessionId();
	public String getProcessId();
}
