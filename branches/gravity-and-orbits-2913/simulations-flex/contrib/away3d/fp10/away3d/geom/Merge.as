﻿package away3d.geom{		import away3d.core.base.Mesh;	import away3d.core.base.Vertex;	import away3d.core.base.UV;	import away3d.core.base.Face;	import away3d.core.math.Number3D;	import away3d.materials.ITriangleMaterial;	import away3d.arcane;		use namespace arcane;	  		/**	 * Class Merge merges two meshes into one.<Merge></code>	 */	public class Merge{				private var _objectspace:Boolean;		private var _unicgeometry:Boolean;		private var _keepMaterial:Boolean;				private function applyPosition(v:Vertex, sceneposition:Number3D, rotationX:Number, rotationY:Number, rotationZ:Number):Vertex		{			var x:Number;			var y:Number;			var z:Number;			var x1:Number;			var y1:Number;						var rad:Number = Math.PI / 180;			var rotx:Number = rotationX * rad;			var roty:Number = rotationY * rad;			var rotz:Number = rotationZ * rad;			var sinx:Number = Math.sin(rotx);			var cosx:Number = Math.cos(rotx);			var siny:Number = Math.sin(roty);			var cosy:Number = Math.cos(roty);			var sinz:Number = Math.sin(rotz);			var cosz:Number = Math.cos(rotz);			x = v.x + sceneposition.x;			y = v.y + sceneposition.y;			z = v.z + sceneposition.z;			y1 = y;			y = y1*cosx+z*-sinx;			z = y1*sinx+z*cosx;						x1 = x;			x = x1*cosy+z*siny;			z = x1*-siny+z*cosy;					x1 = x;			x = x1*cosz+y*-sinz;			y = x1*sinz+y*cosz;			v.setValue(x, y, z);			  			return v;		}		 		private function merge(mesh1:Mesh, mesh2:Mesh):void		{			var i:int;						if(_unicgeometry || !_objectspace){				var v0:Vertex;				var v1:Vertex;				var v2:Vertex;				var uv0:UV;				var uv1:UV;				var uv2:UV;				var face:Face;				var mat:ITriangleMaterial;								for(i = 0;i<mesh2.faces.length;++i){										face = mesh2.faces[i];					v0 = new Vertex(face.v0.x, face.v0.y, face.v0.z);					v1 = new Vertex(face.v1.x, face.v1.y, face.v1.z);					v2 = new Vertex(face.v2.x, face.v2.y, face.v2.z);					uv0 = new UV(face.uv0.u, face.uv0.v);					uv1 = new UV(face.uv1.u, face.uv1.v);					uv2 = new UV(face.uv2.u, face.uv2.v);										if(!_objectspace){						v0 = applyPosition(v0, mesh2.scenePosition, mesh2.rotationX, mesh2.rotationY, mesh2.rotationZ);						v1 = applyPosition(v1, mesh2.scenePosition, mesh2.rotationX, mesh2.rotationY, mesh2.rotationZ);						v2 = applyPosition(v2, mesh2.scenePosition, mesh2.rotationX, mesh2.rotationY, mesh2.rotationZ);					}					mat = (_keepMaterial)? mesh2.material as ITriangleMaterial: mesh1.material as ITriangleMaterial;					mesh1.addFace(new Face(v0, v1, v2, mat, uv0, uv1, uv2 ) );									}							} else {								for(i= 0;i<mesh2.faces.length;++i){					mat = (_keepMaterial)? mesh2.material as ITriangleMaterial : mesh1.material as ITriangleMaterial; 					face = mesh2.faces[i];					mesh1.addFace(new Face(face.v0, face.v1, face.v2, mat, face.uv0, face.uv1, face.uv2 ) );				}							}		}		 		/**		* @param	 objectspace		[optional] Boolean. Defines if mesh2 is merge using its objectspace or the worldtransform. Default is true.		* @param	 unicgeometry	[optional] Boolean. Defines if the receiver object must generate new vertexes, uv's or uses mesh2's. Default is false.		* @param	 keepMaterial		[optional] Boolean. Defines if the receiver object must use the mesh2 material information. If false the merge information from mesh2 gets the mesh1 material. Default is false.		*/				function Merge(objectspace:Boolean = true, unicgeometry:Boolean = false, keepMaterial:Boolean = false):void		{			_objectspace = objectspace;			_unicgeometry = unicgeometry;			_keepMaterial = keepMaterial;		}				/**		*  Merges two meshes into one.		* 		* @param	 mesh1				Mesh. The receiver object that will hold both information.		* @param	 mesh2				Mesh. The object to be merge with mesh1.		*/		public function apply(mesh1:Mesh, mesh2:Mesh):void		{			merge(mesh1, mesh2);		}				/**		* Defines if mesh2 is merge using its objectspace.		*/		public function set objectspace(b:Boolean):void		{			_objectspace = b;		}				public function get objectspace():Boolean		{			return _objectspace;		}		/**		* Defines if mesh2 will be merged using new instances or shared.		*/		public function set unicgeometry(b:Boolean):void		{			_unicgeometry = b;		}				public function get unicgeometry():Boolean		{			return _unicgeometry;		}		/**		* Defines if mesh2 will be merged using its own material information.		*/		public function set keepmaterial(b:Boolean):void		{			_keepMaterial = b;		}				public function get keepMaterial():Boolean		{			return _keepMaterial;		}					}}