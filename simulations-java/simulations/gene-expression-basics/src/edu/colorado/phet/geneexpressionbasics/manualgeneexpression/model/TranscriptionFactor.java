// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.BioShapeUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.AttachmentStateMachine;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.TranscriptionFactorAttachmentStateMachine;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents a transcription factor in the model.  There are
 * multiple transcription factors, and some are positive (in the sense that
 * they increase the likelihood of transcription) and some are negative (i.e.
 * the reduce the likelihood of transcription).
 *
 * @author John Blanco
 */
public class TranscriptionFactor extends MobileBiomolecule {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double WIDTH = 350;   // In nanometers.
    private static final double HEIGHT = 240;  // In nanometers.
    private static final Dimension2D SIZE = new PDimension( WIDTH, HEIGHT );

    // Static definitions of all the transcription factor configurations that
    // are used by this sim.
    public static final TranscriptionFactorConfig TRANSCRIPTION_FACTOR_CONFIG_GENE_1_POS = new TranscriptionFactorConfig( 1, BioShapeUtils.createRandomShape( SIZE, 1000 ), true, Color.yellow );
    public static final TranscriptionFactorConfig TRANSCRIPTION_FACTOR_CONFIG_GENE_1_NEG = new TranscriptionFactorConfig( 1, BioShapeUtils.createRandomShape( SIZE, 2000 ), false, Color.red );
    public static final TranscriptionFactorConfig TRANSCRIPTION_FACTOR_CONFIG_GENE_2_POS_1 = new TranscriptionFactorConfig( 2, BioShapeUtils.createRandomShape( SIZE, 3000 ), true, Color.green );
    public static final TranscriptionFactorConfig TRANSCRIPTION_FACTOR_CONFIG_GENE_2_POS_2 = new TranscriptionFactorConfig( 2, BioShapeUtils.createRandomShape( SIZE, 3050 ), true, Color.cyan );
    public static final TranscriptionFactorConfig TRANSCRIPTION_FACTOR_CONFIG_GENE_2_NEG = new TranscriptionFactorConfig( 2, BioShapeUtils.createRandomShape( SIZE, 4000 ), false, Color.pink );
    public static final TranscriptionFactorConfig TRANSCRIPTION_FACTOR_CONFIG_GENE_3_POS = new TranscriptionFactorConfig( 3, BioShapeUtils.createRandomShape( SIZE, 57 ), true, new Color( 255, 127, 0 ) );
    public static final TranscriptionFactorConfig TRANSCRIPTION_FACTOR_CONFIG_GENE_3_NEG = new TranscriptionFactorConfig( 3, BioShapeUtils.createRandomShape( SIZE, 40 ), false, Color.magenta );

    /**
     * This data structure contains the configuration information for all of the
     * transcription factors used in this sim.
     */
    private static final List<TranscriptionFactorConfig> transcriptionFactorConfigurations = new ArrayList<TranscriptionFactorConfig>() {{
        add( TRANSCRIPTION_FACTOR_CONFIG_GENE_1_POS );
        add( TRANSCRIPTION_FACTOR_CONFIG_GENE_1_NEG );
        add( TRANSCRIPTION_FACTOR_CONFIG_GENE_2_POS_1 );
        add( TRANSCRIPTION_FACTOR_CONFIG_GENE_2_POS_2 );
        add( TRANSCRIPTION_FACTOR_CONFIG_GENE_2_NEG );
        add( TRANSCRIPTION_FACTOR_CONFIG_GENE_3_POS );
        add( TRANSCRIPTION_FACTOR_CONFIG_GENE_3_NEG );
        // Default config used if no match found.
        add( new TranscriptionFactorConfig( Integer.MAX_VALUE, BioShapeUtils.createRandomShape( SIZE, 123 ), false, Color.magenta ) );
    }};

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Configuration used to create this transcription factor, used when
    // comparing factors and creating copies.
    private final TranscriptionFactorConfig config;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public TranscriptionFactor( GeneExpressionModel model, TranscriptionFactorConfig config, Point2D initialPosition ) {
        super( model, config.shape, config.baseColor );
        this.config = config;
        setPosition( initialPosition );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Get an indication of whether this transcription factor is positive
     * (enhances transcription) or negative (prevents or decreases likelihood of
     * transcription).
     *
     * @return
     */
    public boolean isPositive() {
        return config.isPositive;
    }

    @Override public void stepInTime( double dt ) {
        super.stepInTime( dt );
    }

    // Overridden in order to provide some unique behavior for transcription
    // factors.
    @Override protected AttachmentStateMachine createAttachmentStateMachine() {
        return new TranscriptionFactorAttachmentStateMachine( this );
    }

    @Override protected void handleReleasedByUser() {
        super.handleReleasedByUser();

        // A case that is unique to transcription factors: If released on top
        // of another biomolecule on the DNA strand, go directly into the
        // detaching state so that this drifts away from the DNA.  This makes it
        // clear the you can't have two transcription factors in the same
        // place on the DNA.
        for ( MobileBiomolecule biomolecule : model.getOverlappingBiomolecules( this.getShape() ) ) {
            if ( biomolecule.isAttachedToDna() ) {
                attachmentStateMachine.forceImmediateUnattachedButUnavailable();
                break;
            }
        }
    }

    /**
     * Static factory method that generates an instance of a transcription
     * factor for the specified gene and of the specified polarity (i.e.
     * positive or negative).
     *
     * @param geneID
     * @param positive
     * @param initialPosition
     * @return
     */
    public static TranscriptionFactor generateTranscriptionFactor( GeneExpressionModel model, int geneID, boolean positive, Point2D initialPosition ) {
        TranscriptionFactorConfig config = null;
        for ( TranscriptionFactorConfig tfc : transcriptionFactorConfigurations ) {
            if ( tfc.geneID == geneID && tfc.isPositive == positive ) {
                // Matching configuration found.
                config = tfc;
                break;
            }
        }
        // Make it clear that there is a problem if no configuration is found.
        if ( config == null ){
            System.out.println("No config found temp debug.");
        }
        assert config != null;
        if ( config == null ) {
            System.out.println( "Error: No transcription factor information for specified parameters, using default." );
            config = transcriptionFactorConfigurations.get( transcriptionFactorConfigurations.size() - 1 );
        }
        // Create the transcription factor instance.
        return new TranscriptionFactor( model, config, initialPosition );
    }

    @Override public AttachmentSite proposeAttachments() {
        // Transcription factors only attach to DNA.
        return model.getDnaMolecule().considerProposalFrom( this );
    }

    @Override public boolean isAttachedToDna() {
        // Since DNA is the only thing that this attaches to, if it is
        // attached to anything, it is DNA.
        return attachmentStateMachine.isAttachedOrAttaching();
    }

    public TranscriptionFactorConfig getConfig() {
        return config;
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------

    /**
     * Class the defines the shape, color, polarity, etc. of a transcription
     * factor.
     */
    public static class TranscriptionFactorConfig {

        protected final Shape shape;
        protected final Color baseColor;
        protected final int geneID;
        protected final boolean isPositive;

        TranscriptionFactorConfig( int geneID, Shape shape, boolean positive, Color baseColor ) {
            this.shape = shape;
            this.baseColor = baseColor;
            this.geneID = geneID;
            isPositive = positive;
        }
    }
}
