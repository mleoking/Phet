package edu.colorado.phet.scalacommon.record

import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils
import java.awt._
import edu.colorado.phet.common.piccolophet.event.ToolTipHandler
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.DefaultIconButton
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PlayPauseButton
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.RewindButton
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.StepButton
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import scala.collection.mutable.ArrayBuffer
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources
import edu.colorado.phet.common.phetcommon.resources.PhetResources
import java.awt.event.{ActionEvent, ComponentAdapter, ComponentEvent, ActionListener}

import javax.swing._
import java.util.{Hashtable, Dictionary}
import javax.swing._
import edu.colorado.phet.scalacommon.util.Observable
import edu.umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import edu.umd.cs.piccolo.nodes.{PImage, PText}
import edu.umd.cs.piccolo.PNode
import edu.umd.cs.piccolo.util.PBounds
import edu.umd.cs.piccolox.pswing.PSwing
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloTimeControlPanel.BackgroundNode
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources._

class RecordModelControlPanel[T](model: RecordModel[T], simPanel: JComponent, createRightControl: () => PNode, timelineColor: Color, maxTime: Double) extends PhetPCanvas {
  private class MyButtonNode(text: String, icon: Icon, action: () => Unit) extends PText(text) {
    addInputEventListener(new PBasicInputEventHandler() {
      override def mousePressed(event: PInputEvent) = {action()}
    })
  }

  private val nodes = new ArrayBuffer[PNode]
  private val prefSizeM = new Dimension(800, 100)
  setBorder(null)
  setBackground(new JPanel().getBackground)

  protected def addControl(node: PNode) = {
    addScreenChild(node)
    val offsetX: Double = if (nodes.length == 0) 0 else {nodes(nodes.length - 1).getFullBounds.getMaxX + 5}
    node.setOffset(offsetX, node.getOffset.getY + 10)
    nodes += node
  }

  implicit def stringToIcon(string: String): Icon = new ImageIcon(PhetCommonResources.getImage("clock/" + string))

  implicit def functionToButtonListener(f: () => Unit): DefaultIconButton.Listener = new DefaultIconButton.Listener() {
    def buttonPressed = {f()}
  }

  val backgroundNode = new BackgroundNode;
  addScreenChild(backgroundNode)

  val modePanel = new ModePanel(model)


  val clearButton = new JButton(getString("Common.clear"))

  clearButton.addActionListener(() => { //todo : couldn't figure out how to remove ()=> with by name using implicits
    model.clearHistory
    model.setPaused(true)
    model.setRecord(true)
  })


  val rightmostControl = createRightControl()
  rightmostControl.setOffset(0, prefSizeM.getHeight / 2 - rightmostControl.getFullBounds.getHeight / 2)

  val rewind = new RewindButton(50)
  rewind.addListener(() => {
    model.setRecord(false)
    model.setPlaybackIndexFloat(0.0)
    model.setPaused(true)
  })
  model.addListenerByName(updateRewindEnabled)
  updateRewindEnabled
  def updateRewindEnabled = {
    val enabled = model.isPlayback && model.getRecordingHistory.length > 0 && model.getTime != model.getMinRecordedTime
    rewind.setEnabled(enabled)
  }
  rewind.addInputEventListener(new ToolTipHandler(getString("Common.rewind"), this))
  rewind.setOffset(0, 12)


  val clearButtonNode = new PSwing(clearButton)
  val modePanelNode = new PSwing(modePanel)


  val playPause = new PlayPauseButton(75)
  playPause.addListener(new PlayPauseButton.Listener() {
    def playbackStateChanged = model.setPaused(!playPause.isPlaying)
  })
  val playPauseTooltipHandler = new ToolTipHandler(getString("Common.ClockControlPanel.Pause"), this)
  playPause.addInputEventListener(playPauseTooltipHandler)

  def updatePlayPauseButton() = {
    playPause.setPlaying(!model.isPaused)
    playPauseTooltipHandler.setText(if (model.isPaused) getString("Common.ClockControlPanel.Play") else getString("Common.ClockControlPanel.Pause"))
  }
  model.addListener(() => updatePlayPauseButton())
  updatePlayPauseButton()

  val stepButton = new StepButton(50)
  stepButton.setEnabled(false)
  stepButton.addInputEventListener(new ToolTipHandler(getString("Common.ClockControlPanel.Step"), this))
  model.addListener(() => {
    val isLastStep = model.getPlaybackIndex == model.getRecordingHistory.length
    stepButton.setEnabled(model.isPaused && !isLastStep)
  })
  stepButton.addListener(() => {
    if (model.isPlayback) model.stepPlayback()
    else if (model.isRecord) model.stepRecord()
  })
  stepButton.setOffset(0, 12)

  addControl(clearButtonNode)
  addControl(modePanelNode)
  addControl(rewind)
  addControl(playPause)
  addControl(stepButton)
  addControl(rightmostControl)

  val timeline = new Timeline(model, this, timelineColor, maxTime)
  addScreenChild(timeline)

  setPreferredSize(prefSizeM)
  def updateSize = {
    if (simPanel.getWidth > 0) {
      val pref = new Dimension(simPanel.getWidth(), prefSizeM.height)
      setPreferredSize(pref)
      updateLayout
    }
  }
  simPanel.addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) = {
      SwingUtilities.invokeLater(new Runnable() {
        def run = {updateSize}
      })

    }
  })
  updateSize
  addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) = {myUpdateLayout()}
  })

  myUpdateLayout
  def myUpdateLayout() = {
    val buttonDX = 2
    playPause.setOffset(getPreferredSize.width / 2 - playPause.getFullBounds.getWidth / 2, playPause.getOffset.getY)
    rewind.setOffset(playPause.getFullBounds.getX - rewind.getFullBounds.getWidth - buttonDX, rewind.getOffset.getY)
    stepButton.setOffset(playPause.getFullBounds.getMaxX + buttonDX, stepButton.getOffset.getY)
    rightmostControl.setOffset(stepButton.getFullBounds.getMaxX, rightmostControl.getOffset.getY)

    modePanelNode.setOffset(rewind.getFullBounds.getX - modePanelNode.getFullBounds.width, playPause.getFullBounds.getCenterY - modePanelNode.getFullBounds.getHeight / 2)
    clearButtonNode.setOffset(modePanelNode.getFullBounds.getX - clearButtonNode.getFullBounds.width, playPause.getFullBounds.getCenterY - clearButtonNode.getFullBounds.getHeight / 2)

    val halfWidth = playPause.getFullBounds.getCenterX - rightmostControl.getFullBounds.getMaxX
    val blist = for (n <- nodes) yield n.getFullBounds
    val b: PBounds = blist.foldLeft(blist(0))((a, b) => new PBounds(a.createUnion(b)))
    val expanded = RectangleUtils.expand(b, 0, 0)
    backgroundNode.setSize((halfWidth * 2).toInt, expanded.getHeight.toInt)
    backgroundNode.setOffset(playPause.getFullBounds.getCenterX - halfWidth, expanded.getY)
  }
}