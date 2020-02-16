package blockStructure

import global.Global
import java.awt.Point
import java.awt.Rectangle
import java.awt.image.BufferedImage

class BTGrid(
    originalImg: BufferedImage,
    newImg: BufferedImage
) : BlockThreadHandler(
    originalImg,
    newImg
) {

    companion object {
        private fun toRectangle(pos: Point, size: D2D): Rectangle {
            val r = Rectangle()
            r.setLocation(
                (pos.x * size.width).toInt(),
                (pos.y * size.height).toInt()
            )
            r.setSize(
                ((pos.x + 1) * size.width).toInt() - r.x,
                ((pos.y + 1) * size.height).toInt() - r.y
            )
            return r
        }
    }

    private val originalSize = D2D(
        originalImg.width.toDouble() / Global.blocksWide,
        originalImg.height.toDouble() / Global.blocksWide
    )

    private val scaledSize = D2D(
        originalSize.width * Global.scale,
        originalSize.height * Global.scale
    )

    private val postSize = D2D(
        scaledSize.width * Global.postScale,
        scaledSize.height * Global.postScale
    )

    private val nextPos = Point(0, 0)

    override val percent: Double
        get() = (nextPos.getX() / Global.blocksWide + nextPos.y) / Global.blocksWide

    override val isDone: Boolean
        get() {
            synchronized(nextPos) {
                return nextPos.y >= Global.blocksWide
            }
        }

    override val newBlockLocation: BlockLocation?
        get() {
            var orig: Rectangle
            var scaled: Rectangle
            var post: Rectangle
            do {
                val position = getNextPos()
                if (position.y >= Global.blocksWide) {
                    return null
                }
                orig = toRectangle(position, originalSize)
                scaled = toRectangle(position, scaledSize)
                post = toRectangle(position, postSize)
            } while (orig.width <= 0 || orig.height <= 0 || scaled.width <= 0 || scaled.height <= 0 || post.width <= 0 || post.height <= 0
            )
            return BlockLocation(orig, scaled, post)
        }

    private fun getNextPos(): Point {
        synchronized(nextPos) {
            val p = nextPos.clone() as Point
            nextPos.x++
            if (nextPos.x >= Global.blocksWide) {
                nextPos.y++
                nextPos.x = 0
            }
            return p
        }
    }

    override fun removeBlockLocation(blockLocation: BlockLocation?) {}

    override fun addCompleted() {}

    private inner class D2D(val width: Double, val height: Double)
}