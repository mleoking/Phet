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

package Box2D.Dynamics.Contacts{


import Box2D.Common.Math.*;


public class b2ContactConstraintPoint
{
	public var localAnchor1:b2Vec2=new b2Vec2();
	public var localAnchor2:b2Vec2=new b2Vec2();
	public var r1:b2Vec2=new b2Vec2();
	public var r2:b2Vec2=new b2Vec2();
	public var normalImpulse:Number;
	public var tangentImpulse:Number;
	public var positionImpulse:Number;
	public var normalMass:Number;
	public var tangentMass:Number;
	public var equalizedMass:Number;
	public var separation:Number;
	public var velocityBias:Number;
};


}