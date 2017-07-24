/**
 * @ author mhf 
 * 2017.5.21
 */
L.Control.MapRoller = L.Control.extend({

	statics: {
		TITLE: '地图卷帘'
	},
	options: {
		position: 'topright',
		handler: {},
		icon: 'img/roller.png',
		start: false
	},

	toggle: function(evt) {

		if(this.options.start) {
			//关闭卷帘
			evt.target.style.background = "#ffffff";
			this.options.start = false;
			this._map.removeEventListener("mousemove");
			if(this.l_parent != undefined) {
				this.l_parent.style.clip="";//.clip = 'rect(99999px 99999px 999999px -99999px)'
				//this.l_parent.style.opacity=0;
			}
		} else {
			//开启卷帘
			evt.target.style.background = "#ffe99b";
			this.options.start = true;
			/*this._map.on("zoomend", function(e) {
				setDivide(parseInt(this.handle.style.left));
			}, this);
			this._map.on("moveend", function(e) {
				setDivide(parseInt(this.handle.style.left));
			}, this);
			this._map.on("drag", function(e) {
				setDivide(parseInt(this.handle.style.left));
			}, this);*/
			this._map.on("mousemove", function(e) {
				this.setDivide(e.containerPoint.x);
			}, this);
			//this.setDivide(300);
		}

	},

	setDivide: function(x) {
		this.l_parent = this.getLayer(this._map._layers)._container;
		this.handle = L.DomUtil.get("sollerhandle");
		var dragging = false;
		x = Math.max(0, Math.min(x, this._map.getSize()['x']));
		this.handle.style.left = (x) + 'px';
		var layerX = this._map.containerPointToLayerPoint(x, 0).x;
		this.l_parent.style.clip = 'rect(-99999px ' + layerX + 'px 999999px -99999px)';
	},
	getLayer: function(obj) {
		var last;
		for(var i in obj) {
			if(obj.hasOwnProperty(i) && typeof(i) !== 'function') {
				last = obj[i];
			}
		}
		return last;
	},
	onAdd: function(map) {
		var className = 'leaflet-control-roller';

		this._container = L.DomUtil.create('div', 'leaflet-bar1');

		var handle = L.DomUtil.create('div', className + '-sollerhandle',
			this._container);
		handle.id = "sollerhandle";
		var link = L.DomUtil.create('a', className + '-a',
			this._container);
		link.href = '#';
		link.title = L.Control.MapRoller.TITLE;
		var icon = L.DomUtil.create('img', className + '-icon', link);
		icon.src = this.options.icon;

		L.DomEvent.addListener(icon, 'click', L.DomEvent.stopPropagation)
			.addListener(icon, 'click', L.DomEvent.preventDefault)
			.addListener(icon, 'click', this.toggle, this);

		return this._container;
	}
});
L.Control.mapRoller = function(options) {
	return new L.Control.MapRoller(options);
};