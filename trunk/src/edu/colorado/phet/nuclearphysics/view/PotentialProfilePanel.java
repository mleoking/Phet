// todo: Apply a model transform to this panel, so we don't have to
// work in view coordinates all the time.

/**
 * Class: PotentialProfilePanel
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 6:03:01 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.DecayNucleus;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.PotentialProfile;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class PotentialProfilePanel extends ApparatusPanel {

    //
    // Statics
    //
    private static Color axisColor = new Color( 100, 100, 100 );
    private static Stroke axisStroke = new BasicStroke( 1f );
    private static Color backgroundColor = new Color( 230, 255, 255 );
    private static String xAxisLabel = "Disance from Nucleus Center";
    private static String yAxisLabel = "Potential Energy";
    private static Font axisLabelFont;
    private static float ghostAlpha = 0.6f;

    static {
        String family = "SansSerif";
        int style = Font.BOLD;
        int size = 12;
        axisLabelFont = new Font( family, style, size );
    }

    public static AffineTransform scaleInPlaceTx( double scale, double x, double y ) {
        AffineTransform atx = new AffineTransform();
        atx.translate( x, y );
        atx.scale( scale, scale );
        atx.translate( -x, -y );
        return atx;
    }

    public static AffineTransform rotateInPlace( double theta, double x, double y ) {
        AffineTransform atx = new AffineTransform();
        atx.translate( x, y );
        atx.rotate( theta );
        atx.translate( -x, -y );
        return atx;
    }

    //
    // Instance fields and methods
    //
    private ArrayList nucleusGraphics = new ArrayList();
    private NucleusGraphic decayGraphic;
    private PotentialProfileGraphic profileGraphic;
    private PotentialProfile potentialProfile;
    private Point2D.Double origin;
    private Point2D.Double strLoc = new Point2D.Double();

    public PotentialProfilePanel( PotentialProfile potentialProfile ) {
        origin = new Point2D.Double( 250, 600 );
        this.setBackground( backgroundColor );
        this.potentialProfile = potentialProfile;
        profileGraphic = new PotentialProfileGraphic( potentialProfile );
        addGraphic( profileGraphic );
    }

    private void addNucleusGraphic( NucleusGraphic nucleusGraphic ) {
        nucleusGraphics.add( nucleusGraphic );
    }

    public PotentialProfile getPotentialProfile() {
        return potentialProfile;
    }

    public void addGraphic( Graphic graphic ) {
        if( graphic instanceof PotentialProfileGraphic ) {
            addPotentialProfile( (PotentialProfileGraphic)graphic );
        }
        if( graphic instanceof NucleusGraphic ) {
            addNucleusGraphic( (NucleusGraphic)graphic );
        }
        else {
            super.addGraphic( graphic );
        }
    }

    public void removeGraphic( Graphic graphic ) {
//        if( graphic instanceof NucleusGraphic ) {
////            removeNucleus();
//        }
//        else {
        super.removeGraphic( graphic );
//        }
    }

    protected void paintComponent( Graphics graphics ) {

        // Center the profile in the panel
        origin.setLocation( this.getWidth() / 2, this.getHeight() * 2 / 3 );

        super.paintComponent( graphics );
        Graphics2D g2 = (Graphics2D)graphics;

        // Draw axes
        drawAxes( g2 );

        // Draw nuclei
        for( int i = 0; i < nucleusGraphics.size(); i++ ) {
            NucleusGraphic nucleusGraphic = (NucleusGraphic)nucleusGraphics.get( i );
            Nucleus nucleus = nucleusGraphic.getNucleus();
            double xStat = nucleus.getStatisticalLocationOffset().getX();
            double yStat = nucleus.getStatisticalLocationOffset().getY();
            double d = ( Math.sqrt( xStat * xStat + yStat * yStat ) ) * ( xStat > 0 ? 1 : -1 );
            double x = origin.getX() + d;
            double y = ( nucleus instanceof AlphaParticle ? potentialProfile.getWellPotential() + AlphaParticle.RADIUS : 0 );
            // Draw one version referenced off the origin
            nucleusGraphic.paint( g2, (int)x, (int)( origin.getY() ) );
            // Draw a "ghost" version up at the well
            if( nucleusGraphic.getNucleus() instanceof AlphaParticle ) {
                float alpha = ghostAlpha;
                g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha ) );
                nucleusGraphic.paint( g2, (int)x, (int)( origin.getY() - y ) );
                g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1 ) );
            }
        }

        if( decayGraphic != null ) {
            DecayNucleus nucleus = (DecayNucleus)decayGraphic.getNucleus();
            // Note: -y is needed because we're currently working in view coordinates. The profile is a cubic
            // in view coordinates
            double y = nucleus.getPotentialEnergy();
            double x = potentialProfile.getHillX( -y ) *
                       ( nucleus.getStatisticalLocationOffset().getX() > 0 ? 1 : -1 );

            // Draw a ghost coming down the profile first, then the real thing on the x axis
            float alpha = ghostAlpha;
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha ) );
            decayGraphic.paint( g2, (int)( (int)origin.getX() + x ), (int)origin.getY() - (int)y );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1 ) );
            decayGraphic.paint( g2, (int)( (int)origin.getX() + x ), (int)origin.getY() );
        }

        // Paint a dot on the hill for the spot at the same level as the well
//        double yTest = -potentialProfile.getWellPotential();
//        for( int j = 0; j < 1; j++ ) {
//            double xTest = potentialProfile.getAlphaDecayX();
//            g2.setColor( Color.red );
//            g2.fillOval( (int)xTest + (int)origin.getX() - 5, (int)origin.getY() + (int)yTest - 5, 10, 10 );
//        }

    }

    private void drawAxes( Graphics2D g2 ) {

        g2.setColor( axisColor );
        g2.setStroke( axisStroke );
        Line2D.Double xAxis = new Line2D.Double( 0, origin.getY(), this.getWidth(), origin.getY() );
        Line2D.Double yAxis = new Line2D.Double( origin.getX(), 0, origin.getX(), this.getHeight() );
        g2.draw( xAxis );
        g2.draw( yAxis );

        // Draw labels
        g2.setFont( axisLabelFont );
        g2.setColor( Color.black );
        FontMetrics fm = g2.getFontMetrics();
        strLoc.setLocation( origin.getX(), fm.stringWidth( yAxisLabel ) + 10 );
        AffineTransform strTx = rotateInPlace( -Math.PI / 2, strLoc.getX(), strLoc.getY() );
        AffineTransform orgTx = g2.getTransform();
        g2.setTransform( strTx );
        g2.drawString( yAxisLabel, (int)strLoc.getX(), (int)strLoc.getY() );
        g2.setTransform( orgTx );
        strLoc.setLocation( this.getWidth() - fm.stringWidth( xAxisLabel ) - 10,
                            origin.getY() + fm.getHeight() );
        g2.drawString( xAxisLabel, (int)strLoc.getX(), (int)strLoc.getY() );
    }

    private void addPotentialProfile( PotentialProfileGraphic profileGraphic ) {
        if( this.profileGraphic != null ) {
            removeGraphic( this.profileGraphic );
        }
        this.profileGraphic = profileGraphic;
        this.profileGraphic.setOrigin( origin );
        super.addGraphic( profileGraphic );
    }

    public void setNucleus( Nucleus nucleus ) {
        this.addNucleusGraphic( new NucleusGraphic( nucleus ) );
        setPotentialProfile( nucleus.getPotentialProfile() );
    }

    public void setPotentialProfile( PotentialProfile potentialProfile ) {
        this.potentialProfile = potentialProfile;
        this.addPotentialProfile( new PotentialProfileGraphic( potentialProfile ) );
    }

    public void addDecayProduct( Nucleus decayNucleus ) {
        this.decayGraphic = new NucleusGraphic( decayNucleus );
    }


    public void addAlphaParticle( AlphaParticle alphaParticle ) {
        NucleusGraphic graphic = new NucleusGraphic( alphaParticle );
        alphaParticle.addObserver( graphic );
        this.addGraphic( graphic );
    }

    public void clear() {
        this.decayGraphic = null;
        this.nucleusGraphics.clear();
    }
}
