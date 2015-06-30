/*§
  ===========================================================================
  KnapScal
  ===========================================================================
  Copyright (C) 2015 Gianluca Costa
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

package info.gianlucacosta.knapscal.app

import javafx.fxml.FXML

import info.gianlucacosta.knapscal.app.branchbound.strategies.{OptimizedDantzigStrategy, MartelloTothStrategy, DantzigStrategy}
import info.gianlucacosta.knapscal.knapsack.{ItemsFormatter, ItemsParser, Problem}
import info.gianlucacosta.knapscal.knapsack.dynamic.full.DynamicProgrammingSolver
import info.gianlucacosta.knapscal.knapsack.dynamic.optimized.OptimizedDynamicProgrammingSolver

import javafx.event.ActionEvent
import javafx.scene.control.{TextArea, TextField}

import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{ChoiceDialog, Alert}

private class MainSceneController {
  private val itemsParser = new ItemsParser

  @FXML
  private var capacityField: TextField = null

  @FXML
  private var itemsArea: TextArea = null

  @FXML
  private def runBranchBound(event: ActionEvent): Unit = {
    val problem = prepareProblem()
    if (problem.isEmpty) {
      return
    }


    val choices = Seq(
      new DantzigStrategy,
      new OptimizedDantzigStrategy,
      new MartelloTothStrategy
    )


    val choiceDialog = new ChoiceDialog(choices(0), choices) {
      title = "Branch & Bound - Upper bound strategy"
      headerText = "Choose an algorithm for the upper bound:"
    }

    val chosenStrategy = choiceDialog.showAndWait()
    if (chosenStrategy.isEmpty) {
      return
    }


    chosenStrategy.get.run(problem.get)
  }

  private def prepareProblem(): Option[Problem] = {
    try {
      val items = itemsParser.parse(itemsArea.getText)
      val capacity = capacityField.getText.toInt

      return Some(Problem(items, capacity))
    } catch {
      case e: IllegalArgumentException => {
        val illegalInputAlert = new Alert(AlertType.Warning) {
          headerText = "Invalid input"
          contentText = e.getMessage
          dialogPane().setPrefWidth(500)
        }

        illegalInputAlert.showAndWait()

        return None
      }

    }
  }

  @FXML
  private def runDynamicProgramming(event: ActionEvent): Unit = {
    val problem = prepareProblem()

    if (problem.isEmpty) {
      return
    }

    val solver = new DynamicProgrammingSolver()
    val solution = solver.solve(problem.get)

    val solutionArea = new scalafx.scene.control.TextArea {
      prefWidth = 800
      prefHeight = 480
      editable = false


      text = s"""Ordered problem items: ${ItemsFormatter.format(problem.get.items)}
          |
          |${solution.toString()}
        """.stripMargin
    }

    val solutionAlert = new Alert(AlertType.Information) {
      title = "Knapsack - Dynamic Programming"
      headerText = "Solution"
      contentText = solution.value.toString
      dialogPane().setContent(solutionArea)
    }

    solutionAlert.showAndWait()
  }

  @FXML
  private def runOptimizedDynamicProgramming(event: ActionEvent): Unit = {
    val problem = prepareProblem()
    if (problem.isEmpty) {
      return
    }

    val solver = new OptimizedDynamicProgrammingSolver()
    val solution = solver.solve(problem.get)

    val alert = new Alert(AlertType.Information) {
      title = "Knapsack Dynamic Programming - Optimized"
      headerText = None
      contentText = solution.toString()
    }

    alert.showAndWait()
  }


  @FXML
  private def showAboutBox(event: ActionEvent): Unit = {
    AboutBox.showAndWait()
  }
}
