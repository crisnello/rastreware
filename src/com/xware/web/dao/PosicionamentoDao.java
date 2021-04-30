package com.xware.web.dao;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.xware.web.entitie.Posicionamento;
import com.xware.web.entitie.Usuario;
import com.xware.web.entitie.geo.GeoCoordinate;
import com.xware.web.entitie.geo.GeoUtils;
import com.xware.web.exceptions.DaoException;
import com.xware.web.util.Utils;

public class PosicionamentoDao extends BaseDao{

	public List<Posicionamento> posicionamentoAtual(Usuario u) throws Throwable{
		List<Posicionamento> ps = new ArrayList<Posicionamento>();
		try{
			conectar();
			
			con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			
			String query = "SELECT pa.lat,pa.lon,pa.id_veiculo,	" +
					"pa.data_capturado, " +
					"pa.velocidade,v.modelo,v.placa,v.icone,v.id_status,v.possui_bloqueio " +
					"FROM posicionamento_atual pa " +
					"INNER JOIN veiculo v where pa.id_veiculo=v.id and v.id_cliente="+u.getIdCliente();
			
			//usuario comum, filtrar veiculos que pode visualizar
			if(u.getIdPerfil()==2){
				query+=" and v.id in (select uv.id_veiculo from usuario_veiculo uv where uv.id_usuario="+u.getId()+")";
			}
			
			rs = con.createStatement().executeQuery(query);
			
			while(rs.next()){
				Posicionamento p = new Posicionamento();
				p.setLat(rs.getDouble("lat"));
				p.setLon(rs.getDouble("lon"));
				p.setIdVeiculo(rs.getLong("id_veiculo"));
				p.setDataCapturado(rs.getTimestamp("data_capturado"));
				p.setVelocidade(rs.getDouble("velocidade"));
				p.getVeiculo().setModelo(rs.getString("modelo"));
				p.getVeiculo().setPlaca(rs.getString("placa"));
				p.getVeiculo().setIcone(rs.getString("icone"));
				p.getVeiculo().setIdStatus(rs.getInt("id_status"));
				p.getVeiculo().setPossuiBloqueio(rs.getInt("possui_bloqueio"));
				p.getVeiculo().setId(p.getIdVeiculo());
				
				ps.add(p);
			}
			
		}catch (Throwable e) {
			logger.error("Erro processado buscar posicionamentoAtual", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
		
		return ps;
		
	}
	
	public List<Posicionamento> posicionamentoHistorico(Usuario u, long idVeiculo, Date dataDe, Date dataAte) throws Throwable{
		List<Posicionamento> ps = new ArrayList<Posicionamento>();
		try{
			conectar();
			
			con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			
			String query = "SELECT ph.lat,ph.lon,ph.id_veiculo,	ph.data_capturado,ph.tamanho_pacote, " +
					"ph.velocidade,v.modelo,v.placa,v.id_status,ph.angulo " +
					"FROM posicionamento_historico ph " +
					"INNER JOIN veiculo v WHERE " +
					"v.id=ph.id_veiculo AND " +
					"ph.data_capturado BETWEEN ? AND ?" +
					"AND v.id=? AND v.id_cliente = ? ";
			
			//usuario comum, filtrar veiculos que pode visualizar
			if(u.getIdPerfil()==2){
				query+=" and v.id in (select uv.id_veiculo from usuario_veiculo uv where uv.id_usuario="+u.getId()+")";
			}
			
			query+=" ORDER BY ph.data_capturado";
			
			pstm = con.prepareStatement(query);
			
			pstm.setTimestamp(1, new Timestamp(dataDe.getTime()));
			pstm.setTimestamp(2, new Timestamp(dataAte.getTime()));
			pstm.setLong(3, idVeiculo);
			pstm.setLong(4, u.getIdCliente());
			
			rs = pstm.executeQuery();
			
			while(rs.next()){
				Posicionamento p = new Posicionamento();
				p.setLat(rs.getDouble("lat"));
				p.setLon(rs.getDouble("lon"));
				p.setAngulo(rs.getDouble("angulo"));
				p.setIdVeiculo(rs.getLong("id_veiculo"));
				p.setDataCapturado(rs.getTimestamp("data_capturado"));
				p.setTamanhoPacote(rs.getLong("tamanho_pacote"));
				p.setVelocidade(rs.getDouble("velocidade"));
				p.getVeiculo().setModelo(rs.getString("modelo"));
				p.getVeiculo().setPlaca(rs.getString("placa"));
				p.getVeiculo().setIdStatus(rs.getInt("id_status"));
				p.getVeiculo().setId(p.getIdVeiculo());
				
				ps.add(p);
			}
			
		}catch (Throwable e) {
			logger.error("Erro processado buscar posicionamentoHistorico", e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
		
		
		//return ps;
		return ps;
		
	}
	
	
	
	public List<Posicionamento> filtrarPosicionamentos(List<Posicionamento> pos){
		List<Posicionamento> retorno = new ArrayList<Posicionamento>();
		
		if(pos.size()>3){
			
			double totalDistancia = 0;
			long totalTempo = 0;
			
			//corrigir posicionamentos
			for (int i = 0; i < pos.size()-1; i++) {
				Posicionamento ponto1 = pos.get(i);
				Posicionamento ponto2 = pos.get(i+1);
				
				GeoCoordinate coordenada1 = new GeoCoordinate(ponto1.getLat(),ponto1.getLon());
				GeoCoordinate coordenada2 = new GeoCoordinate(ponto2.getLat(),ponto2.getLon());
				
				double distancia = GeoUtils.geoDistanceInKm(coordenada1, coordenada2);
				
				if(Double.isNaN(distancia)){
					distancia = 0.0;
				}
				
				long tempo = Math.abs(ponto1.getDataCapturado().getTime() - ponto2.getDataCapturado().getTime());
				
				totalDistancia+=distancia;
				totalTempo+=tempo;
				
				if(totalDistancia<0.03){
					ponto2.setDataCapturado(ponto1.getDataCapturado());
				}else{
					if(totalTempo> 1000*60*3){
						ponto1.setParado(Boolean.TRUE);
					}
					ponto1.setDistancia(totalDistancia);
					ponto1.setDiferencaTempo(ponto2.getDataCapturado().getTime() - ponto1.getDataCapturado().getTime());
					totalDistancia = 0;
					totalTempo = 0;
					retorno.add(ponto1);
				}
			}
			
			Posicionamento ultimo = retorno.get(retorno.size()-1);
			ultimo.setDataCapturado(pos.get(pos.size()-1).getDataCapturado());
			
			
			//corrigir pontos parados
			if(retorno.size()>=2){
				List<Posicionamento> retornoFixed = new ArrayList<Posicionamento>();
				
				for (int i = 0; i < retorno.size()-1; i++) {
					Posicionamento ponto1 = retorno.get(i);
					Posicionamento ponto2 = retorno.get(i+1);
					
					GeoCoordinate coordenada1 = new GeoCoordinate(ponto1.getLat(),ponto1.getLon());
					GeoCoordinate coordenada2 = new GeoCoordinate(ponto2.getLat(),ponto2.getLon());
					
					double distancia = GeoUtils.geoDistanceInKm(coordenada1, coordenada2);
					
					if(Double.isNaN(distancia)){
						distancia = 0.0;
					}
					
					if(ponto1.isParado() && ponto2.isParado() && distancia < 1){
						ponto2.setLat(ponto1.getLat());
						ponto2.setLon(ponto1.getLon());
						ponto2.setDataCapturado(ponto1.getDataCapturado());
					}else{
						retornoFixed.add(ponto1);
					}
					
				}
				
				retornoFixed.add(retorno.get(retorno.size()-1));
				
				retorno = retornoFixed;
			}
		}		
		
		return retorno;
	}
}
