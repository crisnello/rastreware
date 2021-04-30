var dir_icons = baseUrl+"assets/images/numeric/";
var dir_arrows = baseUrl+"assets/images/arrow/";
//variavel de backup para fazer o botao voltar
var map_markers_bkp = [];


$(document).ready(function() {
	selecionarAba('aba_cercas');
 	td_hover($("#hor-minimalist-a td"));
 	
 	limpar_mapa();
 	initialize_map();
 	init_botao_buscar_endereco();
 	// adiciona um listener para click event no mapa
	google.maps.event.addListener(map, 'click', addLatLng);
	google.maps.event.addListener(map, 'mousemove', mousemove);
	google.maps.event.addListener(map, 'mouseout', mouseout);
	restaurarPontos();
	
	
	$('#colorSelector_button').ColorPicker({
		color: '#0000ff',
		onShow : function(colpkr) {
			$(colpkr).fadeIn(500);
			return false;
		},
		onHide : function(colpkr) {
			$(colpkr).fadeOut(500);
			return false;
		},
		onChange : function(hsb, hex, rgb) {
			$('#colorSelector div').css('backgroundColor', '#' + hex);
			$("#cor").val('#' + hex);
			//atualiza o poligono com a nova cor
			criarPolygon();
		}
	});	
 });


/**
 * transforma o array de latitudes em uma string e grava no campo hidden
 */
function persistirPontos(){
	var pontos = "";
	for ( var int = 0; int < map_markers.length; int++) {
		pontos+=map_markers[int].getPosition().lat()+","+map_markers[int].getPosition().lng();
		if(int<map_markers.length-1){
			pontos+=";";
		}
	}
	$("#pontos_cerca").val(pontos);
}


/**
 * transforma a string de latitudes em um array javascript e joga no mapa
 */
function restaurarPontos(){
	var pontos_string = $("#pontos_cerca").val();
	if(pontos_string){
		map_markers_bkp = [];
		map_markers = [];
		var pontos_array = pontos_string.split(';');
		for(var i=0;i<pontos_array.length;i++){
			var latLon_array = pontos_array[i].split(',');
			var latLon = new google.maps.LatLng(latLon_array[0], latLon_array[1]);
			//cria e adiciona adiciona o marker, atualiza o poligono tbm
		    var marker = criar_marker(latLon,map);
		    map_markers.push(marker);
		}
		criarPolygon();
		setTimeout("centerMap()", 500);
	}
}

function centerMap(){
	if(map_markers.length>0){
		map.setCenter(map_markers[map_markers.length-1].getPosition());
	}
}



/**-------------------------------------------------------------------
 * 				PARTE QUE CONTROLA OS EVENTOS DO MAPA
   -------------------------------------------------------------------*/

/**
 * cria o desenho do poligono
 */
function criarPolygon(){
	if(poly){
		poly.setMap(null);
	}
	
	//pega a cor setada pelo colorpicker para criar o poligono
	var cor = $("#cor").val();
	var polyOptions = {
			clickable:false,
			strokeColor:cor ,
			strokeOpacity: 0.8,
			strokeWeight: 3,
			fillColor: cor,
			fillOpacity: 0.35
	};
	poly = new google.maps.Polygon(polyOptions);
	poly.setMap(map);
	var path = poly.getPath();
	for(var i = 0;i<map_markers.length;i++){
		path.push(map_markers[i].getPosition());
	}
	
}

/**
 * limpa tudo do mapa e variaveis de backup
 */
function limpar_mapa(){
	map_markers_bkp = [];
	clearOverlays();
	criarPolygon();
}

/**
 * desfaz a ultima alteracao do mapa
 * pega o backup do array de markers no array de backups
 * o array de backups é um array de arrays de markers
 */
function desfazer_ultimo(){
	if(map_markers_bkp.length>0){
		clearOverlays();
		var makers_bkp = map_markers_bkp.pop();
		while(makers_bkp.length>0){
			map_markers.push(criar_marker(makers_bkp.pop().getPosition(),map));
		}
		criarPolygon();
	}
}

/**
 * chamado qnd clica no mapa
 * @param event
 */
function addLatLng(event) {
	//faz um backup do estado atual do poligono
    var map_markers_temp = [];
    for(var i=0;i<map_markers.length;i++){
    	map_markers_temp.push(map_markers[i]);
    }
    map_markers_bkp.push(map_markers_temp);
    
    //cria e adiciona adiciona o marker, atualiza o poligono tbm
    var marker = criar_marker(event.latLng,map);
    map_markers.push(marker);
    criarPolygon();
}

function mouseout(event) {
	
}

var maker_move = null;
function mousemove(event) {
	/*
	if(map_markers.length > 1){
		if(maker_move==null){
			var polyOptions = {
			          strokeColor: '#000000',
			          strokeOpacity: 1.0,
			          strokeWeight: 3
			        };
			
			maker_move = new google.maps.Polyline(polyOptions);
			maker_move.setMap(map);	
		}
		var path_maker_move = maker_move.getPath();
		
		path_maker_move.push(map_markers[0].getPosition());
		path_maker_move.push(event.latLng);
		path_maker_move.push(map_markers[map_markers.length-1].getPosition());
		
	}
	*/
}

/**
 * quando inicia o arrasto do marker faz um backup do poligono
 * @param event
 */
function dragstart_marker(event){
	var map_markers_temp = [];
	//seta o map null nesse backup
    for(var i=0;i<map_markers.length;i++){
    	map_markers_temp.push(criar_marker(map_markers[i].getPosition(),null));
    }
    map_markers_bkp.push(map_markers_temp);
}

/**
 * cria o marker no mapa
 * @param latLng
 * @param mapa
 * @returns {google.maps.Marker}
 */
function criar_marker(latLng,mapa){
	// Add a new marker at the new plotted point on the polyline.
	var icon = baseUrl+'assets/images/bullet-blue-icon.png';
	
	var image = new google.maps.MarkerImage(icon, new google.maps.Size(24, 24), new google.maps.Point(0,0), new google.maps.Point(12, 12));
	
    var marker = new google.maps.Marker({
      position: latLng,
      map: mapa,
      draggable:true,
      icon: image
    });
    
    //var atual = map_markers.length+0;
    google.maps.event.addListener(marker, 'dblclick', function(){
    	processar_remover(marker);
    });
    
    google.maps.event.addListener(marker, 'dragstart', dragstart_marker);
    google.maps.event.addListener(marker, 'dragend', criarPolygon);
    return marker;
}

/**
 * ao remover é gerado um novo array de markers, entao tem que redesenha-los
 * @param array
 */
function processar_remover(marker){
	//faz um backup do estado atual do poligono
    var map_markers_temp = [];
    for(var i=0;i<map_markers.length;i++){
    	map_markers_temp.push(criar_marker(map_markers[i].getPosition(),null));
    }
    
    map_markers_bkp.push(map_markers_temp);
    
    var temp = [];
    while(map_markers.length>0)
    {
    	var m_pop = map_markers.pop(); 
    	if(m_pop.getPosition()!=marker.getPosition()){
    		temp.push(m_pop);
    	}else{
    		m_pop.setMap(null);
    	}
    }
    
    while(temp.length>0){
    	var t_pop = temp.pop();
    	map_markers.push(t_pop);

    }
    criarPolygon();
}
