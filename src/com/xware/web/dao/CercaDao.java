package com.xware.web.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.xware.web.entitie.Cerca;
import com.xware.web.entitie.Posicionamento;
import com.xware.web.entitie.Usuario;
import com.xware.web.entitie.Veiculo;
import com.xware.web.exceptions.DaoException;

public class CercaDao extends BaseDao{

	public List<Cerca> buscarCercasVeiculo(long idCliente, long idVeiculo) throws Throwable{
		List<Cerca> cs = new ArrayList<Cerca>();
		try{
			conectar();
			
			con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			
			StringBuilder query = new StringBuilder();
			query.append("select c.nome,c.id,c.cor from cerca c inner join cerca_veiculo cv on cv.id_cerca = c.id ");
			query.append("where c.id_cliente=");
			query.append(idCliente);
			query.append(" and cv.id_veiculo=");
			query.append(idVeiculo);
			
			rs = con.createStatement().executeQuery(query.toString());
			
			while(rs.next()){
				Cerca c = new Cerca();
				c.setId(rs.getLong("id"));
				c.setNome(rs.getString("nome"));
				c.setCor(rs.getString("cor"));
				c.setPos(new ArrayList<Posicionamento>());
				
				String q2 = "select lat,lng from cerca_pontos where id_cerca="+c.getId();
				rs2 = con.createStatement().executeQuery(q2);
				while(rs2.next()){
					Posicionamento p = new Posicionamento();
					
					p.setLat(rs2.getDouble("lat"));
					p.setLon(rs2.getDouble("lng"));
					
					c.getPos().add(p);
				}
				cs.add(c);
			}
			
		}catch (Throwable e) {
			logger.error("Erro processado buscar cercas", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
		
		return cs;
		
	}
	
	public Cerca buscarCerca(long idCliente, long idCerca) throws Throwable{
		Cerca c = null;
		try{
			conectar();
			
			StringBuilder query = new StringBuilder();
			query.append("select nome,id,cor,alerta,monitorar from cerca where id_cliente=");
			query.append(idCliente);
			query.append(" and id=");
			query.append(idCerca);
			
			rs = con.createStatement().executeQuery(query.toString());
			
			while(rs.next()){
				c = new Cerca();
				c.setId(rs.getLong("id"));
				c.setNome(rs.getString("nome"));
				c.setCor(rs.getString("cor"));
				c.setAlerta(rs.getInt("alerta"));
				c.setMonitorar(rs.getInt("monitorar"));
				c.setPos(new ArrayList<Posicionamento>());
				
				String q2 = "select lat,lng from cerca_pontos where id_cerca="+c.getId();
				rs = con.createStatement().executeQuery(q2);
				while(rs.next()){
					Posicionamento p = new Posicionamento();
					
					p.setLat(rs.getDouble("lat"));
					p.setLon(rs.getDouble("lng"));
					
					c.getPos().add(p);
				}
			}
			
		}catch (Throwable e) {
			logger.error("Erro processado buscar cerca", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
		
		return c;
		
	}
	
	public void adicionar(Cerca cerca, List<Veiculo> veiculos, List<Usuario>celulares, List<Usuario> emails)throws Throwable{
		try{
			conectar();
			
			String q1 = "insert into cerca(nome,cor,alerta,monitorar,id_cliente) values (?,?,?,?,?)";
			
			pstm = con.prepareStatement(q1, Statement.RETURN_GENERATED_KEYS);
			
			pstm.setString(1, cerca.getNome());
			pstm.setString(2, cerca.getCor());
			pstm.setInt(3, cerca.getAlerta());
			pstm.setInt(4, cerca.getMonitorar());
			pstm.setLong(5, cerca.getIdCliente());
			
			int affectedRows = pstm.executeUpdate();
	        if (affectedRows == 0) {
	            throw new SQLException("Creating Cerca failed, no rows affected.");
	        }
			
	        rs = pstm.getGeneratedKeys();
	        if (rs.next()) {
	        	cerca.setId(rs.getLong(1));
	        } else {
	            throw new SQLException("Creating Cerca failed, no generated key obtained.");
	        }
	        
	        //grava os pontos da cerca
	        for (Iterator iterator = cerca.getPos().iterator(); iterator.hasNext();) {
				Posicionamento p = (Posicionamento) iterator.next();
				String q2 = "insert into cerca_pontos(lat,lng,id_cerca) values (?,?,?)";
				
				pstm = con.prepareStatement(q2);
				pstm.setDouble(1, p.getLat());
				pstm.setDouble(2, p.getLon());
				pstm.setLong(3, cerca.getId());
				
				pstm.executeUpdate();
			}
	        
	        //seleciona os veiculos que serao monitorados
	        if(veiculos!=null){
	        	for (Iterator iterator = veiculos.iterator(); iterator.hasNext();) {
					Veiculo v = (Veiculo) iterator.next();
					String q3 = "insert into cerca_veiculo(id_veiculo,id_cerca,tipo) values (?,?,1)";
					
					pstm = con.prepareStatement(q3);
					pstm.setLong(1, v.getId());
					pstm.setLong(2, cerca.getId());
					
					pstm.executeUpdate();
				}
	        }
	        
	        //seleciona os emails que receberam alerta em caso de extravio
	        if(emails!=null){
	        	for (Iterator iterator = emails.iterator(); iterator.hasNext();) {
					Usuario u = (Usuario) iterator.next();
					String q3 = "insert into cerca_email(id_cerca,email) values (?,?)";
					
					pstm = con.prepareStatement(q3);
					pstm.setLong(1, cerca.getId());
					pstm.setString(2, u.getEmail());
					pstm.executeUpdate();
				}
	        }
	        
	        //seleciona os celulares que receberam sms em caso de extravio
	        if(celulares!=null){
	        	for (Iterator iterator = celulares.iterator(); iterator.hasNext();) {
					Usuario u = (Usuario) iterator.next();
					String q3 = "insert into cerca_sms(id_cerca,numero) values (?,?)";
					
					pstm = con.prepareStatement(q3);
					pstm.setLong(1, cerca.getId());
					pstm.setString(2, u.getCelular());
					pstm.executeUpdate();
				}
	        }
	        
		}catch (Throwable e) {
			con.rollback();
			logger.error("Erro processado adicionar cerca", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
	}
	
	
	public void atualizar(Usuario u, Cerca cerca, List<Veiculo> veiculos, List<Usuario>celulares, List<Usuario> emails)throws Throwable{
		try{
			conectar();
			
			String q1 = "update cerca set nome=?,cor=?,alerta=?,monitorar=? where id=? and id_cliente=?";
			
			pstm = con.prepareStatement(q1);
			
			pstm.setString(1, cerca.getNome());
			pstm.setString(2, cerca.getCor());
			pstm.setInt(3, cerca.getAlerta());
			pstm.setInt(4, cerca.getMonitorar());
			
			pstm.setLong(5, cerca.getId());
			pstm.setLong(6, cerca.getIdCliente());
			
			
			int affectedRows = pstm.executeUpdate();
			if (affectedRows == 0) {
	            throw new SQLException("atualizar Cerca failed, no rows affected.");
	        }
			
			//remove os pontos antigos
	        String q2 = "delete from cerca_pontos where id_cerca="+cerca.getId();
	        pstm = con.prepareStatement(q2);
	        pstm.executeUpdate();
			
	        //cria novamente com os novos pontos
	        for (Iterator iterator = cerca.getPos().iterator(); iterator.hasNext();) {
				Posicionamento p = (Posicionamento) iterator.next();
				String q3 = "insert into cerca_pontos(lat,lng,id_cerca) values (?,?,?)";
				
				pstm = con.prepareStatement(q3);
				pstm.setDouble(1, p.getLat());
				pstm.setDouble(2, p.getLon());
				pstm.setLong(3, cerca.getId());
				
				pstm.executeUpdate();
			}
	        
	        //remove os veiculos antigos
	        String q3 = "delete from cerca_veiculo where cerca_veiculo.id_cerca="+cerca.getId();
	        //usuario comum, remover apenas os veiculos que pode visualizar
			if(u.getIdPerfil()==2){
				q3+=" and cerca_veiculo.id_veiculo in (select uv.id_veiculo from usuario_veiculo uv where uv.id_usuario="+u.getId()+")";
			}
			
	        
	        pstm = con.prepareStatement(q3);
	        pstm.executeUpdate();
	        
	        
	        //adiciona os novos veiculos
	        if(veiculos!=null){
	        	for (Iterator iterator = veiculos.iterator(); iterator.hasNext();) {
					Veiculo v = (Veiculo) iterator.next();
					String q4 = "insert into cerca_veiculo(id_veiculo,id_cerca,tipo) values (?,?,1)";
					
					pstm = con.prepareStatement(q4);
					pstm.setLong(1, v.getId());
					pstm.setLong(2, cerca.getId());
					
					pstm.executeUpdate();
				}
	        }
	        
	        //remove os emails antigos
	        q3 = "delete from cerca_email where id_cerca="+cerca.getId();
	        pstm = con.prepareStatement(q3);
	        pstm.executeUpdate();
	        
	        //seleciona os emails que receberam alerta em caso de extravio
	        if(emails!=null){
	        	for (Iterator iterator = emails.iterator(); iterator.hasNext();) {
					Usuario usu = (Usuario) iterator.next();
					q3 = "insert into cerca_email(id_cerca,email) values (?,?)";
					
					pstm = con.prepareStatement(q3);
					pstm.setLong(1, cerca.getId());
					pstm.setString(2, usu.getEmail());
					pstm.executeUpdate();
				}
	        }
	        
	        //remove os celulares antigos
	        q3 = "delete from cerca_sms where id_cerca="+cerca.getId();
	        pstm = con.prepareStatement(q3);
	        pstm.executeUpdate();
	        
	        //seleciona os celulares que receberam sms em caso de extravio
	        if(celulares!=null){
	        	for (Iterator iterator = celulares.iterator(); iterator.hasNext();) {
					Usuario usu = (Usuario) iterator.next();
					q3 = "insert into cerca_sms(id_cerca,numero) values (?,?)";
					
					pstm = con.prepareStatement(q3);
					pstm.setLong(1, cerca.getId());
					pstm.setString(2, usu.getCelular());
					pstm.executeUpdate();
				}
	        }
	        
		}catch (Throwable e) {
			con.rollback();
			logger.error("Erro processado adicionar cerca", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
	}
	
	public void excluir(long idCliente, long idCerca) throws Throwable{
		try{
			conectar();
			
			String query =  "delete from cerca where id_cliente="+idCliente+" and id="+idCerca;
			
			int affectedRows = con.createStatement().executeUpdate(query);
			if (affectedRows == 0) {
	            throw new SQLException("excluir Cerca failed, no rows affected.");
	        }
		}catch (Throwable e) {
			logger.error("Erro processado excluir cerca", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
		
	}
	
	public List<Cerca> buscarCercas(long idCliente) throws Throwable{
		List<Cerca> cs = new ArrayList<Cerca>();
		try{
			conectar();
			
			con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			
			String query =  "select id,nome from cerca where id_cliente="+idCliente;
			
			rs = con.createStatement().executeQuery(query);
			
			while(rs.next()){
				Cerca c = new Cerca();
				c.setId(rs.getLong("id"));
				c.setNome(rs.getString("nome"));
				cs.add(c);
			}
			
		}catch (Throwable e) {
			logger.error("Erro processado buscar cercas", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
		
		return cs;
		
	}
	
	
	public List<String> buscarEmailsCerca(long idCerca) throws Throwable{
		List<String> vs = new ArrayList<String>();
		try{
			conectar();
			
			con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			
			String query = "select email from cerca_email where id_cerca="+idCerca;
			
			rs = con.createStatement().executeQuery(query);
			
			while(rs.next()){
				vs.add(rs.getString("email"));
			}
			
		}catch (Throwable e) {
			logger.error("Erro processado buscarEmailsCerca", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
		
		return vs;
	}
	
	public List<String> buscarCelularesCerca(long idCerca) throws Throwable{
		List<String> vs = new ArrayList<String>();
		try{
			conectar();
			
			con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			
			String query = "select numero from cerca_sms where id_cerca="+idCerca;
			
			rs = con.createStatement().executeQuery(query);
			
			while(rs.next()){
				vs.add(rs.getString("numero"));
			}
			
		}catch (Throwable e) {
			logger.error("Erro processado buscarEmailsCerca", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
		
		return vs;
	}
	
}
