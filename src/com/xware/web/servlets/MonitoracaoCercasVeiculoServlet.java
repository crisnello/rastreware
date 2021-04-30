package com.xware.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.xware.web.dao.CercaDao;
import com.xware.web.entitie.Cerca;
import com.xware.web.entitie.Posicionamento;
import com.xware.web.entitie.Usuario;

/**
 * Servlet responsável por buscar as cercas do veículo
 */
@WebServlet(value="/MonitoracaoCercasVeiculoServlet")
public class MonitoracaoCercasVeiculoServlet extends HttpServlet{

	private static final long serialVersionUID = -4100591128710132535L;
	
	protected Logger logger = Logger.getLogger(MonitoracaoCercasVeiculoServlet.class);
	
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
        	PrintWriter out = resp.getWriter();
        	resp.setContentType("application/json");
        	resp.setCharacterEncoding("ISO-8859-1");
        	
        	Usuario u = (Usuario) req.getSession(true).getAttribute("usuario");
        	long idVeiculo = Long.parseLong(String.valueOf(req.getParameter("idVeiculo")));
        	
        	CercaDao dao = new CercaDao();
        	
        	logger.debug("buscarCercasVeiculo("+u.getIdCliente()+", "+idVeiculo+")");
        	
        	List<Cerca> cs = dao.buscarCercasVeiculo(u.getIdCliente(), idVeiculo);
        	
        	//monta json de retorno com o array de cercas, cada cerca contem um array de posicionamentos
        	JSONArray cercas = new JSONArray();
        	for (Iterator iterator = cs.iterator(); iterator.hasNext();) {
				Cerca c = (Cerca) iterator.next();
				JSONObject o = new JSONObject();
				o.put("cor", c.getCor());
				
				JSONArray pontos = new JSONArray();
				for (Iterator iterator2 = c.getPos().iterator(); iterator2.hasNext();) {
					Posicionamento p = (Posicionamento) iterator2.next();
					JSONObject oPos = new JSONObject();
					oPos.put("lat", p.getLat());
					oPos.put("lng", p.getLon());
					pontos.put(oPos);
				}
				o.put("pontos", pontos);
				
				cercas.put(o);
			}
        	
        	out.write(cercas.toString());
        	
		} catch (Throwable e) {
			logger.error("erro buscando cercas", e);
		}
	}

}
