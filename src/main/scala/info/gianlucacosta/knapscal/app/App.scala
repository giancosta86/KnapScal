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

package info.gianlucacosta.knapscal.app

import javafx.application.Application
import javafx.stage.Stage

import info.gianlucacosta.knapscal.ArtifactInfo

import scalafx.scene.image.Image

private object App {
  def getResource(url: String) = getClass().getResource(url)
}

private class App extends Application {
  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle(ArtifactInfo.title)

    primaryStage.getIcons.addAll(new Image(getClass.getResourceAsStream("/info/gianlucacosta/knapscal/icons/mainIcon32.png")))

    val mainScene = new MainScene
    primaryStage.setScene(mainScene)

    primaryStage.show()
  }
}
