/*§
  ===========================================================================
  KnapScal
  ===========================================================================
  Copyright (C) 2015-2016 Gianluca Costa
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

import info.gianlucacosta.eighthbridge.graphs.point2point.visual._
import info.gianlucacosta.knapscal.knapsack.branchbound.Node

import scalafx.geometry.Point2D

case class KnapScalVertex(
                           center: Point2D,
                           node: Node, selected: Boolean = false,
                           id: UUID = UUID.randomUUID()
                         ) extends VisualVertex {
  override def text: String =
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


  override def settings: VisualVertexSettings =
    if (node.isSolution)
      KnapScalSolutionVertexSettings
    else if (node.isStopped)
      KnapScalStoppedVertexSettings
    else
      VisualVertexDefaultSettings


  override def selectedSettings: VisualVertexSettings =
    if (node.isSolution)
      KnapScalSolutionVertexSelectedSettings
    else if (node.isStopped)
      KnapScalStoppedVertexSelectedSettings
    else
      VisualVertexDefaultSelectedSettings


  override def visualCopy(center: Point2D, text: String, selected: Boolean): VisualVertex =
    copy(
      center = center,
      //Do NOT copy the text
      selected = selected
    )
}