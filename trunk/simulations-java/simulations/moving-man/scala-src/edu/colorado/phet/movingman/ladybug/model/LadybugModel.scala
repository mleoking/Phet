package edu.colorado.phet.movingman.ladybug.model

import controlpanel.TimeModel
import edu.colorado.phet.common.motion.model.TimeData
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction
import edu.colorado.phet.movingman.util.Motion2DModel
import java.awt.geom.{Rectangle2D, Point2D}
import scala.collection.mutable.ArrayBuffer
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.common.motion._
import scalacommon.math.Vector2D
import scalacommon.util.Observable

/**
 * This class is the main model for Ladybug2DApplication.  It contains both a model for the current state as well as the history.
 * The smoothing of motion is done by leading the ladybug (with an abstraction called the pen),
 * and using the same model as Motion2D for interpolation.
 */
class LadybugModel extends TimeModel{
  val ladybug = new Ladybug
  private val ladybugMotionModel = new LadybugMotionModel(this)
  private var bounds = new Rectangle2D.Double(-10, -10, 20, 20)
  private var updateMode: UpdateMode = PositionMode
  val tickListeners = new ArrayBuffer[() => Unit]
  val resetListeners = new ArrayBuffer[() => Unit]
  private var frictionless = false
  val motion2DModel = new Motion2DModel(10, 5, LadybugDefaults.defaultLocation.x, LadybugDefaults.defaultLocation.y)
  private val modelHistory = new ArrayBuffer[DataPoint] //recent history used to compute velocities, etc.
  var dt = 0.0

  //State related to recording; consider moving to a trait
  private var playbackSpeed = 1.0

  //samples inputted from the user that will be used to determine the path of the object
  //imagine as a pen on the canvas that leads the ladybug
  case class PenSample(time: Double, location: Vector2D)
  val penPath = new ArrayBuffer[PenSample]
  private var penPoint = new Vector2D //current sample point
  private var penDown = false

  def isFrictionless = frictionless

  def setFrictionless(f: Boolean) = {
    if (frictionless != f) {
      frictionless = f
      if (!frictionless) {
        //todo: make bug come to a smooth stop
        //todo: maybe easiest way is to refactor friction implementation to be more physical and less architectural
        clearSampleHistory
        resetMotion2DModel
        penPoint = ladybug.getPosition
      }
      else {
        clearSampleHistory
        resetMotion2DModel
        penPoint = ladybug.getPosition
      }
      notifyListeners
    }
  }

  def setSamplePoint(pt: Point2D) = {
    this.penPoint = pt
    //todo: send notification?
  }

  def getBounds: Rectangle2D = {
    return new Rectangle2D.Double(bounds.getX, bounds.getY, bounds.getWidth, bounds.getHeight) //defensive copy
  }

  def setBounds(b: Rectangle2D) = {
    bounds.setRect(b.getX, b.getY, b.getWidth, b.getHeight)
  }

  def getLadybugMotionModel() = ladybugMotionModel

  abstract class UpdateMode {def update(dt: Double): Unit}
  object PositionMode extends UpdateMode {def update(dt: Double) = positionMode(dt)}
  object VelocityMode extends UpdateMode {def update(dt: Double) = velocityMode(dt)}
  object AccelerationMode extends UpdateMode {def update(dt: Double) = accelerationMode(dt)}

  def setUpdateModePosition() = {
    if (updateMode != PositionMode) {
      updateMode = PositionMode
      clearSampleHistory
      resetMotion2DModel
    }
  }

  def setUpdateModeVelocity() = {
    if (updateMode != VelocityMode) {
      updateMode = VelocityMode
    }
  }

  def setUpdateModeAcceleration() = {
    updateMode = AccelerationMode
  }

  def getPlaybackIndexFloat(): Double = playbackIndexFloat

  private def getLastSamplePoint = penPath(penPath.length - 1)

  //  println("t\tx\tvx\tax")
  def positionMode(dt: Double) = {
    //    println("pendown=" + penDown)
    if (frictionless && !penDown) {
      velocityMode(dt)
      if (penPath.length > 2) {
        penPoint = ladybug.getPosition
        penPath += new PenSample(time, penPoint)
        motion2DModel.addPointAndUpdate(getLastSamplePoint.location.x, getLastSamplePoint.location.y)
      }
    }
    else {
      if (penPath.length > 2) {
        motion2DModel.addPointAndUpdate(getLastSamplePoint.location.x, getLastSamplePoint.location.y)
        ladybug.setPosition(new Vector2D(motion2DModel.getAvgXMid, motion2DModel.getAvgYMid))
        //added fudge factors for getting the scale right with current settings of motion2d model
        //used spreadsheet to make sure model v and a are approximately correct.
        val vscale = (1.0 / dt) / 10
        val ascale = vscale * vscale * 3.835
        ladybug.setVelocity(new Vector2D(motion2DModel.getXVel, motion2DModel.getYVel) * vscale)
        ladybug.setAcceleration(new Vector2D(motion2DModel.getXAcc, motion2DModel.getYAcc) * ascale)

        //      def debug = {println(time + "\t" + ladybug.getPosition.x + "\t" + ladybug.getVelocity.x + "\t" + ladybug.getAcceleration.x)}
        //      debug
        //      0+1
        //      println("y="+ladybug.getPosition.y)

      } else {
        ladybug.setVelocity(new Vector2D)
        ladybug.setAcceleration(new Vector2D)
      }
      pointInDirectionOfMotion()
    }
  }

  def pointInDirectionOfMotion() = {
    if (estimateVelocity(modelHistory.length - 1).magnitude > 1E-6)
      ladybug.setAngle(estimateAngle())
  }

  def velocityMode(dt: Double) = {
    if (penPath.length > 0)
      motion2DModel.addPointAndUpdate(getLastSamplePoint.location.x, getLastSamplePoint.location.y)
    ladybug.translate(ladybug.getVelocity * dt)

    var accelEstimate = average(modelHistory.length - 15, modelHistory.length - 1, estimateAcceleration)
    ladybug.setAcceleration(accelEstimate)
    pointInDirectionOfMotion()
  }

  def accelerationMode(dt: Double) = {
    ladybug.translate(ladybug.getVelocity * dt)
    ladybug.setVelocity(ladybug.getVelocity + ladybug.getAcceleration * dt)
    pointInDirectionOfMotion()
  }

  def setPlaybackState(state:LadybugState)={
    ladybug.setState(state)
  }

  def update(dt: Double) = {
    this.dt = dt
    if (!paused) {
      tickListeners.foreach(_())
      if (isRecord()) {
        time += dt;
        ladybugMotionModel.update(dt, this)

        modelHistory += new DataPoint(time, ladybug.getState)
        recordHistory += new DataPoint(time, ladybug.getState)
        penPath += new PenSample(time, penPoint)

        while (modelHistory.length > 100) {
          modelHistory.remove(0)
        }
        while (penPath.length > 100) {
          penPath.remove(0)
        }

        while (recordHistory.length > getMaxRecordPoints) {
          //decide whether to remove end of path or beginning of path.
//          recordHistory.remove(recordHistory.length - 1)
          recordHistory.remove(0)
        }

        if (!ladybugMotionModel.isExclusive()) {
          if (penDown) {
            PositionMode.update(dt)
          }
          else {
            updateMode.update(dt)
          }
        }
        notifyListeners()

      } else if (isPlayback()) {
        stepPlayback()
      }
    }
  }

  def getMaxRecordPoints = {
    (LadybugDefaults.timelineLengthSeconds / dt).toInt
  }

  def initManual = {
    println("init: " + ladybug.getPosition)
    resetMotion2DModel
    penPath.clear
    println("cleared sample path: " + penPath.length)
  }

  def readyForInteraction(): Boolean = {
    val recording = isRecord
    val isDonePlayback = (getPlaybackIndex() >= recordHistory.length - 1) && isPaused
    recording || isDonePlayback
  }

  def stepPlayback() = {
    if (getPlaybackIndex() < recordHistory.length) {
      setStateToPlaybackIndex()
      time = recordHistory(getPlaybackIndex()).time
      playbackIndexFloat = playbackIndexFloat + playbackSpeed
      notifyListeners()
    } else {
      if (LadybugDefaults.recordAtEndOfPlayback) {
        setRecord(true)
      }

      if (LadybugDefaults.pauseAtEndOfPlayback) {
        setPaused(true)
      }
    }
  }

  def estimateAngle(): Double = estimateVelocity(modelHistory.length - 1).getAngle

  def getPosition(index: Int): Vector2D = {
    modelHistory(index).state.position
  }

  def estimateVelocity(index: Int): Vector2D = {
    val h = modelHistory.slice(modelHistory.length - 6, modelHistory.length)
    val tx = for (item <- h) yield new TimeData(item.state.position.x, item.time)
    val vx = MotionMath.estimateDerivative(tx.toArray)

    val ty = for (item <- h) yield new TimeData(item.state.position.y, item.time)
    val vy = MotionMath.estimateDerivative(ty.toArray)

    new Vector2D(vx, vy)
  }

  def estimateAcceleration(index: Int): Vector2D = {
    val h = modelHistory.slice(modelHistory.length - 6, modelHistory.length)
    val tx = for (item <- h) yield new TimeData(item.state.velocity.x, item.time)
    val ax = MotionMath.estimateDerivative(tx.toArray)

    val ty = for (item <- h) yield new TimeData(item.state.velocity.y, item.time)
    val ay = MotionMath.estimateDerivative(ty.toArray)

    new Vector2D(ax, ay)
  }

  def average(start: Int, end: Int, function: Int => Vector2D): Vector2D = {
    var sum = new Vector2D
    for (i <- start until end) {
      sum = sum + function(i)
    }
    sum / (end - start)
  }

  def setRecord(rec: Boolean) = {
    if (record != rec) {
      record = rec
      if (record) {
        clearHistoryRemainder()
        ladybug.setVelocity(new Vector2D)
        ladybug.setAcceleration(new Vector2D)
      }

      notifyListeners()
    }
  }

  def clearHistoryRemainder() = {
    val earlyEnough = modelHistory.filter(_.time < time)
    modelHistory.clear
    modelHistory.appendAll(earlyEnough)

    val earlyEnoughRecordData = recordHistory.filter(_.time < time)
    recordHistory.clear
    recordHistory.appendAll(earlyEnoughRecordData)

    clearSampleHistory()
    resetMotion2DModel
  }

  def setPlaybackSpeed(speed: Double) = {
    if (speed != playbackSpeed) {
      playbackSpeed = speed
      notifyListeners()
    }
  }

  def setPlayback(speed: Double) = {
    setPlaybackSpeed(speed)
    setRecord(false)
  }

  def rewind = {
    setPlaybackIndexFloat(0.0)
  }

  def startRecording() = {
    getLadybugMotionModel.motion = LadybugMotionModel.MANUAL
    setRecord(true)
    setPaused(false)
  }

  def resetAll() = {
    record = true
    paused = true
    playbackSpeed = 1.0
    modelHistory.clear()
    recordHistory.clear()
    penPath.clear()

    ladybugMotionModel.resetAll()
    playbackIndexFloat = 0.0
    time = 0
    ladybug.resetAll()
    frictionless = false
    resetMotion2DModel

    notifyListeners()
  }

  override def clearHistory() = {
    modelHistory.clear()
    penPath.clear()

    super.clearHistory()  //do super last to call notifyListeners
  }

  def clearSampleHistory() = penPath.clear()

  def resetMotion2DModel() = {
    motion2DModel.reset(ladybug.getPosition.x, ladybug.getPosition.y)
    resetListeners.foreach(_())
  }

  def returnLadybug() = {
    ladybug.setPosition(LadybugDefaults.defaultLocation)
    ladybug.setVelocity(new Vector2D)
    penPath.clear()
    setSamplePoint(ladybug.getPosition)
    resetMotion2DModel
    notifyListeners()
  }

  def setPenDown(p: Boolean) = {
    penDown = p
    notifyListeners()
  }
}