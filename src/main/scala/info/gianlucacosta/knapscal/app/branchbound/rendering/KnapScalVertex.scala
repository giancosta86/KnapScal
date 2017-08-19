/*§
  ===========================================================================
  KnapScal
  ===========================================================================
  Copyright (C) 2015-2017 Gianluca Costa
  ===========================================================================
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  ===========================================================================
*/

package info.gianlucacosta.knapscal.app.branchbound.rendering

import java.util.UUID

import info.gianlucacosta.eighthbridge.fx.canvas.basic.BasicVertex
import info.gianlucacosta.knapscal.knapsack.branchbound.Node

import scalafx.geometry.Point2D


object KnapScalVertex {
  def formatNode(node: Node): String =
    if (node.isSolution) {
      (
        s"Index = ${node.index}\n"
          + s"*SOLUTION*\n"
          + s"z = ${node.totalProfit}\n"
          + s"I = ${node.takenItems.mkString("[", ",\n", "]")}"
        )
    } else {
      (s"Index = ${node.index}\n"
        + s"${if (node.isUpperBoundComputed) "Ū" else "U"} = ${node.upperBound}\n"
        + s"P = ${node.totalProfit};  W = ${node.totalWeight}"
        + s"${if (node.isStopped) "\n*STOP*" else ""}"
        )
    }
}

case class KnapScalVertex(
                           node: Node,
                           center: Point2D,
                           selected: Boolean = false,

                           id: UUID = UUID.randomUUID()
                         ) extends BasicVertex[KnapScalVertex] {
  override def text: String =
    KnapScalVertex.formatNode(node)


  override def styleClasses: List[String] =
    if (node.isSolution)
      List("solution")
    else if (node.isStopped)
      List("stopped")
    else
      List()


  override def visualCopy(center: Point2D, selected: Boolean): KnapScalVertex =
    copy(center = center, selected = selected)
}