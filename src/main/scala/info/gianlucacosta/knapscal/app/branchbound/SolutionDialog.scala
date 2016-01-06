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

import info.gianlucacosta.knapscal.knapsack.{ItemsFormatter, Problem}
import info.gianlucacosta.knapscal.knapsack.branchbound.{Solution, Node}

import scalafx.geometry.Insets
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.layout.BorderPane

private class SolutionDialog(problem: Problem, solution: Solution) extends Alert(AlertType.Information) {
  title = "Knapsack - Branch & Bound"
  headerText = "Solution"
  contentText = solution.bestNode.toString()

  val solutionTextArea = new TextArea {
    prefHeight = 140
    editable = false
    margin = Insets(0, 0, 15, 0)

    text = s"""Ordered problem items: ${ItemsFormatter.format(problem.items)}
        |
        |${solution}""".stripMargin
  }


  val treeRootItem = buildTreeItem(solution.rootNode)
  val solutionTreeView = new TreeView[String](treeRootItem) {
    margin = Insets(0, 0, 0, 20)
  }


  val legendLabel = new Label {
    text = """NODE LEGEND:
             |
             |* Node index
             |
             |* Generating branch condition
             |
             |* Ū = computed upper bound
             |* U = inherited upper bound
             |
             |* P = total profit or solution value
             |
             |* W = total weight
             |
             |* I = Items taken
             |
             |*STOP* = Node exploration stopped
             |*SOL* = The node is a solution
           """.stripMargin

    margin = Insets(0, 5, 0, 0)
  }


  val solutionPane = new BorderPane {
    top = solutionTextArea
    left = legendLabel
    center = solutionTreeView

    prefWidth = 1100
    prefHeight = 550
  }

  dialogPane().setContent(solutionPane)


  private def buildTreeItem(modelNode: Node): TreeItem[String] = {
    val result = new TreeItem[String](modelNode.toString())
    result.expanded = true

    if (modelNode.takingNode.isDefined) {
      val takingViewNode = buildTreeItem(modelNode.takingNode.get)
      result.children.add(takingViewNode)
    }

    if (modelNode.leavingNode.isDefined) {
      val leavingViewNode = buildTreeItem(modelNode.leavingNode.get)
      result.children.add(leavingViewNode)
    }

    return result
  }
}
