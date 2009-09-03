package edu.colorado.phet.motionseries.tests

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.nodes.PhetPPath
import common.piccolophet.PhetPCanvas
import java.awt.event.{ComponentEvent, ComponentAdapter}
import java.awt.geom.{Ellipse2D, Rectangle2D}
import java.awt.{Color, BasicStroke, Component}
import javax.swing.JFrame
import scalacommon.util.Observable
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode

object TestCoordinateFrames {
  def main(args: Array[String]) {
    new StartTest().start()
  }
}

class StageNode(stage: Stage, canvas: Component, node: PNode) extends PNode {
  addChild(node)
  canvas.addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) = updateLayout()
  })
  stage.addListener(() => updateLayout())
  updateLayout()
  def updateLayout() = {
    val canvasWidth = canvas.getWidth
    val canvasHeight = canvas.getHeight
    val widthScale = canvasWidth.toDouble / stage.width
    val heightScale = canvasHeight.toDouble / stage.height
    val scale = Math.min(widthScale, heightScale)
    val patchedScale = if (scale > 0) scale else 1.0
    setScale(patchedScale)

    val scaledWidth = patchedScale * stage.width
    val scaledHeight = patchedScale * stage.height
    setOffset(canvasWidth / 2 - scaledWidth / 2, canvasHeight / 2 - scaledHeight / 2)
  }
}

class ModelNode(transform: ModelViewTransform2D, node: PNode) extends PNode {
  addChild(node)
  setTransform(transform.getAffineTransform)
}

class MyPText(str: String, x: Double, y: Double, scale: Double) extends PText(str) {
  def this(str: String, x: Double, y: Double) = this (str, x, y, 1.0)
  setScale(scale)
  setOffset(x, y)
}

class Stage(private var _width: Double, private var _height: Double) extends Observable {
  def width = _width

  def height = _height

  def setSize(w: Double, h: Double) = {
    this._width = w
    this._height = h
    notifyListeners()
  }
}

class MyCanvas(stageWidth: Int, stageHeight: Int, modelBounds: Rectangle2D.Double) extends PhetPCanvas {
  val stage = new Stage(stageWidth, stageHeight)
  val transform = new ModelViewTransform2D(modelBounds, new Rectangle2D.Double(0, 0, stageWidth, stageHeight))

  def setStageBounds(w: Double, h: Double) = {
    stage.setSize(w, h)
    transform.setViewBounds(new Rectangle2D.Double(0, 0, w, h))
  }

  def addScreenNode(node: PNode) = getLayer.addChild(node)

  def addStageNode(node: PNode) = addScreenNode(new StageNode(stage, this, node))

  def addModelNode(node: PNode) = addStageNode(new ModelNode(transform, node))
}

class StartTest {
  def start() = {
    val stageWidth = 200
    val stageHeight = 100
    val modelBounds = new Rectangle2D.Double(0, 0, 2E-6, 1E-6)
    val canvas = new MyCanvas(stageWidth, stageHeight, modelBounds)
    canvas.addScreenNode(new MyPText("Hello from screen at 50,50", 50, 50))
    val stageText = new MyPText("Hello from Stage at 100,50", 100, 50, 0.5)
    canvas.addStageNode(stageText)
    canvas.addStageNode(new PhetPPath(new Rectangle2D.Double(0, 0, stageWidth, stageHeight), new BasicStroke(2), Color.yellow))
    canvas.addScreenNode(new MyPText("Hello from screen at 100,100", 100, 100))
    canvas.addModelNode(new PhetPPath(new Ellipse2D.Double(0, 0, 0.5E-6, 0.5E-6), Color.blue))
    canvas.addModelNode(new MyPText("hello from left edge of world bounds", modelBounds.getMinX, modelBounds.getCenterY, 1E-6 / 100))

    //center one node beneath another, though they be in different coordinate frames
    val rectNode = new PhetPPath(new Rectangle2D.Double(0, 0, 50, 10), Color.red)
    canvas.addScreenNode(rectNode)
    def updateRectNodeLocation() = {
      var rectNodeBounds = rectNode.globalToLocal(stageText.getGlobalFullBounds)
      rectNodeBounds = rectNode.localToParent(rectNodeBounds)
      rectNode.setOffset(rectNodeBounds.getCenterX - rectNode.getFullBounds.getWidth / 2, rectNodeBounds.getMaxY)
    }
    updateRectNodeLocation()
    //coordinates can change, so need to update when they do
    canvas.addComponentListener(new ComponentAdapter() {override def componentResized(e: ComponentEvent) = {updateRectNodeLocation()}})

    //todo: compute stage bounds dynamically, based on contents of the stage
    //todo: maybe stage bounds should be mutable, since it is preferable to create the nodes as children of the canvas

    //todo: how to implement pan/zoom with this paradigm

    canvas.setStageBounds(200,200)

    val frame = new JFrame
    frame.setContentPane(canvas)
    frame.setSize(800, 600)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setVisible(true)
  }
}