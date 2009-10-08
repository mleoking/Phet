﻿package away3d.core.base{    import away3d.core.math.*;		/**	 * Vertex position value object.	 */    public class VertexPosition    {    	/**    	 * Defines the x coordinate.    	 */        public var x:Number;    	/**    	 * Defines the y coordinate.    	 */        public var y:Number;    	/**    	 * Defines the z coordinate.    	 */        public var z:Number;        public var vertex:Vertex;		/**		 * Creates a new <code>VertexPosition</code> object.		 *		 * @param	vertex	The vertex object used to define the default x, y and z values.		 */        public function VertexPosition(vertex:Vertex)        {            this.vertex = vertex;            this.x = 0;            this.y = 0;            this.z = 0;        }		/**		 * Adjusts the position of the vertex object incrementally.		 *		 * @param	k	The fraction by which to adjust the vertex values.		 */        public function adjust(k:Number = 1):void        {            vertex.adjust(x, y, z, k);        }				/**		 * Adjusts the position of the vertex object by Number3D.		 *		 * @param	value	Amount to add in Number3D format.		 */        public function add(value:Number3D):void        {			vertex.add(value);        }				/**		 * Transforms the position of the vertex object by the given 3d matrix.		 *		 * @param	m	The 3d matrix to use.		 */        public function transform(m:MatrixAway3D):void        {        	vertex.transform(m);        }        			/**		 * Reset the position of the vertex object by Number3D.		 */        public function reset():void        {			vertex.reset();        }				//temp patch, to retreive the missing async indexes for md2 files and as3 animated outputs. 		//returns position into vertices array        public function getIndex(vertices:Array):int        {			var ox:Number = vertex.x;			var oy:Number = vertex.y;			var oz:Number = vertex.z;						vertex.x = NaN;			vertex.y = NaN;			vertex.z = NaN;						var id:int = 0;			for(var i:int = 0;i<vertices.length;++i){				if(  isNaN(vertices[i].x) && isNaN(vertices[i].y) && isNaN(vertices[i].z)){ 					id = i;					break;				}			}						vertex.x = ox;			vertex.y = oy;			vertex.z = oz;						return id;        }    }}