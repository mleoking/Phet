package edu.colorado.phet.ladybugmotion2d.canvas

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{Ellipse2D, GeneralPath, Point2D}
import model.{LadybugState, LadybugModel}

import scalacommon.math.Vector2D
import scalacommon.util.Observable
import scalacommon.record.{DataPoint, RecordModel}
import umd.cs.piccolo.nodes.PPath
import umd.cs.piccolo.PNode
import java.awt.{BasicStroke, Color}
import java.lang.Math._
import scalacommon.Predef._

class LadybugDotTraceNode(model: LadybugModel, transform: ModelViewTransform2D,
                          shouldBeVisible: () => Boolean, observable: Observable, maxFade: Double)
        extends LadybugTraceNode(model, transform, shouldBeVisible, observable) {
  val node = new PNode
  addChild(node)

  //have to cache pnodes for performance reasons
  val nodeCache = scala.collection.mutable.Map.empty[Vector2D, DotNode]

  update()

  class DotNode(val point: Vector2D, dt: Double) extends PNode {
    val path = new PhetPPath(new Ellipse2D.Double(point.getX - 5, point.getY - 5, 10, 10), toColor(dt, maxFade))
    addChild(path)

    def setDT(dt: Double) = path.setPaint(toColor(dt, maxFade))
  }

  def update() = {
    implicit def historyToPoint(dataPoint: DataPoint[LadybugState]) = new Point2D.Float(dataPoint.state.position.x.toFloat, dataPoint.state.position.y.toFloat)

    //todo: some code duplicated with LadbyugFadeTraceNode
    var unusedKeys = scala.collection.mutable.Set.empty[Vector2D]
    unusedKeys ++= nodeCache.keySet.elements
    for (h <- getHistoryToShow()) {
      val pt: Point2D.Float = h
      val tx = transform.modelToView(pt)
      val dt = abs(model.getTime - h.time)

      unusedKeys -= tx
      try {
        //cache checks are very expensive, only do it once
        nodeCache(tx).setDT(dt)
      } catch {
        case e: NoSuchElementException => {
          val dotNode = new DotNode(tx, dt)
          node.addChild(dotNode)
          nodeCache(tx) = dotNode
        }
        case _ => {}
      }

    }
    for (a <- unusedKeys) {
      node.removeChild(nodeCache(a))
    }
    nodeCache --= unusedKeys
  }
}