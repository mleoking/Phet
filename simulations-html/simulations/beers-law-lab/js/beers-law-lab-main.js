// Copyright 2013, University of Colorado

/**
 * Main entry point for the "Beer's Law Lab" sim.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require( [
             'easel',
             'phetcommon/util/Logger',
             'phetcommon/view/ModelViewTransform2D',
             'common/view/CanvasQuirks',
             'beerslaw/model/BeersLawModel',
             'beerslaw/view/BeersLawStage',
             'i18n!../nls/beers-law-lab-strings'
         ],
         function ( Easel, Logger, ModelViewTransform2D, CanvasQuirks, BeersLawModel, BeersLawStage, Strings ) {

             Logger.enabled = true;

             // Title --------------------------------------------------------------------

             $( 'title' ).html( Strings.title );

             // Model --------------------------------------------------------------------

             var model = new BeersLawModel();

             // View --------------------------------------------------------------------

             var canvas = document.getElementById( 'canvas' );
             CanvasQuirks.fixTextCursor( canvas );
             var stage = new BeersLawStage( canvas, model );

             // Animation loop ----------------------------------------------------------

             Easel.Ticker.addListener( model );
             Easel.Ticker.addListener( stage );
             Easel.Ticker.addListener( stage.frameRateDisplay );
             Easel.Ticker.setFPS( 60 );
             Easel.Touch.enable( stage, false, false );
         } );
