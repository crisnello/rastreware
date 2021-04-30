package com.xware.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
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

import com.xware.web.dao.PosicionamentoDao;
import com.xware.web.entitie.Posicionamento;
import com.xware.web.entitie.Usuario;

/**
 * Servlet responsavel por buscar os posicionamentos atuais dos veículos na tela de monitoração
 */
@WebServlet(value="/MonitoracaoServlet")
public class MonitoracaoServlet extends HttpServlet{

	private static final long serialVersionUID = -1804239419712042270L;
	
	private Logger logger = Logger.getLogger(this.getClass());

	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        try {
        	PrintWriter out = resp.getWriter();
        	
        	resp.setContentType("application/json");
        	resp.setCharacterEncoding("ISO-8859-1");
    		
            PosicionamentoDao dao = new PosicionamentoDao();
        	
            Usuario u = (Usuario) req.getSession(true).getAttribute("usuario");
            
			List<Posicionamento> ps = dao.posicionamentoAtual(u);
			
			SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");			
			//monta array json com os posicionamentos dos veiculos
			JSONArray array = new JSONArray();
			for (Iterator iterator = ps.iterator(); iterator.hasNext();) {
				Posicionamento p = (Posicionamento) iterator.next();
				
				JSONObject o = new JSONObject();
				o.put("lat", p.getLat());
				o.put("lon", p.getLon());
				o.put("id_veiculo", p.getVeiculo().getId());
				o.put("data_capturado",fmt.format(p.getDataCapturado()));
				o.put("velocidade", p.getVelocidade());
				o.put("modelo", p.getVeiculo().getModelo());
				o.put("placa", p.getVeiculo().getPlaca());
				o.put("icone", p.getVeiculo().getIcone());
				o.put("id_status", p.getVeiculo().getIdStatus());
				o.put("possui_bloqueio", p.getVeiculo().getPossuiBloqueio());
				
				array.put(o);
			}
			
			out.write(array.toString());
		} catch (Throwable e) {
			logger.error("erro buscando posicionamentos", e);
		}
        
	}
}
