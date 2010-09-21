package edu.colorado.phet.densityandbuoyancy.view.away3d {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;

import edu.colorado.phet.densityandbuoyancy.view.away3d.Sprite3D;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class TextFieldMesh extends Sprite3D {
    private var _textField:TextField;
    private var format:TextFormat;

    public function TextFieldMesh(text:String, format:TextFormat = null) {
        this.format = format;

        const sprite:Sprite = new Sprite();
        _textField = new TextField();
        _textField.autoSize = TextFieldAutoSize.LEFT;

        setTextNoResize(text);

        sprite.addChild(_textField);

        super(sprite);
        drawBackground();
    }

    protected function drawBackground():void {
        sprite.graphics.clear();
        sprite.graphics.beginFill(DensityConstants.CONTROL_PANEL_COLOR, 0.6);
        sprite.graphics.drawRect(0, 0, _textField.width, _textField.height);
        sprite.graphics.endFill();
    }

    public function set text(t:String):void {
        setTextNoResize(t);
        drawBackground();
        resize();
    }

    private function setTextNoResize(t:String):void {
        _textField.text = t;
        if (format != null) {
            _textField.setTextFormat(format);
        }
    }
}
}