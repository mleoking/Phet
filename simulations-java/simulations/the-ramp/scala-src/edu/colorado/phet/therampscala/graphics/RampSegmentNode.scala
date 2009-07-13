package edu.colorado.phet.therampscala.graphics


import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import java.awt.{Paint, Color, BasicStroke}
import scalacommon.util.Observable
import umd.cs.piccolo.PNode
import common.piccolophet.nodes.PhetPPath
import model.RampSegment
import common.piccolophet.event.CursorHandler

import scalacommon.math.Vector2D
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import scalacommon.Predef._
import java.lang.Math._

trait HasPaint extends PNode {
  def paintColor_=(p: Paint): Unit

  def paintColor: Paint
}

class RampSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D) extends PNode with HasPaint {
  val line = new PhetPPath(new Color(184, 131, 24), new BasicStroke(2f), new Color(91, 78, 49))
  addChild(line)
  defineInvokeAndPass(rampSegment.addListenerByName) {
    line.setPathTo(mytransform.createTransformedShape(new BasicStroke(0.4f).createStrokedShape(rampSegment.toLine2D)))
  }

  def paintColor_=(p: Paint) = line.setPaint(p)

  def paintColor = line.getPaint
}

trait Rotatable extends Observable {
  def startPoint: Vector2D

  def endPoint_=(newPt: Vector2D)

  def length: Double

  def getUnitVector: Vector2D

  def endPoint: Vector2D

  def startPoint_=(newPt: Vector2D)

  def getPivot = new Vector2D

}
class RotationHandler(val mytransform: ModelViewTransform2D,
                      val node: PNode,
                      val rotatable: Rotatable,
                      min: Double,
                      max: Double)
        extends PBasicInputEventHandler {
  override def mouseDragged(event: PInputEvent) = {
    val modelPt = mytransform.viewToModel(event.getPositionRelativeTo(node.getParent))

    val deltaView = event.getDeltaRelativeTo(node.getParent)
    val deltaModel = mytransform.viewToModelDifferential(deltaView.width, deltaView.height)

    val oldPtModel = modelPt - deltaModel

    val oldAngle = (rotatable.getPivot - oldPtModel).getAngle
    val newAngle = (rotatable.getPivot - modelPt).getAngle

    //should be a small delta
    var deltaAngle = newAngle - oldAngle
    while (deltaAngle > PI) deltaAngle = deltaAngle - PI * 2
    while (deltaAngle < -PI) deltaAngle = deltaAngle + PI * 2

    totalDelta += deltaAngle
    val proposedAngle = origAngle + totalDelta

    val angle = if (proposedAngle > max) max else if (proposedAngle < min) min else proposedAngle
    //        println("origAngle="+origAngle+", delta="+deltaAngle+", totalDelta="+totalDelta+", proposedAngle="+proposedAngle+" min="+min+", max="+max+", angle="+angle)

    val newPt = new Vector2D(angle) * rotatable.length
    rotatable.endPoint = newPt
  }

  var totalDelta = 0.0
  var origAngle = 0.0

  override def mousePressed(event: PInputEvent) = {
    totalDelta = 0

    val modelPt = mytransform.viewToModel(event.getPositionRelativeTo(node.getParent))
    val oldAngle = (modelPt - rotatable.getPivot).getAngle
    //    println("model pt="+modelPt+", pivot="+rotatable.getPivot+", oldAngle="+oldAngle)
    origAngle = oldAngle
  }
}

class RotatableSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D) extends RampSegmentNode(rampSegment, mytransform) {
  line.addInputEventListener(new CursorHandler)
  line.addInputEventListener(new RotationHandler(mytransform, line, rampSegment, 0, PI / 2))
}

class ReverseRotatableSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D) extends RampSegmentNode(rampSegment, mytransform) {
  line.addInputEventListener(new CursorHandler)
  line.addInputEventListener(new RotationHandler(mytransform, line, new Reverse(rampSegment).reverse, PI / 2 + 1E-6, PI - (1E-6))) //todo: atan2 returns angle between -pi and +pi, so end behavior is incorrect
}

class Reverse(target: Rotatable) {
  //this one rotates about the end point, facilitates reuse of some view classes while still allowing generalized model objects
  object reverse extends Rotatable {
    def length = target.length

    def startPoint = target.endPoint

    def endPoint = target.startPoint

    def getUnitVector = target.getUnitVector * -1

    def endPoint_=(newPt: Vector2D) = target.startPoint = newPt

    def startPoint_=(newPt: Vector2D) = target.endPoint = newPt

    override def addListenerByName(listener: => Unit) = target.addListenerByName(listener)

    override def notifyListeners() = target.notifyListeners()

    override def removeListener(listener: () => Unit) = target.removeListener(listener)

    override def addListener(listener: () => Unit) = target.addListener(listener)
  }
}



