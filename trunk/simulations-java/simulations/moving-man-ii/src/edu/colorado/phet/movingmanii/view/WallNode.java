package edu.colorado.phet.movingmanii.view;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.movingmanii.MovingManIIResources;
import edu.colorado.phet.movingmanii.model.MutableBoolean;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import java.io.IOException;

/**
 * @author Sam Reid
 */
public class WallNode extends PNode {
    private final Function.LinearFunction linearFunction;
    private final double x;
    private MutableBoolean walls;
    private final PImage minimizeButton;
    private final PImage maximizeButton;
    private PImage wallNode;

    public WallNode(Range modelRange, final Range viewRange, double x, final MutableBoolean walls) {
        this.x = x;
        this.walls = walls;
        try {
            wallNode = new PImage(BufferedImageUtils.multiScaleToHeight(MovingManIIResources.loadBufferedImage("wall.jpg"), 100));
            addChild(wallNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        linearFunction = new Function.LinearFunction(modelRange.getMin(), modelRange.getMax(), viewRange.getMin(), viewRange.getMax());
        viewRange.addObserver(new SimpleObserver() {
            public void update() {
                linearFunction.setOutput(viewRange.getMin(), viewRange.getMax());
                updateLocation();
            }
        });
        updateLocation();

        {//close button
            minimizeButton = new PImage(PhetCommonResources.getImage(PhetCommonResources.IMAGE_CLOSE_BUTTON));
            minimizeButton.addInputEventListener(new CursorHandler());
            addChild(minimizeButton);
            minimizeButton.addInputEventListener(new PBasicInputEventHandler() {
                public void mouseReleased(PInputEvent event) {
                    walls.setValue(false);
                }
            });
        }

        { //restore button
            maximizeButton = new PImage(PhetCommonResources.getImage(PhetCommonResources.IMAGE_MAXIMIZE_BUTTON));
            maximizeButton.addInputEventListener(new CursorHandler());
            addChild(maximizeButton);
            maximizeButton.addInputEventListener(new PBasicInputEventHandler() {
                public void mouseReleased(PInputEvent event) {
                    walls.setValue(true);
                }
            });
        }
        walls.addObserver(new SimpleObserver() {
            public void update() {
                updateVisibility();
            }
        });
        updateVisibility();
    }

    private void updateVisibility() {
        wallNode.setVisible(walls.getValue());
        minimizeButton.setVisible(walls.getValue());
        maximizeButton.setVisible(!walls.getValue());
    }

    private void updateLocation() {
        setOffset(linearFunction.evaluate(x) - getFullBounds().getWidth() / 2, 0);
    }
}
