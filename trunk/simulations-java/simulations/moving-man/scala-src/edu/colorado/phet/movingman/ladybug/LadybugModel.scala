package edu.colorado.phet.movingman.ladybug

import scala.collection.mutable.ArrayBuffer
import LadybugUtil._

class LadybugModel extends Observable[LadybugModel] {
  val ladybug = new Ladybug

  val history = new ArrayBuffer[DataPoint]
  private var time: Double = 0;

  def setUpdateModePosition = updateMode = positionMode

  def setUpdateModeVelocity = updateMode = velocityMode

  def setUpdateModeAcceleration = updateMode = accelerationMode

  def positionMode(dt: Double) = {
    if (estimateVelocity(history.length - 1).magnitude > 1E-6)
      ladybug.setAngle(estimateAngle())

    var velocityEstimate = average(history.length - 3, history.length - 1, estimateVelocity)
    ladybug.setVelocity(velocityEstimate)

    var accelEstimate = average(history.length - 15, history.length - 1, estimateAcceleration)
    ladybug.setAcceleration(accelEstimate)
  }

  def velocityMode(dt: Double) = {
    ladybug.translate(ladybug.getVelocity * dt)

    var accelEstimate = average(history.length - 15, history.length - 1, estimateAcceleration)
    ladybug.setAcceleration(accelEstimate)
  }

  def accelerationMode(dt: Double) = {
    ladybug.translate(ladybug.getVelocity * dt)
    ladybug.setVelocity(ladybug.getVelocity + ladybug.getAcceleration * dt)
  }

  private var updateMode: (Double) => Unit = positionMode

  def update(dt: Double) = {
    time += dt;
    history += new DataPoint(time, ladybug.getState)

    if (history.length > 20) {
      updateMode(dt)
    }
    notifyListeners(this)
  }

  def estimateAngle(): Double = estimateVelocity(history.length - 1).getAngle

  def getPosition(index: Int): Vector2D = {
    history(index).state.position
  }

  def estimateVelocity(index: Int): Vector2D = {
    val dx = getPosition(index) - getPosition(index - 1)
    val dt = history(index).time - history(index - 1).time
    dx / dt
  }

  def estimateAcceleration(index: Int): Vector2D = {
    val dv = estimateVelocity(index) - estimateVelocity(index - 1)
    val dt = history(index).time - history(index - 1).time
    dv / dt
  }

  def average(start: Int, end: Int, function: Int => Vector2D): Vector2D = {
    var sum = new Vector2D
    for (i <- start until end) {
      sum = sum + function(i)
    }
    sum / (end - start)
  }

}