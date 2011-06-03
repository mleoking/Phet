package edu.colorado.phet.normalmodes {
import edu.colorado.phet.flexcommon.AboutDialog;
import edu.colorado.phet.normalmodes.*;

//model of 1D array of coupled masses and springs

import flash.events.*;
import flash.utils.*;

//contains Timer class

public class Model {

    public var view: View;          //view associated with this model
    //physical variables
    private var m:Number;           //mass in kg of each mass in array (all masses equal)
    private var k:Number;           //spring constant in N/m of each spring in array (all springs equal)
    private var b:Number;           //damping constant: F_drag = -b*v
    private var L:Number;           //distance between fixed walls in meters
    private var nMax:int;           //maximum possible number of mobile masses in 1D array
    private var N:int;              //number of mobile masses in 1D array; does not include the 2 virtual stationary masses at wall positions
    private var x_arr:Array;        //array of positions of masses; array length = N + 2, since 2 stationary masses at x = 0 and x = L
    private var v_arr:Array;        //array of velocities of masses, array length = N+2, elements 0 and N+1 have value zero
    private var a_arr:Array;        //array of accelerations of masses,
    private var aPre_arr:Array;     //array of accelerations in previous time step, needed for velocity verlet
    //time variables
    private var _paused: Boolean;  //true if sim paused
    private var t: Number;		    //time in seconds
    private var tInt: Number;       //time rounded up to nearest second, for testing only
    private var lastTime: Number;	//time in previous timeStep
    private var tRate: Number;	    //1 = real time; 0.25 = 1/4 of real time, etc.
    private var dt: Number;  	    //default time step in seconds
    private var msTimer: Timer;	    //millisecond timer

    public function Model( ) {

        this.x_arr = new Array(nMax + 2);     //nMax = max nbr of mobile masses, +2 virtual stationary masses at ends
        this.v_arr = new Array(nMax + 2);
        this.a_arr = new Array(nMax + 2);
        this.aPre_arr = new Array(nMax + 2);
        this.initialize();
    }//end of constructor

    private function initialize() {
        this.nMax = 10;             //maximum of 10 mobile masses in array
        this.N = 1;                 //start with 1 or 3 mobile masses
        this.m = 0.1;               //100 gram masses
        this.k = this.m*4*Math.PI*Math.PI;  //k set so that period of motion is about 1 sec
        this.b = 0;                 //initial damping = 0, F_drag = -b*v
        this.L = 1;                 //1 meter between fixed walls
        this.initializeKinematicArrays();
        this.setInitialPositions(); //for testing only
        this._paused = false;
        this.t = 0;
        this.tInt = 1;
        this.dt = 0.01;
        this.tRate = 1;
        this.msTimer = new Timer( this.dt * 1000 );   //argument of Timer constructor is time step in ms
        this.msTimer.addEventListener( TimerEvent.TIMER, stepForward );
        this.startMotion();
    }//end initialize()


    private function initializeKinematicArrays():void{
        var arrLength:int = this.N + 2;
        for(var i:int = 0; i < arrLength; i++){
            this.x_arr[i] = i*this.L/(this.N + 1);  //space masses evenly between x = 0 and x = L
            this.v_arr[i] = 0;                      //initial velocities = 0;
            this.a_arr[i] = 0;                      //initial accelerations = 0
            this.aPre_arr[i] = 0;
        }
    }//end initializeKinematicArrays()

    //for testing only
    private function setInitialPositions():void{
       var arrLength:int = this.N + 2;
       for(var i:int = 1; i < (arrLength - 1); i++){
            this.x_arr[i] = i*this.L/(this.N + 1) + 0.1;
       }
    }

    //SETTERS and GETTERS
    public function setN(nbrOfMobileMasses:int):void{
        if(nbrOfMobileMasses > this.nMax){
            this.N = this.nMax;
            trace("ERROR: nbr of masses too high");
        }else if(nbrOfMobileMasses < 1){
            this.N = 1;
            trace("ERROR: nbr of masses too low");
        }else{
           this.N = nbrOfMobileMasses;
        }
        this.initializeKinematicArrays();
        this.updateView();
    }//end setN


    public function setB(b:Number):void{
        if(b < 0 || b > 2*Math.sqrt(this.m*this.k)){         //if b negative or if b > critical damping value
            trace("ERROR: damping constant out of bounds")
        }
        this.b = b;
    }

    public function setTRate(rate:Number):void{
        this.tRate = rate;
    }

    public function getDt(): Number {
        return this.dt;
    }

    public function get paused() {
        return this._paused;
    }
    //END SETTERS and GETTERS


    public function pauseSim(): void {
        this._paused = true;
        this.msTimer.stop();
        //this.running = false;
    }

    public function unPauseSim(): void {
        this._paused = false;
    }


    public function startMotion(): void {
        //this.running = true;
        trace("Model.startMotion called.");
        if ( !this._paused ) {
            this.msTimer.start();
        }
    }

    public function stopMotion(): void {
        //this.running = false;
        if ( !this._paused ) {
            this.msTimer.stop();
        }
    }

    private function stepForward( evt: TimerEvent ): void {
        //need function without event argument
        this.singleStep();
        evt.updateAfterEvent();
    }

    private function singleStep(): void {
//        var currentTime = getTimer() / 1000;              //flash.utils.getTimer()
//        var realDt: Number = currentTime - this.lastTime;
//        this.lastTime = currentTime;
//        //time step must not exceed 0.04 seconds.
//        //If time step < 0.04 s, then sim uses time-based animation.  Else uses frame-based animation
//        if ( realDt < 0.04 ) {
//            this.dt = this.tRate * realDt;
//        }
//        else {
//            this.dt = this.tRate * 0.04;
//        }
        this.t += this.dt;

        //velocity verlet algorithm
        for(var i:int = 1; i <= this.N; i++){      //loop thru all mobile masses  (masses on ends always stationary)
            //var a: Number = (this.k/this.m)*(x_arr[i+1] + x_arr[i-1] - 2*x_arr[i]);
            x_arr[i] = x_arr[i] + v_arr[i] * dt + (1 / 2) * a_arr[i] * dt * dt;
            aPre_arr[i] = a_arr[i];   //store current accelerations for next step
            //var vp:Number = v_arr[i] + a_arr[i] * dt;		//post velocity, only needed if computing drag
        }//end 1st for loop

        for(var i:int = 1; i <= this.N; i++){      //loop thru all mobile masses  (masses on ends always stationary)
            this.a_arr[i] = (this.k/this.m)*(x_arr[i+1] + x_arr[i-1] - 2*x_arr[i]);		//post-acceleration
            v_arr[i] = v_arr[i] + 0.5 * (this.aPre_arr[i] + a_arr[i]) * dt;
        }//end 2nd for loop
        //this.test();
        updateView();
    } //end singleStep()

    public function singleStepWhenPaused():void{
        this.dt = this.tRate * 0.02;
        this.t += this.dt;
        this.singleStep( );
        updateView();
    }

    //for testing only
    public function test():void{
        if(this.t > this.tInt){
            trace("x_1 = " + x_arr[1] + "   at t = " + this.t);
            this.tInt += 1;
        }
    }

//    public function singleStepWhenPaused(): void {
//        this.dt = this.tRate * 0.02;
//        this.t += this.dt;
//        //trace("ShakerModel.singleStep called. realDt = "+realDt);
//        //this.y0 = this.A*Math.sin(2*Math.PI*f*t + this.phase);
//        if ( this.running ) {
//
//        }
//
//
//        updateView();
//    }


    public function registerView( view: View ): void {
        this.view = view;
    }

    public function updateView(): void {
        this.view.update();
    }

}//end of class

}//end of package