/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.persistence;

import edu.colorado.phet.boundstates.enums.BSBottomPlotMode;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.*;


/**
 * BSModuleConfig is a JavaBean-compliant data structure that stores
 * module configuration information for BSModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSModuleConfig implements BSSerializable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Clock
    private boolean _clockRunning;
    private int _clockIndex;
    
    // Model
    private BSParticleConfig _particleConfig;
    private BSAsymmetricConfig _asymmetricConfig;
    private BSCoulomb1DConfig _coulomb1DConfig;
    private BSCoulomb3DConfig _coulomb3DConfig;
    private BSHarmonicOscillatorConfig _harmonicOscillatorConfig;
    private BSSquareConfig _squareConfig;
    private String _selectedWellTypeName;
    private double[] _superpositionCoefficients;
    
    // Controls
    private String _bottomPlotModeName;
    private boolean _magnifyingGlassSelected;
    private boolean _realSelected;
    private boolean _imaginarySelected;
    private boolean _magnitudeSelected;
    private boolean _phaseSelected;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public BSModuleConfig() {}
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setClockRunning( boolean clockRunning ) {
        _clockRunning = clockRunning;
    }
    
    public boolean isClockRunning() {
        return _clockRunning;
    }

    public void setClockIndex( int clockIndex ) {
        _clockIndex = clockIndex;
    }
    
    public int getClockIndex() {
        return _clockIndex;
    }
    
    public String getBottomPlotModeName() {
        return _bottomPlotModeName;
    }
    
    public void setBottomPlotModeName( String bottomPlotModeName ) {
        _bottomPlotModeName = bottomPlotModeName;
    }
    
    public boolean isRealSelected() {
        return _realSelected;
    }
    
    public void setRealSelected( boolean realSelected ) {
        _realSelected = realSelected;
    }
    
    public boolean isImaginarySelected() {
        return _imaginarySelected;
    }
    
    public void setImaginarySelected( boolean imaginarySelected ) {
        _imaginarySelected = imaginarySelected;
    }
    
    public boolean isMagnitudeSelected() {
        return _magnitudeSelected;
    }
    
    public void setMagnitudeSelected( boolean magnitudeSelected ) {
        _magnitudeSelected = magnitudeSelected;
    }
    
    public boolean isPhaseSelected() {
        return _phaseSelected;
    }
    
    public void setPhaseSelected( boolean phaseSelected ) {
        _phaseSelected = phaseSelected;
    }

    public boolean isMagnifyingGlassSelected() {
        return _magnifyingGlassSelected;
    }
  
    public void setMagnifyingGlassSelected( boolean magnifyingGlassSelected ) {
        _magnifyingGlassSelected = magnifyingGlassSelected;
    }

    public BSParticleConfig getParticleConfig() {
        return _particleConfig;
    }
    
    public void setParticleConfig( BSParticleConfig particleConfig ) {
        _particleConfig = particleConfig;
    }

    public BSAsymmetricConfig getAsymmetricConfig() {
        return _asymmetricConfig;
    }
    
    public void setAsymmetricConfig( BSAsymmetricConfig asymmetricConfig ) {
        _asymmetricConfig = asymmetricConfig;
    }
    
    public BSCoulomb1DConfig getCoulomb1DConfig() {
        return _coulomb1DConfig;
    }
    
    public void setCoulomb1DConfig( BSCoulomb1DConfig coulomb1DConfig ) {
        _coulomb1DConfig = coulomb1DConfig;
    }
    
    public BSCoulomb3DConfig getCoulomb3DConfig() {
        return _coulomb3DConfig;
    }
    
    public void setCoulomb3DConfig( BSCoulomb3DConfig coulomb3DConfig ) {
        _coulomb3DConfig = coulomb3DConfig;
    }
    
    public BSHarmonicOscillatorConfig getHarmonicOscillatorConfig() {
        return _harmonicOscillatorConfig;
    }
    
    public void setHarmonicOscillatorConfig( BSHarmonicOscillatorConfig harmonicOscillatorConfig ) {
        _harmonicOscillatorConfig = harmonicOscillatorConfig;
    }
    
    public String getSelectedWellTypeName() {
        return _selectedWellTypeName;
    }
    
    public void setSelectedWellTypeName( String selectedWellTypeName ) {
        _selectedWellTypeName = selectedWellTypeName;
    }

    public BSSquareConfig getSquareConfig() {
        return _squareConfig;
    }
    
    public void setSquareConfig( BSSquareConfig squareConfig ) {
        _squareConfig = squareConfig;
    }
    
    public double[] getSuperpositionCoefficients() {
        return _superpositionCoefficients;
    }
    
    public void setSuperpositionCoefficients( double[] superpositionCoefficients ) {
        _superpositionCoefficients = superpositionCoefficients;
    }
    
    //----------------------------------------------------------------------------
    // Convenience methods for non-JavaBean objects
    //----------------------------------------------------------------------------

    public BSBottomPlotMode loadBottomPlotMode() {
        return BSBottomPlotMode.getByName( _bottomPlotModeName );
    }
    
    public void saveBottomPlotMode( BSBottomPlotMode bottomPlotMode ) {
        setBottomPlotModeName( bottomPlotMode.getName() );
    }
    
    public BSWellType loadSelectedWellType() {
        return BSWellType.getByName( _selectedWellTypeName );
    }
    
    public void saveSelectedWellType( BSWellType wellType ) {
        setSelectedWellTypeName( wellType.getName() );
    }
    
    public BSParticle loadParticle() {
        return _particleConfig.toParticle();
    }
    
    public void saveParticle( BSParticle particle ) {
        setParticleConfig( new BSParticleConfig( particle ) );
    }
    
    public BSAsymmetricWell loadAsymmetricPotential( BSParticle particle ) {
        return _asymmetricConfig.toPotential( particle );
    }
    
    public void saveAsymmetricPotential( BSAsymmetricWell potential ) {
        if ( potential != null ) {
            setAsymmetricConfig( new BSAsymmetricConfig( potential ) );
        }
    }
    
    public BSCoulomb1DWells loadCoulomb1DPotential( BSParticle particle ) {
        return _coulomb1DConfig.toPotential( particle );
    }
    
    public void saveCoulomb1DPotential( BSCoulomb1DWells potential ) {
        if ( potential != null ) {
            setCoulomb1DConfig( new BSCoulomb1DConfig( potential ) );
        }
    }
    
    public BSCoulomb3DWell loadCoulomb3DPotential( BSParticle particle ) {
        return _coulomb3DConfig.toPotential( particle );
    }
    
    public void saveCoulomb3DPotential( BSCoulomb3DWell potential ) {
        if ( potential != null ) {
            setCoulomb3DConfig( new BSCoulomb3DConfig( potential ) );
        }
    }
    
    public BSHarmonicOscillatorWell loadHarmonicOscillatorPotential( BSParticle particle ) {
        return _harmonicOscillatorConfig.toPotential( particle );
    }
    
    public void saveHarmonicOscillatorPotential( BSHarmonicOscillatorWell potential ) {
        if ( potential != null ) {
            setHarmonicOscillatorConfig( new BSHarmonicOscillatorConfig( potential ) );
        }
    }
    
    public BSSquareWells loadSquarePotential( BSParticle particle ) {
        return _squareConfig.toPotential( particle );
    }
    
    public void saveSquarePotential( BSSquareWells potential ) {
        if ( potential != null ) {
            setSquareConfig( new BSSquareConfig( potential ) );
        }
    }
}
