package edu.colorado.phet.motionseries.sims.theramp.robotmovingcompany

import edu.colorado.phet.common.phetcommon.view.PhetFrame
import javax.swing.JFrame
import edu.colorado.phet.motionseries.model.MotionSeriesModel
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.Predef._
import edu.colorado.phet.motionseries.{MotionSeriesResources, MotionSeriesDefaults, MotionSeriesModule}

class RobotMovingCompanyModule(frame: PhetFrame,
                               clock: ScalaClock)
        extends MotionSeriesModule(frame, clock, "module.robotMovingCompany".translate, 5, false, MotionSeriesDefaults.defaultRampAngle,false) {
  
  override def reset() = {
    super.reset()
    motionSeriesModel.frictionless = false
  }

  override def resetAll() = {
    super.resetAll()
    motionSeriesModel.frictionless = false
  }

  override def createMotionSeriesModel(defaultBeadPosition: Double, pausedOnReset: Boolean, initialAngle: Double) = {
    new MotionSeriesModel(defaultBeadPosition, pausedOnReset, initialAngle) {
      override def updateSegmentLengths() = setSegmentLengths(rampLength, rampLength)
      frictionless = false
    }
  }

  val gameModel = new RobotMovingCompanyGameModel(motionSeriesModel, clock)

  gameModel.itemFinishedListeners += ((scalaRampObject, result) => {
    val audioClip = result match {
      case Result(_, true, _, _) => Some("smash0.wav".literal)
      case Result(true, false, _, _) => Some("tintagel/DIAMOND.WAV".literal)
      case Result(false, false, _, _) => Some("tintagel/PERSONAL.WAV".literal)
      case _ => None
    }
    if (!audioClip.isEmpty) MotionSeriesResources.getAudioClip(audioClip.get).play()
  })

  val canvas = new RobotMovingCompanyCanvas(motionSeriesModel, coordinateSystemModel, fbdModel, vectorViewModel, frame, gameModel, MotionSeriesDefaults.forceMotionFrictionArea)

  setSimulationPanel(canvas)
  setLogoPanelVisible(false)
}