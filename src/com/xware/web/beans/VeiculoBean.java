package com.xware.web.beans;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.xware.web.dao.RastreadorDao;
import com.xware.web.dao.VeiculoDao;
import com.xware.web.entitie.Rastreador;
import com.xware.web.entitie.Usuario;
import com.xware.web.entitie.Veiculo;
import com.xware.web.util.Utils;

@ManagedBean(name="veiculoBean")
@RequestScoped
public class VeiculoBean implements Serializable{

	protected Logger logger = Logger.getLogger(this.getClass());
	
	private static final long serialVersionUID = 1613491953240919499L;

	private List<Veiculo> veiculos;
	
	private Veiculo veiculo;
	
	private List<Rastreador> rastreadores;
	
	public VeiculoBean() {
		setVeiculo(new Veiculo());
	}
	
	
	public String abrirEditar(){
		try{
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
			
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			VeiculoDao dao = new VeiculoDao();
			//busca o veiculo selecionado
			setVeiculo(dao.buscarVeiculo(Long.parseLong(id),u.getIdCliente()));
			
			return "/pages/veiculo/veiculoEditar";
		}catch(Throwable e){
			logger.error("erro ao gravar veiculo", e);
			Utils.addMessageErro("Falha ao buscar veículo.");
		}
		return "/pages/veiculo/template";
	}
	
	
	public String salvarEditar(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			veiculo.setIdCliente(u.getIdCliente());
			
			VeiculoDao dao = new VeiculoDao();
			
			dao.atualizar(veiculo);
			
			Utils.addMessageSucesso("Veículo atualizado com sucesso.");
			return "/pages/veiculo/template";
		}catch(Throwable e){
			logger.error("erro ao gravar veiculo", e);
			Utils.addMessageErro("Falha ao buscar veículo.");
		}
		return "/pages/veiculo/veiculoEditar";
	}
	
	
	public String excluir(){
		try{
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
			
			VeiculoDao dao = new VeiculoDao();
			
			dao.excluir(Long.parseLong(id));
			
			Utils.addMessageSucesso("Veículo excluído com sucesso.");
			return "/pages/veiculo/template";
		}catch(Throwable e){
			logger.error("erro ao excluir veiculo", e);
			Utils.addMessageErro("Falha ao excluir veículo.");
		}
		return "/pages/veiculo/veiculoEditar";
	}
	
	public String adicionar(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
		
			veiculo.setIdCliente(u.getIdCliente());
			
			VeiculoDao dao = new VeiculoDao();
			dao.adicionar(veiculo);
			
			Utils.addMessageSucesso("Veículo adicionado com sucesso.");
			
			return "/pages/veiculo/template";
		}catch(Throwable e){
			logger.error("erro ao gravar veiculo", e);
			Utils.addMessageErro("Falha ao adicionar veículo.");
		}	
		return "/pages/veiculo/veiculoAdd";
	}

	public List<Veiculo> getVeiculos() {
		try{
			VeiculoDao dao = new VeiculoDao();
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			setVeiculos(dao.buscarVeiculos(u));
		}catch(Throwable t){
			logger.error("erro ao obter veiculos", t);
		}
		return veiculos;
	}

	public void setVeiculos(List<Veiculo> veiculos) {
		this.veiculos = veiculos;
	}

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	public List<Rastreador> getRastreadores() {
		try{
			if(rastreadores == null){
				Usuario u = (Usuario) Utils.buscarSessao("usuario");
				RastreadorDao dao = new RastreadorDao();
				setRastreadores(dao.buscar(u.getIdCliente()));
			}
		}catch(Throwable t){
			logger.error("erro ao obter rastreadores", t);
		}
		return rastreadores;
	}

	public void setRastreadores(List<Rastreador> rastreadores) {
		this.rastreadores = rastreadores;
	}

}
