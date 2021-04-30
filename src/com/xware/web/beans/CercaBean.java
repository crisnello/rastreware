package com.xware.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

import com.xware.web.beans.validator.EmailValidator;
import com.xware.web.dao.CercaDao;
import com.xware.web.dao.UsuarioDao;
import com.xware.web.dao.VeiculoDao;
import com.xware.web.entitie.Cerca;
import com.xware.web.entitie.Usuario;
import com.xware.web.entitie.Veiculo;
import com.xware.web.util.Utils;


@ManagedBean(name="cercaBean")
public class CercaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(this.getClass());
	
	private List<Cerca> cercas;
	
	private Cerca cerca;
	
	private DualListModel<Veiculo> veiculoPick;
	
	private DualListModel<Usuario> emailPick;
	
	private DualListModel<Usuario> celularPick;
	
	private String emailAdd;
	
	public CercaBean() {
		atualizarCercas();
		
		setCerca(new Cerca());
		cerca.setEmails(new ArrayList<String>());
		
		atualizarPick();
	}

	
	public void adicionarEmail(ActionEvent e){
		logger.debug("add email:"+emailAdd);
		
		if(!EmailValidator.validar(emailAdd)){
			Utils.addMessageFor("Email inválido","inputEmail");
			return;
		}
		
		cerca.getEmails().add(emailAdd);
		setEmailAdd("");
	}
	
	private void atualizarPick(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			VeiculoDao dao = new VeiculoDao();
			
			setVeiculoPick(new DualListModel<Veiculo>(dao.buscarVeiculos(u), new ArrayList<Veiculo>()));
			
			UsuarioDao daoUsuario = new UsuarioDao();
			
			List<Usuario> us = daoUsuario.buscarUsuarios(u.getIdCliente());
			
			setEmailPick(new DualListModel<Usuario>(us, new ArrayList<Usuario>()));
			
			List<Usuario> usCelular = new ArrayList<Usuario>();
			for (int i = 0; i < us.size(); i++) {
				Usuario uCelular = us.get(i);
				if(uCelular.getCelular()!=null && !"".equals(uCelular.getCelular())){
					usCelular.add(uCelular);
				}
			}
			
			setCelularPick(new DualListModel<Usuario>(usCelular, new ArrayList<Usuario>()));
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao obter obter veículos.");	
		}
	}
	
	public String adicionar() {
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			cerca.setIdCliente(u.getIdCliente());
			
			CercaDao dao = new CercaDao();
			
			dao.adicionar(cerca, veiculoPick.getTarget(),celularPick.getTarget(),emailPick.getTarget());
			
			atualizarCercas();
			
			Utils.addMessageSucesso("Cerca adicionada com sucesso.");
			return "/pages/cerca/template";
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao gravar cerca.");	
		}
		
		return "/pages/cerca/cercaAdd";
	}
	
	
	public String excluir() {
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
			
			CercaDao dao = new CercaDao();
			dao.excluir(u.getIdCliente(), Long.parseLong(id));
			
			atualizarCercas();
			Utils.addMessageSucesso("Cerca excluída com sucesso.");
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao excluir cerca.");
			return "/pages/cerca/cercaEditar";
		}
		return "/pages/cerca/template";
	}
	
	public String salvarEditar() {
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			CercaDao dao = new CercaDao();
			
			cerca.setIdCliente(u.getIdCliente());
			dao.atualizar(u, cerca, veiculoPick.getTarget(),celularPick.getTarget(),emailPick.getTarget());
			
			atualizarCercas();
			
			Utils.addMessageSucesso("Cerca alterada com sucesso.");
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao salvar cerca.");
			return "/pages/cerca/cercaEditar";
		}
		
		return "/pages/cerca/template";
	}
	
	public String abrirEditar() {
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
			
			CercaDao dao = new CercaDao();
			
			long idCerca = Long.parseLong(id);
			
			
			setCerca(dao.buscarCerca(u.getIdCliente(),idCerca));
			
			VeiculoDao daoVeiculos = new VeiculoDao();
			
			//balancear listas de veiculos
			List<Veiculo> sourceNew = daoVeiculos.buscarVeiculos(u);
			
			List<Veiculo> target = daoVeiculos.buscarVeiculosCerca(u, idCerca);
			
			List<Veiculo> source = new ArrayList<Veiculo>();
			for (Iterator iterator = sourceNew.iterator(); iterator.hasNext();) {
				Veiculo v = (Veiculo) iterator.next();
				boolean contem = false;
				for (Iterator iterator2 = target.iterator(); iterator2.hasNext();) {
					Veiculo vT = (Veiculo) iterator2.next();
					if(vT.getId()==v.getId()){
						contem = true;
						break;
					}
				}
				if(!contem){
					source.add(v);
				}
			}
			
			veiculoPick.setSource(source);
			veiculoPick.setTarget(target);
			
			
			
			//emails e celulares
			
			UsuarioDao daoUsuario = new UsuarioDao();
			List<Usuario> usuarios = daoUsuario.buscarUsuarios(u.getIdCliente());
			
			//balancear listar de email
			List<Usuario> emailTarget = new ArrayList<Usuario>();
			List<Usuario> emailSource = new ArrayList<Usuario>();
			
			List<String> emails = dao.buscarEmailsCerca(idCerca);
			for (int i = 0; i < usuarios.size(); i++) {
				Usuario usu = usuarios.get(i);
				boolean contem = false;
				for (int j = 0; j < emails.size(); j++) {
					String email = emails.get(j);
					if(usu.getEmail().equalsIgnoreCase(email)){
						contem = true;
						emailTarget.add(usu);
						break;
					}
				}
				if(!contem){
					emailSource.add(usu);	
				}
			}
			
			setEmailPick(new DualListModel<Usuario>(emailSource, emailTarget));
			
			//balancear listar de celulares
			List<Usuario> celularesTarget = new ArrayList<Usuario>();
			List<Usuario> celularesSource = new ArrayList<Usuario>();
			List<String> celulares = dao.buscarCelularesCerca(idCerca);
			for (int i = 0; i < usuarios.size(); i++) {
				Usuario usu = usuarios.get(i);
				boolean contem = false;
				for (int j = 0; j < celulares.size(); j++) {
					String cel = celulares.get(j);
					if(cel.equalsIgnoreCase(usu.getCelular())){
						contem = true;
						celularesTarget.add(usu);
						break;
					}
				}
				if(!contem){
					if(usu.getCelular()!=null && !"".equals(usu.getCelular())){
						celularesSource.add(usu);	
					}
				}
			}
			
			setCelularPick(new DualListModel<Usuario>(celularesSource, celularesTarget));
			
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao buscar cerca.");
			return "/pages/cerca/template";
		}
		
		return "/pages/cerca/cercaEditar";
	}
	
	private void atualizarCercas(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			CercaDao dao = new CercaDao();
			
			cercas = dao.buscarCercas(u.getIdCliente());
			
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao obter cercas.");
		}
	}
	
	public List<Cerca> getCercas() {
		return cercas;
	}

	public void setCercas(List<Cerca> cercas) {
		this.cercas = cercas;
	}


	public Cerca getCerca() {
		return cerca;
	}


	public void setCerca(Cerca cerca) {
		this.cerca = cerca;
	}


	public DualListModel<Veiculo> getVeiculoPick() {
		return veiculoPick;
	}


	public void setVeiculoPick(DualListModel<Veiculo> veiculoPick) {
		this.veiculoPick = veiculoPick;
	}


	public String getEmailAdd() {
		return emailAdd;
	}


	public void setEmailAdd(String emailAdd) {
		this.emailAdd = emailAdd;
	}


	public DualListModel<Usuario> getEmailPick() {
		return emailPick;
	}


	public void setEmailPick(DualListModel<Usuario> emailPick) {
		this.emailPick = emailPick;
	}


	public DualListModel<Usuario> getCelularPick() {
		return celularPick;
	}


	public void setCelularPick(DualListModel<Usuario> celularPick) {
		this.celularPick = celularPick;
	}
	
}
