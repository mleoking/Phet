package edu.colorado.phet.therampscala.forcesandmotion

import charts.{AbstractChartNode, SeriesSelectionControl, RampGraph}
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.PhetPCanvas
import model.RampModel
import common.motion.model.DefaultTemporalVariable
import common.motion.graphs._
import RampDefaults._
import RampResources._

class ForcesAndMotionChartNode(transform: ModelViewTransform2D, canvas: PhetPCanvas, model: RampModel)
        extends AbstractChartNode(transform, canvas, model) {
  val parallelAppliedForceVariable = new DefaultTemporalVariable() {
    override def setValue(value: Double) = model.bead.parallelAppliedForce = value
  }
  model.stepListeners += (() => {
    if (inTimeRange(model.getTime))
      parallelAppliedForceVariable.addValue(model.bead.appliedForce.dot(model.bead.getRampUnitVector), model.getTime)
  })

  val appliedForceSeries = new ControlGraphSeries(formatForce("forces.applied".translate), appliedForceColor, abbrevUnused, N, characterUnused, parallelAppliedForceVariable)
  val frictionSeries = new ControlGraphSeries(formatForce("forces.friction".translate), frictionForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.frictionForce))
  val gravitySeries = new ControlGraphSeries(formatForce("forces.Gravity".translate), gravityForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.gravityForce))
  val wallSeries = new ControlGraphSeries(formatForce("forces.Wall".translate), wallForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.wallForce))
  val netForceSeries = new ControlGraphSeries(formatForce("forces.Net".translate), totalForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.totalForce))
  val forceSeriesList = appliedForceSeries :: frictionSeries :: gravitySeries :: wallSeries :: netForceSeries :: Nil

  val parallelForceControlGraph = new RampGraph(appliedForceSeries, canvas, timeseriesModel, updateableObject, model) {
    for (s <- forceSeriesList.tail) addSeries(s)
  }

  parallelForceControlGraph.addControl(new SeriesSelectionControl("forces.parallel-title-with-units".translate, 5) {
    addToGrid(appliedForceSeries, createEditableLabel)
    for (s <- forceSeriesList.tail) addToGrid(s)
  })
  //
  //  val totalEnergySeries = new ControlGraphSeries(formatEnergy("energy.total".translate), totalEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getTotalEnergy))
  //  val keSeries = new ControlGraphSeries(formatEnergy("energy.kinetic".translate), kineticEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getKineticEnergy))
  //  val peSeries = new ControlGraphSeries(formatEnergy("energy.potential".translate), potentialEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getPotentialEnergy))
  //  val thermalEnergySeries = new ControlGraphSeries(formatEnergy("energy.thermal".translate), thermalEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getThermalEnergy))
  //  val appliedWorkSeries = new ControlGraphSeries(formatWork("work.applied".translate), appliedWorkColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getAppliedWork))
  //  val gravityWorkSeries = new ControlGraphSeries(formatWork("work.gravity".translate), gravityWorkColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getGravityWork))
  //  val frictionWorkSeries = new ControlGraphSeries(formatWork("work.friction".translate), frictionWorkColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getFrictiveWork))
  //  val energyWorkSeriesList = totalEnergySeries :: keSeries :: peSeries :: thermalEnergySeries :: appliedWorkSeries :: gravityWorkSeries :: frictionWorkSeries :: Nil
  //
  //  val workEnergyGraph = new RampGraph(totalEnergySeries, canvas, timeseriesModel, updateableObject, model) {
  //    setEditable(false)
  //    setDomainUpperBound(20)
  //    getJFreeChartNode.setBuffered(false)
  //    getJFreeChartNode.setPiccoloSeries()
  //    for (s <- energyWorkSeriesList.tail) addSeries(s)
  //  }
  //  workEnergyGraph.addControl(new SeriesSelectionControl("forces.work-energy-title-with-units".translate, 7) {
  //    for (s <- energyWorkSeriesList) addToGrid(s)
  //  })

  val accelerationVariable = new DefaultTemporalVariable() {
    override def setValue(accel: Double) = {
      val desiredTotalForce = accel * model.bead.mass
      val currentTotalForceWithoutAppliedForce = model.bead.getParallelComponent(model.bead.totalForce - model.bead.appliedForce)
      val appliedForce = desiredTotalForce - currentTotalForceWithoutAppliedForce
      model.bead.parallelAppliedForce = appliedForce
    }
  }
  model.stepListeners += (() => {if (inTimeRange(model.getTime)) accelerationVariable.addValue(model.bead.acceleration, model.getTime)})
  val accelerationSeries = new ControlGraphSeries("Acceleration", RampDefaults.accelerationColor, "accel", "m/s/s", characterUnused, accelerationVariable)
  val accelerationGraph = new RampGraph(accelerationSeries, canvas, timeseriesModel, updateableObject, model) {
    setVerticalRange(-100, 100)
  }
  accelerationGraph.addControl(new SeriesSelectionControl("acceleration", 5) {
    addToGrid(accelerationSeries, createEditableLabel)
  })

  val velocityVariable = new DefaultTemporalVariable() {
    override def setValue(v: Double) = {
      model.bead.setVelocity(v)
    }
  }
  model.stepListeners += (() => {if (inTimeRange(model.getTime)) velocityVariable.addValue(model.bead.velocity, model.getTime)})
  val velocitySeries = new ControlGraphSeries("Velocity", RampDefaults.velocityColor, "vel", "m/s", characterUnused, velocityVariable)
  val velocityGraph = new RampGraph(velocitySeries, canvas, timeseriesModel, updateableObject, model) {
    setVerticalRange(-50, 50)
  }
  velocityGraph.addControl(new SeriesSelectionControl("velocity", 5) {
    addToGrid(velocitySeries, createEditableLabel)
  })

  val positionVariable = new DefaultTemporalVariable() {
    override def setValue(x: Double) = {
      model.bead.setPosition(x)
    }
  }
  model.stepListeners += (() => {if (inTimeRange(model.getTime)) positionVariable.addValue(model.bead.position, model.getTime)})
  val positionSeries = new ControlGraphSeries("Position", RampDefaults.positionColor, "x", "m", characterUnused, positionVariable)
  val positionGraph = new RampGraph(positionSeries, canvas, timeseriesModel, updateableObject, model) {
    setVerticalRange(-10, 10)
  }
  positionGraph.addControl(new SeriesSelectionControl("velocity", 5) {
    addToGrid(positionSeries, createEditableLabel)
  })

  val parallelForcesString = "forces.parallel-title".translate
  val graphs = Array(new MinimizableControlGraph(parallelForcesString, parallelForceControlGraph)
    //    ,new MinimizableControlGraph("forces.work-energy-title".translate, workEnergyGraph))
    , new MinimizableControlGraph("acceleration", accelerationGraph, true)
    , new MinimizableControlGraph("velocity", velocityGraph, true)
    , new MinimizableControlGraph("position", positionGraph, true)
    )

  val graphSetNode = new GraphSetNode(new GraphSetModel(new GraphSuite(graphs))) {
    override def getMaxAvailableHeight(availableHeight: Double) = availableHeight
    setAlignedLayout()
  }

  addChild(graphSetNode)
  updatePosition()
}