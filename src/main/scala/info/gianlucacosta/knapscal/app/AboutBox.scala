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

import scalafx.geometry.{Pos, Insets}
import scalafx.scene.control.{Button, Label, Alert}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{HBox, VBox, BorderPane}
import scalafx.scene.text.{FontWeight, Font}

import scalafx.Includes._


private object AboutBox extends Alert(AlertType.Information) {
  title = s"About ${AppInfo.getName}..."
  headerText = None
  contentText = null

  graphic = null

  dialogPane().setContent(new BorderPane {
    left = new ImageView(new Image(AppInfo.getMainIcon(128)))

    center = new VBox {
      padding = Insets(20)
      spacing = 20

      children = Seq(
        new Label {
          font = Font.font("sans-serif", FontWeight.Bold, 22)
          text = AppInfo.getName
        },

        new Label {
          text = s"Version ${AppInfo.getVersion}"
        },

        new Label {
          text = s"Copyright © ${AppInfo.getCopyrightYears} Gianluca Costa"
        },


        new Label {
          text = s"This software is released under the following license:\n   ${AppInfo.getLicense}"
        },

        new Label {
          text =
            """Special thanks to Professor Silvano Martello
              |for his valuable advice and teaching.""".stripMargin
        }
      )
    }

    bottom = new HBox {
      padding = Insets(5, 20, 5, 20)
      spacing = 15

      alignment = Pos.Center

      children = Seq (
        new Button("Visit website") {
          prefWidth = 170

          onAction = handle({
            DesktopUtils.openBrowser(AppInfo.getWebsite)
          })
        },

        new Button("Facebook page") {
          prefWidth = 170

          onAction = handle({
            DesktopUtils.openBrowser(AppInfo.getFacebookPage)
          })
        }
      )
    }
  }.delegate)
}
