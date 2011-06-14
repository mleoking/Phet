﻿/*
* Copyright (c) 2006-2007 Erin Catto http://www.gphysics.com
*
* This software is provided 'as-is', without any express or implied
* warranty.  In no event will the authors be held liable for any damages
* arising from the use of this software.
* Permission is granted to anyone to use this software for any purpose,
* including commercial applications, and to alter it and redistribute it
* freely, subject to the following restrictions:
* 1. The origin of this software must not be misrepresented; you must not
* claim that you wrote the original software. If you use this software
* in a product, an acknowledgment in the product documentation would be
* appreciated but is not required.
* 2. Altered source versions must be plainly marked as such, and must not be
* misrepresented as being the original software.
* 3. This notice may not be removed or altered from any source distribution.
*/

package Box2D.Dynamics{


import Box2D.Collision.*;
import Box2D.Collision.Shapes.*;
import Box2D.Dynamics.Contacts.*;
import Box2D.Dynamics.*;
import Box2D.Common.Math.*;
import Box2D.Common.*;

import flash.display.Sprite;


/// Implement and register this class with a b2World to provide debug drawing of physics
/// entities in your game.
public class b2DebugDraw
{

	public function b2DebugDraw(){
		m_drawFlags = 0;
	}

	//virtual ~b2DebugDraw() {}

	//enum
	//{
		static public var e_shapeBit:uint 			= 0x0001; ///< draw shapes
		static public var e_jointBit:uint			= 0x0002; ///< draw joint connections
		static public var e_coreShapeBit:uint		= 0x0004; ///< draw core (TOI) shapes
		static public var e_aabbBit:uint			= 0x0008; ///< draw axis aligned bounding boxes
		static public var e_obbBit:uint				= 0x0010; ///< draw oriented bounding boxes
		static public var e_pairBit:uint			= 0x0020; ///< draw broad-phase pairs
		static public var e_centerOfMassBit:uint	= 0x0040; ///< draw center of mass frame
	//};

	/// Set the drawing flags.
	public function SetFlags(flags:uint) : void{
		m_drawFlags = flags;
	}

	/// Get the drawing flags.
	public function GetFlags() : uint{
		return m_drawFlags;
	}
	
	/// Append flags to the current flags.
	public function AppendFlags(flags:uint) : void{
		m_drawFlags |= flags;
	}

	/// Clear flags from the current flags.
	public function ClearFlags(flags:uint) : void{
		m_drawFlags &= ~flags;
	}

	/// Draw a closed polygon provided in CCW order.
	public virtual function DrawPolygon(vertices:Array, vertexCount:int, color:b2Color) : void{
		
		m_sprite.graphics.lineStyle(m_lineThickness, color.color, m_alpha);
		m_sprite.graphics.moveTo(vertices[0].x * m_drawScale, vertices[0].y * m_drawScale);
		for (var i:int = 1; i < vertexCount; i++){
				m_sprite.graphics.lineTo(vertices[i].x * m_drawScale, vertices[i].y * m_drawScale);
		}
		m_sprite.graphics.lineTo(vertices[0].x * m_drawScale, vertices[0].y * m_drawScale);
		
	}

	/// Draw a solid closed polygon provided in CCW order.
	public virtual function DrawSolidPolygon(vertices:Array, vertexCount:int, color:b2Color) : void{
		
		m_sprite.graphics.lineStyle(m_lineThickness, color.color, m_alpha);
		m_sprite.graphics.moveTo(vertices[0].x * m_drawScale, vertices[0].y * m_drawScale);
		m_sprite.graphics.beginFill(color.color, m_fillAlpha);
		for (var i:int = 1; i < vertexCount; i++){
				m_sprite.graphics.lineTo(vertices[i].x * m_drawScale, vertices[i].y * m_drawScale);
		}
		m_sprite.graphics.lineTo(vertices[0].x * m_drawScale, vertices[0].y * m_drawScale);
		m_sprite.graphics.endFill();
		
	}

	/// Draw a circle.
	public virtual function DrawCircle(center:b2Vec2, radius:Number, color:b2Color) : void{
		
		m_sprite.graphics.lineStyle(m_lineThickness, color.color, m_alpha);
		m_sprite.graphics.drawCircle(center.x * m_drawScale, center.y * m_drawScale, radius * m_drawScale);
		
	}
	
	/// Draw a solid circle.
	public virtual function DrawSolidCircle(center:b2Vec2, radius:Number, axis:b2Vec2, color:b2Color) : void{
		
		m_sprite.graphics.lineStyle(m_lineThickness, color.color, m_alpha);
		m_sprite.graphics.moveTo(0,0);
		m_sprite.graphics.beginFill(color.color, m_fillAlpha);
		m_sprite.graphics.drawCircle(center.x * m_drawScale, center.y * m_drawScale, radius * m_drawScale);
		m_sprite.graphics.endFill();
		m_sprite.graphics.moveTo(center.x * m_drawScale, center.y * m_drawScale);
		m_sprite.graphics.lineTo((center.x + axis.x*radius) * m_drawScale, (center.y + axis.y*radius) * m_drawScale);
		
	}

	
	/// Draw a line segment.
	public virtual function DrawSegment(p1:b2Vec2, p2:b2Vec2, color:b2Color) : void{
		
		m_sprite.graphics.lineStyle(m_lineThickness, color.color, m_alpha);
		m_sprite.graphics.moveTo(p1.x * m_drawScale, p1.y * m_drawScale);
		m_sprite.graphics.lineTo(p2.x * m_drawScale, p2.y * m_drawScale);
		
	}

	/// Draw a transform. Choose your own length scale.
	/// @param xf a transform.
	public virtual function DrawXForm(xf:b2XForm) : void{
		
		m_sprite.graphics.lineStyle(m_lineThickness, 0xff0000, m_alpha);
		m_sprite.graphics.moveTo(xf.position.x * m_drawScale, xf.position.y * m_drawScale);
		m_sprite.graphics.lineTo((xf.position.x + m_xformScale*xf.R.col1.x) * m_drawScale, (xf.position.y + m_xformScale*xf.R.col1.y) * m_drawScale);
		
		m_sprite.graphics.lineStyle(m_lineThickness, 0x00ff00, m_alpha);
		m_sprite.graphics.moveTo(xf.position.x * m_drawScale, xf.position.y * m_drawScale);
		m_sprite.graphics.lineTo((xf.position.x + m_xformScale*xf.R.col2.x) * m_drawScale, (xf.position.y + m_xformScale*xf.R.col2.y) * m_drawScale);
		
	}
	
	
	
	public var m_drawFlags:uint;
	public var m_sprite:Sprite;
	public var m_drawScale:Number = 1.0;
	
	public var m_lineThickness:Number = 1.0;
	public var m_alpha:Number = 1.0;
	public var m_fillAlpha:Number = 1.0;
	public var m_xformScale:Number = 1.0;
	
};

}
