document.addEventListener("DOMContentLoaded", function() {
    var points = {
        "4": {
            "lat": "56.152700",
            "lon": "47.252597"
        },
        "1": {
            "lat": "56.155293",
            "lon": "47.189367"
        },
        "8": {
            "lat": "56.151188",
            "lon": "47.252071"
        },
        "9": {
            "lat": "56.150847",
            "lon": "47.253764"
        },
        "3": {
            "lat": "56.152184",
            "lon": "47.244305"
        },
        "2": {
            "lat": "56.153319",
            "lon": "47.238772"
        },
        "7": {
            "lat": "56.148216",
            "lon": "47.243431"
        },
        "5": {
            "lat": "56.146783",
            "lon": "47.224635"
        },
        "6": {
            "lat": "56.146436",
            "lon": "47.232503"
        },
        "10": {
            "lat": "56.145682",
            "lon": "47.238563"
        },
        "15": {
            "lat": "56.142833",
            "lon": "47.239221"
        },
        "13": {
            "lat": "56.145700",
            "lon": "47.254699"
        },
        "14": {
            "lat": "56.149801",
            "lon": "47.261892"
        },
        "21": {
            "lat": "56.141377",
            "lon": "47.252464"
        },
        "12": {
            "lat": "56.146138",
            "lon": "47.251038"
        },
        "17": {
            "lat": "56.143749",
            "lon": "47.251205"
        },
        "18": {
            "lat": "56.141033",
            "lon": "47.240444"
        },
        "11": {
            "lat": "56.146415",
            "lon": "47.248161"
        },
        "16": {
            "lat": "56.144001",
            "lon": "47.246922"
        },
        "20": {
            "lat": "56.140423",
            "lon": "47.249534"
        },
        "19": {
            "lat": "56.140240",
            "lon": "47.243733"
        },
        "23": {
            "lat": "56.134954",
            "lon": "47.241326"
        },
        "22": {
            "lat": "56.139500",
            "lon": "47.247238"
        },
        "24": {
            "lat": "56.134191",
            "lon": "47.246445"
        },
        "25": {
            "lat": "56.131620",
            "lon": "47.246103"
        },
        "26": {
            "lat": "56.141872",
            "lon": "47.247746"
        }
    };

    // Инициализация карт
    var startMap = L.map('start-map').setView([56.1527, 47.252597], 12);
    var endMap = L.map('end-map').setView([56.1527, 47.252597], 12);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(startMap);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(endMap);

    var startMarker, endMarker;

    // Иконки маркеров
    var blueIcon = new L.Icon({
        iconUrl: 'http://maps.google.com/mapfiles/ms/icons/blue-dot.png',
        iconSize: [32, 32],
        iconAnchor: [16, 32],
        popupAnchor: [0, -32]
    });

    var redIcon = new L.Icon({
        iconUrl: 'http://maps.google.com/mapfiles/ms/icons/red-dot.png',
        iconSize: [32, 32],
        iconAnchor: [16, 32],
        popupAnchor: [0, -32]
    });

    // Функция для установки маркеров на карту
    function setMarkers(map, inputId) {
        var selectedMarker = null;

        for (var key in points) {
            var point = points[key];
            var marker = L.marker([point.lat, point.lon], { icon: blueIcon }).addTo(map);

            marker.on('click', (function(key) {
                return function(e) {
                    if (selectedMarker) {
                        selectedMarker.setIcon(blueIcon);
                    }
                    e.target.setIcon(redIcon);
                    selectedMarker = e.target;
                    document.getElementById(inputId).value = key;
                }
            })(key));
        }
    }

    // Установка маркеров на карты
    setMarkers(startMap, 'start');
    setMarkers(endMap, 'end');
});