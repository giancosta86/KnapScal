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

import java.awt.Desktop
import java.io.IOException
import java.net.{URISyntaxException, URI}

object DesktopUtils {
  def openBrowser(url: String) {
    val browserThread = new Thread {
      override def run() {
        val desktop = Desktop.getDesktop()

        if (desktop == null) {
          throw new UnsupportedOperationException()
        }

        try {
          desktop.browse(new URI(url))
        } catch {
          case ex@(_: IOException | _: URISyntaxException) => {
            throw new UnsupportedOperationException(ex)
          }
        }
      }
    }

    browserThread.start()
  }
}
