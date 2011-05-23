/**
 * General purpose label to be used in control panels, etc.
 * Created by Michael Dubson
 * Date: 5/22/11
 * Time: 2:12 PM
 */
package edu.colorado.phet.resonance {
import flash.display.Graphics;
import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;
import flash.text.TextFormatAlign;

public class NiceLabel extends Sprite {
    private var label_txt: TextField;
    private var tFormat: TextFormat;
    private var fontSize: int;
    private var fontColor:Number;

    public function NiceLabel( fontSize:int = 15, labelText_str:String = "Label") {
        this.fontSize = fontSize;
        this.fontColor = 0x000000;       //default color is black
        this.label_txt = new TextField();
        this.label_txt.text = labelText_str;
        this.tFormat = new TextFormat();
        this.setTextFormat();
        this.setLabel();
        this.addChild(this.label_txt)
    } //end of constructor


    public function setTextFormat(): void {
        this.tFormat.align = TextFormatAlign.LEFT;
        this.tFormat.font = "Arial";
        this.tFormat.color = this.fontColor;
        this.tFormat.size = this.fontSize;
        //this.label_txt.setTextFormat( this.tFormat );
        //trace("ControlPanel.setTFormat buttonWidth = "+this.myButtonWidth );
    }

    private function setLabel(): void {
        this.label_txt.selectable = false;
        this.label_txt.autoSize = TextFieldAutoSize.LEFT;
        //this.label_txt.text = "Label";
        this.label_txt.setTextFormat( this.tFormat );
        //this.label_txt.x = 0;// -0.5*this.label_txt.width;
        this.label_txt.y = 0;
        //this.addChild( this.label_txt );
        //this.label_txt.border = true;      //for testing only
    }//end createLabel()

    public function setText(labelText_str:String):void{
        this.label_txt.text = labelText_str;
        //this.setLabel();
        this.label_txt.setTextFormat( this.tFormat );

        var pixWidth:Number = this.label_txt.textWidth;
        var pixHeight:Number = this.label_txt.textHeight;
        //this.label_txt.x = 0; //-pixWidth/2;
        trace("NiceLabel.label_txt.x = "+this.label_txt.x);
        this.drawBounds( pixWidth,  pixHeight );
    }//end setText()

    public function setFontSize( fontSize:int ):void{
        this.fontSize = fontSize;
        this.tFormat.size = this.fontSize;
        this.label_txt.setTextFormat( this.tFormat );
    }

    public function setFontColor( fontColor:Number):void{
        this.fontColor = fontColor;
        this.tFormat.color = this.fontColor;
        this.label_txt.setTextFormat( this.tFormat );
    }

    //for testing purposes only
    private function drawBounds(w:Number,  h:Number):void{
      var g:Graphics = this.graphics;
      g.clear();
      g.lineStyle(1, 0x000000, 0);
      //g.clear();
      g.beginFill(0xff0000);
      g.drawRect(0, 0, w, h);
      g.endFill();
      //trace("NiceLabel.drawBounds this.width = "+this.width);
    }

}//end of class
} //end of package
