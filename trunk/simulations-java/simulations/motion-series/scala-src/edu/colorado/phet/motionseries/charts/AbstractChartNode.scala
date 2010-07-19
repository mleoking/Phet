package edu.colorado.phet.motionseries.charts
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
//package edu.colorado.phet.motionseries.charts
//
//import edu.colorado.phet.motionseries.Predef._
//import edu.colorado.phet.motionseries.graphics.MotionSeriesCanvas
//import java.awt.event._
//import edu.colorado.phet.common.phetcommon.view.util.PhetFont
//import edu.colorado.phet.common.motion.graphs._
//import edu.colorado.phet.common.motion.model._
//import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock
//import edu.colorado.phet.common.timeseries.model.{RecordableModel, TimeSeriesModel}
//import edu.colorado.phet.motionseries.model.MotionSeriesModel
//import edu.colorado.phet.motionseries.MotionSeriesDefaults
//
//import edu.umd.cs.piccolo.PNode
//import edu.colorado.phet.scalacommon.math.Vector2D
//import edu.colorado.phet.motionseries.javastage.stage.PlayArea
//import javax.swing._
//import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel.HistoryClearListener
//import edu.colorado.phet.common.phetcommon.util.{SimpleObserver, DefaultDecimalFormat}
//import java.awt.geom.Rectangle2D
//import edu.colorado.phet.common.motion.charts._
//import edu.umd.cs.piccolox.pswing.PSwing
//import java.beans.{PropertyChangeEvent, PropertyChangeListener}
//import java.awt.{Color, FlowLayout}
//
//case class Graph(title: String, graph: MinimizableControlChart, minimized: Boolean)
//
object Defaults {
  def createFont = new PhetFont(12, true)

////  def addListener(series: IDataSeries, listener: () => Unit) = {
////    series.addListener(new ControlGraphSeries.Adapter() {
////      override def visibilityChanged = listener()
////    })
////  }
}
//
//abstract class AbstractChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends PNode {
//  def createVariable(getter: () => Double) = {
//    val variable = new MotionSeriesDefaultTemporalVariable(model)
//    model.stepListeners += (() => variable.doAddValue(getter(), model.getTime))
//    model.resetListeners_+=(() => variable.doSetValue(getter()))
//    model.playbackListeners += (() => variable.doSetValue(getter()))
//    model.addObserver(new SimpleObserver() {
//      def update = variable.doSetValue(getter()) //todo: does this ever get called when it shouldn't, such as too many times or duplicate of stepListener above?
//    })
//
//    variable
//  }
//
//  //Create a variable for the parallel component of the vector specified
//  def createParallelVariable(getter: () => Vector2D) = createVariable(() => getter().dot(model.bead.getRampUnitVector))
//
//  val recordableModel = new RecordableModel() {
//    def getState: Object = model.getTime.asInstanceOf[Object]
//
//    def resetTime = {}
//
//    def clear = {}
//
//    def setState(o: Any) = model.setTime(o.asInstanceOf[Double])
//
//    def stepInTime(simulationTimeChange: Double) = {}
//  }
//
//  val timeseriesModel = new TimeSeriesModel(recordableModel, new ConstantDtClock(30, 1.0)) { //todo: remove dummy clock
//    override def setPlaybackTime(requestedTime: Double) = model.setTime(requestedTime) //skip bounds checking in parent
//  }
//  model.addHistoryClearListener(new HistoryClearListener {
//    def historyCleared = timeseriesModel.clear(true)
//  })
//  //  model.historyRemainderClearListeners += (() => {
//  //    //    timeseriesModel.clear(true) //todo: how did this clear the serieses?  By listener chaining.
//  //    //    for (pt <- model.recordHistory) timeseriesModel.addSeriesPoint(pt.state,pt.time)
//  //  })
//
//  val updateableObject = new UpdateableObject {
//    def setUpdateStrategy(updateStrategy: UpdateStrategy) = {}
//  }
//  import edu.colorado.phet.motionseries.MotionSeriesResources._
//  val N = "units.abbr.newtons".translate
//  val J = "units.abbr.joules".translate
//  val characterUnused = "".literal
//  val abbrevUnused = "".literal
//
//
//  canvas.addComponentListener(new ComponentAdapter() {override def componentResized(e: ComponentEvent) = {updateLayout()}})
//
//  def updateLayout() = {
//    val y = canvas.modelToScreen(0, -1).getY
//    val h = canvas.getHeight - y
//    val padX = 2
//    val padY = padX
//    _graphSetNode.setBounds(padX, y + padY, canvas.getWidth - padX * 2, h - padY * 2)
//  }
//
//  private var _graphSetNode: MyGraphSetNode = null
//
//  def init(graphs: Seq[Graph]) = {
//    //    correlateDomains(graphs)
//    //    val minimizableGraphs = for (g <- graphs) yield new MinimizableControlGraph(g.title, g.graph, g.minimized, model)
//    _graphSetNode = new MyGraphSetNode(graphs) {
//      //      override def getMaxAvailableHeight(availableHeight: Double) = availableHeight
//      //      setAlignedLayout()
//    }
//    addChild(_graphSetNode)
//    updateLayout()
//  }
//}
////Adds the clear button to the strip also containing the close button
//class MinimizableMotionSeriesChart(controlChart: ControlChart,model:MotionSeriesModel)
//        extends MinimizableControlChart("title",controlChart) {
//  val clearButton = new PSwing(new JButton(new AbstractAction("controls.clear".translate) {
//    def actionPerformed(e: ActionEvent) = {
//      //todo: coalesce with RecordModelControlPanel, duplicated from there
//      model.clearHistory()
//      model.setPaused(true)
//      model.setRecord(true)
//    }
//  }))
//
//  def updateClearButtonVisible() = clearButton.setVisible(model.getNumRecordedPoints > 0)
//  model.addListenerByName {
//    updateClearButtonVisible()
//  }
//  updateClearButtonVisible()
//
//  //todo: should use the bounds of the minimize button
////  def updateClearButtonLocation() = clearButton.setOffset(getCloseButton.getFullBounds.getX - clearButton.getFullBounds.getWidth, 5)
////  updateClearButtonLocation()
//
////  getCloseButton.addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener {
////    def propertyChange(evt: PropertyChangeEvent) = updateClearButtonLocation()
////  })
////  getCloseButton.getParent.addChild(clearButton) //todo: when code freeze is lifted, improve API on minimizable control graph for adding buttons
////  
////  def getCloseButton = new PNode
//  addChild(clearButton)
//}
//class MyGraphSetNode(graphs: Seq[Graph]) extends MultiControlChart((for (g <- graphs) yield g.graph).toArray) {
//}
//
//case class IDataSeries(title:String,color:Color,abbr:String,units:String,character:String,temporalSeries:TemporalDataSeries){
//  def setVisible(b:Boolean){}
//  def getTitle = title
//  def isVisible = true
//  def getTemporalVariable = new DefaultTemporalVariable
//}
//
//class MotionSeriesGraph(defaultSeries: IDataSeries,
//                        timeseriesModel: TimeSeriesModel,
//                        updateableObject: UpdateableObject,
//                        model: MotionSeriesModel,
//                        minRangeValue: Double,
//                        maxRangeValue: Double)
//        extends TemporalChart(new Rectangle2D.Double(0, -10, 20, 20), new ChartCursor()) { //canvas, defaultSeries, "".literal, "".literal, minRangeValue, maxRangeValue, true, timeseriesModel, updateableObject) {
//  
//  def toMinimizableControlChart = {
//    new MinimizableMotionSeriesChart(new ControlChart(new PNode(),new PNode(),this,new ChartZoomControlNode(this)),model)
//  }
//  
//  def addControl(control: JComponent) = {
//  }
//
//  def setEditable(b: Boolean) = {}
//
//  def addSeries(series: IDataSeries) = {
//    super.addDataSeries(series.temporalSeries,series.color)
//  }
//
//  def reset() = {
//    getDataModelBounds.setHorizontalRange(0, MotionSeriesDefaults.MAX_CHART_DISPLAY_TIME)
//    getDataModelBounds.setVerticalRange(minRangeValue, maxRangeValue)
//  }
//
//  model resetListeners_+= reset
//  reset()
//
//  //  override def createSliderNode(thumb: PNode, highlightColor: Color) = {
//  ////    new JFreeChartSliderNode(getJFreeChartNode, thumb, highlightColor) {
//  ////      val text = new ShadowHTMLNode(defaultSeries.getTitle)
//  ////      text.setFont(new PhetFont(14, true))
//  ////      text.setColor(defaultSeries.getColor)
//  ////      text.rotate(-java.lang.Math.PI / 2)
//  ////      val textParent = new PNode
//  ////      textParent.addChild(text)
//  ////      textParent.setPickable(false)
//  ////      textParent.setChildrenPickable(false)
//  ////      addChild(textParent)
//  ////
//  ////      override def updateLayout() = {
//  ////        if (textParent != null) setSliderDecorationInset(textParent.getFullBounds.getWidth + 5)
//  ////        super.updateLayout()
//  ////        if (textParent != null)
//  ////          textParent.setOffset(getTrackFullBounds.getX - textParent.getFullBounds.getWidth - getThumbFullBounds.getWidth / 2,
//  ////            getTrackFullBounds.getY + textParent.getFullBounds.getHeight + getTrackFullBounds.getHeight / 2 - textParent.getFullBounds.getHeight / 2)
//  ////      }
//  ////      updateLayout()
//  ////    }
//  //  }
//
//  //todo: a more elegant solution would be to make MotionControlGraph use an interface, then to write an adapter
//  //for the existing recording/playback model, instead of overriding bits and pieces to obtain this functionality
//
//  //  override def getCursorShouldBeVisible = model.isPlayback
//  //  model addListener updateCursorVisible
//  //
//  //  override def getMaxCursorDragTime = model.getMaxRecordedTime
//  //  model addListener updateCursorMaxDragTime
//  //
//  //  override def getPlaybackTime = model.getTime
//  //  model addListener updateCursorLocation
//  //
//  //  override def createGraphTimeControlNode(timeSeriesModel: TimeSeriesModel) = new GraphTimeControlNode(timeSeriesModel) {
//  //    override def setEditable(editable: Boolean) = {
//  //      super.setEditable(false)
//  //    }
//  //  }
//  //
//  //  override def createReadoutTitleNode(series: ControlGraphSeries) = null
//}