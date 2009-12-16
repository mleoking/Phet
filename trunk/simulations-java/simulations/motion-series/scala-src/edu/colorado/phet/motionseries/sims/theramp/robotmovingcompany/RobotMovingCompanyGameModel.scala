package edu.colorado.phet.motionseries.sims.theramp.robotmovingcompany

import collection.mutable.{HashMap, ArrayBuffer}
import edu.colorado.phet.common.phetcommon.math.MathUtil
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.scalacommon.math.Vector2D
import java.lang.Math._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.model._

class RobotMovingCompanyGameModel(val model: MotionSeriesModel,
                                  clock: ScalaClock,
                                  initAngle: Double,
                                  val appliedForceAmount: Double,
                                  val objectList: List[MotionSeriesObject])
        extends Observable {
  val DEFAULT_ROBOT_ENERGY = appliedForceAmount * 6
  val energyScale = MotionSeriesDefaults.rampRobotForce / appliedForceAmount / 10.0
  private var _robotEnergy = DEFAULT_ROBOT_ENERGY
  val surfaceModel = new SurfaceModel
  val airborneFloor = -9.0

  private var _launched = false
  private var _objectIndex = 0
  private val resultMap = new HashMap[MotionSeriesObject, Result]

  //Event notifications
  val beadCreatedListeners = new ArrayBuffer[(ForceBead, MotionSeriesObject) => Unit]
  val itemFinishedListeners = new ArrayBuffer[(MotionSeriesObject, Result) => Unit]
  val gameFinishListeners = new ArrayBuffer[() => Unit]

  val housePosition = 6
  val house = model.createBead(housePosition, MotionSeriesDefaults.house.width, MotionSeriesDefaults.house.height)
  val door = model.createBead(housePosition, MotionSeriesDefaults.door.width, MotionSeriesDefaults.door.height)
  private var _doorOpenAmount = 0.0

  def doorOpenAmount = _doorOpenAmount

  val doorListeners = new ArrayBuffer[() => Unit]
  val doorBackground = model.createBead(housePosition, MotionSeriesDefaults.doorBackground.width, MotionSeriesDefaults.doorBackground.height)
  private var _bead: ForceBead = null

  clock.addClockListener(dt => if (!model.isPaused && _bead != null) _bead.stepInTime(dt))
  resetAll()

  def resetAll() = {
    model.rampSegments(1).setAngle(0)
    model.walls = false
    model.rampSegments(0).startPoint = new Vector2D(-10, 0).rotate(-initAngle)
    model.rampSegments(0).startPoint = new Vector2D(-10, 0).rotate(-initAngle)
    _launched = false
    resultMap.clear()
    setObjectIndex(0)
    surfaceModel.resetAll()
  }

  def bead = _bead

  def robotEnergy = _robotEnergy

  def setObjectIndex(newIndex: Int) = {
    if (_bead != null) {
      //todo: use remove listener paradigm
      //todo: remove applied force listener
      _bead.remove()
    }

    _objectIndex = newIndex
    _robotEnergy = DEFAULT_ROBOT_ENERGY
    notifyListeners()

    val sel = selectedObject
    model.setPaused(true)

    _bead = model.createBead(-model.rampSegments(0).length, sel.width)

    bead.mass = sel.mass
    bead.staticFriction = sel.staticFriction
    bead.kineticFriction = sel.kineticFriction
    bead.height = sel.height
    bead.airborneFloor = airborneFloor
    bead.surfaceFrictionStrategy = surfaceModel
    bead.crashListeners += (() => itemLostOffCliff(sel))
    val beadRef = _bead //use a reference for closures below

    def _inFrontOfDoor(b: Bead) = {
      val range = 1.15 //NP: I would simply make the grabbing area smaller - maybe 10-15% beyond the door border.
      val leftSide = door.position - door.width / 2.0 * range
      val rightSide = door.position + door.width / 2.0 * range
      b.position >= leftSide && b.position <= rightSide
    }

    model.stepListeners += (() => {
      if (!containsKey(sel)) { //todo: this line is a workaround that makes sure the following logic only happesn if this object hasn't been scored already
        //todo: it should be rewritten to remove listeners when an object is scored
        val inFrontOfDoor = _inFrontOfDoor(beadRef)
        if (inFrontOfDoor)
          _doorOpenAmount = _doorOpenAmount + 0.1
        else
          _doorOpenAmount = _doorOpenAmount - 0.1
        _doorOpenAmount = MathUtil.clamp(0, _doorOpenAmount, 1.0)
        doorListeners.foreach(_())
      }
    })

    object listener extends Function0[Unit] {
      def apply() = {
        if (!containsKey(sel)) {
          val pushing = abs(beadRef.parallelAppliedForce) > 0
          val atRest = abs(beadRef.velocity) < 1E-6
          val inFrontOfDoor = _inFrontOfDoor(beadRef)
          val stoppedAtHouse = inFrontOfDoor && atRest //okay to be pushing
          val stoppedAndOutOfEnergy = atRest && _robotEnergy == 0
          val crashed = atRest && beadRef.position2D.y < 0 //todo: won't this be wrong if the object falls off slowly?  What about checking for Crashed strategy?
          if (stoppedAtHouse) {
            itemDelivered(sel, beadRef)
            bead.removeListener(this)
          }
          else if (stoppedAndOutOfEnergy || crashed) {
            itemLost(sel)
            bead.removeListener(this)
          }
        }
      }
    }

    bead.addListener(listener)

    _bead.workListeners += (work => {
      _robotEnergy = _robotEnergy - abs(work)
      if (_robotEnergy <= 0) {
        _robotEnergy = 0
        _bead.parallelAppliedForce = 0
      }
      notifyListeners()
    })

    launched = false
    beadCreatedListeners.foreach(_(bead, sel))
    bead.parallelAppliedForce = 0 //make sure applied force slider sets to zero, have to do this after listeners are attached
  }

  def containsKey(a: MotionSeriesObject) = resultMap.contains(a)

  def launched_=(b: Boolean) = {_launched = b; notifyListeners()}

  def launched = _launched

  def readyForNext = {
    //ready for next if all items have been scored
    val itemsPrepared = _objectIndex + 1
    val scored = movedItems + lostItems
    scored >= itemsPrepared
  }

  def nextObject() = setObjectIndex(_objectIndex + 1)

  def itemFinished(o: MotionSeriesObject, r: Result) = {
    resultMap += o -> r
    itemFinishedListeners.foreach(_(o, r))
    if (resultMap.size == objectList.length) {
      gameFinishListeners.foreach(_())
    }
    //    else //  automatically go to next object when you score or lose the object (instead of hitting next object button)
    //      nextObject()
    notifyListeners()
  }

  def isLastObject(o: MotionSeriesObject) = objectList(objectList.length - 1) eq o

  def itemLostOffCliff(o: MotionSeriesObject) = itemFinished(o, Result(false, true, o.points, robotEnergy.toInt))

  def itemLost(o: MotionSeriesObject) = itemFinished(o, Result(false, false, o.points, robotEnergy.toInt))

  val deliverList = new ArrayBuffer[Bead]

  private var _inputAllowed = true

  def inputAllowed = _inputAllowed

  def itemDelivered(o: MotionSeriesObject, beadRef: ForceBead) = {
    if (!deliverList.contains(beadRef)) {
      deliverList += beadRef
      object listener extends Function0[Unit] { //it's an object so we can refer to it as this below
        def apply() = {
          _inputAllowed = false
          beadRef.parallelAppliedForce = 0.0
          val x = beadRef.position
          val xf = house.position
          val vel = 0.2 * (if (xf - x > 0) 1 else -1)
          beadRef.setPosition(beadRef.position + vel)
          if ((beadRef.position - house.position).abs <= vel.abs) {
            model.stepListeners -= this
            itemFinished(o, Result(true, false, o.points, robotEnergy.toInt))
            _inputAllowed = true
          }
        }
      }
      model.stepListeners += listener
    }
  }

  def count(b: Boolean) = if (b) 1 else 0

  def movedItems = {
    val counts = for (v <- resultMap.values) yield count(v.success)
    counts.foldLeft(0)(_ + _)
  }

  def selectedObject = objectList(_objectIndex)

  def lostItems = {
    val counts = for (v <- resultMap.values) yield count(!v.success)
    counts.foldLeft(0)(_ + _)
  }

  def score = {
    val scores = for (v <- resultMap.values) yield v.score
    scores.foldLeft(0)(_ + _)
  }
}

case class Result(success: Boolean, cliff: Boolean, objectPoints: Int, robotEnergy: Int) {
  def score = totalObjectPoints + totalEnergyPoints

  def scoreMultiplier = if (success) 1 else 0

  val pointsPerJoule = scoreMultiplier * 0.1
  val totalObjectPoints = objectPoints * scoreMultiplier
  val totalEnergyPoints = (robotEnergy * pointsPerJoule).toInt
}