package com.xware.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.xware.web.dao.VeiculoDao;
import com.xware.web.entitie.Usuario;

/**
 * Servlet responsável pelo bloqueio do veículo.
 */
@WebServlet(value="/MonitoracaoBloqueioServlet")
public class MonitoracaoBloqueioServlet extends HttpServlet{

	
	private static final long serialVersionUID = 7652080745320120044L;
	protected Logger logger = Logger.getLogger(MonitoracaoBloqueioServlet.class);

	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
        try {
        	
        	resp.setContentType("text/html");
        	resp.setCharacterEncoding("ISO-8859-1");

        	//parametros de entrada
        	long idVeiculo = Long.parseLong(String.valueOf(req.getParameter("id_veiculo")));
        	String senhaBloqueio = String.valueOf(req.getParameter("senha_bloqueio"));
        	String tipo = String.valueOf(req.getParameter("tipo"));
        	
            VeiculoDao dao = new VeiculoDao();
            Usuario u = (Usuario) req.getSession(true).getAttribute("usuario");
            
            //valida os parametros de bloqueio
            long idRastreador = dao.validarBloqueio(idVeiculo, senhaBloqueio, u.getIdCliente());
            if(idRastreador>0){
            	
            	int idStatus = 4;
            	if("1".equals(tipo)){
            		idStatus = 2;
            	}
            	
            	//efetiva o bloqueio
            	dao.bloquear(idVeiculo, idRastreador, idStatus, u.getId());
            	
            }else{
            	out.write("false");
            }
            
            
		} catch (Throwable e) {
			out.write("false");
			logger.error("erro bloqueando veiculo", e);
		}
        
	}
}
