package com.xware.web.beans.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.xware.web.dao.VeiculoDao;
import com.xware.web.entitie.Usuario;
import com.xware.web.entitie.Veiculo;
import com.xware.web.util.Utils;

@FacesConverter(value="veiculoConverter")
public class VeiculoConverter implements Converter {

	public Object getAsObject(FacesContext ctx, UIComponent ui, String valor) {
		Veiculo v = null;
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			VeiculoDao dao = new VeiculoDao();
			v = dao.buscarVeiculo(Long.parseLong(valor),u.getIdCliente());
		}catch(Throwable e){
			Utils.addMessageErro("Erro ao converter veículo.");
		}
		
		return v;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent ui, Object valor) {
		if(valor!=null){
			Veiculo v = (Veiculo) valor;
			return String.valueOf(v.getId());
		}
		return null;
	}

}
