// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

case class Entry(time: Long, //Time since sim started in millisec
                 actor: String,
                 event: String,
                 parameters: Map[String, String]) {

  //Checks for a match for actor, event and optional params
  def matches(actor: String, event: String, params: Map[String, String]): Boolean = {
    for ( key <- params.keys ) {
      if ( !hasParameter(key, params(key)) ) {
        return false
      }
    }
    this.actor == actor && this.event == event
  }

  //Checks for a match for actor (event omitted) and optional params
  def matches(actor: String, params: Map[String, String]): Boolean = {
    for ( param <- params ) {
      if ( !hasParameter(param._1, param._2) ) {
        return false
      }
    }
    this.actor == actor
  }

  //Get the specified parameter value, if it exists, otherwise "?"
  def apply(key: String): String = {
    if ( parameters.contains(key) ) {
      parameters(key)
    }
    else {
      "?"
    }
  }

  def hasParameter(key: String, value: String): Boolean = parameters.contains(key) && parameters(key) == value

  def hasParameters(e: Entry, pairs: Seq[Pair[String, String]]): Boolean = {
    for ( p <- pairs ) {
      if ( !hasParameter(p._1, p._2) ) {
        return false
      }
    }
    true
  }
}