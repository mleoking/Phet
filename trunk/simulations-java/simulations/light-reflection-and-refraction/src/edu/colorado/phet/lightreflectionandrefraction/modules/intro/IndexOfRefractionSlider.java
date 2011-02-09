// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.util.Hashtable;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.HorizontalLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;

/**
 * @author Sam Reid
 */
public class IndexOfRefractionSlider extends LinearValueControl {
    public IndexOfRefractionSlider( final Property<Medium> medium, final Property<Function1<Double, Color>> colorMappingFunction, String text ) {
        super( 1, 1.6, 1.3, text, "0.00", "", new HorizontalLayoutStrategy() );
        setTickLabels( new Hashtable<Object, Object>() {{
            put( LRRModel.N_AIR, new TickLabel( "Air" ) );
            put( LRRModel.N_WATER, new TickLabel( "Water" ) );
            put( LRRModel.N_GLASS, new TickLabel( "Glass" ) );
//            put( LRRModel.N_DIAMOND, new TickLabel( "Diamond" ) );//commented out while we determine how to handle overlapping labels
        }} );
        getSlider().setMinorTickSpacing( 0 );
        getSlider().setMajorTickSpacing( 0 );
//        addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                medium.setValue( new Medium( medium.getValue().getShape(), getValue(), colorMappingFunction.getValue().apply( getValue() ) ) );
//            }
//        } );
    }
}
