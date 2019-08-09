package com.reactlibrary.utils

import android.support.test.annotation.UiThreadTest
import android.support.test.runner.AndroidJUnit4
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.nodes.props.Bounding
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilsTest {

    @Test
    @UiThreadTest
    fun shouldReturnBasicBoundingWhenCollisionShapeIsNotBox() {
        val node = Node()
        node.localPosition = Vector3(1f, 1f, 1f)

        val bounding = Utils.calculateBoundsOfNode(node)

        Assert.assertNotNull(bounding)
        Assert.assertEquals(node.localPosition.x, bounding.left)
        Assert.assertEquals(node.localPosition.x, bounding.right)
        Assert.assertEquals(node.localPosition.y, bounding.top)
        Assert.assertEquals(node.localPosition.y, bounding.bottom)
    }

    @Test
    @UiThreadTest
    fun shouldReturnBoundingWithWidestAndHighestAreaOfAllNodes() {
        val testNode1 = Node()
        val testNode2 = Node()
        val testNode3 = Node()
        testNode1.localPosition = Vector3(1f,2f,3f)
        testNode2.localPosition = Vector3(10f, 20f, 30f)
        testNode3.localPosition = Vector3(100f, 200f, 300f)

        val bounding = Utils.calculateSumBounds(listOf(testNode1, testNode2, testNode3))

        Assert.assertNotNull(bounding)
        Assert.assertEquals(1f, bounding.left)
        Assert.assertEquals(100f, bounding.right)
        Assert.assertEquals(2f, bounding.bottom)
        Assert.assertEquals(200f, bounding.top)
    }

    @Test
    @UiThreadTest
    fun shouldReturnEmptyBoundingWhenListOfNodesIsEmpty() {
        val bounding = Utils.calculateSumBounds(emptyList())

        Assert.assertEquals(Bounding(0f, 0f, 0f, 0f), bounding)
    }

    @Test
    @UiThreadTest
    fun shouldReturnFirstNodeBoundingIfListContainsOnlyOneNode() {
        val testNode = Node()
        testNode.localPosition = Vector3(1f, 1f, 1f)

        val bounding = Utils.calculateSumBounds(listOf(testNode))

        Assert.assertEquals(Bounding(1f, 1f, 1f, 1f), bounding)
    }

}