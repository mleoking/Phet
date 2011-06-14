﻿package away3d.materials.shaders {	import away3d.core.light.DirectionalLight;		import away3d.containers.*;	import away3d.arcane;	import away3d.core.base.*;	import away3d.core.draw.*;	import away3d.core.math.*;	import away3d.core.render.*;	import away3d.core.utils.*;		import flash.display.*;	import flash.geom.*;		use namespace arcane;		/**	 * Diffuse shader class for directional lighting.	 * 	 * @see away3d.lights.DirectionalLight3D	 */    public class DiffusePhongShader extends AbstractShader    {		//private var eTriVal:Number = 512/Math.PI;		private var _diffuseTransform:Matrix3D;		private var _szx:Number;		private var _szy:Number;		private var _szz:Number;		private var _normal0z:Number;		private var _normal1z:Number;		private var _normal2z:Number;		private var eTriConst:Number = 512/Math.PI;        		/**		 * @inheritDoc		 */        protected function clearFaces(source:Object3D, view:View3D):void        {        	view;//TODO : FDT Warning        	notifyMaterialUpdate();        	        	for each (var _faceMaterialVO:FaceMaterialVO in _faceDictionary) {        		if (source == _faceMaterialVO.source) {	        		if (!_faceMaterialVO.cleared)	        			_faceMaterialVO.clear();	        		_faceMaterialVO.invalidated = true;	        	}        	}        }				/**		 * @inheritDoc		 */        protected override function renderShader(tri:DrawTriangle):void        {			_faceVO = tri.faceVO;						_n0 = _source.geometry.getVertexNormal(_face.v0);			_n1 = _source.geometry.getVertexNormal(_face.v1);			_n2 = _source.geometry.getVertexNormal(_face.v2);						var _source_lightarray_directionals:Array = _source.lightarray.directionals;			for each (var directional:DirectionalLight in _source_lightarray_directionals)	    	{				_diffuseTransform = directional.diffuseTransform[_source];								_szx = _diffuseTransform.szx;				_szy = _diffuseTransform.szy;				_szz = _diffuseTransform.szz;								_normal0z = _n0.x * _szx + _n0.y * _szy + _n0.z * _szz;				_normal1z = _n1.x * _szx + _n1.y * _szy + _n1.z * _szz;				_normal2z = _n2.x * _szx + _n2.y * _szy + _n2.z * _szz;								//check to see if the uv triangle lies inside the bitmap area				if (_normal0z > 0 || _normal1z > 0 || _normal2z > 0) {										eTri0x = eTriConst*Math.acos(_normal0z);										//store a clone					if (_faceMaterialVO.cleared && !_parentFaceMaterialVO.updated) {						_faceMaterialVO.bitmap = _parentFaceMaterialVO.bitmap.clone();						_faceMaterialVO.bitmap.lock();					}										_faceMaterialVO.cleared = false;					_faceMaterialVO.updated = true;										//calulate mapping					_mapping.a = eTriConst*Math.acos(_normal1z) - eTri0x;					_mapping.b = 127;					_mapping.c = eTriConst*Math.acos(_normal2z) - eTri0x;					_mapping.d = 255;					_mapping.tx = eTri0x;					_mapping.ty = 0;		            _mapping.invert();		            _mapping.concat(_faceMaterialVO.invtexturemapping);		            					//draw into faceBitmap					_graphics = _s.graphics;					_graphics.clear();					_graphics.beginBitmapFill(directional.diffuseBitmap, _mapping, false, smooth);					_graphics.drawRect(0, 0, _bitmapRect.width, _bitmapRect.height);		            _graphics.endFill();					_faceMaterialVO.bitmap.draw(_s, null, null, blendMode);					//_faceMaterialVO.bitmap.draw(directional.diffuseBitmap, _mapping, null, blendMode, _faceMaterialVO.bitmap.rect, smooth);				}	    	}        }        		/**		 * Creates a new <code>DiffusePhongShader</code> object.		 * 		 * @param	init	[optional]	An initialisation object for specifying default instance properties.		 */        public function DiffusePhongShader(init:Object = null)        {        	super(init);        }        		/**		 * @inheritDoc		 */		public override function updateMaterial(source:Object3D, view:View3D):void        {        	var _source_lightarray_directionals:Array = source.lightarray.directionals;        	for each (var directional:DirectionalLight in _source_lightarray_directionals) {        		if (!directional.diffuseTransform[source] || view.scene.updatedObjects[source]) {        			directional.setDiffuseTransform(source);        			clearFaces(source, view);        		}        	}        }        		/**		 * @inheritDoc		 */        public override function renderLayer(tri:DrawTriangle, layer:Sprite, level:int):int        {        	super.renderLayer(tri, layer, level);        	        	var _lights_directionals:Array = _lights.directionals;        	for each (var directional:DirectionalLight in _lights_directionals)        	{        		if (_lights.numLights > 1) {					_shape = _session.getLightShape(this, level++, layer, directional);		        	_shape.blendMode = blendMode;		        	_graphics = _shape.graphics;		        } else {		        	_graphics = layer.graphics;		        }        		        		_diffuseTransform = directional.diffuseTransform[_source];								_n0 = _source.geometry.getVertexNormal(_face.v0);				_n1 = _source.geometry.getVertexNormal(_face.v1);				_n2 = _source.geometry.getVertexNormal(_face.v2);								_szx = _diffuseTransform.szx;				_szy = _diffuseTransform.szy;				_szz = _diffuseTransform.szz;								_normal0z = _n0.x * _szx + _n0.y * _szy + _n0.z * _szz;				_normal1z = _n1.x * _szx + _n1.y * _szy + _n1.z * _szz;				_normal2z = _n2.x * _szx + _n2.y * _szy + _n2.z * _szz;	        		        					eTri0x = eTriConst*Math.acos(_normal0z);								_mapping.a = eTriConst*Math.acos(_normal1z) - eTri0x;				_mapping.b = 127;				_mapping.c = eTriConst*Math.acos(_normal2z) - eTri0x;				_mapping.d = 255;				_mapping.tx = eTri0x;				_mapping.ty = 0;	            _mapping.invert();	            				_source.session.renderTriangleBitmap(directional.ambientDiffuseBitmap, _mapping, tri.screenVertices, tri.screenIndices, tri.startIndex, tri.endIndex, smooth, false, _graphics);        	}						if (debug)                _source.session.renderTriangleLine(0, 0x0000FF, 1, tri.screenVertices, tri.screenCommands, tri.screenIndices, tri.startIndex, tri.endIndex);                        return level;        }    }}