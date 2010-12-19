//MainView contains all views, acts as mediator, communication hub for views
package edu.colorado.phet.resonance {
//import edu.colorado.phet.flashcommon.FlashCommonCS4;

import flash.display.*;

import mx.containers.Canvas;

//import flash.geom.ColorTransform;

public class MainView extends Canvas {
    var myShakerModel: ShakerModel;
    var myShakerView: ShakerView;
    var myControlPanel: ControlPanel;
    var phetLogo: Sprite;
    var stageH: Number;
    var stageW: Number;

    public function MainView( myShakerModel: ShakerModel, stageW: Number, stageH: Number ) {
        percentWidth = 100;
        percentHeight = 100;
        this.stageH = stageH;
        this.stageW = stageW;
        this.myShakerModel = myShakerModel;
        this.myShakerView = new ShakerView( this, myShakerModel );
        this.addChild( new SpriteUIComponent( myShakerView ) );
        this.myShakerView.x = 0.40 * stageW;
        this.myShakerView.y = 0.6 * stageH;

        this.myControlPanel = new ControlPanel( this, myShakerModel );
        this.addChild(  myControlPanel  );
        //this.myControlPanel.right = 10;    //does not work, "right" is a style property
        //this.myControlPanel.setStyle("right", 10);    //this works, but forces the control panel on the far right
        this.myControlPanel.x = 0.8 * stageW; //- 3 * this.myControlPanel.width;
        this.myControlPanel.y = 0.1 * stageH;

        this.phetLogo = new PhetIcon();
        this.addChild( new SpriteUIComponent( phetLogo ) );
        this.phetLogo.x = stageW - 1.5 * this.phetLogo.width;
        this.phetLogo.y = stageH - 1.5 * this.phetLogo.height;

    }//end of constructor

    public function setNbrResonators( nbrR: int ): void {
        this.myShakerModel.setNbrResonators( nbrR );
        this.myShakerView.setNbrResonators( nbrR );
    }

    private function waitForGraphicsLoad(): void {
        //trace("MainView.momentumView.scale_slider.height = "+this.momentumView.scale_slider.height);
    }

}//end of class
}//end of package