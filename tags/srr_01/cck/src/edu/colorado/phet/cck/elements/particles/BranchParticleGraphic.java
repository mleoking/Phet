/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.particles;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.BranchObserver;
import edu.colorado.phet.cck.elements.junction.Junction;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.math.PhetVector;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Observable;
import java.util.Observer;

/**
 * User: Sam Reid
 * Date: Sep 4, 2003
 * Time: 3:36:19 AM
 * Copyright (c) Sep 4, 2003 by Sam Reid
 */
public class BranchParticleGraphic implements Graphic {
    BranchParticle particle;
    ModelViewTransform2d transform;
    private Module module;
    private BufferedImage image;
    private ImageObserver obs;
    private Point viewCoord;
    private int width = 20;
    private int height = 20;

    public BranchParticleGraphic(BranchParticle particle, ModelViewTransform2d transform, Module module, BufferedImage image, ImageObserver obs) {
        this.particle = particle;
        this.transform = transform;
        this.module = module;
        this.image = image;
        this.obs = obs;
        this.width = image.getWidth();
        this.height = image.getHeight();
        transform.addTransformListener(new TransformListener() {
            public void transformChanged(ModelViewTransform2d modelViewTransform2d) {
                stateChanged();
            }
        });
        particle.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                stateChanged();
            }
        });
        particle.getBranch().addObserver(new BranchObserver() {
            public void junctionMoved(Branch branch2, Junction j) {
                stateChanged();
            }

            public void currentOrVoltageChanged(Branch branch2) {
            }
        });
        stateChanged();
    }

    private void stateChanged() {
        PhetVector loc = particle.getPosition2D();
        this.viewCoord = transform.modelToView(loc);
        module.getApparatusPanel().repaint();
    }

    public void paint(Graphics2D g) {
//        g.setColor(Color.blue);
//        g.fillRect(viewCoord.x-width/2,viewCoord.y-height/2,width,height);
        g.drawImage(image, viewCoord.x - width / 2, viewCoord.y - height / 2, obs);
    }

    public BranchParticle getBranchParticle() {
        return particle;
    }

}
