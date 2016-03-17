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

package info.gianlucacosta.knapscal.app.branchbound.rendering

import info.gianlucacosta.eighthbridge.graphs.point2point.visual.VisualVertexSettings

import scalafx.scene.paint.Color

object KnapScalStoppedVertexSelectedSettings extends VisualVertexSettings(
  background = Color.valueOf("#e56767"),
  borderSize = 2,
  borderColor = Color.Black,
  fontName = "Arial",
  fontSize = 14,
  fontColor = Color.Black,
  padding = 12,
  rounding = 16
)
