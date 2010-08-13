package edu.colorado.phet.densityflex.components {
import edu.colorado.phet.densityflex.model.NumericProperty;

import edu.colorado.phet.densityflex.view.units.Unit;

import flash.display.DisplayObject;

import mx.containers.GridItem;
import mx.containers.GridRow;
import mx.controls.HSlider;
import mx.controls.Label;
import mx.controls.TextInput;
import mx.events.FlexEvent;
import mx.events.SliderEvent;

public class PropertyEditor extends GridRow {
    private var property:NumericProperty;
    public static const SLIDER_WIDTH:Number = 250;

    public function PropertyEditor(property:NumericProperty, minimimum:Number, maximum:Number,unit:Unit) {
        super();
        this.property = property;

        var label:Label = new Label();
        label.text = property.name;
        addGridItem(label);

        addGridItem(createSlider(property, minimimum, maximum));

        const textField:TextInput = new TextInput();
        textField.width = 100;
        textField.restrict = ".0-9";
        function updateText():void {
            textField.text = unit.fromSI(property.value).toFixed(2);
        }

        updateText();
        const listener:Function = function myfunction():void {
            property.value = unit.toSI(Number(textField.text));
        };
        textField.addEventListener(FlexEvent.VALUE_COMMIT, listener);
        textField.addEventListener(FlexEvent.ENTER, listener);
        property.addListener(updateText);
        addGridItem(textField);

        var unitsLabel:Label = new Label();
        unitsLabel.text = unit.name;
        addGridItem(unitsLabel);
    }

    protected function createSlider(property:NumericProperty, minimum:Number, maximum:Number):HSlider {
        const slider:HSlider = new HSlider();
        slider.width = SLIDER_WIDTH;
        slider.minimum = minimum;
        slider.maximum = maximum;
        slider.liveDragging = true;
        slider.thumbCount = 1;
        function sliderDragHandler(event:SliderEvent):void {
            property.value = event.value;
        }

        slider.addEventListener(SliderEvent.THUMB_DRAG, sliderDragHandler);
        function updateSlider():void {
            slider.value = property.value;
            try {
                slider.getThumbAt(0).alpha = Math.max(0.25, //This is the minimum alpha that will be shown.  Beyond 0.25 is too hard to see anything.
                        Math.min(1, slider.maximum / property.value) //The more the value goes above the slider's maximum, make more transparent.  But keep alpha =1 if it is in the slider range.
                        );
            } catch(exception:Error) {

            }
        }

        updateSlider();
        property.addListener(updateSlider);
        return slider;
    }

    private function addGridItem(displayObject:DisplayObject):void {
        const item:GridItem = new GridItem();
        item.addChild(displayObject);
        addChild(item);
    }
}
}