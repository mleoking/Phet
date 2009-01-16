package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.view.ControlPanel
import _root_.edu.colorado.phet.common.phetcommon.view.ResetAllButton
import _root_.scala.swing._
import java.awt.Dimension
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{Box, JButton, JRadioButton, JLabel}

class LadybugControlPanel(module: LadybugModule) extends ControlPanel(module) {
  val myModule = module;
  def createBox = Box.createRigidArea(new Dimension(10, 10))

  implicit def scalaSwingToAWT(component: Component) = component.peer

  class VectorControlPanel(m: VectorVisibilityModel) extends BoxPanel(Orientation.Vertical) {
    contents += new MyRadioButton("Show velocity vector", {
      m.velocityVectorVisible = true
      m.accelerationVectorVisible = false
    }
      , m.velocityVectorVisible && !m.accelerationVectorVisible,
      m)

    contents += new MyRadioButton("Show acceleration vector", {
      m.velocityVectorVisible = false
      m.accelerationVectorVisible = true
    }
      , !m.velocityVectorVisible && m.accelerationVectorVisible,
      m)

    contents += new MyRadioButton("Show both", {
      m.velocityVectorVisible = true
      m.accelerationVectorVisible = true
    }
      , m.velocityVectorVisible && m.accelerationVectorVisible,
      m)

    contents += new MyRadioButton("Hide Vectors", {
      m.velocityVectorVisible = false
      m.accelerationVectorVisible = false
    }
      , !m.velocityVectorVisible && !m.accelerationVectorVisible,
      m)
  }
  addControl(new VectorControlPanel(module.getVectorVisibilityModel))

  addControl(new JLabel("Choose Motion"))
  addControl(new JRadioButton("Manual"))
  addControl(new JRadioButton("Linear"))
  addControl(new JRadioButton("Circular"))
  addControl(new JRadioButton("Ellipse"))
  addControl(createBox)

  class TraceControlPanel(m: PathVisibilityModel) extends BoxPanel(Orientation.Vertical) {
    contents += new Label("Trace")
    contents += new MyRadioButton("Solid", {
      m.lineVisible = true
      m.dotsVisible = false
    }
      , m.lineVisible && !m.dotsVisible,
      m)

    contents += new MyRadioButton("Dots", {
      m.lineVisible = false
      m.dotsVisible = true
    }
      , !m.lineVisible && m.dotsVisible,
      m)

    contents += new MyRadioButton("Off", {
      m.lineVisible= false
      m.dotsVisible = false
    }
      , !m.lineVisible && !m.dotsVisible,
      m)
  }
  addControl(new TraceControlPanel(module.getPathVisibilityModel))
  //  addControl(new JLabel("Trace"))
  //  addControl(new JRadioButton("Solid"))
  //  addControl(new JRadioButton("Dots"))
  //  addControl(new JRadioButton("Off"))
  //  addControl(new JButton("Clear Trace"))
  addControl(createBox)

  addControl(new RemoteControl)
  addControl(createBox)
  addControl(new ResetAllButton(this))
}