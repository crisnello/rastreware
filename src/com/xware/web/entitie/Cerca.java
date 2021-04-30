package com.xware.web.entitie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cerca implements Serializable{

	private static final long serialVersionUID = -4518970136085006669L;

	private List<Posicionamento> pos;
	
	private List<String> emails;
	
	private List<String> celulares;
		
	private long id;
	
	private String nome;
	
	//cor default
	private String cor = "0000ff";
	
	private int alerta;
	
	//default 24 horas
	private int monitorar = 24;
	
	private long idCliente;
	
	private String segunda_de;
	
	private String segunda_ate;
	
	private String terca_de;
	
	private String terca_ate;
	
	private String quarta_de;
	
	private String quarta_ate;
	
	private String quinta_de;
	
	private String quinta_ate;
	
	private String sexta_de;
	
	private String sexta_ate;
	
	private String sabado_de;
	
	private String sabado_ate;
	
	private String domingo_de;
	
	private String domingo_ate;

	//variavel usada na edicao da cerca para manter os pontos do mapa como string
	private String posicionamentosStr = "";
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public int getAlerta() {
		return alerta;
	}

	public void setAlerta(int alerta) {
		this.alerta = alerta;
	}

	public int getMonitorar() {
		return monitorar;
	}

	public void setMonitorar(int monitorar) {
		this.monitorar = monitorar;
	}

	public long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(long idCliente) {
		this.idCliente = idCliente;
	}

	public String getSegunda_de() {
		return segunda_de;
	}

	public void setSegunda_de(String segunda_de) {
		this.segunda_de = segunda_de;
	}

	public String getSegunda_ate() {
		return segunda_ate;
	}

	public void setSegunda_ate(String segunda_ate) {
		this.segunda_ate = segunda_ate;
	}

	public String getTerca_de() {
		return terca_de;
	}

	public void setTerca_de(String terca_de) {
		this.terca_de = terca_de;
	}

	public String getTerca_ate() {
		return terca_ate;
	}

	public void setTerca_ate(String terca_ate) {
		this.terca_ate = terca_ate;
	}

	public String getQuarta_de() {
		return quarta_de;
	}

	public void setQuarta_de(String quarta_de) {
		this.quarta_de = quarta_de;
	}

	public String getQuarta_ate() {
		return quarta_ate;
	}

	public void setQuarta_ate(String quarta_ate) {
		this.quarta_ate = quarta_ate;
	}

	public String getQuinta_de() {
		return quinta_de;
	}

	public void setQuinta_de(String quinta_de) {
		this.quinta_de = quinta_de;
	}

	public String getQuinta_ate() {
		return quinta_ate;
	}

	public void setQuinta_ate(String quinta_ate) {
		this.quinta_ate = quinta_ate;
	}

	public String getSexta_de() {
		return sexta_de;
	}

	public void setSexta_de(String sexta_de) {
		this.sexta_de = sexta_de;
	}

	public String getSexta_ate() {
		return sexta_ate;
	}

	public void setSexta_ate(String sexta_ate) {
		this.sexta_ate = sexta_ate;
	}

	public String getSabado_de() {
		return sabado_de;
	}

	public void setSabado_de(String sabado_de) {
		this.sabado_de = sabado_de;
	}

	public String getSabado_ate() {
		return sabado_ate;
	}

	public void setSabado_ate(String sabado_ate) {
		this.sabado_ate = sabado_ate;
	}

	public String getDomingo_de() {
		return domingo_de;
	}

	public void setDomingo_de(String domingo_de) {
		this.domingo_de = domingo_de;
	}

	public String getDomingo_ate() {
		return domingo_ate;
	}

	public void setDomingo_ate(String domingo_ate) {
		this.domingo_ate = domingo_ate;
	}

	public List<Posicionamento> getPos() {
		return pos;
	}

	public void setPos(List<Posicionamento> pos) {
		this.pos = pos;
	}

	public String getPosicionamentosStr() {
		
		if(pos!=null && pos.size()>0){
			posicionamentosStr = "";
			
			for (Iterator iterator = pos.iterator(); iterator.hasNext();) {
				Posicionamento p = (Posicionamento) iterator.next();
				posicionamentosStr+=p.getLat()+","+p.getLon()+";";
			}
			
			if(posicionamentosStr.endsWith(";")){
				posicionamentosStr = posicionamentosStr.substring(0, posicionamentosStr.length()-1);
			}
			
		}
		
		return posicionamentosStr;
	}

	public void setPosicionamentosStr(String posicionamentosStr) {
		if(posicionamentosStr!=null && !"".equals(posicionamentosStr)){
			if(pos==null){
				setPos(new ArrayList<Posicionamento>());
			}
			pos.clear();
			
			String lats[] = posicionamentosStr.split(";");
			for (int i = 0; i < lats.length; i++) {
				if(lats[i].length()>1){
					String lat[] = lats[i].split(",");
					Posicionamento p = new Posicionamento();
					p.setLat(Double.parseDouble(lat[0]));
					p.setLon(Double.parseDouble(lat[1]));
					pos.add(p);
				}
			}
			
		}
		this.posicionamentosStr = posicionamentosStr;
	}

	public List<String> getEmails() {
		return emails;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

	public List<String> getCelulares() {
		return celulares;
	}

	public void setCelulares(List<String> celulares) {
		this.celulares = celulares;
	}
	
}

