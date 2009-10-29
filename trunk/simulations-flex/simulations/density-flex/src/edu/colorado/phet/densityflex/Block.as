package edu.colorado.phet.densityflex {

import flash.geom.ColorTransform;

/**
 * This class represents the model object for a block.
 */
public class Block implements IPositioned {
    private var mass : Number;
    private var width : Number;
    private var height: Number;
    private var depth : Number;
    private var x:Number;
    private var y:Number;
    private var z:Number;
    private var color:ColorTransform;
    private var listeners: Array;

    public function Block( initialMass : Number, size : Number, color : ColorTransform ) : void {
        this.width = size;
        this.height = size;
        this.depth = size;
        this.x=0;
        this.y=0;
        this.z = size / 2 + 101;
        this.mass = initialMass;
        this.color = color;
        this.listeners = new Array();
    }

    public function addListener(listener:Listener):void{
        listeners.push(listener);
    }

    public function getWidth():Number {
        return this.width;
    }

    public function getHeight():Number {
        return this.height;
    }

    public function getDepth():Number {
        return this.depth;
    }

    public function getX():Number {
        return x;
    }

    public function getY():Number {
        return y;
    }

    public function getZ():Number {
        return z;
    }

    public function getColor():ColorTransform {
        return color;
    }

    public function getMass():Number {
        return mass;
    }

    public function setPosition(x:Number, y:Number): void {
        this.x=x;
        this.y=y;

        //todo: notify listeners
        for each (var listener:Listener in listeners){
            listener.update();
        }
    }
}
}