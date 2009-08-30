package edu.colorado.phet.therampscala.model

import common.phetcommon.math.MathUtil
import scalacommon.math.Vector2D
import java.lang.Math._
import therampscala.Predef._

abstract class MotionStrategy(val bead: Bead) {
  def stepInTime(dt: Double)

  def wallForce: Vector2D

  def frictionForce: Vector2D

  def normalForce: Vector2D

  def position2D: Vector2D

  def getAngle: Double

  //accessors/adapters for subclass convenience
  //This class was originally designed to be an inner class of Bead, but IntelliJ debugger didn't support debug into inner classes at the time
  //so these classes were refactored to be top level classes to enable debugging.  They can be refactored back to inner classes when there is better debug support

  def totalForce = bead.totalForce

  def gravityForce = bead.gravityForce

  def mass = bead.mass

  def normalForceVector = bead.normalForceVector

  def positionMapper = bead.positionMapper

  def rampSegmentAccessor = bead.rampSegmentAccessor

  def wallRange = bead.wallRange()

  def position = bead.position

  def appliedForce = bead.appliedForce

  def gravity = bead.gravity

  def notificationsEnabled = bead.notificationsEnabled

  def getTotalEnergy = bead.getTotalEnergy

  def state = bead.state

  def velocity = bead.velocity

  def workListeners = bead.workListeners

  def surfaceFriction = bead.surfaceFriction

  def thermalEnergy = bead.thermalEnergy

  def airborneFloor = bead.airborneFloor

  def crashListeners = bead.crashListeners

  def getRampUnitVector = bead.getRampUnitVector

  def staticFriction = bead.staticFriction

  def kineticFriction = bead.kineticFriction

  def width = bead.width

  def wallsExist = bead.wallsExist

  def getVelocityVectorDirection = bead.getVelocityVectorDirection
}

class Crashed(_position2D: Vector2D, _angle: Double, bead: Bead) extends MotionStrategy(bead) {
  def stepInTime(dt: Double) = {}

  def wallForce = new Vector2D

  def frictionForce = new Vector2D

  def normalForce = gravityForce * -1

  def position2D = _position2D

  def getAngle = _angle
}

class Airborne(private var _position2D: Vector2D, private var _velocity2D: Vector2D, _angle: Double, bead: Bead) extends MotionStrategy(bead: Bead) {
  def getAngle = _angle

  def velocity2D = _velocity2D

  override def stepInTime(dt: Double) = {
    val tf = totalForce
    val accel = totalForce / mass
    _velocity2D = _velocity2D + accel * dt
    _position2D = _position2D + _velocity2D * dt
    if (_position2D.y <= airborneFloor) {
      bead.attachState = new Crashed(new Vector2D(_position2D.x, bead.airborneFloor), _angle, bead)
      crashListeners.foreach(_())
    }
    normalForceVector.notifyListeners() //since ramp segment or motion state might have changed; could improve performance on this by only sending notifications when we are sure the ramp segment has changed
    bead.notifyListeners() //to get the new normalforce
  }

  override def wallForce = new Vector2D

  override def frictionForce = new Vector2D

  override def normalForce = new Vector2D

  override def position2D = _position2D
}

class Grounded(bead: Bead) extends MotionStrategy(bead) {
  def position2D = positionMapper(position)

  def getAngle = rampSegmentAccessor(position).getUnitVector.getAngle

  def normalForce = {
    val magnitude = (gravityForce * -1) dot getRampUnitVector.rotate(PI / 2)
    val angle = getRampUnitVector.getAngle + PI / 2
    new Vector2D(angle) * magnitude
  }

  override def wallForce = {
    val leftBound = bead.wallRange().min + width / 2
    val rightBound = bead.wallRange().max - width / 2
    if (position <= leftBound && bead.forceToParallelAcceleration(appliedForce) < 0 && wallsExist) appliedForce * -1
    else if (position >= rightBound && bead.forceToParallelAcceleration(appliedForce) > 0 && wallsExist) appliedForce * -1 //todo: account for gravity force
    else new Vector2D
  }

  def multiBodyFriction(f: Double) = bead.surfaceFrictionStrategy.getTotalFriction(f)

  override def frictionForce = {
    if (surfaceFriction()) {
      //stepInTime samples at least one value less than 1E-12 on direction change to handle static friction
      if (velocity.abs < 1E-12) {

        //use up to fMax in preventing the object from moving
        //see static friction discussion here: http://en.wikipedia.org/wiki/Friction
        val fMax = abs(multiBodyFriction(staticFriction) * normalForce.magnitude)
        val netForceWithoutFriction = appliedForce + gravityForce + normalForce + wallForce

        val magnitude = if (netForceWithoutFriction.magnitude >= fMax) fMax else netForceWithoutFriction.magnitude
        new Vector2D(netForceWithoutFriction.getAngle + PI) * magnitude
      }
      else {
        //object is moving, just use kinetic friction
        val vel = (positionMapper(position) - positionMapper(position - velocity * 1E-6))
        new Vector2D(vel.getAngle + PI) * normalForce.magnitude * multiBodyFriction(kineticFriction)
      }
    }
    else new Vector2D
  }

  case class SettableState(position: Double, velocity: Double, thermalEnergy: Double, crashEnergy: Double) {
    def setPosition(p: Double) = new SettableState(p, velocity, thermalEnergy, crashEnergy)

    def setVelocity(v: Double) = new SettableState(position, v, thermalEnergy, crashEnergy)

    def setThermalEnergy(t: Double) = new SettableState(position, velocity, t, crashEnergy)

    def setPositionAndVelocity(p: Double, v: Double) = new SettableState(p, v, thermalEnergy, crashEnergy)

    //todo: this is duplicated with code in Bead
    lazy val totalEnergy = ke + pe + thermalEnergy
    lazy val ke = mass * velocity * velocity / 2.0
    lazy val pe = mass * gravity.abs * positionMapper(position).y //assumes positionmapper doesn't change, which is true during stepintime
  }

  def testingTester(dt: Double) = {
    println("hello there")
  }

  override def stepInTime(dt: Double) = {
    bead.notificationsEnabled = false //make sure only to send notifications as a batch at the end; improves performance by 17%
    val origEnergy = getTotalEnergy
    val origState = state
    val newState = getNewState(dt, origState, origEnergy)

    if (newState.position > bead.wallRange().max + width / 2 && !wallsExist) {
      bead.attachState = new Airborne(position2D, new Vector2D(getVelocityVectorDirection) * velocity, getAngle, bead)
      bead.parallelAppliedForce = 0
    }
    val distanceVector = positionMapper(newState.position) - positionMapper(origState.position)
    val work = appliedForce dot distanceVector
    workListeners.foreach(_(work))
    bead.setPosition(newState.position)
    bead.setVelocity(newState.velocity)
    bead.thermalEnergy = newState.thermalEnergy
    bead.setCrashEnergy(newState.crashEnergy)

    bead.notificationsEnabled = true;
    bead.notifyListeners() //do as a batch, since it's a performance problem to do this several times in this method call
  }

  def getNewState(dt: Double, origState: BeadState, origEnergy: Double) = {
    val origVel = velocity
    val desiredVel = bead.netForceToParallelVelocity(totalForce, dt)
    //stepInTime samples at least one value less than 1E-12 on direction change to handle static friction
    //see docs in static friction computation
    val newVelocity = if ((origVel < 0 && desiredVel > 0) || (origVel > 0 && desiredVel < 0)) 0.0 else desiredVel
    val requestedPosition = position + newVelocity * dt
    val stateAfterVelocityUpdate = new SettableState(requestedPosition, newVelocity, origState.thermalEnergy, origState.crashEnergy)

    val isKineticFriction = surfaceFriction() && kineticFriction > 0
    val leftBound = bead.wallRange().min + width / 2
    val rightBound = bead.wallRange().max - width / 2
    val collidedLeft = requestedPosition <= leftBound && wallsExist
    val collidedRight = requestedPosition >= rightBound && wallsExist
    val collided = collidedLeft || collidedRight
    val crashEnergy = stateAfterVelocityUpdate.ke //this is the energy it would lose in a crash
    //      println("Crash energy from getNewState: "+crashEnergy)
    if (collidedRight) {
      println("collided right")
    }
    val stateAfterCollision = if (collidedLeft && isKineticFriction) {
      new SettableState(leftBound, 0, stateAfterVelocityUpdate.thermalEnergy + crashEnergy, origState.crashEnergy + crashEnergy)
    }
    else if (collidedRight && isKineticFriction) {
      new SettableState(rightBound, 0, stateAfterVelocityUpdate.thermalEnergy + crashEnergy, origState.crashEnergy + crashEnergy)
    }
    else if (collided) { //bounce
      stateAfterVelocityUpdate.setVelocity(-newVelocity)
    }
    else {
      stateAfterVelocityUpdate
    }

    val dx = stateAfterCollision.position - origState.position

    //account for external forces, such as the applied force, which should increase the total energy
    val appliedEnergy = (appliedForce dot bead.getVelocityVectorUnitVector(stateAfterCollision.velocity)) * dx.abs

    //      val thermalFromWork = getThermalEnergy + abs((frictionForce dot getVelocityVectorUnitVector(stateAfterBounds.velocity)) * dx) //work done by friction force, absolute value
    //todo: this may differ significantly from thermalFromWork
    val thermalFromEnergy = if (isKineticFriction && !collided)
      origEnergy - stateAfterCollision.ke - stateAfterCollision.pe + appliedEnergy
    else if (isKineticFriction && collided) {
      //choose thermal energy so energy is exactly conserved
      origEnergy + appliedEnergy - stateAfterCollision.ke - stateAfterCollision.pe
    }
    else
      origState.thermalEnergy

    def getVelocityToConserveEnergy(state: SettableState) = {
      val sign = MathUtil.getSign(state.velocity)
      sign * sqrt(abs(2.0 / mass * (origEnergy + appliedEnergy - state.pe - origState.thermalEnergy)))
    }

    //we'd like to just use thermalFromEnergy, since it guarantees conservation of energy
    //however, it may lead to a decrease in thermal energy, which would be physically incorrect
    //      val stateAfterThermalEnergy = stateAfterBounds.setThermalEnergy(thermalFromWork)
    val stateAfterThermalEnergy = stateAfterCollision.setThermalEnergy(thermalFromEnergy)
    val dE = stateAfterThermalEnergy.totalEnergy - origEnergy
    val dT = stateAfterThermalEnergy.thermalEnergy - origState.thermalEnergy

    //drop in thermal energy indicates a problem, since total thermal energy should never decrease
    //preliminary tests indicate this happens when switching between ramp segment 0 and 1
    val stateAfterPatchup = if (dT < 0) {
      val patchedVelocity = getVelocityToConserveEnergy(stateAfterThermalEnergy)
      val patch = stateAfterThermalEnergy.setThermalEnergy(origState.thermalEnergy).setVelocity(patchedVelocity)
      val dEPatch = stateAfterThermalEnergy.totalEnergy - origEnergy
      if (dEPatch.abs > 1E-8) {
        println("applied energy = ".literal + appliedEnergy + ", dT = ".literal + dT + ", origVel=".literal + stateAfterThermalEnergy.velocity + ", newV=".literal + patchedVelocity + ", dE=".literal + dEPatch)
        //accept some problem here
        //todo: should the state be changed, given that energy is problematic?
        patch
      } else
        patch
    } else {
      stateAfterThermalEnergy
    }

    val finalState = if (abs(stateAfterPatchup.totalEnergy - origEnergy - appliedEnergy) > 1E-8 && stateAfterPatchup.velocity.abs > 1E-3) {
      stateAfterPatchup.setVelocity(getVelocityToConserveEnergy(stateAfterThermalEnergy))
    } else {
      stateAfterPatchup
    }

    val patchPosition = if (abs(finalState.totalEnergy - origEnergy) > 1E-8 && getAngle != 0.0) {
      val x = (origEnergy + appliedEnergy - finalState.thermalEnergy - finalState.ke) / mass / gravity.abs / sin(getAngle)
      stateAfterPatchup.setPosition(x)
    } else {
      finalState
    }

    val delta = patchPosition.totalEnergy - origEnergy - appliedEnergy
    if (delta.abs > 1E-8) {
      println("failed to conserve energy, delta=".literal + delta)
    }

    //      println("iskineticfriction = "+ isKineticFriction +", "+frictionForce)
    patchPosition
  }
}