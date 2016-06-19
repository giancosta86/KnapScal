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

import javafx.scene.Node
import javafx.stage.Stage

import info.gianlucacosta.helios.apps.{AppInfo, AuroraAppInfo}
import info.gianlucacosta.helios.fx.apps.{AppBase, AppMain, SplashStage}
import info.gianlucacosta.knapscal.ArtifactInfo
import info.gianlucacosta.knapscal.icons.MainIcon

import scalafx.application.Platform
import scalafx.stage.Screen


object App extends AppMain[App](classOf[App])

class App extends AppBase(AuroraAppInfo(ArtifactInfo, MainIcon)) {
  override def startup(appInfo: AppInfo, splashStage: SplashStage, primaryStage: Stage): Unit = {
    val mainScene = new MainScene(appInfo)

    Platform.runLater {
      primaryStage.setScene(mainScene)
    }

    Platform.runLater {
      primaryStage.sizeToScene()
    }

    Platform.runLater {
      primaryStage.show()

      primaryStage.centerOnScreen()
    }
  }
}

