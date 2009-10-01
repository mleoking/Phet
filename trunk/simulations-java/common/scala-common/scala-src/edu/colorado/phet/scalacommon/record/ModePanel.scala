package edu.colorado.phet.scalacommon.record

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import scala.swing.{Component, Panel}
import java.awt.Color
import javax.swing.BoxLayout._
import javax.swing.{BoxLayout, JPanel, JComponent}
import java.awt.Color._
import edu.colorado.phet.scalacommon.swing.MyRadioButton
import PhetCommonResources._

class ModePanel[T](model: RecordModel[T]) extends JPanel {
  setLayout(new BoxLayout(this, Y_AXIS))
  setBackground(new Color(0, 0, 0, 0))

  val recordingButton = addComponent{
    new MyRadioButton(getString("Common.record"), model.setRecord(true), model.isRecord, model.addListener) {
      font = new PhetFont(15, true)
    }
  }
  recordingButton.peer.setBackground(new Color(0, 0, 0, 0))
  val playbackButton = addComponent{
    def handlePressed() = {
      model.setRecord(false)
      model.setPlaybackIndexFloat(0.0)
      model.setPaused(true)
    }
    new MyRadioButton(getString("Common.playback"), handlePressed(), model.isPlayback, model.addListener) {
      font = new PhetFont(15, true)
    }
  }
  playbackButton.peer.setBackground(new Color(0, 0, 0, 0))

  addListener(model.addListenerByName){
    def color(b: Boolean) = if (b) red else black
    recordingButton.peer.setForeground(color(recordingButton.peer.isSelected && !model.isPaused && !model.isRecordingFull))
    playbackButton.peer.setForeground(color(playbackButton.peer.isSelected && !model.isPaused))
  }

  //a control structure that (1) creates a swing component and (2) automatically adds it
  //a suitable replacement for something like
  //val button=createButton
  //add(button)
  def addComponent[T <: Component](m: => T): T = {
    val component = m
    add(component.peer)
    component
  }

  //adds a listener to some model, and also invokes the update implementation
  //a suitable replacement for something like:
  //model.addListener(update)
  //update
  def addListener(addListener: (=> Unit) => Unit)(updateFunction: => Unit) = {
    addListener(updateFunction)
    updateFunction
  }
}