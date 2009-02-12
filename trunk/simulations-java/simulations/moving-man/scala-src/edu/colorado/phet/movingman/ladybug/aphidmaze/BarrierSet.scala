package edu.colorado.phet.movingman.ladybug.model.aphidmaze

import edu.colorado.phet.movingman.ladybug.aphidmaze.MazeGenerator
import edu.colorado.phet.movingman.ladybug.aphidmaze.Wall
import java.awt.geom.{Line2D, Rectangle2D}
import scala.collection.mutable.ArrayBuffer
import edu.colorado.phet.movingman.ladybug.LadybugUtil._

class BarrierSet {
  val rectangles = new ArrayBuffer[Rectangle2D]
  val lines = new ArrayBuffer[Line2D.Double]

  //  rectangles += new Rectangle2D.Double(-10, -9, 20, 1)
  //  rectangles += new Rectangle2D.Double(-10, 9, 20, 1)
  //  rectangles += new Rectangle2D.Double(-9, -10, 1, 20)
  //  rectangles += new Rectangle2D.Double(9, -10, 1, 20)

  def update(ladybug: Ladybug) = {
//    lines.foreach((line:Line2D.Double)=>{
//      line.x1 =line.x1+Math.random*0.01
//      line.y1 =line.y1+Math.random*0.01
//    })
  }

  def containsPoint(pt: Vector2D): Boolean = {
    rectangles.foldLeft(false)((value: Boolean, cur: Rectangle2D) => cur.contains(pt) || value)
  }

  val mg = new MazeGenerator

  for (w <- mg.walls) {
    //    rectangles+=toRectangle(w)
    lines += toLine(w)
  }

  def toRectangle(w: Wall): Rectangle2D = {
    new Rectangle2D.Double(w.x, w.y, w.dx + 0.1, w.dy + 0.1)
  }

  def toLine(w: Wall): Line2D.Double= {
    new Line2D.Double(w.x, w.y, w.x+w.dx, w.y+w.dy)
  }

}