package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Substance;
import edu.colorado.phet.densityandbuoyancy.view.units.Units;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import mx.containers.Grid;
import mx.containers.HBox;
import mx.containers.Panel;
import mx.containers.VBox;
import mx.controls.ComboBox;
import mx.controls.Label;
import mx.events.ListEvent;

public class CustomObjectPropertiesPanel extends DensityVBox{
    private var grid:Grid = new Grid();
    private var densityObject:DensityObject;
    private var comboBox:ComboBox;

    public function CustomObjectPropertiesPanel(densityObject:DensityObject, units:Units) {
        super();
        this.densityObject = densityObject;

        //TODO: remove listeners from former density object
        this.densityObject = densityObject;

        //TODO: connect mass values
        //        function massListener():void {densityObject.setMass(iDensityObject.getMass().value);}
        //        iDensityObject.getMass().addListener(massListener);
        function volumeListener():void {
            densityObject.setVolume(densityObject.getVolume());
        }

        densityObject.getVolumeProperty().addListener(volumeListener);
        function densityListener():void {
            densityObject.setDensity(densityObject.getDensity());
        }

        densityObject.getDensityProperty().addListener(densityListener);

        grid.addChild(new PropertyEditor(densityObject.getMassProperty(), DensityConstants.MIN_MASS, DensityConstants.MAX_MASS, units.massUnit));
        grid.addChild(new PropertyEditor(densityObject.getVolumeProperty(), DensityConstants.MIN_VOLUME, DensityConstants.MAX_VOLUME, units.volumeUnit));
        grid.addChild(new DensityEditor(densityObject.getDensityProperty(), DensityConstants.MIN_DENSITY, DensityConstants.MAX_DENSITY, units.densityUnit, densityObject));

        comboBox = new ComboBox();
        comboBox.dataProvider = [Substance.WOOD,Substance.WATER_BALLOON,Substance.LEAD,Substance.CUSTOM];
        comboBox.labelField = "name";//uses the "name" get property on Substance to identify the name
        function myListener():void {
            trace("comboBox.selectedItem=" + comboBox.selectedItem);
            if (comboBox.selectedItem.isCustom()) {
                if (!densityObject.getSubstance().isCustom()) {
                    densityObject.substance = new Substance(FlexSimStrings.get("customObject.custom", "Custom"), densityObject.getDensity(), true);
                }
            } else {
                densityObject.substance = Substance(comboBox.selectedItem);
            }
        }

        comboBox.addEventListener(ListEvent.CHANGE, myListener);
        densityObject.addSubstanceListener(function f():void {
            if (densityObject.getSubstance().isCustom()) {
                comboBox.selectedItem = Substance.CUSTOM;
            }
            else {
                comboBox.selectedItem = densityObject.getSubstance();
            }
        });

        var label:Label = new Label();
        label.text=FlexSimStrings.get("customObject.material", "Material");
        
        var comboBoxPanel:HBox = new HBox();
        comboBoxPanel.addChild(label);
        comboBoxPanel.addChild(comboBox);
        addChild(comboBoxPanel);

        addChild(grid);
    }

}
}