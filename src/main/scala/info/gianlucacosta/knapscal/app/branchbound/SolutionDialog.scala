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

package info.gianlucacosta.knapscal.app.branchbound

import info.gianlucacosta.eighthbridge.fx.canvas.GraphCanvas
import info.gianlucacosta.eighthbridge.fx.canvas.basic.DragDropController
import info.gianlucacosta.eighthbridge.graphs.point2point.visual.{DefaultVisualGraph, DefaultVisualLink, VisualGraph}
import info.gianlucacosta.knapscal.app.branchbound.rendering.KnapScalVertex
import info.gianlucacosta.knapscal.knapsack.branchbound.{Node, Solution}
import info.gianlucacosta.knapscal.knapsack.{ItemsFormatter, Problem}

import scalafx.geometry.{Dimension2D, Insets, Point2D}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.layout.BorderPane


private class SolutionDialog(problem: Problem, solution: Solution, estimatedNodeDimension: Dimension2D) extends Alert(AlertType.Information) {
  title = "Knapsack - Branch & Bound"
  headerText = "Solution"
  contentText = solution.bestNode.toString
  resizable = true


  private val solutionTextArea = new TextArea {
    prefHeight = 140
    editable = false
    margin = Insets(0, 0, 15, 0)

    text =
      s"""Ordered problem items: ${ItemsFormatter.format(problem.items)}
          |
        |${solution}""".stripMargin
  }


  private val legendLabel = new Label {
    text =
      """NODE LEGEND:
        |
        |* Index = exploration index
        |
        |* Ū = computed upper bound
        |* U = inherited upper bound
        |
        |* P = cumulated profit
        |* W = cumulated weight
        |
        |* I = Items taken
        |
        |*STOP* = Skip more branching
        |
        |*SOLUTION* = A solution node
        |
        |* z = solution value
      """.stripMargin

    margin = Insets(0, 5, 0, 0)
  }


  private val visualGraph = buildVisualGraph

  private val solutionScrollPane = new ScrollPane {
    content = new GraphCanvas(
      new DragDropController,
      visualGraph
    )

    hvalue = hmax() / 2
  }


  private val solutionPane = new BorderPane {
    top = solutionTextArea
    left = legendLabel
    center = solutionScrollPane

    prefWidth = 1100
    prefHeight = 550
  }

  dialogPane().setContent(solutionPane)


  private def buildVisualGraph: VisualGraph = {
    val horizontalLeafPadding = 10
    val verticalPadding = 40

    val itemsCount = problem.items.size
    val levelsCount = itemsCount + 1
    val leavesCount = math.pow(2, itemsCount)

    val estimatedNodeWidth = estimatedNodeDimension.width
    val estimatedNodeHeight = estimatedNodeDimension.height


    val graphWidth =
      (
        2 * horizontalLeafPadding
          + estimatedNodeDimension.width * leavesCount
          + horizontalLeafPadding * (leavesCount - 1)
        )


    val graphHeight =
      (
        2 * verticalPadding
          + estimatedNodeHeight * levelsCount
          + verticalPadding * (levelsCount - 1)
        )


    def horizontalPadding(levelIndex: Int): Double =
      if (levelIndex == levelsCount - 1) {
        horizontalLeafPadding
      } else {
        val nextLevelPadding = horizontalPadding(levelIndex + 1)
        nextLevelPadding + 2 * estimatedNodeWidth + (nextLevelPadding - estimatedNodeWidth)
      }


    val rootNode = solution.rootNode

    val rootVertex = new KnapScalVertex(
      center = new Point2D(
        graphWidth / 2,
        verticalPadding + estimatedNodeHeight / 2
      ),

      node = rootNode
    )


    val rootGraph = new DefaultVisualGraph(
      false,
      new Dimension2D(graphWidth, graphHeight)
    ).addVertex(rootVertex)


    def recursiveBuildGraph(parentGraph: VisualGraph, parentNode: Node, parentVertex: KnapScalVertex): VisualGraph = {
      val currentLevelIndex = parentNode.level + 1

      if (currentLevelIndex == levelsCount) {
        return parentGraph
      }

      val currentHorizontalPadding = math.abs(horizontalPadding(currentLevelIndex))

      val deltaFromParentCenter = new Point2D(
        currentHorizontalPadding / 2 + estimatedNodeWidth,
        verticalPadding + estimatedNodeHeight
      )

      var solutionGraph = parentGraph

      parentNode.takingNode.foreach(takingNode => {
        val takingVertex = new KnapScalVertex(
          center = new Point2D(
            parentVertex.center.x - deltaFromParentCenter.x,
            parentVertex.center.y + deltaFromParentCenter.y
          ),

          node = takingNode
        )

        val takingLink = new DefaultVisualLink(text = s"X${currentLevelIndex} = 1")

        val takingGraph =
          solutionGraph
            .addVertex(takingVertex)
            .bindLink(parentVertex, takingVertex, takingLink)


        solutionGraph = recursiveBuildGraph(takingGraph, takingNode, takingVertex)
      })


      parentNode.leavingNode.foreach(leavingNode => {
        val leavingVertex = new KnapScalVertex(
          center = new Point2D(
            parentVertex.center.x + deltaFromParentCenter.x,
            parentVertex.center.y + deltaFromParentCenter.y
          ),

          node = leavingNode
        )

        val leavingLink = new DefaultVisualLink(text = s"X${currentLevelIndex} = 0")

        val leavingGraph = solutionGraph
          .addVertex(leavingVertex)
          .bindLink(parentVertex, leavingVertex, leavingLink)

        solutionGraph = recursiveBuildGraph(leavingGraph, leavingNode, leavingVertex)
      })


      solutionGraph
    }


    recursiveBuildGraph(rootGraph, rootNode, rootVertex)
  }
}
