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


import java.util.UUID

import info.gianlucacosta.eighthbridge.fx.canvas.basic.DefaultBasicLink
import info.gianlucacosta.eighthbridge.graphs.point2point.ArcBinding
import info.gianlucacosta.eighthbridge.graphs.point2point.visual.VisualGraph
import info.gianlucacosta.knapscal.knapsack.branchbound.Node

import scalafx.geometry.{BoundingBox, Bounds, Dimension2D, Point2D}


object KnapScalGraph {
  private val VerticalSpacing = 60
  private val HorizontalSpacing = 40

  private type CenterX = Double
  private type CenterY = Double
  private type Width = Double
  private type Height = Double


  def create(rootNode: Node): KnapScalGraph = {
    val nodeParentTuples: Seq[(Node, Node)] =
      getNodeParentTuples(rootNode)


    val parentMap: Map[Node, Node] =
      nodeParentTuples
        .toMap


    val allNodes =
      Seq(rootNode) ++
        nodeParentTuples
          .map(_._1)
          .distinct


    val nodeSizes: Map[Node, Dimension2D] =
      allNodes
        .map(node => node -> estimateNodeSize(node))
        .toMap


    val levelNodes: Map[Int, Seq[Node]] =
      allNodes
        .groupBy(_.level)


    val levelHeights: Map[Int, Height] =
      levelNodes.map {
        case (levelIndex, nodes) =>
          levelIndex ->
            nodes.map(node => nodeSizes(node).height).max
      }


    val lastLevelIndex =
      levelNodes.keySet.max


    val levelCenterYs: Map[Int, CenterY] = {
      val rootCenterY =
        VerticalSpacing + nodeSizes(rootNode).height / 2


      Range.inclusive(1, lastLevelIndex)
        .foldLeft(Seq(rootCenterY))((cumulatedCenterYs, currentLevel) => {
          val previousLevel = currentLevel - 1
          val previousHeight = levelHeights(previousLevel)
          val previousCenterY = cumulatedCenterYs.head

          val currentHeight = levelHeights(currentLevel)

          val currentCenterY = previousCenterY + previousHeight / 2 + VerticalSpacing + currentHeight / 2

          Seq(currentCenterY) ++ cumulatedCenterYs
        })
        .reverse
        .zipWithIndex
        .map(pair => (pair._2, pair._1))
        .toMap
    }


    require(levelCenterYs.size == lastLevelIndex + 1)


    val centerXs: Map[Node, CenterX] =
      Range.inclusive(lastLevelIndex, 0, -1)
        .foldLeft(Map[Node, CenterX]())((cumulatedCenterXs, levelIndex) => {
          val currentNodes = levelNodes(levelIndex)

          cumulatedCenterXs ++ getLevelCenterXs(cumulatedCenterXs, currentNodes, nodeSizes)
        })


    require(
      centerXs.size == allNodes.size,
      s"Required nodes: ${allNodes.size}. Found: ${centerXs.size}"
    )


    val vertexMap: Map[Node, KnapScalVertex] =
      allNodes.map(node =>
        node -> KnapScalVertex(
          node,
          new Point2D(
            centerXs(node),
            levelCenterYs(node.level)
          ),

          nodeSizes(node)
        )
      )
        .toMap


    val (links: Seq[DefaultBasicLink], bindings: Seq[ArcBinding]) =
      createLinksAndBindings(vertexMap, rootNode)
        .unzip


    new KnapScalGraph(
      vertexMap.values.toSet,
      links.toSet,
      bindings.toSet
    )
  }


  private def getNodeParentTuples(currentRootNode: Node)
  : Seq[(Node, Node)] = {
    val takingSubtree: Seq[(Node, Node)] = currentRootNode.takingNode.map(takingNode =>
      Seq(takingNode -> currentRootNode) ++
        getNodeParentTuples(takingNode)
    ).getOrElse(
      Seq()
    )


    val leavingSubtree: Seq[(Node, Node)] = currentRootNode.leavingNode.map(leavingNode =>
      Seq(leavingNode -> currentRootNode) ++
        getNodeParentTuples(leavingNode)
    ).getOrElse(
      Seq()
    )

    takingSubtree ++ leavingSubtree
  }


  private def estimateNodeSize(node: Node): Dimension2D = {
    val nodeLines = KnapScalVertex.formatNode(node).split("\n")

    val width =
      11 * nodeLines
        .map(_.length)
        .max

    val height =
      20 + 20 * nodeLines.length

    new Dimension2D(width, height)
  }


  private def getLevelCenterXs(lowerLevelCenterXs: Map[Node, CenterX], levelNodes: Seq[Node], nodeSizes: Map[Node, Dimension2D])
  : Map[Node, CenterX] = {

    levelNodes
      .foldLeft(Seq[(Node, (CenterX, Width))]())(
        (cumulatedTuplesInLevel, currentNode) => {

          val takingNodeCenterXOption: Option[CenterX] =
            currentNode.takingNode.map(takingNode =>
              lowerLevelCenterXs(takingNode)
            )


          val leavingNodeCenterXOption: Option[CenterX] =
            currentNode.leavingNode.map(leavingNode =>
              lowerLevelCenterXs(leavingNode)
            )


          val currentWidth =
            nodeSizes(currentNode).width


          val currentCenterX: CenterX =
            cumulatedTuplesInLevel.headOption.map(previousTupleInLevel => {
              val (_, (previousCenterX: CenterX, previousWidth: Width)) = previousTupleInLevel

              previousCenterX + previousWidth / 2 + HorizontalSpacing + currentWidth / 2
            }).getOrElse({
              if (takingNodeCenterXOption.isEmpty && leavingNodeCenterXOption.isEmpty)
                HorizontalSpacing + currentWidth / 2
              else if (takingNodeCenterXOption.isEmpty || leavingNodeCenterXOption.isEmpty)
                takingNodeCenterXOption.getOrElse(leavingNodeCenterXOption.get)
              else {
                val takingNode = currentNode.takingNode.get
                val takingNodeWidth = nodeSizes(takingNode).width
                takingNodeCenterXOption.get + takingNodeWidth / 2 + HorizontalSpacing / 2
              }
            })


          val currentTuple: (Node, (CenterX, Width)) =
            currentNode ->(currentCenterX, currentWidth)

          Seq(currentTuple) ++ cumulatedTuplesInLevel
        })
      .map {
        case (node, (centerX, _)) =>
          node -> centerX
      }
      .toMap
  }


  private def createLinksAndBindings(
                                      vertexMap: Map[Node, KnapScalVertex],
                                      currentRootNode: Node
                                    ):
  Seq[(DefaultBasicLink, ArcBinding)] = {
    val currentRootVertex =
      vertexMap(currentRootNode)

    val takingSequence: Seq[(DefaultBasicLink, ArcBinding)] =
      currentRootNode.takingNode.map(takingNode => {
        val takingVertex =
          vertexMap(takingNode)

        val takingLink =
          new DefaultBasicLink()

        val arcBinding =
          ArcBinding(UUID.randomUUID(), currentRootVertex.id, takingVertex.id, takingLink.id)

        Seq(takingLink -> arcBinding) ++
          createLinksAndBindings(vertexMap, takingNode)
      })

        .getOrElse(Seq())


    val leavingSequence: Seq[(DefaultBasicLink, ArcBinding)] =
      currentRootNode.leavingNode.map(leavingNode => {
        val leavingVertex =
          vertexMap(leavingNode)

        val leavingLink =
          new DefaultBasicLink()

        val arcBinding =
          ArcBinding(UUID.randomUUID(), currentRootVertex.id, leavingVertex.id, leavingLink.id)

        Seq(leavingLink -> arcBinding) ++
          createLinksAndBindings(vertexMap, leavingNode)
      })
        .getOrElse(Seq())

    takingSequence ++ leavingSequence
  }
}


case class KnapScalGraph private(
                                  vertexes: Set[KnapScalVertex],
                                  links: Set[DefaultBasicLink],
                                  bindings: Set[ArcBinding],
                                  selectionBounds: Bounds = new BoundingBox(0, 0, 0, 0)
                                ) extends VisualGraph[KnapScalVertex, DefaultBasicLink, KnapScalGraph] {
  override def renderDirected: Boolean =
    true


  override def dimension: Dimension2D = {
    val width =
      vertexes
        .map(vertex =>
          vertex.center.x + vertex.size.width / 2 + KnapScalGraph.HorizontalSpacing
        )
        .max

    val height =
      vertexes
        .map(vertex =>
          vertex.center.y + vertex.size.height / 2 + KnapScalGraph.VerticalSpacing
        )
        .max

    new Dimension2D(width, height)

  }


  override def visualCopy(renderDirected: Boolean, dimension: Dimension2D, selectionBounds: Bounds): KnapScalGraph =
    copy(selectionBounds = selectionBounds)


  override protected def graphCopy(vertexes: Set[KnapScalVertex], links: Set[DefaultBasicLink], bindings: Set[ArcBinding]): KnapScalGraph =
    copy(
      vertexes = vertexes,
      links = links,
      bindings = bindings
    )
}
