package edu.colorado.phet.semiconductor.macro.energyprobe;

import edu.colorado.phet.common.math.PhetVector;

/**
 * User: Sam Reid
 * Date: Feb 2, 2004
 * Time: 8:25:04 PM
 * Copyright (c) Feb 2, 2004 by Sam Reid
 */
public class Cable {
    Lead lead;
    private PhetVector attachmentPoint;


    public Cable( Lead lead, PhetVector attachmentPoint ) {
        this.lead = lead;
        this.attachmentPoint = attachmentPoint;
    }

    public Lead getLead() {
        return lead;
    }

    public PhetVector getAttachmentPoint() {
        return attachmentPoint;
    }
}
