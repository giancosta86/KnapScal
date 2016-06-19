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
import info.gianlucacosta.eighthbridge.fx.canvas.basic.{BasicStyles, DefaultBasicLink, DragDropController}
import info.gianlucacosta.knapscal.app.App
import info.gianlucacosta.knapscal.app.branchbound.rendering.{KnapScalGraph, KnapScalVertex}
import info.gianlucacosta.knapscal.knapsack.branchbound.Solution
import info.gianlucacosta.knapscal.knapsack.{ItemsFormatter, Problem}

import scalafx.geometry.Insets
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control._
import scalafx.scene.layout.BorderPane


private class SolutionDialog(problem: Problem, solution: Solution) extends Alert(AlertType.Information) {
  title = "Knapsack - Branch & Bound"
  headerText = "Solution"
  contentText = solution.bestNode.toString
  resizable = true

  dialogPane().getStylesheets.addAll(
    BasicStyles.resourceUrl.toExternalForm,
    App.getResource("KnapScal.css").toExternalForm
  )

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


  private val solutionScrollPane = new ScrollPane {
    content = new GraphCanvas[KnapScalVertex, DefaultBasicLink, KnapScalGraph](
      new DragDropController,
      KnapScalGraph.create(solution.rootNode)
    )

    hvalue = hmax() / 2
    hbarPolicy = ScrollBarPolicy.Never
    vbarPolicy = ScrollBarPolicy.Never
  }


  private val solutionPane = new BorderPane {
    top = solutionTextArea
    left = legendLabel
    center = solutionScrollPane

    prefWidth = 1100
    prefHeight = 550
  }

  dialogPane().setContent(solutionPane)
}
