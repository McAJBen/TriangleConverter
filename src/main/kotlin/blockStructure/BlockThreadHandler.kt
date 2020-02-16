package blockStructure

import global.Global
import toTimeString
import triangleStructure.Block
import triangleStructure.TrianglesFile.Companion.compare
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage

abstract class BlockThreadHandler internal constructor(
    private val originalImg: BufferedImage,
    private val newImg: BufferedImage
) {

    companion object {
        private val PINK = Color(255, 0, 255)

        private fun getSubImage(b: BufferedImage, r: Rectangle): BufferedImage {
            return b.getSubimage(r.x, r.y, r.width, r.height)
        }
    }

    private val ignoreAlpha = !hasAlpha(newImg)

    private val btArray: Array<BT> = Array(Global.threadCount) { BT(it) }

    private val startTime = System.currentTimeMillis()

    abstract val percent: Double

    abstract val isDone: Boolean

    abstract val newBlockLocation: BlockLocation?

    val runTime: String
        get() {
            return secondsFromStart.toTimeString()
        }

    val estimatedEndTime: String
        get() {
            val runtime = secondsFromStart
            val endTime = (runtime / percent).toLong() - runtime
            return endTime.toTimeString()
        }

    fun paint(g2d: Graphics2D, size: Dimension) {
        for (b in btArray) {
            val xScale = size.getWidth() / newImg.width
            val yScale = size.getHeight() / newImg.height
            b.paint(g2d, xScale, yScale)
        }
    }

    fun execute() {
        for (b in btArray) {
            b.start()
        }
        for (b in btArray) {
            try {
                b.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun hasAlpha(b: BufferedImage): Boolean {
        if (ignoreAlpha) {
            return false
        }
        for (i in 0 until b.width) {
            for (j in 0 until b.height) {
                if (b.getRGB(i, j) == 0) {
                    return true
                }
            }
        }
        return false
    }

    private val secondsFromStart: Long
        get() = (System.currentTimeMillis() - startTime) / 1000

    private fun paintTo(b: BufferedImage, rect: Rectangle) {
        synchronized(newImg) { newImg.createGraphics().drawImage(b, rect.x, rect.y, rect.width, rect.height, null) }
    }

    abstract fun removeBlockLocation(blockLocation: BlockLocation?)

    abstract fun addCompleted()

    private inner class BT(index: Int) : Thread("BT:$index") {

        private val index = "$index"

        private var currentTestImage: BufferedImage? = null

        private var blockLocation: BlockLocation? = null

        private var active = false

        private var ignoreAlphaChunk = false

        fun paint(g: Graphics2D, xScale: Double, yScale: Double) {
            if (active) {
                val rect = Rectangle(
                    (blockLocation!!.post.x * xScale).toInt(),
                    (blockLocation!!.post.y * yScale).toInt(),
                    (blockLocation!!.post.width * xScale).toInt(),
                    (blockLocation!!.post.height * yScale).toInt()
                )
                if (currentTestImage != null) {
                    g.drawImage(
                        currentTestImage,
                        rect.x, rect.y,
                        rect.width, rect.height, null
                    )
                }
                if (Global.preDrawOutline) {
                    g.color = PINK
                    g.drawString(
                        index,
                        rect.x + 1,
                        rect.y + 11
                    )
                    g.drawRect(
                        rect.x, rect.y,
                        rect.width - 1, rect.height - 1
                    )
                }
            }
        }

        private fun compute(block: Block) {
            while (!block.isDone(ignoreAlphaChunk)) {
                block.move()
                if (Global.preDraw) {
                    currentTestImage = block.getImage()
                }
            }
        }

        override fun run() {
            while (!isDone) {
                blockLocation = newBlockLocation
                currentTestImage = null
                active = true
                if (blockLocation == null) {
                    active = false
                    break
                }
                val compareImage =
                    getSubImage(originalImg, blockLocation!!.original)
                val baseImg = getSubImage(newImg, blockLocation!!.post)
                ignoreAlphaChunk = !hasAlpha(baseImg)
                var bestScore = 0.0
                var bestBlock: Block? = null
                for (sample in 0 until Global.maxSamples) {
                    val block =
                        Block(compareImage, baseImg, blockLocation!!.scaled.size)
                    compute(block)
                    if (bestScore < block.maxScore) {
                        bestBlock = block
                        bestScore = block.maxScore
                    }
                }
                if (Global.postScale != 1.0) {
                    val block = Block(
                        compareImage,
                        baseImg,
                        blockLocation!!.post.size,
                        bestBlock!!.getTriangles()
                    )
                    compute(block)
                    bestBlock = block
                }
                // if (first drawing || better than last drawing)
                if (!ignoreAlpha || bestBlock!!.maxScore >= compare(compareImage, baseImg)) {
                    paintTo(bestBlock!!.getImage(blockLocation!!.post.size), blockLocation!!.post)
                    addCompleted()
                }
                active = false
                removeBlockLocation(blockLocation)
            }
        }
    }
}