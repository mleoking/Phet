/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 6/12/11
 * Time: 6:20 AM
 * To change this template use File | Settings | File Templates.
 */

//model for 2D array of masses and springs
package edu.colorado.phet.normalmodes.model {
import edu.colorado.phet.normalmodes.*;
import edu.colorado.phet.normalmodes.view.MainView;
import edu.colorado.phet.normalmodes.view.View2;

import flash.events.TimerEvent;
import flash.utils.Timer;
import flash.utils.getTimer;

public class Model2 {

    public var myMainView: MainView;
    public var view: View2;          //view associated with this model
    //physical variables
    private var m:Number;           //mass in kg of each mass in array (all masses equal)
    private var k:Number;           //spring constant in N/m of each spring in array (all springs equal)
    private var b:Number;           //damping constant: F_drag = -b*v, currently unused
    private var _L:Number;          //1D distance between fixed walls in meters, masses are in LxL box
    private var _nMax:int;          //maximum possible number of mobile masses in 1D array, number of mobile masses in 2D array is nMax*nMax
    private var _N:int;             //number of mobile masses in 1D array; does not include the 2 virtual stationary masses at wall positions
    private var nChanged:Boolean;   //flag to indicate number of mobile masses has changed, so must update view
    private var modesChanged:Boolean;//flag to indicate that mode amplitudes and phases have been zeroed
    private var modesZeroed:Boolean;//flag to indicate that mode amplitudes and phases have been zeroed, so must clear buttonArrayPanel
    private var _verletOn:Boolean;  //true is using Verlet algorithm, false is using exact algorithm
    private var _xModes:Boolean;     //true if x-motion modes only; false if y-motion modes only
    public var nbrStepsSinceRelease:int; //number of time steps since one of the masses was ungrabbed;
    private var x0_arr:Array;       //2D array of equilibrium x-positions of masses; array size = N+2 * N+2, since 2 stationary masses in each row and column at x = 0 and x = L, y = 0 and y = L
    private var y0_arr:Array;       //2D array of equilibrium y-positions of masses
    private var sx_arr:Array;       //2D array of x-components of s-positions of masses, sx = distance from equilibrium positions in x-direction
    private var sy_arr:Array;       //2D array of x-components of s-positions of masses, sy = distance from equilibrium positions in y-direction        
    private var vx_arr:Array;       //2D array of x-comps of velocities of masses, array size = N+2 * N+2, elements 0 and N+1 have value zero
    private var vy_arr:Array;       //2D array of y-comps of velocities of masses, array size = N+2 * N+2, elements 0 and N+1 have value zero    
    private var ax_arr:Array;       //2D array of x-accelerations of masses,
    private var ay_arr:Array;       //2D array of y-accelerations of masses,
    private var axPre_arr:Array;    //array of x-accelerations in previous time step, needed for velocity verlet
    private var ayPre_arr:Array;    //array of y-accelerations in previous time step, needed for velocity verlet
       
    private var modeOmega_arr:Array; //2D array of normal mode angular frequencies, omega = 2*pi*f array size is N*N
    private var modeAmpliX_arr:Array; //2D array of normal mode amplitudes for x-polarization
    private var modeAmpliY_arr:Array; //2D array of normal mode amplitudes for y-polarization
    private var modePhase_arr:Array; //2D array of normal mode phases
    private var _grabbedMassIndices:Array;    //i, j indices of mass grabbed by mouse
    //private var _longitudinalMode:Boolean;  //true if in longitudinal mode, false if in transverse mode

    //time variables
    private var _paused: Boolean;   //true if sim paused
    private var t: Number;		    //time in seconds
    private var tInt: Number;       //time rounded down to nearest whole sec, for testing only
    private var lastTime: Number;	//time in previous timeStep
    private var lastTimeDuringMouseDrag; //used to allow mouse user to "throw" the mass
    private var tRate: Number;	    //1 = real time; 0.25 = 1/4 of real time, etc.
    private var dt: Number;  	    //default time step in seconds
    private var msTimer: Timer;	    //millisecond timer

    public function Model2( myMainView: MainView ) {
        this.myMainView = myMainView;
        //all 2D arrays are in row-column format, x (column) increases to right; y (row) increases down
        this._nMax = 10;      //maximum of 10*10 mobile masses in 2D array
        this.x0_arr = new Array(_nMax + 2);     //_nMax = max nbr of mobile masses, +2 virtual stationary masses at ends
        this.y0_arr = new Array(_nMax + 2);
        this.sx_arr = new Array(_nMax + 2);
        this.sy_arr = new Array(_nMax + 2);
        this.vx_arr = new Array(_nMax + 2);
        this.vy_arr = new Array(_nMax + 2);
        this.ax_arr = new Array(_nMax + 2);
        this.ay_arr = new Array(_nMax + 2);
        this.axPre_arr = new Array(_nMax + 2);
        this.ayPre_arr = new Array(_nMax + 2);
        this._grabbedMassIndices = new Array( 2 );
        this.modeOmega_arr = new Array( _nMax + 1 );   //1st row and column elements are dummies, so mode 1,1 is element i=1, j=1
        this.modeAmpliX_arr = new Array( _nMax + 1 );
        this.modeAmpliY_arr = new Array( _nMax + 1 );
        this.modePhase_arr = new Array( _nMax + 1 );
        for(var i:int = 0; i < _nMax+2; i++){
            x0_arr[i] = new Array(_nMax + 2);     //_nMax = max nbr of mobile masses, +2 virtual stationary masses at ends
            y0_arr[i] = new Array(_nMax + 2);
            sx_arr[i] = new Array(_nMax + 2);
            sy_arr[i] = new Array(_nMax + 2);
            vx_arr[i] = new Array(_nMax + 2);
            vy_arr[i] = new Array(_nMax + 2);
            ax_arr[i] = new Array(_nMax + 2);
            ay_arr[i] = new Array(_nMax + 2);
            axPre_arr[i] = new Array(_nMax + 2);
            ayPre_arr[i] = new Array(_nMax + 2);
            modeOmega_arr[i] = new Array( _nMax + 1 );  //1st row and column elements are dummies, so mode 1,1 is element i=1, j=1
            modeAmpliX_arr[i] = new Array( _nMax + 1 );  //1st row and column elements are dummies, so mode 1,1 is element i=1, j=1
            modeAmpliY_arr[i] = new Array( _nMax + 1 );  //1st row and column elements are dummies, so mode 1,1 is element i=1, j=1
            modePhase_arr[i] = new Array( _nMax + 1 );  //1st row and column elements are dummies, so mode 1,1 is element i=1, j=1
        }
        
        this.initialize();
    }//end of constructor

    private function initialize():void{
        this._N = 5;                 //start with 5*5  mobile masses
        this.nChanged = false;
        this.modesChanged = false;
        this.modesZeroed = false;
        this._xModes = true;
        this.nbrStepsSinceRelease = 10;  //just needs to be larger than 3
        this._verletOn = true;
        this.m = 0.1;               //100 gram masses
        this.k = this.m*4*Math.PI*Math.PI;  //k set so that period of motion is about 1 sec
        this.b = 0;                 //initial damping = 0, F_drag = -b*v
        this._L = 1;                //1 meter between fixed walls
        this._grabbedMassIndices = [ 0, 0 ];      //top left mass (index 0, 0) is always stationary
        //this._longitudinalMode = true;
        this.initializeKinematicArrays();
        this.initializeModeArrays();
        //this.setInitialPositions(); //for testing only
        this._paused = false;
        this.t = 0;
        this.tInt = 0;
        //this.tInt = 1;              //testing only
        this.dt = 0.02;
        this.tRate = 1;
        this.msTimer = new Timer( this.dt * 1000 );   //argument of Timer constructor is time step in ms
        this.msTimer.addEventListener( TimerEvent.TIMER, stepForward );
        //this.startMotion();
    }//end initialize()

    public function initializeKinematicArrays():void{
        this._verletOn = true;
        var oneDLength:int = this._N + 2;                       //length of one row or one column in square array
        for(var i:int = 0; i < oneDLength; i++){
            for (var j: int = 0; j < oneDLength; j++){
                //Note that (i, j) is (row, column) is (y, x)
                this.x0_arr[i][j] = j*this._L/(this._N + 1);  //space masses evenly between x = 0 and x = L
                this.y0_arr[i][j] = i*this._L/(this._N + 1);  //space masses evenly between y = 0 and y = L
                this.sx_arr[i][j] = 0;
                this.sy_arr[i][j] = 0;
                this.vx_arr[i][j] = 0;                      //initial velocities = 0;
                this.vy_arr[i][j] = 0;                     
                this.ax_arr[i][j] = 0;                      //initial accelerations = 0
                this.ay_arr[i][j] = 0; 
                this.axPre_arr[i][j] = 0;
                this.ayPre_arr[i][j] = 0;
            }
        }
    }//end initializeKinematicArrays()

    private function initializeModeArrays():void{
        for(var i:int = 0; i < _nMax; i++){
            for(var j:int = 0; j < _nMax; j++){
                modeAmpliX_arr[i][j] = 0;
                modeAmpliY_arr[i][j] = 0;
                modePhase_arr[i][j] = 0;
            }
        }
        this.setResonantFrequencies();
    }

    private function setResonantFrequencies():void{
        var omega0:Number = Math.sqrt( k/m );
        //row i = 0, column j = 0 are dummies, unused
        for(var i:int = 1; i <= _N; i++){
            for(var j:int = 1; j <= _N; j++){
                var r:int = i;
                var s:int = j;
                var omega_r:Number = 2*omega0*Math.sin( r*Math.PI/(2*(_N + 1 )));
                var omega_s:Number = 2*omega0*Math.sin( s*Math.PI/(2*(_N + 1 )));
                modeOmega_arr[i][j] = Math.sqrt( omega_r*omega_r + omega_s*omega_s ); //not certain this is correct
            }
        }
    }

    public function zeroModeArrays():void{
        for(var i:int = 0; i <= _nMax; i++){
            for(var j:int = 0; j <= _nMax; j++){
                modeAmpliX_arr[i][j] = 0;
                modeAmpliY_arr[i][j] = 0;
                modePhase_arr[i][j] = 0;
            }
        }
        this.modesZeroed = true;
        updateView();
    }

    //SETTERS and GETTERS
    public function setN(nbrMobileMassesInRow:int):void{
        if(nbrMobileMassesInRow > this._nMax){
            this._N = this._nMax;
            trace("ERROR: nbr of masses too high");
        }else if(nbrMobileMassesInRow < 1){
            this._N = 1;
            trace("ERROR: nbr of masses too low");
        }else{
           this._N = nbrMobileMassesInRow;
        }
        this.initializeKinematicArrays();
        this.setResonantFrequencies();
        this.nChanged = true;
        this.updateView();
    }//end setN

    public function get N():int {
        return this._N;
    }

    public function get L():Number{
      return this._L;
    }

    public function get nMax():int {
        return this._nMax;
    }

    public function set verletOn(tOrF:Boolean):void{
        this._verletOn = tOrF;
    }


    public function setXY(i:int, j:int,  xPos:Number, yPos:Number ):void{
        var previousSx:Number =  this.sx_arr[i][j];
        var previousSy:Number =  this.sy_arr[i][j];
        var sxPos:Number = xPos - this.x0_arr[i][j];
        var syPos:Number = yPos - this.y0_arr[i][j];
        this.sx_arr[i][j] = sxPos;
        this.sy_arr[i][j] = syPos;

        var currentTime:Number = getTimer() / 1000;              //flash.utils.getTimer()
        var realDt: Number = 0.01; //currentTime - this.lastTime;
        this.lastTimeDuringMouseDrag = currentTime;
        var vX:Number = (sxPos - previousSx)/realDt;
        var vY:Number = (syPos - previousSy)/realDt;
        this.vx_arr[i][j] = vX;
        this.vy_arr[i][j] = vY;
        //trace("Model1.setXY  xPos = "+xPos+"    yPos = "+yPos);
        //trace("Model1.setXY i = " + i +"   j = " + j + "   sxPos = "+ sxPos +"    syPos = "+syPos);
    }

    public function getXY(i:int, j:int):Array{
        var xPos:Number  = this.x0_arr[i][j] + this.sx_arr[i][j];
        var yPos:Number = this.y0_arr[i][j] + this.sy_arr[i][j];
        var xAndY:Array = new Array(2);
        xAndY[0] = xPos;
        xAndY[1] = yPos;
        return xAndY ;
    }


    //currently unused, because model has no damping
    public function setB(b:Number):void{
        if(b < 0 || b > 2*Math.sqrt(this.m*this.k)){         //if b negative or if b > critical damping value
            trace("ERROR: damping constant out of bounds")
        }
        this.b = b;
    }

    public function set xModes( tOrF:Boolean ):void{
        this._xModes = tOrF;
    }

    public function setModeAmpli( modeNbrR:int, modeNbrS:int, A:Number ):void{
        //trace("Model2.setModeAmpli  r = " + modeNbrR + "    s = "+modeNbrS );
        this._verletOn = false;
        this.modeAmpliX_arr[ modeNbrR ][ modeNbrS ] = A;
        this.modeAmpliY_arr[ modeNbrR ][ modeNbrS ] = A;
        this.modesChanged = true;
        updateView();
        this.modesChanged = false;
    }

    public function getModeAmpli( modeNbrR:int, modeNbrS:int ):Number{
        return this.modeAmpliX_arr[ modeNbrR ][ modeNbrS ] ;
    }

/*    public function setModePhase( modeNbr:int,  phase:Number ):void{
        this.modePhase_arr[ modeNbr - 1 ] = phase;
        this.modesChanged = true;
        updateView();
        this.modesChanged = false;
    }

    public function getModePhase( modeNbr:int ):Number{
        return this.modePhase_arr[ modeNbr - 1 ];
    }*/

    public function setTRate(rate:Number):void{
        this.tRate = rate;
    }

    public function getDt(): Number {
        return this.dt;
    }

    public function get paused():Boolean {
        return this._paused;
    }

    //called from MassView.startTargetDrag();
    public function set grabbedMassIndices(iJIndices:Array){
        this._grabbedMassIndices = iJIndices;
        //trace("Model2.setGrabbeMassIndices called. u = " + this._grabbedMassIndices[0] + "   j = " + this._grabbedMassIndices[1]);
    }
    //END SETTERS and GETTERS


    public function pauseSim(): void {
        this._paused = true;
        this.msTimer.stop();
        //this.running = false;
    }

    public function unPauseSim(): void {
        this._paused = false;
        this.msTimer.start();
    }

    public function startMotion(): void {
        //trace("Model.startMotion called.");
        if ( !this._paused ) {
            this.msTimer.start();
        }
    }

    public function stopMotion(): void {
        if ( !this._paused ) {
            this.msTimer.stop();
        }
    }

    private function stepForward( evt: TimerEvent ): void {
        //need function without event argument
        this.singleStep();
        evt.updateAfterEvent();
    }

    public function computeModeAmplitudesAndPhases():void{
        //var counter:int = 0;         //for testing
        //var t0:Number = getTimer();
        var N:int  = this._N;
        var muX:Array = new Array( N + 1 );
        var nuX:Array = new Array( N + 1 );
        var muY:Array = new Array( N + 1 );
        var nuY:Array = new Array( N + 1 );
        for(var r:int = 0; r <= N; r++ ){
            muX[ r ] = new Array( N + 1);
            nuX[ r ] = new Array( N + 1);
            muY[ r ] = new Array( N + 1);
            nuY[ r ] = new Array( N + 1);
        }
        for ( r = 1; r <= N; r++ ){
            for ( var s:int = 1; s <= N; s++ ){
                muX[r][s] = 0;
                nuX[r][s] = 0;
                muY[r][s] = 0;
                nuY[r][s] = 0;
                for (var i:int = 1; i <= N; i++ ){
                    for ( var j:int = 1; j <= N; j++){
                        var sineProduct:Number = Math.sin(i*r*Math.PI/(N+1))*Math.sin(j*s*Math.PI/(N+1));
                        muX[r][s] += (4/((N+1)*(N+1)))*sx_arr[i][j]*sineProduct
                        nuX[r][s] += (4/(this.modeOmega_arr[r][s]*(N+1)*(N+1)))*vx_arr[i][j]*sineProduct
                        muY[r][s] += (4/((N+1)*(N+1)))*sy_arr[i][j]*sineProduct
                        nuY[r][s] += (4/(this.modeOmega_arr[r][s]*(N+1)*(N+1)))*vy_arr[i][j]*sineProduct
                        //counter += 1;     //testing only
                    }//end for j loop
                } //end for i loop
                this.modeAmpliX_arr[r][s] = Math.sqrt( muX[r][s]*muX[r][s] + nuX[r][s]*nuX[r][s] );
                this.modeAmpliY_arr[r][s] = Math.sqrt( muY[r][s]*muY[r][s] + nuY[r][s]*nuY[r][s] );
                //this.modePhase_arr[r] = Math.atan2( -nu[ r ], mu[ r ]) ;
            }//end for s loop
        } //end for r
        this.modesChanged = true;
        this.updateView();
        //this.modesChanged = false;

        //for testing only: test shows takes 0.024 s to loop through 10000 times with no sine function calc. T
        //Takes only 0.031 s with sine function calc.
        //var t:Number = (getTimer() - t0)/1000;
        //trace("Model2.computeAmplitudesAndPhases. Counter = "+ counter+"   time is "+ t + " s");
    }//end computeModeAmplitudesAndPhases();


    private function singleStep():void{
        var currentTime:Number = getTimer() / 1000;              //flash.utils.getTimer()
        var realDt: Number = currentTime - this.lastTime;
        this.lastTime = currentTime;
        //time step must not exceed 0.04 seconds.
        //If time step < 0.04 s, then sim uses time-based animation.  Else uses frame-based animation
        if ( realDt < 0.06 ) {
            this.dt = this.tRate * realDt;
        }
        else {
            this.dt = this.tRate * 0.06;
        }
        this.t += this.dt;

        if( this._verletOn ){
           this.singleStepVerlet();
        }else{
            this.singleStepExact();
        }
        this.updateView();
    }

    private function singleStepVerlet():void{       //velocity verlet algorithm
        //trace("Model2.t = "+ this.t);
//        if(this._grabbedMassIndices[0] == 2 && this._grabbedMassIndices[1] == 2) {
//            trace("Model2.singleStepVerlet.  massView [2,2] grabbed");
//        }
        for(var i:int = 1; i <= this._N; i++){
            for(var j:int = 1; j <= this._N; j++){
                if( this._grabbedMassIndices[0] != i || this._grabbedMassIndices[1] != j ){
                    //this.sx_arr[]
                    sx_arr[i][j] = sx_arr[i][j] + vx_arr[i][j] * dt + (1 / 2) * ax_arr[i][j] * dt * dt;
                    sy_arr[i][j] = sy_arr[i][j] + vy_arr[i][j] * dt + (1 / 2) * ay_arr[i][j] * dt * dt;
                    axPre_arr[i][j] = ax_arr[i][j];   //store current accelerations for next step
                    ayPre_arr[i][j] = ay_arr[i][j];

                }

            }//end for j loop
        }//end for i loop
//        if(this.t > this.tInt ){
//            this.tInt += 1;
//            trace("i = "+this._grabbedMassIndices[0]+"   j = "+this._grabbedMassIndices[1])
//            trace("Model2.singleStepVerlet.  t = "+ this.tInt + "   sx_arr[4][3] = "+ sx_arr[4][3]);
//        }

        for(i = 1; i <= this._N; i++){
            for(j = 1; j <= this._N; j++){
                if( this._grabbedMassIndices[0] != i || this._grabbedMassIndices[1] != j ){
                    this.ax_arr[i][j] = (this.k/this.m)*(sx_arr[i][j+1] + sx_arr[i][j-1] - 2*sx_arr[i][j] + sx_arr[i+1][j] + sx_arr[i-1][j] - 2*sx_arr[i][j]);		//post-acceleration
                    this.ay_arr[i][j] = (this.k/this.m)*(sy_arr[i+1][j] + sy_arr[i-1][j] - 2*sy_arr[i][j] + sy_arr[i][j+1] + sy_arr[i][j-1] - 2*sy_arr[i][j]);
                    vx_arr[i][j] = vx_arr[i][j] + 0.5 * (this.axPre_arr[i][j] + ax_arr[i][j]) * dt;
                    vy_arr[i][j] = vy_arr[i][j] + 0.5 * (this.ayPre_arr[i][j] + ay_arr[i][j]) * dt;
                }
            }//end for j loop
        }//end for i loop
    }//end singleStepVerlet()


    private function singleStepExact():void{
        var pi:Number = Math.PI;
        for(var i:int = 1; i <= this._N; i++ ){
            for( var j:int = 1; j <= this._N;  j++ ){
                sx_arr[i][j] = 0;
                sy_arr[i][j] = 0;
                for( var r:int = 1; r <= this._N; r++ ){
                    for(var s:int =1; s <= this._N; s++ ){
                        if(this._xModes){
                            this.sx_arr[i][j] += modeAmpliX_arr[r][s]*Math.sin( i*r*pi/(_N+1))*Math.sin(j*s*pi/(_N+1))*Math.cos(modeOmega_arr[r][s]*this.t - modePhase_arr[r][s]);
                        }else{
                            this.sy_arr[i][j] += modeAmpliY_arr[r][s]*Math.sin( i*r*pi/(_N+1))*Math.sin(j*s*pi/(_N+1))*Math.cos(modeOmega_arr[r][s]*this.t - modePhase_arr[r][s]);
                        }
                    }//end for s
                }//end for r
            }//end for j
        }//end for i
    }//end singleStepExact()


    public function singleStepWhenPaused():void{
        this.dt = this.tRate * 0.02;
        this.t += this.dt;
        this.singleStepVerlet( );
        updateView();
    }

    public function registerView( view: View2 ): void {
        this.view = view;    //only one view, so far
    }

    public function updateView(): void {
        if(nChanged){
            this.view.setNbrMasses();
            this.myMainView.myButtonArrayPanel.setNbrButtons()
            this.nChanged = false;
        }
        if( modesZeroed ){
            this.myMainView.myButtonArrayPanel.setNbrButtons();
            this.modesZeroed = false;
        }
        if( modesChanged ){
            this.myMainView.myButtonArrayPanel.setButtonColors();
            this.modesChanged = false;
        }
        this.view.update();
    }//end updateView()

}
}
