let map;
let trains = {}; 

function initMap() {
	map = new google.maps.Map(document.getElementById("map"), {
		center: { lat: -13.244968, lng: -52.994556 },
		zoom: 5
	});
}

$(document).ready(function () {


    var socket;
    window.$charts = {};
    Chart.defaults.scale.display = false;
    Chart.defaults.global.legend.display = false;
    
    $('#connect_form').submit(function () {
        var host = $("#url").val();
        socket = new WebSocket(host);
        $('#connect').fadeOut({ duration:'fast' });
        $('#disconnect').fadeIn();
        $('#send_form_input').removeAttr('disabled');

        // Add a connect listener
        socket.onopen = function () {
            $('#msg').append('<p class="event">Socket News Status: ' + socket.readyState + ' (open)</p>');
        }

    	socket.onmessage = function handleMessage(msg) {
    		let json = JSON.parse(msg.data);
    		let route;
    		if(json.type === "gps") {
    			if(trains[json.id]) {
    				route = trains[json.id]['route'];
    			} else {
    				route = newRoute(map);
    				trains[json.id] = {};
    			}
    			let positions = json.position.split(',');
    			route.getPath().push(
					new google.maps.LatLng( parseFloat(positions[1]), parseFloat(positions[0]) ) 
				);
    			trains[json.id]['route'] = route;
    		}
    	};
        
//        socket.onmessage = function (msg) {
//
//            var json = JSON.parse(msg.data);
//            // extract fields
//            var id = json.id;
//            var average = json.average;
//            var lastMeasures = json.lastMeasures;
//            var type = json.type;
//            
//            let existsPanel = $("#patient-" + id).length
//            if(!existsPanel) {
//            	let $monitor = $('<div id="patient-' + id + '" class="monitor-card">');
//            	$monitor.append('<canvas id="monitor-chart-' + id + '"></canvas>');
//            	$monitor.append('<p class="monitor-bpm">0 bpm</p>');
//            	$monitor.append('<p class="monitor-temp">0 &deg;C</p>');
//            	$monitor.appendTo("#main");
//            	newChart(id);
//            } else {
//            	let typeInfo = getInfoFor(type, json);
//            	$("#patient-" + id).find(typeInfo.textClass).text(typeInfo.averageMeasure);
//            	var lastMeasure = lastMeasures.pop();
//            	updateChart(id, typeInfo.lastMeasure);
//            }
//        }

        socket.onclose = function () {
            $('#msg').append('<p class="event">Socket News Status: ' + socket.readyState + ' (Closed)</p>');
        }


        return false;
    });

    $('#disconnect_form').submit(function () {

        socket.close();

        $('#msg').append('<p class="event">Socket News Status: ' + socket.readyState + ' (Closed)</p>');

        $('#disconnect').fadeOut({ duration:'fast' });
        $('#connect').fadeIn();
        $('#send_form_input').addAttr('disabled');

        return false;
    });
});

function newRoute(map) {
      let route = new google.maps.Polyline({
	    strokeColor: "#F00",
	    strokeOpacity: 1.0,
	    strokeWeight: 2
	  });
	  route.setMap(map);
	  return route;
}

function newRoutePosition(trains, id) {
	
}

function getInfoFor(type, measure) {
	let lastMeasure = measure.lastMeasures.pop();
	if(type === 'heart') {
		return {"textClass": ".monitor-bpm", "lastMeasure": lastMeasure.bpm, "averageMeasure": measure.average.bpm + " bpm"};
	}
	if(type === 'temperature') {
		return {"textClass": ".monitor-temp", "lastMeasure": lastMeasure.temp.toFixed(2), "averageMeasure": measure.average.temp.toFixed(2) + " C"};
	}
	if(type === 'pressure') {
		
	}
}

function newChart(id) {
	let ctx = document.getElementById('monitor-chart-' + id).getContext('2d');
	let chart = new Chart(ctx, {
        // The type of chart we want to create
        type: 'line',

        // The data for our dataset
        data: {
            labels: ["1", "2", "3", "4", "5"],
            datasets: [{
                fill: false,
                backgroundColor: 'rgba(0, 0, 0, 0.1)',
                borderColor: 'rgb(108, 255, 72)',
                lineTension: 0.1,
                data: [0, 0, 0, 0, 0]
            }]
        },

        // Configuration options go here
        options: {}
	});
	window.$charts[id] = chart;
}

function updateChart(id, value) {
	window.$charts[id].data.datasets[0].data.shift();
	window.$charts[id].data.datasets[0].data.push(value);
	window.$charts[id].update();
}
