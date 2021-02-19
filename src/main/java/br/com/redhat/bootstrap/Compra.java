package br.com.redhat.bootstrap;

import java.io.Serializable;
import java.util.Date;

public class Compra implements Serializable {

	private static final long serialVersionUID = -5756088987442366624L;
	
	private Long id;
	private Long valor;
	private Long itens;
	private String status;
	private Date criadoEm;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getValor() {
		return valor;
	}
	public void setValor(Long valor) {
		this.valor = valor;
	}
	public Long getItens() {
		return itens;
	}
	public void setItens(Long itens) {
		this.itens = itens;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Override
	public String toString() {
		return String.format("toString: { id = %s; valor = %s; itens = %s; status = %s; criadoEm = %s}", id, valor,itens,status,criadoEm);
	}
}
