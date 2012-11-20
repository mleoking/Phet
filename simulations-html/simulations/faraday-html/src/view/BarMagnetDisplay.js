// Copyright 2002-2012, University of Colorado

/**
 * Display object for the bar magnet.
 *
 * Uses the "Combination Inheritance" pattern to extend Easel's Bitmap type.
 * Combination Inheritance combines 2 other patterns, "Constructor Stealing" and "Prototype Chaining",
 * and is reportedly "the most frequently used inheritance pattern in JavaScript".
 * The most significant downside of this pattern is that the supertype's constructor is called twice:
 * once inside the subtype's constructor, and once to to create the subtype's prototype.
 * (Professional JavaScript for Web Developers, Zakas, Wrox Press, p 209-210.)
 *
 * Usage: var barMagnet = new BarMagnet2();
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel',
          'common/MathUtil',
          'view/DragHandler',
          'image!resources/images/barMagnet.png'
        ],
        function ( Easel, MathUtil, DragHandler, barMagnetImage ) {

    /**
     * @class BarMagnetDisplay
     * @constructor
     * @param {BarMagnet} barMagnet
     * @param {ModelViewTransform} mvt
     */
    function BarMagnetDisplay( barMagnet, mvt ) {

        // Use constructor stealing to inherit instance properties.
        Easel.Bitmap.call( this, barMagnetImage );

        // Compute scale factors to match model.
        this.scaleX = mvt.modelToViewScalar( barMagnet.size.width ) / this.image.width;
        this.scaleY = mvt.modelToViewScalar( barMagnet.size.height ) / this.image.height;

        // Move registration point to the center.
        this.regX = this.image.width / 2;
        this.regY = this.image.height / 2;

        // Dragging.
        DragHandler.register( this, function( point ) {
            barMagnet.location.set( mvt.viewToModel( point ) );
        });

        // Register for synchronization with model.
        var thisDisplayObject = this;

        // @param {Point} location
        function updateLocation( location ) {
            var point = mvt.modelToView( location );
            thisDisplayObject.x = point.x;
            thisDisplayObject.y = point.y;
        }
        barMagnet.location.addObserver( updateLocation );

        // @param {Number} orientation in radians
        function updateOrientation( orientation ) {
            thisDisplayObject.rotation = MathUtil.toDegrees( orientation );
        }
        barMagnet.orientation.addObserver( updateOrientation );

        // sync now
        updateLocation( barMagnet.location.get() );
        updateOrientation( barMagnet.orientation.get() );
    }

    // Use prototype chaining to inherit properties and methods on the prototype.
    BarMagnetDisplay.prototype = new Easel.Bitmap();

    return BarMagnetDisplay;
} );
