package com.xware.web.beans.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.xware.web.dao.UsuarioDao;
import com.xware.web.entitie.Usuario;
import com.xware.web.util.Utils;

@FacesConverter(value="usuarioConverter")
public class UsuarioConverter implements Converter {

	public Object getAsObject(FacesContext ctx, UIComponent ui, String valor) {
		Usuario u = null;
		try{
			UsuarioDao dao = new UsuarioDao();
			u = dao.buscarUsuario(Long.parseLong(valor));
		}catch(Throwable e){
			Utils.addMessageErro("Erro ao converter veículo.");
		}
		
		return u;
	}

	public String getAsString(FacesContext ctx, UIComponent ui, Object valor) {
		if(valor!=null){
			Usuario u = (Usuario) valor;
			return String.valueOf(u.getId());
		}
		return null;
	}

}
