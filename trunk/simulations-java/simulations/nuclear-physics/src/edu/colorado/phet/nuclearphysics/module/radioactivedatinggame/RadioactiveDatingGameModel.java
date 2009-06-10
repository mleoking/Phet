/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class defines a model (in the model-view-controller paradigm) that
 * defines a set a geological strata (or layers) containing objects that can
 * be dated using radiometric means.
 *
 * @author John Blanco
 */
public class RadioactiveDatingGameModel {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	private static final double TOTAL_DEPTH_OF_STRATA = 16;
	private static final double NUMBER_OF_STRATA = 5;
	private static final double NOMINAL_STRATUM_DEPTH = TOTAL_DEPTH_OF_STRATA / NUMBER_OF_STRATA; 
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

	private ArrayList<DatableObject> _datableObjects = new ArrayList<DatableObject>();
	private ArrayList<Stratum> _strata = new ArrayList<Stratum>();
	private RadiometricDatingMeter _meter;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public RadioactiveDatingGameModel()
    {
    	// Add the strata to the model.  Add the top layer first, then loop
    	// through adding the rest.
    	
    	Stratum stratum;
        stratum = new Stratum(new Stratum.LayerLine(0), new Stratum.LayerLine(NOMINAL_STRATUM_DEPTH));
        _strata.add(stratum);
        
        for (int i = 1; i < NUMBER_OF_STRATA; i++){
        	// Add the next stratum.
            stratum = new Stratum( stratum.getBottomLine(), new Stratum.LayerLine((i+1) * NOMINAL_STRATUM_DEPTH ));
            _strata.add( stratum );
        }
        
        // Add the datable objects.
        // Params:                             name, image file, location(x, y), size, rotation angle (radians), age (ms)
        _datableObjects.add(new DatableObject("House", "house.png", new Point2D.Double(8, 4), 6, 0, MultiNucleusDecayModel.convertYearsToMs(75)));
        _datableObjects.add(new DatableObject("Trilobyte", "trilobyte_fossil.png", new Point2D.Double(0, -11), 3.5, 0, MultiNucleusDecayModel.convertYearsToMs(500E6)));
    	_datableObjects.add(new DatableObject("Animal Skull", "skull_animal.png", new Point2D.Double(-21, 2), 3, Math.PI/4, MultiNucleusDecayModel.convertYearsToMs(25)));
    	_datableObjects.add(new DatableObject("Living Tree", "tree_1.png", new Point2D.Double(-16, 5), 5.5, 0, 0));
    	_datableObjects.add(new DatableObject("Distant Living Tree", "tree_1.png", new Point2D.Double(0, 3.5), 2, 0, 0));
    	_datableObjects.add(new DatableObject("Fish Fossil", "fish_fossil.png", new Point2D.Double(-15, -8), 7, 0, MultiNucleusDecayModel.convertYearsToMs(10E6)));
    	_datableObjects.add(new DatableObject("Dead Tree", "dead_tree.png", new Point2D.Double(23, 1.5), 6, Math.PI/2, MultiNucleusDecayModel.convertYearsToMs(100)));
    	_datableObjects.add(new DatableObject("Fish Bones", "fish_bones.png", new Point2D.Double(-20, -1.5), 5, 0, MultiNucleusDecayModel.convertYearsToMs(1000)));
    	_datableObjects.add(new DatableObject("Pottery", "pottery.png", new Point2D.Double(-10, -4.5), 3.8, Math.PI/2, MultiNucleusDecayModel.convertYearsToMs(5000)));
    	_datableObjects.add(new DatableObject("Rock 1", "rock_1.png", new Point2D.Double(-9.0, 2.5), 3, 0, MultiNucleusDecayModel.convertYearsToMs(1E9)));
    	_datableObjects.add(new DatableObject("Rock 2", "rock_2.png", new Point2D.Double(-4, -1.5), 1.5, 0, MultiNucleusDecayModel.convertYearsToMs(1E9)));
    	_datableObjects.add(new DatableObject("Rock 3", "rock_3.png", new Point2D.Double(-22, -14.5), 2.5, 0, MultiNucleusDecayModel.convertYearsToMs(2E9)));
    	_datableObjects.add(new DatableObject("Rock 4", "rock_4.png", new Point2D.Double(12, -11), 1.5, 0, MultiNucleusDecayModel.convertYearsToMs(2E9)));
    	_datableObjects.add(new DatableObject("Rock 5", "rock_4.png", new Point2D.Double(6, -4.5), 2, 0, MultiNucleusDecayModel.convertYearsToMs(2E9)));
    	_datableObjects.add(new DatableObject("Rock 6", "rock_6.png", new Point2D.Double(15, -7.5), 1.5, 0, MultiNucleusDecayModel.convertYearsToMs(3E9)));
    	_datableObjects.add(new DatableObject("Rock 7", "rock_7.png", new Point2D.Double(20, -15), 2.5, 0, MultiNucleusDecayModel.convertYearsToMs(3E9)));
    	_datableObjects.add(new DatableObject("Rock 8", "rock_8.png", new Point2D.Double(-13, -11), 2, 0, MultiNucleusDecayModel.convertYearsToMs(3.5E9)));
    	_datableObjects.add(new DatableObject("Animal Skull 2", "skull_animal_2.png", new Point2D.Double(-8, -14.5), 4.5, 0, MultiNucleusDecayModel.convertYearsToMs(220E6)));
    	_datableObjects.add(new DatableObject("Human Skull", "skull_human.png", new Point2D.Double(13, -1.5), 2.4, 0, MultiNucleusDecayModel.convertYearsToMs(1000)));
    	_datableObjects.add(new DatableObject("Cup", "cup.png", new Point2D.Double(4, -2), 3.2,  -Math.PI / 3, MultiNucleusDecayModel.convertYearsToMs(1000)));
    	_datableObjects.add(new DatableObject("Bone", "bone.png", new Point2D.Double(7, -15), 4.5, 0, MultiNucleusDecayModel.convertYearsToMs(220E6)));

    	// Add the meter and register for user-initiated movements.
    	_meter = new RadiometricDatingMeter( this );
    	
    	_meter.getProbeModel().addObserver(new SimpleObserver(){
			public void update() {
				getDatableItemAtLocation( _meter.getProbeModel().getTipLocation() );
				
			}
    	});
    }

    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public Iterable<DatableObject> getItemIterable(){
    	return _datableObjects;
    }
    
    public Iterable<Stratum> getStratumIterable(){
        return _strata;
    }

    public int getLayerCount() {
        return _strata.size();
    }

    public Stratum getLayer( int i ) {
        return _strata.get(i);
    }
    
    public RadiometricDatingMeter getMeter(){
    	return _meter;
    }
    
    /**
     * Get the lowest point of the model for which a stratum (a.k.a. a layer
     * of sediment) is found.
     * 
     * @return
     */
    public double getBottomOfStrata(){
    	double bottom = 0;
    	for ( Stratum stratum : _strata ){
    		if ( stratum.getBottomOfStratumY() < bottom ){
    			bottom = stratum.getBottomOfStratumY();
    		}
    	}
    	return bottom;
    }
    
    /**
     * Get the highest point of the model for which a stratum (a.k.a. a layer
     * of sediment) is found.
     * 
     * @return
     */
    public double getTopOfStrata(){
    	double top = Double.NEGATIVE_INFINITY;
    	for ( Stratum stratum : _strata ){
    		if ( stratum.getTopOfStratumY() > top ){
    			top = stratum.getTopOfStratumY();
    		}
    	}
    	return top;
    }
    
    //------------------------------------------------------------------------
    // Other Methods
    //------------------------------------------------------------------------

    /**
     * Get the datable item at the specified model location, or null if there
     * isn't anything there.
     */
    public DatableObject getDatableItemAtLocation( Point2D probeLocation ){

    	DatableObject datableItem = null;
    	
    	for ( DatableObject datableObject : _datableObjects ){
    		if (datableObject.contains(probeLocation)){
    			datableItem = datableObject;
    		}
    	}
    	
    	return datableItem;
    }
}
