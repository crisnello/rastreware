package com.xware.web.entitie;

import java.io.Serializable;
import java.util.Date;

public class Posicionamento implements Serializable{

	private static final long serialVersionUID = -1912683384333267598L;

	private Date dataCapturado;
	
	private Date dataRecebido;
	
	private double lat;
	
	private double lon;
	
	private double angulo;
	
	private double velocidade;
	
	private long idVeiculo;
	
	private long tamanhoPacote;
	
	private long idRastreador;
	
	private Veiculo veiculo;

	//variaveis de controle
	private double distancia;
	private boolean parado;
	private long diferencaTempo;
	
	public Posicionamento() {
		setVeiculo(new Veiculo());
	}
	
	public Date getDataCapturado() {
		return dataCapturado;
	}

	public void setDataCapturado(Date dataCapturado) {
		this.dataCapturado = dataCapturado;
	}

	public Date getDataRecebido() {
		return dataRecebido;
	}

	public void setDataRecebido(Date dataRecebido) {
		this.dataRecebido = dataRecebido;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getAngulo() {
		return angulo;
	}

	public void setAngulo(double angulo) {
		this.angulo = angulo;
	}

	public double getVelocidade() {
		return velocidade;
	}

	public void setVelocidade(double velocidade) {
		this.velocidade = velocidade;
	}

	public long getIdVeiculo() {
		return idVeiculo;
	}

	public void setIdVeiculo(long idVeiculo) {
		this.idVeiculo = idVeiculo;
	}

	public long getIdRastreador() {
		return idRastreador;
	}

	public void setIdRastreador(long idRastreador) {
		this.idRastreador = idRastreador;
	}

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	public double getDistancia() {
		return distancia;
	}

	public void setDistancia(double distancia) {
		this.distancia = distancia;
	}

	public boolean isParado() {
		return parado;
	}

	public void setParado(boolean parado) {
		this.parado = parado;
	}

	public long getDiferencaTempo() {
		return diferencaTempo;
	}

	public void setDiferencaTempo(long diferencaTempo) {
		this.diferencaTempo = diferencaTempo;
	}

	public long getTamanhoPacote() {
		return tamanhoPacote;
	}

	public void setTamanhoPacote(long tamanhoPacote) {
		this.tamanhoPacote = tamanhoPacote;
	}

}
