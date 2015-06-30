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

package info.gianlucacosta.knapscal.app.branchbound

import info.gianlucacosta.knapscal.knapsack.branchbound.{UpperBoundFunction, BranchBoundSolver}
import info.gianlucacosta.knapscal.knapsack.{Problem}

abstract class BranchBoundStrategy(name: String, upperBoundFunction: UpperBoundFunction) {
  def run(problem: Problem) = {
    val solver = new BranchBoundSolver(upperBoundFunction)
    val solution = solver.solve(problem)

    val solutionDialog = new SolutionDialog(problem, solution)
    solutionDialog.showAndWait()
  }

  override def toString: String = name
}
