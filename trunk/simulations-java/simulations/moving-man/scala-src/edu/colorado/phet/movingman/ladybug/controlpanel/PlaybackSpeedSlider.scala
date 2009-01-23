package edu.colorado.phet.movingman.ladybug.controlpanel

import _root_.edu.colorado.phet.common.phetcommon.math.Function.LinearFunction
import _root_.edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl
import java.util.Hashtable
import javax.swing.event.{ChangeListener, ChangeEvent}
import javax.swing.{JSlider, JLabel}

import model.LadybugModel
import umd.cs.piccolo.PNode
import umd.cs.piccolox.pswing.PSwing

class PlaybackSpeedSlider(model: LadybugModel) extends PNode {
    val slider = new JSlider
    val transform = new LinearFunction(slider.getMinimum, slider.getMaximum, 0.5, 2.0)

    val dict = new Hashtable[Integer, JLabel]

    implicit def stringToJLabel(text: String): JLabel = new JLabel(text)
    dict.put(slider.getMinimum, "slow")
    dict.put(slider.getMaximum, "fast")

    slider.setLabelTable(dict)
    slider.setPaintLabels(true)
    val playbackSpeedSlider = new PSwing(slider)
    addChild(playbackSpeedSlider)
    slider.addChangeListener(new ChangeListener() {
        def stateChanged(e: ChangeEvent) = {
            model.setPlayback(transform.evaluate(slider.getValue))
        }
    })

    def updatePlaybackSliderVisible = playbackSpeedSlider.setVisible(model.isPlayback)
    model.addListener(() => {updatePlaybackSliderVisible})
    updatePlaybackSliderVisible
}