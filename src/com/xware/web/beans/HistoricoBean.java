package com.xware.web.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.xware.web.dao.PosicionamentoDao;
import com.xware.web.entitie.Posicionamento;
import com.xware.web.entitie.Usuario;
import com.xware.web.util.Utils;

@ManagedBean(name="historicoBean")
@RequestScoped
public class HistoricoBean implements Serializable{

	private static final long serialVersionUID = 6539090559512795004L;
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	private Date dataMaxima;
	
	private Date dataDe;
	
	private Date dataAte;
	
	private boolean carregarCercas = false;
	
	private long idVeiculo;
	
	private String posicionamentos;
	
	public HistoricoBean() {
		setDataAte(new Date());
		setDataDe(new Date());
		setDataMaxima(new Date());
	}

	public void consultarHistorico(){
		logger.debug("consultando historico");
		try{
			PosicionamentoDao dao = new PosicionamentoDao();
			
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			Calendar calAte = Calendar.getInstance();
			calAte.setTime(getDataAte());
			calAte.set(Calendar.HOUR_OF_DAY, 23);
			calAte.set(Calendar.MINUTE, 59);
			calAte.set(Calendar.SECOND, 59);
			calAte.set(Calendar.MILLISECOND, 999);
			
			Calendar calDe = Calendar.getInstance();
			calDe.setTime(getDataDe());
			calDe.set(Calendar.HOUR_OF_DAY, 0);
			calDe.set(Calendar.MINUTE, 0);
			calDe.set(Calendar.SECOND, 0);
			calDe.set(Calendar.MILLISECOND, 1);
			
			if(dataAte.before(dataDe)){
				Utils.addMessageErro("Intervalo de datas inválido, 'Data de' maior que 'Data até.'");
				return;
			}
			
			if(Utils.intervaloDias(dataDe, dataAte) > 14){
				Utils.addMessageErro("Intervalo de datas não deve ser maior que 14 dias.");
				return;
			}
			
			List<Posicionamento> pos = dao.filtrarPosicionamentos(dao.posicionamentoHistorico(u, getIdVeiculo(), calDe.getTime(), calAte.getTime()));
			
			if(pos.size()==0){
				Utils.addMessageWarn("Sem histórico para o intervalo de datas.");
			}else{
				Utils.addMessageSucesso("Exibindo "+pos.size()+" pontos de posicionamentos.");
			}
			
			JSONArray json = new JSONArray();
			
			SimpleDateFormat dataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
			for (Iterator iterator = pos.iterator(); iterator.hasNext();) {
				Posicionamento p = (Posicionamento) iterator.next();
				
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("lat", p.getLat());
				jsonObj.put("lon", p.getLon());
				jsonObj.put("angulo", p.getAngulo());
				jsonObj.put("velocidade", p.getVelocidade());
				jsonObj.put("data_capturado",dataHora.format(new Date(p.getDataCapturado().getTime()+p.getDiferencaTempo())));
				jsonObj.put("dif_time", p.getDiferencaTempo());
				jsonObj.put("parado", p.isParado());
				if(p.isParado()){
					jsonObj.put("de", dataHora.format(p.getDataCapturado()) );
				}
				
				json.put(jsonObj);
			}
			
			
			String jsonString = json.toString();
			//logger.debug(jsonString);
			
			setPosicionamentos(jsonString);
			
		}catch(Throwable t){
			logger.error("erro consultando historico", t);
		}
	}

	
	


	public boolean isCarregarCercas() {
		return carregarCercas;
	}


	public void setCarregarCercas(boolean carregarCercas) {
		this.carregarCercas = carregarCercas;
	}


	public long getIdVeiculo() {
		return idVeiculo;
	}


	public void setIdVeiculo(long idVeiculo) {
		this.idVeiculo = idVeiculo;
	}


	public String getPosicionamentos() {
		return posicionamentos;
	}


	public void setPosicionamentos(String posicionamentos) {
		this.posicionamentos = posicionamentos;
	}


	public Date getDataDe() {
		return dataDe;
	}


	public void setDataDe(Date dataDe) {
		this.dataDe = dataDe;
	}


	public Date getDataAte() {
		return dataAte;
	}


	public void setDataAte(Date dataAte) {
		this.dataAte = dataAte;
	}


	public Date getDataMaxima() {
		return dataMaxima;
	}


	public void setDataMaxima(Date dataMaxima) {
		this.dataMaxima = dataMaxima;
	}

}
