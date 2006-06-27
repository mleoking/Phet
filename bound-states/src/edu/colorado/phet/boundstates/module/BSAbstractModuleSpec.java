/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.module;

import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.util.DoubleRange;
import edu.colorado.phet.boundstates.util.IntegerRange;

/**
 * BSAbstractModuleSpec contains the information needed to set up a module,
 * including ranges, flags, combo box choices, etc.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractModuleSpec {

    private String _id;
    
    private BSWellType[] _wellTypes;
    private BSWellType _defaultWellType;

    private boolean _offsetControlSupported;
    private boolean _superpositionControlsSupported;
    private boolean _particleControlsSupported;
    private boolean _magnifyingGlassSupported;
    private boolean _magnifyingGlassSelected;

    private DoubleRange _massMultiplierRange;
    
    private IntegerRange _numberOfWellsRange;
    
    private BSWellSpec _asymmetricSpec;
    private BSWellSpec _coulomb1DSpec;
    private BSWellSpec _coulomb3DSpec;
    private BSWellSpec _harmonicOscillatorSpec;
    private BSWellSpec _squareSpec;

    private double _magnification;
    
    public BSAbstractModuleSpec() {}
    
    public String getId() {
        return _id;
    }
    
    public void setId( String id ) {
        _id = id;
    }
    
    public boolean isOffsetControlSupported() {
        return _offsetControlSupported;
    }
    
    protected void setOffsetControlSupported( boolean offsetControlSupported ) {
        _offsetControlSupported = offsetControlSupported;
    }

    public DoubleRange getMassMultiplierRange() {
        return _massMultiplierRange;
    }
    
    protected void setMassMultiplierRange( DoubleRange massMultiplierRange ) {
        _massMultiplierRange = massMultiplierRange;
    }
    
    public IntegerRange getNumberOfWellsRange() {
        return _numberOfWellsRange;
    }
    
    public void setNumberOfWellsRange( IntegerRange numberOfWellsRange ) {
        _numberOfWellsRange = numberOfWellsRange;
    }

    public boolean isParticleControlsSupported() {
        return _particleControlsSupported;
    }
    
    protected void setParticleControlsSupported( boolean supportsParticleControls ) {
        _particleControlsSupported = supportsParticleControls;
    }
    
    public boolean isSuperpositionControlsSupported() {
        return _superpositionControlsSupported;
    }
    
    protected void setSuperpositionControlsSupported( boolean supportsSuperpositionControls ) {
        _superpositionControlsSupported = supportsSuperpositionControls;
    }
    
    public BSWellType[] getWellTypes() {
        return _wellTypes;
    }
    
    protected void setWellTypes( BSWellType[] wellTypes ) {
        _wellTypes = wellTypes;
    }

    public BSWellType getDefaultWellType() {
        return _defaultWellType;
    }
    
    protected void setDefaultWellType( BSWellType defaultWellType ) {
        _defaultWellType = defaultWellType;
    }
    
    public boolean isMagnifyingGlassSupported() {
        return _magnifyingGlassSupported;
    }
    
    protected void setMagnifyingGlassSupported( boolean magnifyingGlassSupported ) {
        _magnifyingGlassSupported = magnifyingGlassSupported;
    }

    public boolean isMagnifyingGlassSelected() {
        return _magnifyingGlassSelected;
    }
    
    protected void setMagnifyingGlassSelected( boolean magnifyingGlassSelected ) {
        _magnifyingGlassSelected = magnifyingGlassSelected;
    }
    
    public double getMagnification() {
        return _magnification;
    }

    protected void setMagnification( double magnification ) {
        _magnification = magnification;
    }
    
    public BSWellSpec getAsymmetricSpec() {
        return _asymmetricSpec;
    }
    
    protected void setAsymmetricSpec( BSWellSpec asymmetricRangeSpec ) {
        _asymmetricSpec = asymmetricRangeSpec;
    }
    
    public BSWellSpec getCoulomb1DSpec() {
        return _coulomb1DSpec;
    }
    
    protected void setCoulomb1DSpec( BSWellSpec coulomb1DRangeSpec ) {
        _coulomb1DSpec = coulomb1DRangeSpec;
    }
    
    public BSWellSpec getCoulomb3DSpec() {
        return _coulomb3DSpec;
    }
    
    protected void setCoulomb3DSpec( BSWellSpec coulomb3DRangeSpec ) {
        _coulomb3DSpec = coulomb3DRangeSpec;
    }
    
    public BSWellSpec getHarmonicOscillatorSpec() {
        return _harmonicOscillatorSpec;
    }
    
    protected void setHarmonicOscillatorSpec( BSWellSpec harmonicOscillatorRangeSpec ) {
        _harmonicOscillatorSpec = harmonicOscillatorRangeSpec;
    }

    public BSWellSpec getSquareSpec() {
        return _squareSpec;
    }
    
    protected void setSquareSpec( BSWellSpec squareRangeSpec ) {
        _squareSpec = squareRangeSpec;
    }
    
    public BSWellSpec getRangeSpec( BSWellType wellType ) {
        BSWellSpec rangeSpec = null;
        if ( wellType == BSWellType.ASYMMETRIC ) {
            rangeSpec = _asymmetricSpec;
        }
        else if ( wellType == BSWellType.COULOMB_1D ) {
            rangeSpec = _coulomb1DSpec;
        }
        else if ( wellType == BSWellType.COULOMB_3D ) {
            rangeSpec = _coulomb3DSpec;
        }
        else if ( wellType == BSWellType.HARMONIC_OSCILLATOR ) {
            rangeSpec = _harmonicOscillatorSpec;
        }
        else if ( wellType == BSWellType.SQUARE ) {
            rangeSpec = _squareSpec;
        }
        else {
            throw new UnsupportedOperationException( "unsupported well type: " + wellType );
        }
        return rangeSpec;
    }
}
