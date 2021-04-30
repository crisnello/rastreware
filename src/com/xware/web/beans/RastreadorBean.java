package com.xware.web.beans;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.xware.web.dao.RastreadorDao;
import com.xware.web.entitie.Rastreador;
import com.xware.web.entitie.RastreadorTipo;
import com.xware.web.entitie.Usuario;
import com.xware.web.util.Utils;

@ManagedBean(name="rastreadorBean")
@RequestScoped
public class RastreadorBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected Logger logger = Logger.getLogger(this.getClass());

	private List<Rastreador> rastreadores;
	
	private List<RastreadorTipo> rastreadorTipo;
	
	private Rastreador rastreador;
	
	public RastreadorBean() {
		setRastreador(new Rastreador());
		
		Usuario u = (Usuario) Utils.buscarSessao("usuario");
		
		try {
			RastreadorDao dao = new RastreadorDao();
			setRastreadores(dao.buscar(u.getIdCliente()));
			setRastreadorTipo(dao.tiposRastreador());
		} catch (Throwable e) {
			Utils.addMessageErro("Erro ao obter rastreadores.");
		}
	}

	public String abrirEditar() {
		try{
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
			
			RastreadorDao dao = new RastreadorDao();
			
			rastreador = dao.buscarRastreador(Long.parseLong(id));
			
			rastreador.setSenhaConfirm(rastreador.getSenha());
			
		}catch(Throwable e){
			Utils.addMessageErro("Erro ao abrir rastreador.");
			return "/pages/rastreador/rastreadorAdd";
		}
		return "/pages/rastreador/rastreadorEditar";
	}
	
	public String salvarEditar() {
		try{
			if(!rastreador.getSenha().equals(rastreador.getSenhaConfirm())){
				Utils.addMessageErro("Senhas não conferem.");
				return "/pages/rastreador/rastreadorEditar";
			}
			
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			rastreador.setIdCliente(u.getIdCliente());
			
			RastreadorDao dao = new RastreadorDao();
			
			dao.atualizar(rastreador);
		
			Utils.addMessageSucesso("Rastreador atualizado com sucesso.");
			
			//atualiza lista de rastreadores
			setRastreadores(dao.buscar(u.getIdCliente()));
		}catch(Throwable e){
			Utils.addMessageErro("Erro ao salvar rastreador.");
			return "/pages/rastreador/rastreadorEditar";
		}
		return "/pages/rastreador/template";
	}
	
	public String excluir() {
		try{
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
			
			RastreadorDao dao = new RastreadorDao();
			
			dao.excluir(Long.parseLong(id));
			
			Utils.addMessageSucesso("Rastreador excluído com sucesso.");
			
			//atualiza lista de rastreadores
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			setRastreadores(dao.buscar(u.getIdCliente()));
		}catch(Throwable e){
			Utils.addMessageErro("Erro ao excluir rastreador.");
			return "/pages/rastreador/rastreadorEditar";
		}
		return "/pages/rastreador/template";
	}
	
	public String adicionar() {
		try{
			if(!rastreador.getSenha().equals(rastreador.getSenhaConfirm())){
				Utils.addMessageErro("Senhas não conferem.");
				return "/pages/rastreador/rastreadorAdd";
			}
			
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			rastreador.setIdCliente(u.getIdCliente());
			
			RastreadorDao dao = new RastreadorDao();
			
			dao.adicionar(rastreador);
		
			Utils.addMessageSucesso("Rastreador adicionado com sucesso.");
			
			//atualiza lista de rastreadores
			setRastreadores(dao.buscar(u.getIdCliente()));
		}catch(Throwable e){
			Utils.addMessageErro("Erro ao adicionar rastreador.");
			return "/pages/rastreador/rastreadorAdd";
		}
		
		return "/pages/rastreador/template";
	}
	
	public List<Rastreador> getRastreadores() {
		return rastreadores;
	}

	public void setRastreadores(List<Rastreador> rastreadores) {
		this.rastreadores = rastreadores;
	}

	public Rastreador getRastreador() {
		return rastreador;
	}

	public void setRastreador(Rastreador rastreador) {
		this.rastreador = rastreador;
	}

	public List<RastreadorTipo> getRastreadorTipo() {
		return rastreadorTipo;
	}

	public void setRastreadorTipo(List<RastreadorTipo> rastreadorTipo) {
		this.rastreadorTipo = rastreadorTipo;
	}
	
	
}
