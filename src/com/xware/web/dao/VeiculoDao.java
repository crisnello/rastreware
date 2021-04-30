package com.xware.web.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.xware.web.entitie.Usuario;
import com.xware.web.entitie.Veiculo;
import com.xware.web.exceptions.DaoException;

public class VeiculoDao extends BaseDao{

	public long validarBloqueio(long idVeiculo, String senhaBloqueio, long idCliente) throws Throwable{
		long idRastreador = 0;
		try{
			conectar();
			
			con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			
			String query = "select r.id from rastreador r " +
					"inner join veiculo v on v.id_rastreador = r.id " +
					"where v.id="+idVeiculo+" and r.senha='"+senhaBloqueio+"' and v.id_cliente="+idCliente;
			
			
			logger.debug(query);
			
			rs = con.createStatement().executeQuery(query);
			
			if(rs.next()){
				idRastreador = rs.getLong("id");
			}
			
		}catch (Throwable e) {
			logger.error("Erro processado bloqueio", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
		
		return idRastreador;
	}
	
	
	public void adicionar(Veiculo v) throws Throwable{
		try{
			conectar();
			
			String query = "insert into veiculo(modelo,placa,data_cadastro,km,icone,possui_bloqueio,data_alteracao,id_status,id_rastreador,id_cliente) values(? ,? ,? ,? ,? ,? ,? , ?, ?, ?)" ;
			
			pstm = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			pstm.setString(1, v.getModelo());
			pstm.setString(2, v.getPlaca());
			pstm.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			pstm.setDouble(4, v.getKm());
			pstm.setString(5, v.getIcone());
			pstm.setInt(6, v.getPossuiBloqueio());
			pstm.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			pstm.setInt(8, 1);
			pstm.setLong(9, v.getIdRastreador());
			pstm.setLong(10, v.getIdCliente());
			
			int affectedRows = pstm.executeUpdate();
	        if (affectedRows == 0) {
	            throw new SQLException("Creating Veiculo failed, no rows affected.");
	        }
	        
	        rs = pstm.getGeneratedKeys();
	        if (rs.next()) {
	            v.setId(rs.getLong(1));
	        } else {
	            throw new SQLException("Creating Veiculo failed, no generated key obtained.");
	        }
	        
	        	        
		}catch (Throwable e) {
			con.rollback();
			logger.error("Erro processado adicionar", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
	}
	
	
	public void atualizar(Veiculo v) throws Throwable{
		try{
			conectar();
			
			//atualizar veiculo
			String q1 = "update veiculo set modelo=?,placa=?,km=?,icone=?,possui_bloqueio=?,data_alteracao=?,id_rastreador=? where id=?";
			
			pstm = con.prepareStatement(q1);
			pstm.setString(1, v.getModelo());
			pstm.setString(2, v.getPlaca());
			pstm.setDouble(3, v.getKm());
			pstm.setString(4, v.getIcone());
			pstm.setInt(5, v.getPossuiBloqueio());
			pstm.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			pstm.setLong(7, v.getIdRastreador());
			
			pstm.setLong(8, v.getId());
			
			int affectedRows = pstm.executeUpdate();
	        if (affectedRows == 0) {
	            throw new SQLException("Updating Veiculo failed, no rows affected.");
	        }
	        
		}catch (Throwable e) {
			con.rollback();
			logger.error("Erro processado adicionar", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
	}
	
	public void bloquear(long idVeiculo, long idRastreador, int idStatus, long idUsuario) throws Throwable{
		try{
			conectar();
			
			String query = "insert into rastreador_bloquear(data_cadastro,id_rastreador,id_usuario,id_veiculo,id_status) values(? , ? , ? , ? , ?)" ;
			
			pstm = con.prepareStatement(query);
			pstm.setDate(1, new Date(System.currentTimeMillis()));
			pstm.setLong(2, idRastreador);
			pstm.setLong(3, idRastreador);
			pstm.setLong(4, idVeiculo);
			pstm.setInt(5, idStatus);
			
			pstm.executeUpdate();
			
			String q2 = "update veiculo set id_status=?, data_alteracao=? where id=?";
			pstm = con.prepareStatement(q2);
			pstm.setInt(1, idStatus);
			pstm.setDate(2, new Date(System.currentTimeMillis()));
			pstm.setLong(3, idVeiculo);
			
			pstm.executeUpdate();
			
		}catch (Throwable e) {
			con.rollback();
			logger.error("Erro processado bloqueio", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
	}
	
	
	public void excluir(long idVeiculo) throws Throwable{
		try{
			conectar();
			
			String q1 = "delete from veiculo where id = ?" ;
			
			pstm = con.prepareStatement(q1);
			pstm.setLong(1, idVeiculo);
			
			pstm.executeUpdate();
			
		}catch (Throwable e) {
			con.rollback();
			logger.error("Erro processado excluir", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
	}
	
	/**
	 * Funcao para buscar os veiculos que estao associados a determinada cerca
	 * @param idCliente
	 * @param idCerca
	 * @return
	 * @throws Throwable
	 */
	public List<Veiculo> buscarVeiculosCerca(Usuario u, long idCerca) throws Throwable{
		List<Veiculo> vs = new ArrayList<Veiculo>();
		try{
			conectar();
			
			con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			
			String query = "select v.id,v.icone,v.modelo,v.placa,v.data_alteracao, v.id_cliente,v.id_rastreador," +
					"v.id_status,v.km, v.possui_bloqueio,v.data_cadastro " +
					"from veiculo v inner join cerca_veiculo cv on cv.id_veiculo = v.id and cv.id_cerca="+idCerca+" and v.id_cliente="+u.getIdCliente();
			
			//usuario comum, filtrar veiculos que pode visualizar
			if(u.getIdPerfil()==2){
				query+=" and v.id in (select uv.id_veiculo from usuario_veiculo uv where uv.id_usuario="+u.getId()+")";
			}
			
			rs = con.createStatement().executeQuery(query);
			
			while(rs.next()){
				Veiculo v = new Veiculo();
				v.setId(rs.getLong("id"));
				v.setDataCadastro(rs.getDate("data_cadastro"));
				try{
					v.setDataAlteracao(rs.getDate("data_alteracao"));
				}catch(SQLException esql){
					v.setDataAlteracao(null);
				}
				v.setIcone(rs.getString("icone"));
				v.setIdCliente(rs.getLong("id_cliente"));
				v.setIdRastreador(rs.getLong("id_rastreador"));
				v.setIdStatus(rs.getInt("id_status"));
				v.setKm(rs.getDouble("km"));
				v.setModelo(rs.getString("modelo"));
				v.setPlaca(rs.getString("placa"));
				v.setPossuiBloqueio(rs.getInt("possui_bloqueio"));
				vs.add(v);
			}
			
		}catch (Throwable e) {
			logger.error("Erro processado buscar veiculos", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
		
		return vs;
	}
	
	public List<Veiculo> buscarVeiculos(Usuario u) throws Throwable{
		List<Veiculo> vs = new ArrayList<Veiculo>();
		try{
			conectar();
			
			con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			
			String query = "select v.id,v.icone,v.modelo,v.placa,v.data_alteracao, v.id_cliente,v.id_rastreador," +
					"v.id_status,v.km, v.possui_bloqueio,v.data_cadastro " +
					"from veiculo v " +
					"where v.id_cliente="+u.getIdCliente();
			
			//usuario comum, filtrar veiculos que pode visualizar
			if(u.getIdPerfil()==2){
				query+=" and v.id in (select uv.id_veiculo from usuario_veiculo uv where uv.id_usuario="+u.getId()+")";
			}
			
			query+=" group by v.id"; 
			
			rs = con.createStatement().executeQuery(query);
			
			while(rs.next()){
				Veiculo v = new Veiculo();
				v.setId(rs.getLong("id"));
				v.setDataCadastro(rs.getDate("data_cadastro"));
				try{
					v.setDataAlteracao(rs.getDate("data_alteracao"));
				}catch(SQLException esql){
					v.setDataAlteracao(null);
				}
				v.setIcone(rs.getString("icone"));
				v.setIdCliente(rs.getLong("id_cliente"));
				v.setIdRastreador(rs.getLong("id_rastreador"));
				v.setIdStatus(rs.getInt("id_status"));
				v.setKm(rs.getDouble("km"));
				v.setModelo(rs.getString("modelo"));
				v.setPlaca(rs.getString("placa"));
				v.setPossuiBloqueio(rs.getInt("possui_bloqueio"));
				vs.add(v);
			}
			
		}catch (Throwable e) {
			logger.error("Erro processado buscar veiculos", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
		
		return vs;
	}
	
	
	public Veiculo buscarVeiculo(long id,long idCliente) throws Throwable{
		Veiculo v = null;
		try{
			conectar();
			
			String query = "select v.id,v.icone,v.modelo,v.placa,v.data_alteracao, v.id_cliente,v.id_rastreador," +
					"v.id_status,v.km, v.possui_bloqueio,v.data_cadastro " +
					"from veiculo v " +
					"where v.id="+id+" and v.id_cliente="+idCliente;
			
			rs = con.createStatement().executeQuery(query);
			
			if(rs.next()){
				v = new Veiculo();
				v.setId(rs.getLong("id"));
				v.setDataCadastro(rs.getDate("data_cadastro"));
				try{
					v.setDataAlteracao(rs.getDate("data_alteracao"));
				}catch(SQLException esql){
					v.setDataAlteracao(null);
				}
				v.setIcone(rs.getString("icone"));
				v.setIdCliente(rs.getLong("id_cliente"));
				v.setIdRastreador(rs.getLong("id_rastreador"));
				v.setIdStatus(rs.getInt("id_status"));
				v.setKm(rs.getDouble("km"));
				v.setModelo(rs.getString("modelo"));
				v.setPlaca(rs.getString("placa"));
				v.setPossuiBloqueio(rs.getInt("possui_bloqueio"));
			}
			
		}catch (Throwable e) {
			logger.error("Erro processado buscar veiculos", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
		
		return v;
		
	}
}
