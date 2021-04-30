package com.xware.web.entitie;

import java.io.Serializable;

public class Rastreador implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private long id;
	
	private long idCliente;
	
	private String nome;
	
	private String codigo;
	
	private int idTipo;
	
	private String numCel;
	
	private int timeDiference;
	
	private String senha;
	
	//variaveis de controle
	private String senhaConfirm;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(long idCliente) {
		this.idCliente = idCliente;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public int getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(int idTipo) {
		this.idTipo = idTipo;
	}

	public String getNumCel() {
		return numCel;
	}

	public void setNumCel(String numCel) {
		this.numCel = numCel;
	}

	public int getTimeDiference() {
		return timeDiference;
	}

	public void setTimeDiference(int timeDiference) {
		this.timeDiference = timeDiference;
	}

	public String getSenhaConfirm() {
		return senhaConfirm;
	}

	public void setSenhaConfirm(String senhaConfirm) {
		this.senhaConfirm = senhaConfirm;
	}
	
	

}
