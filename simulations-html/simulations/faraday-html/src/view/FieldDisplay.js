// Copyright 2002-2012, University of Colorado

/**
 * Display object for the E-field.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel' ], function( Easel ) {

    /**
     * @param {FaradayModel} model
     * @param {ModelViewTransform} mvt
     * @constructor
     */
    function FieldDisplay( model, mvt ) {
        // constructor stealing
        Easel.Text.call( this, "field", "bold 36px Arial", 'white' );
    }

    // prototype chaining
    FieldDisplay.prototype = new Easel.Text();

    return FieldDisplay;
} );
