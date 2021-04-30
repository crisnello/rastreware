/*************************************************************************
 ***************************** controle das cercas ***********************
 *************************************************************************/

var polygons = [];

/**
 * busca as cercas do veiculo
 * @param idVeiculo
 */
function carregar_cercas_veiculo(idVeiculo){
	try{
		limparPolygons();
		$.post(baseUrl+"MonitoracaoCercasVeiculoServlet?idVeiculo="+idVeiculo,function(data) {
			if(data && data!=0){
				var cercas_json = data;
				for(var i = 0;i<cercas_json.length; i++){
					var cerca_json  = cercas_json[i];
					var cor = cerca_json.cor;
					var pontos_cerca = cerca_json.pontos;
					var latLngs = [];
					for(var j = 0;j<pontos_cerca.length; j++){
						var pontos_lat_lng = pontos_cerca[j];
						var myLatLng = new google.maps.LatLng(pontos_lat_lng.lat, pontos_lat_lng.lng);
						latLngs.push(myLatLng);
					}
					criarPolygon(cor,latLngs);
				}
			}
		});
	}catch (e) {
	}
}

/**
 * remove as cercas do mapa
 */
function limparPolygons(){
	if(polygons){
		for(var i = 0;i<polygons.length;i++){
			polygons[i].setMap(null);
		}
		polygons = [];
	}
}

/**
 * cria o desenho do poligono
 */
function criarPolygon(cor, pontos){
	var polyOptions = {
			clickable:false,
			strokeColor:cor ,
			strokeOpacity: 1,
			strokeWeight: 0.1,
			fillColor: cor,
			fillOpacity: 0.35
	};
	var poly = new google.maps.Polygon(polyOptions);
	poly.setMap(map);
	var path = poly.getPath();
	for(var i = 0;i<pontos.length;i++){
		path.push(pontos[i]);
	}
	
	polygons.push(poly);
}