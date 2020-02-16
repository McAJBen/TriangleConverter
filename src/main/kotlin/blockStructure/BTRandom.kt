package blockStructure

import global.Global
import global.Global.getRandInt
import global.Global.randDouble
import global.Global.randomBlocks
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.util.*

class BTRandom(
    originalImg: BufferedImage,
    newImg: BufferedImage
) : BlockThreadHandler(
    originalImg,
    newImg
) {

    companion object {
        private fun toRectangle(r: Rectangle, scale: Double): Rectangle {
            return Rectangle(
                (r.x * scale).toInt(),
                (r.y * scale).toInt(),
                (r.width * scale).toInt(),
                (r.height * scale).toInt()
            )
        }
    }

    private var randomPlacementsLeft: Int = randomBlocks

    private val imageSize: Dimension = Dimension(
        originalImg.width,
        originalImg.height
    )

    private val defaultSize = Dimension(
        imageSize.width / Global.blocksWide,
        imageSize.height / Global.blocksWide
    )

    private val alreadyTakenBlocks = ArrayList<Rectangle>(Global.threadCount)

    override val isDone: Boolean
        get() {
            synchronized(alreadyTakenBlocks) {
                return randomPlacementsLeft <= 0
            }
        }

    override val percent: Double
        get() = 1.0 - randomPlacementsLeft.toDouble() / randomBlocks

    override val newBlockLocation: BlockLocation?
        get() {
            synchronized(alreadyTakenBlocks) {
                if (randomPlacementsLeft <= alreadyTakenBlocks.size) {
                    return null
                }
                var orig: Rectangle
                var scaled: Rectangle
                var post: Rectangle
                var bl: BlockLocation
                do {
                    orig = validRect
                    scaled = toRectangle(orig, Global.scale)
                    post = toRectangle(orig, Global.scale * Global.postScale)
                    bl = BlockLocation(orig, scaled, post)
                } while (scaled.width <= 0 || scaled.height <= 0 || post.width <= 0 || post.height <= 0
                )
                alreadyTakenBlocks.add(orig)
                return bl
            }
        }

    private val validRect: Rectangle
        get() {
            while (true) {
                val size = block
                for (i in 0..99) {
                    val orig = getRandomRect(size)
                    if (orig.width > 0 && orig.height > 0) {
                        if (Global.allowCollision || !collides(orig)) {
                            return orig
                        }
                    }
                }
                try {
                    Thread.sleep(1)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

    private fun getRandomRect(size: Dimension): Rectangle {
        return Rectangle(
            getRandInt(imageSize.width - size.width),
            getRandInt(imageSize.height - size.height),
            size.width,
            size.height
        )
    }

    private val block: Dimension
        get() {
            return Dimension(
                (defaultSize.width * (0.8 + randDouble * 0.2)).toInt().coerceAtLeast(1),
                (defaultSize.height * (0.8 + randDouble * 0.2)).toInt().coerceAtLeast(1)
            )
        }

    private fun collides(rect: Rectangle): Boolean {
        for (bl in alreadyTakenBlocks) {
            if (bl.intersects(rect)) {
                return true
            }
        }
        return false
    }

    override fun removeBlockLocation(blockLocation: BlockLocation?) {
        synchronized(alreadyTakenBlocks) {
            alreadyTakenBlocks.remove(blockLocation!!.original)
        }
    }

    override fun addCompleted() {
        randomPlacementsLeft--
        if (randomPlacementsLeft < 0) {
            println(randomPlacementsLeft)
        }
    }
}