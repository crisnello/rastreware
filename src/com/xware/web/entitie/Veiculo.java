package com.xware.web.entitie;

import java.io.Serializable;
import java.util.Date;

public class Veiculo implements Serializable{

	private static final long serialVersionUID = 656616495185174720L;
	
	private long id;
	
	private String modelo;
	
	private String placa;
	
	private Date dataCadastro;
	
	private double km;
	
	private String icone;
	
	private int possuiBloqueio;
	
	private Date dataAlteracao;
	
	private int idStatus;
	
	private long idRastreador;
	
	private long idCliente;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public double getKm() {
		return km;
	}

	public void setKm(double km) {
		this.km = km;
	}

	public String getIcone() {
		return icone;
	}

	public void setIcone(String icone) {
		this.icone = icone;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public int getIdStatus() {
		return idStatus;
	}

	public void setIdStatus(int idStatus) {
		this.idStatus = idStatus;
	}

	public long getIdRastreador() {
		return idRastreador;
	}

	public void setIdRastreador(long idRastreador) {
		this.idRastreador = idRastreador;
	}

	public long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(long idCliente) {
		this.idCliente = idCliente;
	}

	public int getPossuiBloqueio() {
		return possuiBloqueio;
	}

	public void setPossuiBloqueio(int possuiBloqueio) {
		this.possuiBloqueio = possuiBloqueio;
	}

}
