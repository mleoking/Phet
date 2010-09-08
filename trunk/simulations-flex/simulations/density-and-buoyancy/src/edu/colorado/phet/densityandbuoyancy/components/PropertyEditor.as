package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.NumericProperty;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;
import edu.colorado.phet.flashcommon.MathUtil;

import flash.display.DisplayObject;
import flash.events.FocusEvent;

import mx.containers.GridItem;
import mx.containers.GridRow;
import mx.controls.Label;
import mx.controls.TextInput;
import mx.events.FlexEvent;
import mx.events.SliderEvent;

public class PropertyEditor extends GridRow {
    private var property:NumericProperty;
    public static const SLIDER_WIDTH:Number = 280;
    private static const FONT_SIZE:Number = 12;

    protected const textField:TextInput = new TextInput();

    /**
     *
     * @param property
     * @param minimum specified in SI
     * @param maximum specified in SI
     * @param unit
     * @param densityObject
     */
    public function PropertyEditor(property:NumericProperty, minimum:Number, maximum:Number, unit:Unit, densityObject:DensityObject) {
        super();
        this.property = property;

        var label:Label = new Label();
        label.text = property.name;
        label.setStyle(DensityConstants.FLEX_FONT_SIZE, FONT_SIZE);
        label.setStyle(DensityConstants.FLEX_FONT_WEIGHT, DensityConstants.FLEX_FONT_BOLD);
        addGridItem(label);

        addGridItem(createSlider(property, minimum, maximum, unit, densityObject));

        textField.setStyle(DensityConstants.FLEX_FONT_SIZE, FONT_SIZE);
        textField.width = DensityConstants.SLIDER_READOUT_TEXT_FIELD_WIDTH;
        textField.restrict = ".0-9";//TODO: does this handle languages that use a comma instead of a decimal place?
        function updateText():void {
            textField.text = unit.fromSI(property.value).toFixed(DensityConstants.NUMBER_OF_DECIMAL_PLACES);
        }

        function updateModelFromTextField():void {
            const number:Number = unit.toSI(Number(textField.text));
            property.value = MathUtil.clamp(minimum, number, maximum);
            if (number < minimum || number > maximum)
                updateText();
        }

        updateText();
        const listener:Function = function myfunction():void {
            if (focusManager != null) { //Have to do a null check because it can be null if the component is not in the scene graph?
                if (focusManager.getFocus() == textField) {//Only update the model if the user is editing the text field, otherwise there are loops that cause errant behavior
                    updateModelFromTextField();
                }
            }
        };

        textField.addEventListener(FocusEvent.FOCUS_OUT, updateModelFromTextField);
        textField.addEventListener(FlexEvent.VALUE_COMMIT, listener);
        textField.addEventListener(FlexEvent.ENTER, listener);

        property.addListener(updateText);
        addGridItem(textField);

        var unitsLabel:Label = new Label();
        unitsLabel.setStyle(DensityConstants.FLEX_FONT_SIZE, FONT_SIZE);
        unitsLabel.text = unit.name;
        addGridItem(unitsLabel);
    }

    //The density slider requires a reference to the density object in order to bound the volume when necessary.
    //This is because when selecting Styrofoam or other non-dense objects, then moving the mass slider to maximum, 
    //The volume increases dramatically, making the object larger than the pool size.
    protected function createSlider(property:NumericProperty, minimum:Number, maximum:Number, unit:Unit, densityObject:DensityObject):SliderDecorator {
        const slider:SliderDecorator = new SliderDecorator();
        slider.setStyle("dataTipOffset", 0);//Without this fix, data tips appear very far from the tip of the slider thumb, see http://blog.flexexamples.com/2007/11/03/customizing-a-slider-controls-data-tip/
        slider.sliderThumbClass = MySliderThumb;
        slider.sliderWidth = SLIDER_WIDTH;

        slider.minimum = unit.fromSI(minimum);
        slider.maximum = unit.fromSI(maximum);
        slider.liveDragging = true;
        function sliderDragHandler(event:SliderEvent):void {
            var newValue:Number = unit.toSI(event.value);
            if (newValue > 3 && densityObject.material.equals(Material.STYROFOAM)) {
                newValue = 3;
                slider.value = 3;
            }
            property.value = newValue;
        }

        //This different functionality is necessary to support track presses and keyboard handling
        //Otherwise, when one slider reaches its min or max, it sets that value back on the model
        function trackPressAndKeyboardHandler(event:SliderEvent):void {
            if (event.value != slider.maximum && event.value != slider.minimum) {
                sliderDragHandler(event);
            }
        }

        slider.addSliderEventListener(SliderEvent.THUMB_DRAG, sliderDragHandler);
        slider.addSliderEventListener(SliderEvent.CHANGE, trackPressAndKeyboardHandler);
        function updateSlider():void {
            const setValue:Number = unit.fromSI(property.value);
            slider.value = setValue;
            try {
                var alphaValue:Number = 1;
                if (setValue > slider.maximum) {
                    alphaValue = 0.25;
                }
                slider.getThumbAt(0).alpha = alphaValue;
                //Keeping around this code until discussion about continuous alpha slider knob is concluded.
                //                        Math.max(0.25, //This is the minimum alpha that will be shown.  Beyond 0.25 is too hard to see anything.
                //                        Math.min(1, slider.maximum / setValue) //The more the value goes above the slider's maximum, make more transparent.  But keep alpha =1 if it is in the slider range.
                //                        );
            }
            catch(exception:Error) {

            }
        }

        updateSlider();
        property.addListener(updateSlider);


        //        var firstThumb:SliderThumb = SliderThumb(slider.getThumbAt(0));
        //        var t= firstThumb.getExplicitOrMeasuredWidth()/2;
        //        const textField:TextField = new TextField();
        //        textField.text="hello";
        //        textField.y=-20;
        //        slider.addChild(textField);
        //        
        return slider;
    }

    private function addGridItem(displayObject:DisplayObject):void {
        const item:GridItem = new GridItem();
        item.addChild(displayObject);
        addChild(item);
    }

    protected override function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
        super.updateDisplayList(unscaledWidth, unscaledHeight);
    }
}
}