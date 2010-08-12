package edu.colorado.phet.densityflex.components {
import edu.colorado.phet.densityflex.model.DensityObject;

import edu.colorado.phet.densityflex.model.Substance;

import mx.containers.Grid;
import mx.containers.Panel;
import mx.controls.ComboBox;

public class CustomObjectPropertiesPanel extends Panel {
    private var grid:Grid = new Grid();
    private var densityObject:DensityObject;
    private var comboBox:ComboBox;

    public function CustomObjectPropertiesPanel(densityObject:DensityObject) {
        super();
        this.title = "Properties";
        this.densityObject=densityObject;
        
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
        
        grid.addChild(new PropertyEditor(densityObject.getMassProperty(),1,10000));
        grid.addChild(new PropertyEditor(densityObject.getVolumeProperty(),1,100));
        grid.addChild(new DensityEditor(densityObject.getDensityProperty(),Substance.WOOD.getDensity() * 0.5,Substance.LEAD.getDensity() * 1.1));//have a maximum a bit beyond lead so students don't think lead is the most dense thing in the world));

        comboBox = new ComboBox();
        comboBox.dataProvider = [Substance.WOOD,Substance.WATER_BALLOON,Substance.LEAD,Substance.CUSTOM];
        comboBox.labelField = "name";
        function myListener():void{
            trace("comboBox.selectedItem="+comboBox.selectedItem);
            if (comboBox.selectedItem.isCustom()){
                if (!densityObject.getSubstance().isCustom()){
                    densityObject.substance = new Substance("Custom",densityObject.getDensity());
                }
            }   else{
                densityObject.setDensity(comboBox.selectedItem.getDensity());
            }
        }
        comboBox.addEventListener("change",myListener);
        densityObject.addSubstanceListener(function f():void {
            if (densityObject.getSubstance().isCustom()) {
                comboBox.selectedItem = Substance.CUSTOM;
            }
            else {
                comboBox.selectedItem = densityObject.getSubstance();
            }
        });
        addChild(comboBox);

        addChild(grid);
    }

}
}