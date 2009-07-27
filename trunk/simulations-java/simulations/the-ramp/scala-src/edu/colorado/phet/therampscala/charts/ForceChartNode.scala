package edu.colorado.phet.therampscala.charts

import common.phetcommon.view.util.{PhetFont}
import common.motion.graphs._
import common.motion.model._
import common.phetcommon.model.clock.ConstantDtClock
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.VerticalLayoutPanel
import common.piccolophet.{PhetPCanvas}
import common.timeseries.model.{RecordableModel, TimeSeriesModel}
import java.awt.geom.Point2D
import java.text.DecimalFormat
import javax.swing.{JPanel, JLabel}
import model.{RampModel}
import swing.MyCheckBox
import umd.cs.piccolo.PNode
import scalacommon.math.Vector2D

class ForceChartNode(transform: ModelViewTransform2D, canvas: PhetPCanvas, model: RampModel) extends PNode {
  val parallelAppliedForceVariable = new DefaultTemporalVariable() {
    override def setValue(value: Double) = model.bead.parallelAppliedForce = value
  }
  model.stepListeners += (() => parallelAppliedForceVariable.addValue( model.bead.appliedForce.dot(new Vector2D(model.bead.getVelocityVectorDirection)), model.getTime))

  def createVariable(getter: () => Vector2D) = {
    val variable = new DefaultTemporalVariable()
    model.stepListeners += (() => variable.addValue(getter().dot(new Vector2D(model.bead.getVelocityVectorDirection)), model.getTime))
    variable
  }

  val parallelFriction = createVariable(() => model.bead.frictionForce)
  val gravityForce = createVariable(() => model.bead.gravityForce)
  val wallForce = createVariable(() => model.bead.wallForce)

  val recordableModel = new RecordableModel() {
    def getState = "hello"

    def resetTime = {}

    def clear = {}

    def setState(o: Any) = {}

    def stepInTime(simulationTimeChange: Double) = {}
  }
  val timeseriesModel = new TimeSeriesModel(recordableModel, new ConstantDtClock(30, 1.0))
  val updateableObject = new UpdateableObject {
    def setUpdateStrategy(updateStrategy: UpdateStrategy) = {}
  }
  val appliedForceSeries = new ControlGraphSeries("Parallel Applied Force", RampDefaults.appliedForceColor, "Fa", "N", "", parallelAppliedForceVariable)
  val frictionSeries = new ControlGraphSeries("Parallel Friction Force", RampDefaults.frictionForceColor, "Ff", "N", "", parallelFriction)
  val gravitySeries = new ControlGraphSeries("Parallel Gravity Force", RampDefaults.gravityForceColor, "Fg", "N", "", gravityForce)
  val wallSeries = new ControlGraphSeries("Parallel Wall Force", RampDefaults.wallForceColor, "Fw", "N", "", wallForce)
  val parallelForceChart = new MotionControlGraph(canvas, appliedForceSeries, "label", "title", -2000, 2000, true, timeseriesModel, updateableObject) {
    setDomainUpperBound(20)
    getJFreeChartNode.setBuffered(false)
    getJFreeChartNode.setPiccoloSeries() //works better on an unbuffered chart
    addSeries(frictionSeries)
    addSeries(gravitySeries)
    addSeries(wallSeries)
  }

  def addListener(series: ControlGraphSeries, listener: () => Unit) = {
    series.addListener(new ControlGraphSeries.Adapter() {
      override def visibilityChanged = listener()
    })
  }
  class SeriesControlSelectorBox(series: ControlGraphSeries) extends MyCheckBox(series.getTitle, series.setVisible(_), series.isVisible, addListener(series, _))
  class SeriesControlSelector(series: ControlGraphSeries) extends JPanel {
    add(new SeriesControlSelectorBox(series).peer)
    val label = new JLabel("hello there, long label")
    series.getTemporalVariable.addListener(new ITemporalVariable.Listener() {
      def dataCleared = {}

      def dataAdded(data: TimeData) = {}

      def valueChanged = label.setText(new DecimalFormat("0.00").format(series.getTemporalVariable.getValue) + "")
    })
    add(label)
  }
  class SeriesSelectionControl extends VerticalLayoutPanel {
    val jLabel = new JLabel("Parallel Forces (N)")
    jLabel.setFont(new PhetFont(20, true))
    add(jLabel)
    add(new SeriesControlSelector(appliedForceSeries))
    add(new SeriesControlSelector(frictionSeries))
    add(new SeriesControlSelector(gravitySeries))
    add(new SeriesControlSelector(wallSeries))
  }
  parallelForceChart.addControl(new SeriesSelectionControl)

  //  val y = new MotionControlGraph(canvas, controlGraphSeries, "label", "title", 0, 10, true, timeseriesModel, updateableObject) {
  //    setDomainUpperBound(20)
  //    getJFreeChartNode.setBuffered(false)
  //    getJFreeChartNode.setPiccoloSeries()
  //  }
  //  val set = new GraphSetNode(new GraphSetModel(new GraphSuite(Array(new MinimizableControlGraph("x", x), new MinimizableControlGraph("y", y)))))
  val graphSetNode = new GraphSetNode(new GraphSetModel(new GraphSuite(Array(new MinimizableControlGraph("Parallel Forces(N)", parallelForceChart))))) {
    override def getMaxAvailableHeight(availableHeight: Double) = availableHeight
  }
  graphSetNode.setAlignedLayout()

  addChild(graphSetNode)

  def updatePosition() = {
    val viewLoc = transform.modelToView(new Point2D.Double(0, -1))
    val viewBounds = transform.getViewBounds
    val h = viewBounds.getHeight - viewLoc.y
    graphSetNode.setBounds(viewBounds.getX, viewLoc.y, viewBounds.getWidth, h)
  }
  updatePosition()
}