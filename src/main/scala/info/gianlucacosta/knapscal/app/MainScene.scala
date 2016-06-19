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

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.BorderPane

import info.gianlucacosta.helios.apps.AppInfo

import scalafx.application.Platform


private class MainScene(appInfo: AppInfo) extends Scene({
  val loader = new FXMLLoader()

  loader.setLocation(classOf[MainScene].getResource("MainScene.fxml"))

  val root = loader.load[BorderPane]

  val controller = loader.getController[MainSceneController]

  Platform.runLater {
    root.setPrefSize(700, 450)
  }

  Platform.runLater {
    controller.setup(appInfo)
  }

  root
})