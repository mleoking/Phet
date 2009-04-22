package edu.colorado.phet.therampscala.graphics

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.PhetFont
import common.piccolophet.nodes.{PhetPPath, ArrowNode}
import common.piccolophet.PhetPCanvas
import java.awt.geom.{Point2D, Rectangle2D}
import java.awt.{BasicStroke, Color}
import javax.swing.JFrame
import scalacommon.math.Vector2D
import scalacommon.util.Observable
import umd.cs.piccolo.nodes.{PText, PPath}
import umd.cs.piccolo.PNode
import scalacommon.Predef._

trait Vector extends Observable {
  def getValue: Vector2D

  def getColor: Color
}
class FreeBodyDiagramNode(val width: Int, val height: Int, val modelWidth: Double, val modelHeight: Double, vectors: Vector*) extends PNode {
  val transformT = new ModelViewTransform2D(new Rectangle2D.Double(-modelWidth / 2, -modelHeight / 2, modelWidth, modelHeight),
    new Rectangle2D.Double(0, 0, width, height), true)
  val background = new PhetPPath(new Rectangle2D.Double(0, 0, width, height), Color.white, new BasicStroke(2), Color.darkGray)
  addChild(background)
  val arrowInset = 4

  class AxisNode(x0: Double, y0: Double, x1: Double, y1: Double, label: String) extends PNode {
    val axisNode = new ArrowNode(new Point2D.Double(x0, y0), new Point2D.Double(x1, y1), 5, 5, 2)
    axisNode.setStroke(null)
    axisNode.setPaint(Color.black)
    addChild(axisNode)
    val text = new PText(label)
    text.setFont(new PhetFont(16, true))
    text.setOffset(x1 - text.getFullBounds.getWidth * 1.5, y1)
    addChild(text)
  }
  addChild(new AxisNode(0 + arrowInset, height / 2, width - arrowInset, height / 2, "x"))
  addChild(new AxisNode(width / 2, height - arrowInset, width / 2, 0 + arrowInset, "y"))
  for (vector <- vectors) addVector(vector)

  class VectorNode(val vector: Vector) extends PNode {
    val arrowNode = new ArrowNode(transformT.modelToViewDouble(0, 0), transformT.modelToViewDouble(vector.getValue), 20, 20, 10)
    vector.addListenerByName(arrowNode.setTipAndTailLocations(transformT.modelToViewDouble(vector.getValue), transformT.modelToViewDouble(0, 0)))
    arrowNode.setPaint(vector.getColor)
    addChild(arrowNode)
  }
  def addVector(vector: Vector) = addChild(new VectorNode(vector))
}

object TestFBD extends Application {
  val frame = new JFrame
  val canvas = new PhetPCanvas
  canvas.addScreenChild(new FreeBodyDiagramNode(200, 200, 20, 20, new Vector() {
    def getValue = new Vector2D(5, 5)

    def getColor = Color.blue
  }))
  frame.setContentPane(canvas)
  frame.setSize(800, 600)
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.setVisible(true)
}