package edu.colorado.phet.motionseries.graphics

import phet.common.phetcommon.resources.PhetCommonResources
import motionseries.MotionSeriesResources._
import model.MotionSeriesModel
import umd.cs.piccolo.nodes.PImage
import phet.common.piccolophet.event.CursorHandler
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import scalacommon.Predef._

trait CloseButton extends BeadNode {
  val closeButton = new PImage(PhetCommonResources.getImage("buttons/closeButton.png".literal))
  closeButton.addInputEventListener(new CursorHandler)

  val openButton = new PImage(PhetCommonResources.getImage("buttons/maximizeButton.png".literal))
  openButton.addInputEventListener(new CursorHandler)

  addChild(closeButton)
  addChild(openButton)
  update()

  def model: MotionSeriesModel

  override def update() = {
    super.update()
    if (closeButton != null) {
      closeButton.setOffset(imageNode.getFullBounds.getX, imageNode.getFullBounds.getY)
      openButton.setOffset(imageNode.getFullBounds.getX, imageNode.getFullBounds.getY)
    }
  }
  closeButton.addInputEventListener(new PBasicInputEventHandler {
    override def mousePressed(event: PInputEvent) = model.walls = false
  })
  openButton.addInputEventListener(new PBasicInputEventHandler {
    override def mousePressed(event: PInputEvent) = model.walls = true
  })
  defineInvokeAndPass(model.addListenerByName) {
    imageNode.setVisible(model.walls)
    imageNode.setPickable(model.walls)
    imageNode.setChildrenPickable(model.walls)

    closeButton.setVisible(model.walls)
    closeButton.setPickable(model.walls)
    closeButton.setChildrenPickable(model.walls)

    openButton.setVisible(!model.walls)
    openButton.setPickable(!model.walls)
    openButton.setChildrenPickable(!model.walls)
  }
}