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

import info.gianlucacosta.eighthbridge.fx.canvas.basic._
import info.gianlucacosta.eighthbridge.graphs.point2point.ArcBinding
import info.gianlucacosta.eighthbridge.graphs.point2point.visual.VisualGraph
import info.gianlucacosta.knapscal.app.App
import info.gianlucacosta.knapscal.knapsack.branchbound.Node

import scalafx.geometry.{Dimension2D, Point2D}


object KnapScalGraph {
  private val VerticalSpacing = 60
  private val HorizontalSpacing = 40

  private type CenterX = Double
  private type CenterY = Double
  private type Width = Double
  private type Height = Double


  val Stylesheets = List[String](
    BasicStyles.resourceUrl.toExternalForm,
    App.getResource("KnapScal.css").toExternalForm
  )


  def create(rootNode: Node): KnapScalGraph = {
    val nodeParentTuples: List[(Node, Node)] =
      getNodeParentTuples(rootNode)


    val parentMap: Map[Node, Node] =
      nodeParentTuples
        .toMap


    val allNodes =
      List(rootNode) ++
        nodeParentTuples
          .map(_._1)
          .distinct



    val nodeDimensionQueries =
      allNodes.map(node =>
        BasicVertexNode.DimensionQuery(
          KnapScalVertex.formatNode(node)
        )
      )


    val nodeDimensions: Map[Node, Dimension2D] =
      allNodes
        .zip(
          BasicVertexNode.getDimensions(
            KnapScalGraph.Stylesheets,
            nodeDimensionQueries
          )
        )
        .toMap


    val levelNodes: Map[Int, List[Node]] =
      allNodes
        .groupBy(_.level)


    val levelHeights: Map[Int, Height] =
      levelNodes.map {
        case (levelIndex, nodes) =>
          levelIndex ->
            nodes.map(node => nodeDimensions(node).height).max
      }


    val lastLevelIndex =
      levelNodes.keySet.max


    val levelCenterYs: Map[Int, CenterY] = {
      val rootCenterY =
        VerticalSpacing + nodeDimensions(rootNode).height / 2


      Range.inclusive(1, lastLevelIndex)
        .foldLeft(List(rootCenterY))((cumulatedCenterYs, currentLevel) => {
          val previousLevel = currentLevel - 1
          val previousHeight = levelHeights(previousLevel)
          val previousCenterY = cumulatedCenterYs.head

          val currentHeight = levelHeights(currentLevel)

          val currentCenterY = previousCenterY + previousHeight / 2 + VerticalSpacing + currentHeight / 2

          List(currentCenterY) ++ cumulatedCenterYs
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

          cumulatedCenterXs ++ getLevelCenterXs(cumulatedCenterXs, currentNodes, nodeDimensions)
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
          )
        )
      )
        .toMap


    val vertexDimensions: Map[KnapScalVertex, Dimension2D] =
      nodeDimensions.map {
        case (node, dimension) =>
          vertexMap(node) -> dimension
      }


    val (links: List[DefaultBasicLink], bindings: List[ArcBinding]) =
      createLinksAndBindings(vertexMap, rootNode)
        .unzip


    new KnapScalGraph(
      vertexMap.values.toSet,
      links.toSet,
      bindings.toSet,
      vertexDimensions
    )
  }


  private def getNodeParentTuples(currentRootNode: Node)
  : List[(Node, Node)] = {
    val takingSubtree: List[(Node, Node)] = currentRootNode.takingNode.map(takingNode =>
      List(takingNode -> currentRootNode) ++
        getNodeParentTuples(takingNode)
    ).getOrElse(
      List()
    )


    val leavingSubtree: List[(Node, Node)] = currentRootNode.leavingNode.map(leavingNode =>
      List(leavingNode -> currentRootNode) ++
        getNodeParentTuples(leavingNode)
    ).getOrElse(
      List()
    )

    takingSubtree ++ leavingSubtree
  }


  private def getLevelCenterXs(lowerLevelCenterXs: Map[Node, CenterX], levelNodes: List[Node], nodeSizes: Map[Node, Dimension2D])
  : Map[Node, CenterX] = {

    levelNodes
      .foldLeft(List[(Node, (CenterX, Width))]())(
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

          List(currentTuple) ++ cumulatedTuplesInLevel
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
  List[(DefaultBasicLink, ArcBinding)] = {
    val currentRootVertex =
      vertexMap(currentRootNode)

    val takingSequence: List[(DefaultBasicLink, ArcBinding)] =
      currentRootNode.takingNode.map(takingNode => {
        val takingVertex =
          vertexMap(takingNode)

        val takingLink =
          new DefaultBasicLink()

        val arcBinding =
          ArcBinding(UUID.randomUUID(), currentRootVertex.id, takingVertex.id, takingLink.id)

        List(takingLink -> arcBinding) ++
          createLinksAndBindings(vertexMap, takingNode)
      })

        .getOrElse(List())


    val leavingSequence: List[(DefaultBasicLink, ArcBinding)] =
      currentRootNode.leavingNode.map(leavingNode => {
        val leavingVertex =
          vertexMap(leavingNode)

        val leavingLink =
          new DefaultBasicLink


        val arcBinding =
          ArcBinding(UUID.randomUUID(), currentRootVertex.id, leavingVertex.id, leavingLink.id)

        List(leavingLink -> arcBinding) ++
          createLinksAndBindings(vertexMap, leavingNode)
      })
        .getOrElse(List())

    takingSequence ++ leavingSequence
  }
}


case class KnapScalGraph private(
                                  vertexes: Set[KnapScalVertex],
                                  links: Set[DefaultBasicLink],
                                  bindings: Set[ArcBinding],
                                  vertexDimensions: Map[KnapScalVertex, Dimension2D]
                                ) extends VisualGraph[KnapScalVertex, DefaultBasicLink, KnapScalGraph] {

  override protected def graphCopy(vertexes: Set[KnapScalVertex], links: Set[DefaultBasicLink], bindings: Set[ArcBinding]): KnapScalGraph =
    copy(
      vertexes = vertexes,
      links = links,
      bindings = bindings
    )
}
