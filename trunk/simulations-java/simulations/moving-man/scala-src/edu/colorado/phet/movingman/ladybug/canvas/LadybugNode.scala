package edu.colorado.phet.movingman.ladybug.canvas

import controlpanel.VectorVisibilityModel
import model.{Ladybug, Vector2D, LadybugModel}
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.util.PImageFactory
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.umd.cs.piccolo.PNode
import java.awt.Color
import java.awt.geom.{AffineTransform, Point2D}

import umd.cs.piccolo.event.{PInputEventListener, PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.{PPath, PImage}
import LadybugUtil._

class LadybugNode(model: LadybugModel, ladybug: Ladybug, transform: ModelViewTransform2D, vectorVisibilityModel: VectorVisibilityModel) extends PNode {
  var interactive = true
  model.addListener((m: LadybugModel) => {
    updateInteractive()
  })
  def updateInteractive() = {interactive = !model.isPlayback}

  //decorator
  class ToggleListener(listener: PInputEventListener) extends PInputEventListener {
    def processEvent(aEvent: PInputEvent, t: Int) = {
      if (interactive) {
        listener.processEvent(aEvent, t)
      }
    }
  }

  val arrowSetNode = new ArrowSetNode(ladybug, transform, vectorVisibilityModel)
  val pimage = new PImage(MovingManResources.loadBufferedImage("ladybug/ladybug.png"))

  ladybug.addListener(updateLadybug)
  updateLadybug(ladybug)

  addChild(arrowSetNode)
  addChild(pimage)

  transform.addTransformListener(new TransformListener() {
    def transformChanged(mvt: ModelViewTransform2D) = {
      updateLadybug(ladybug)
    }
  })

  //  val cursorHandler = new CursorHandler
  addInputEventListener(new ToggleListener(new CursorHandler))
  val inputHandler = new PBasicInputEventHandler() {
    override def mouseDragged(event: PInputEvent) = {
      model.startRecording()

      val diff = transform.viewToModelDifferential(event.getDeltaRelativeTo(getParent).width, event.getDeltaRelativeTo(getParent).height)
      ladybug.translate(diff)
    }

    override def mousePressed(event: PInputEvent) = {
      model.startRecording()
    }
  }
  addInputEventListener(new ToggleListener(inputHandler))
  updateInteractive()

  def updateLadybug(ladybug: Ladybug): Unit = {

    val modelPosition = ladybug.getPosition
    val viewPosition = transform.modelToView(modelPosition)
    pimage.setTransform(new AffineTransform)
    val dx = new Vector2D(pimage.getImage.getWidth(null), pimage.getImage.getHeight(null))

    pimage.translate(viewPosition.x - dx.x / 2, viewPosition.y - dx.y / 2)
    pimage.rotateAboutPoint(ladybug.getAngle,
      pimage.getFullBounds.getCenter2D.getX - (viewPosition.x - dx.x / 2),
      pimage.getFullBounds.getCenter2D.getY - (viewPosition.y - dx.y / 2))

  }
}