package com.xware.web.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.xware.web.entitie.Rastreador;
import com.xware.web.entitie.RastreadorTipo;
import com.xware.web.exceptions.DaoException;

public class RastreadorDao extends BaseDao {
	
	public List<Rastreador> buscar(long idCliente) throws Throwable{
		
		List<Rastreador> rastreadores = new ArrayList<Rastreador>();
		
		try{
			conectar();
			
			con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			
			String query = "Select * from rastreador where id_cliente = "+idCliente;
			
			rs = con.createStatement().executeQuery(query);
			
			while(rs.next()){
				Rastreador r = new Rastreador();
				r.setId(rs.getLong("id"));
				r.setCodigo(rs.getString("codigo"));
				r.setIdCliente(rs.getLong("id_cliente"));
				r.setIdTipo(rs.getInt("id_tipo"));
				r.setNome(rs.getString("nome"));
				r.setNumCel(rs.getString("num_cel"));
				r.setSenha(rs.getString("senha"));
				r.setTimeDiference(rs.getInt("time_diference"));
				rastreadores.add(r);
			}
			
		}catch(Throwable t){
			logger.error("Erro processado buscar rastreadores", t);
			throw new DaoException(t.getMessage(), t.getCause());
		}finally{
			desconectar();
		}
		
		return rastreadores;
		
	}
	
	public Rastreador buscarRastreador(long idRastreador) throws Throwable{
		Rastreador r = null;
		
		try{
			conectar();
			
			String query = "Select * from rastreador where id = "+idRastreador;
			
			rs = con.createStatement().executeQuery(query);
			
			if(rs.next()){
				r = new Rastreador();
				r.setId(rs.getLong("id"));
				r.setCodigo(rs.getString("codigo"));
				r.setIdCliente(rs.getLong("id_cliente"));
				r.setIdTipo(rs.getInt("id_tipo"));
				r.setNome(rs.getString("nome"));
				r.setNumCel(rs.getString("num_cel"));
				r.setSenha(rs.getString("senha"));
				r.setTimeDiference(rs.getInt("time_diference"));
			}
			
		}catch(Throwable t){
			logger.error("Erro processado buscar rastreador", t);
			throw new DaoException(t.getMessage(), t.getCause());
		}finally{
			desconectar();
		}
		
		return r;
	}
	
	public void adicionar(Rastreador r) throws Throwable {
		try{
			conectar();
			
			String q = "insert into rastreador(nome,codigo,senha,id_tipo,num_cel,id_cliente) values (?,?,?,?,?,?)";
			
			pstm = con.prepareStatement(q);
			
			pstm.setString(1, r.getNome());
			pstm.setString(2, r.getCodigo());
			pstm.setString(3, r.getSenha());
			pstm.setInt(4, r.getIdTipo());
			pstm.setString(5, r.getNumCel());
			pstm.setLong(6, r.getIdCliente());
			
			pstm.executeUpdate();
			
		}catch(Throwable t){
			logger.error("Erro processado adicionar rastreador", t);
			throw new DaoException(t.getMessage(), t.getCause());
		}finally{
			desconectar();
		}
	}
	
	public void atualizar(Rastreador r) throws Throwable {
		try{
			conectar();
			
			String q = "update rastreador set nome=?,codigo=?,senha=?,id_tipo=?,num_cel=? where id = ?";
			
			pstm = con.prepareStatement(q);
			
			pstm.setString(1, r.getNome());
			pstm.setString(2, r.getCodigo());
			pstm.setString(3, r.getSenha());
			pstm.setInt(4, r.getIdTipo());
			pstm.setString(5, r.getNumCel());
			
			pstm.setLong(6, r.getId());
			
			pstm.executeUpdate();
			
		}catch(Throwable t){
			logger.error("Erro processado atualizar rastreador", t);
			throw new DaoException(t.getMessage(), t.getCause());
		}finally{
			desconectar();
		}
	}
	
	public void excluir(long idRastreador) throws Throwable {
		try{
			conectar();
			
			String q = "delete from rastreador where id="+idRastreador;
			
			pstm = con.prepareStatement(q);
			
			pstm.executeUpdate();
			
		}catch(Throwable t){
			logger.error("Erro processado excluir rastreador", t);
			throw new DaoException(t.getMessage(), t.getCause());
		}finally{
			desconectar();
		}
	}
	
	public List<RastreadorTipo> tiposRastreador() throws Throwable{
	
		List<RastreadorTipo> rastreadores = new ArrayList<RastreadorTipo>();
		
		try{
			conectar();
			
			con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			
			String query = "Select * from rastreador_tipo";
			
			rs = con.createStatement().executeQuery(query);
			
			while(rs.next()){
				RastreadorTipo r = new RastreadorTipo();
				r.setId(rs.getInt("id"));
				r.setNome(rs.getString("nome"));
				rastreadores.add(r);
			}
			
		}catch(Throwable t){
			logger.error("Erro processado buscar tiposRastreador", t);
			throw new DaoException(t.getMessage(), t.getCause());
		}finally{
			desconectar();
		}
		
		return rastreadores;
		
	}

}
