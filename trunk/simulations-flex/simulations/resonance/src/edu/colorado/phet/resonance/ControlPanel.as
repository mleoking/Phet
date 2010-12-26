﻿package edu.colorado.phet.resonance {

import flash.display.*;
import flash.events.Event;
import flash.events.MouseEvent;

import mx.containers.Canvas;
import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.Button;
import mx.controls.HSlider;
import mx.controls.Label;
import mx.controls.RadioButton;
import mx.controls.RadioButtonGroup;

public class ControlPanel extends Canvas {

    private var myMainView: MainView;
    private var shakerModel: ShakerModel;
    private var background: VBox;
    private var radioButtonBox: HBox;
    private var innerBckgrnd: VBox;
    private var dampingSlider: HSlider;
    private var nbrResonatorsSlider: HSlider;
    private var gravityOnOff_rbg: RadioButtonGroup;

    private var gravity_lbl: Label;
    private var resonatorNbr_lbl: Label;
    private var mSlider: HSlider;
    private var kSlider: HSlider;
    private var freq_lbl: Label;
    private var resetButton: Button;

    private var selectedResonatorNbr: int;	//index number of currently selected resonator

    public function ControlPanel( myMainView: MainView, model: ShakerModel ) {
        super();
        this.myMainView = myMainView;
        this.shakerModel = model;
        this.init();
    }//end of constructor

    public function addSprite( s: Sprite ): void {
        this.addChild( new SpriteUIComponent( s ) );
    }

    public function init(): void {

        this.background = new VBox();
        this.background.setStyle( "backgroundColor", 0x66ff66 );
        this.background.percentWidth = 100;
        this.background.percentHeight = 100;
        this.background.setStyle( "borderStyle", "solid" )
        this.background.setStyle( "borderColor", 0x009900 );
        this.background.setStyle( "cornerRadius", 15 );
        this.background.setStyle( "borderThickness", 8 );
        this.background.setStyle( "paddingTop", 30 );
        this.background.setStyle( "paddingBottom", 20 );
        this.background.setStyle( "paddingRight", 7 );
        this.background.setStyle( "paddingLeft", 7 );
        this.background.setStyle( "verticalGap", 10 );
        with ( this.background ) {
            setStyle( "horizontalAlign", "center" );
        }

        this.innerBckgrnd = new VBox();
        with ( this.innerBckgrnd ) {
            setStyle( "backgroundColor", 0x00ff00 );
            percentWidth = 100;
            percentHeight = 100;
            setStyle( "borderStyle", "solid" );
            setStyle( "borderColor", 0x0000ff );
            setStyle( "cornerRadius", 8 );
            setStyle( "borderThickness", 3 );
            setStyle( "paddingTop", 5 );
            setStyle( "paddingBottom", 5 );
            setStyle( "paddingRight", 5 );
            setStyle( "paddingLeft", 5 );
            setStyle( "verticalGap", 0 );
            // setStyle("horizontalAlign" , "center");
        }


        //HorizontalSlider(action:Function, lengthInPix:int, minVal:Number, maxVal:Number, detented:Boolean = false, nbrTics:int = 0)
        this.dampingSlider = new HSlider(); //new HorizontalSlider( setDamping, 100, 0.05, 1 );
        this.formatSlider(this.dampingSlider);
        with ( this.dampingSlider ) {
            minimum = 0.05;
            maximum = 5;
            labels = ["", "damping", ""];
        }

        this.dampingSlider.addEventListener( Event.CHANGE, setDamping );

        this.nbrResonatorsSlider = new HSlider();
        this.formatSlider(this.nbrResonatorsSlider);
        with ( this.nbrResonatorsSlider ) {
            minimum = 1;
            maximum = 10;
            labels = ["", "Resonators", ""];
            snapInterval = 1;
            tickInterval = 1;
        }

        this.nbrResonatorsSlider.addEventListener( Event.CHANGE, onChangeNbrResonators );

        this.radioButtonBox = new HBox();

        this.gravity_lbl = new Label();
        this.gravity_lbl.text = "Gravity";
        this.gravity_lbl.setStyle( "fontSize", 14 );

        this.gravityOnOff_rbg = new RadioButtonGroup();
        var rb1: RadioButton = new RadioButton();
        var rb2: RadioButton = new RadioButton();
        rb1.group = gravityOnOff_rbg;
        rb2.group = gravityOnOff_rbg;
        rb1.label = "on";
        rb2.label = "off";
        rb1.value = 1;
        rb2.value = 0;
        rb1.selected = false;
        rb2.selected = true;
        rb1.setStyle( "fontSize", 14 );
        rb2.setStyle( "fontSize", 14 );
        rb1.setStyle( "horizontalGap", 0 );
        rb2.setStyle( "horizontalGap", 0 );

        this.gravityOnOff_rbg.addEventListener( Event.CHANGE, clickGravity );

        this.resonatorNbr_lbl = new Label();

        with ( this.resonatorNbr_lbl ) {
            text = "Resonator #";
            setStyle( "fontFamily", "Arial" );
            setStyle( "fontSize", 14 );
            percentWidth = 90;
            setStyle( "textAlign", "center" );
        }


        this.mSlider = new HSlider();
        this.formatSlider(this.mSlider);
        with ( this.mSlider ) {
            minimum = 0.2;
            maximum = 4;
            labels = ["", "mass", ""];
            // This doesn't work: setStyle("labelPlacement", "bottom");
        }
        this.mSlider.addEventListener( Event.CHANGE, onChangeM );

        this.kSlider = new HSlider();
        this.formatSlider(this.kSlider);
        with ( this.kSlider ) {
            minimum = 10;
            maximum = 400;
            labels = ["", "spring constant", ""];
        }
        ;
        this.kSlider.addEventListener( Event.CHANGE, onChangeK );

        this.freq_lbl = new Label();
        with ( this.freq_lbl ) {
            text = "frequency = #";
            setStyle( "fontFamily", "Arial" );
            setStyle( "fontSize", 14 );
            percentWidth = 90;
            setStyle( "textAlign", "center" );
        }
        ;

        this.resetButton = new Button();
        with ( this.resetButton ) {
            label = " Reset All "
            buttonMode = true;
        }
        this.resetButton.addEventListener( MouseEvent.MOUSE_UP, resetResonators );

        this.addChild( this.background );
        this.background.addChild( nbrResonatorsSlider );
        this.background.addChild( dampingSlider );
        this.background.addChild( radioButtonBox );
        this.radioButtonBox.addChild( gravity_lbl );
        this.radioButtonBox.addChild( rb1 );
        this.radioButtonBox.addChild( rb2 );

        this.innerBckgrnd.addChild( this.resonatorNbr_lbl );
        this.innerBckgrnd.addChild( this.mSlider );
        this.innerBckgrnd.addChild( this.kSlider );
        this.innerBckgrnd.addChild( this.freq_lbl );
        this.background.addChild( innerBckgrnd );
        this.background.addChild( this.resetButton );

    } //end of init()

    function formatSlider( mySlider: HSlider ): void {
        mySlider.buttonMode = true;
        mySlider.liveDragging = true;
        mySlider.setStyle( "labelOffset", 25 );
    };


    public function setResonatorIndex( rNbr: int ): void {
        this.selectedResonatorNbr = rNbr;
        var rNbr_str: String = rNbr.toFixed( 0 );
        this.resonatorNbr_lbl.text = "Resonator " + rNbr_str;
        var m: Number = this.shakerModel.resonatorModel_arr[rNbr - 1].getM();
        //trace("ControlPanel.setResonatorIndex. m = "+m);
        this.mSlider.value = m;
        var k: Number = this.shakerModel.resonatorModel_arr[rNbr - 1].getK();
        //trace("ControlPanel.setResonatorIndex. k = "+k);
        this.kSlider.value = k;
        this.setFreqLabel();
        //var resFreq:Number = this.shakerModel.resonatorModel_arr[rNbr - 1].getF0();
        //var fNbr_str:String =  resFreq.toFixed(2);
        //this.freq_lbl.text = "frequency = " + fNbr_str;
    }

    private function setFreqLabel(): void {
        var rNbr: int = this.selectedResonatorNbr;
        var resFreq: Number = this.shakerModel.resonatorModel_arr[rNbr - 1].getF0();
        var resFreq_str: String = resFreq.toFixed( 2 );
        this.freq_lbl.text = "frequency = " + resFreq_str + " Hz";
    }

    public function setDamping( evt: Event ): void {
        var b: Number = this.dampingSlider.value;
        this.shakerModel.setB( b );
    }

    private function onChangeNbrResonators( evt: Event ): void {
        var nbrR: int = this.nbrResonatorsSlider.value;
        //trace("ControlPanel.setNbrResonators called. nbrR = " + nbrR);
        this.setNbrResonators( nbrR );
    }

    private function clickGravity( evt: Event ): void {
        var val: Object = this.gravityOnOff_rbg.selectedValue;
        if ( val == 1 ) {
            this.shakerModel.setG( 5 );
            trace( "1" );
        }
        else {
            this.shakerModel.setG( 0 );
            trace( "2" );
        }
    }

    public function setNbrResonators( nbrR: int ): void {
        this.myMainView.setNbrResonators( nbrR );
    }

    private function onChangeM( evt: Event ): void {
        this.setMass();
    }

    public function setMass(): void {
        var indx: int = this.selectedResonatorNbr - 1;
        var m: Number = this.mSlider.value;
        this.shakerModel.resonatorModel_arr[indx].setM( m );
        this.setFreqLabel();
        //trace("ControlPanel.setMass() mass = "+ m);
    }

    private function onChangeK( evt: Event ): void {
        this.setK();
    }

    public function setK(): void {
        var indx: int = this.selectedResonatorNbr - 1;
        var k: Number = this.kSlider.value;
        this.shakerModel.resonatorModel_arr[indx].setK( k );
        this.setFreqLabel();
        //trace("ControlPanel.setK() k = "+ k);
    }

    private function resetResonators( evt: MouseEvent ): void {
        this.shakerModel.resetInitialResonatorArray();
        //this.setResonatorIndex( this.selectedResonatorNbr );
        //trace("ControlPanel.resetResonators() called.");

    }

}//end of class

}//end of package