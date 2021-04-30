package com.xware.web.beans;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import com.xware.web.dao.ClienteDao;
import com.xware.web.dao.VeiculoDao;
import com.xware.web.entitie.Cliente;
import com.xware.web.entitie.Usuario;
import com.xware.web.entitie.Veiculo;
import com.xware.web.util.Utils;

@ManagedBean(name="monitoracaoBean")
@RequestScoped
public class MonitoracaoBean implements Serializable {

	private static final long serialVersionUID = 5439352856687176305L;
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	//lista de clientes, apenas usuarios com idPerfil == 1 podem visualizar
	private List<Cliente> clientes;

	private List<Veiculo> veiculos;
	
	public List<Cliente> getClientes() {
		if(clientes==null){
			try {
				clientes = new ClienteDao().buscarClientes();
			} catch (Throwable e) {
				logger.error("Erro ao buscar lista de clientes", e);
			}
		}
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public List<Veiculo> getVeiculos() {
		try {
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			VeiculoDao dao = new VeiculoDao();
		
			veiculos = dao.buscarVeiculos(u);
		} catch (Throwable e) {
			logger.error("Erro ao buscar veiculos", e);
		}
		
		return veiculos;
	}

	public void setVeiculos(List<Veiculo> veiculos) {
		this.veiculos = veiculos;
	}
	
}
