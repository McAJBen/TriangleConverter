package triangleStructure

import global.G
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.util.*

class Block(
    compareImg: BufferedImage?,
    baseImg: BufferedImage?,
    size: Dimension,
    triangles: ArrayList<Triangle> = ArrayList(listOf(Triangle()))
) {

    companion object {
        private const val MAX_STAGNANT_VAL = 100.0
    }

    // Image this Block is trying to solve
    private val compareChunk = BufferedImage(
        size.width,
        size.height,
        BufferedImage.TYPE_4BYTE_ABGR
    ).apply {
        createGraphics().drawImage(compareImg, 0, 0, size.width, size.height, null)
    }

    // Best set of triangles found so far
    private var bestTriFile = TrianglesFile(triangles, size, baseImg)

    // last tested image
    private var lastImgChunk = bestTriFile.getImage()

    // Max comparison score recorded by bestTriFile
    var maxScore = bestTriFile.compare(compareChunk)
        private set

    // moves done since last improvement
    private var stagnantCount = 0.0

    // checks triangleMode to modify bestTriFile and see if it improves
    private var triangleMode = TriangleMode.RANDOM

    constructor(
        compareImage: BufferedImage,
        baseImg: BufferedImage,
        size: Dimension
    ) : this(
        compareImage,
        baseImg,
        size,
        ArrayList(listOf(Triangle()))
    )

    fun move() {
        val modifyTriFile = TrianglesFile(bestTriFile)

        when (triangleMode) {
            TriangleMode.RANDOM -> modifyTriFile.modifyRandom()
            TriangleMode.COLOR_10 -> modifyTriFile.modifyColor10()
            TriangleMode.SHAPE_FULL -> modifyTriFile.modifyShapeFull()
            TriangleMode.SHAPE_10 -> modifyTriFile.modifyShape10()
            TriangleMode.REMOVE -> modifyTriFile.modifyRemove()
        }

        val modifyScore = modifyTriFile.compare(compareChunk)

        lastImgChunk = if (G.getPreDrawShowBest()) {
            bestTriFile.getImage()
        } else {
            modifyTriFile.getImage()
        }

        // checks if the modify improved
        if (modifyScore >= maxScore) {
            if (modifyScore > maxScore) {
                maxScore = modifyScore
                stagnantCount = 0.0
            } else {
                stagnantCount++
            }
            bestTriFile = modifyTriFile
        } else {
            stagnantCount++
        }

        // if modifying at the current triangleMode isn't doing enough
        if (stagnantCount > MAX_STAGNANT_VAL) {
            triangleMode = triangleMode.next()
            stagnantCount = 0.0
            // if triangleMode is at the end try adding another triangle
            if (triangleMode == TriangleMode.RANDOM) {
                bestTriFile.addTriangle()
                while (bestTriFile.size > G.getTriangles()) {
                    bestTriFile.removeBackTriangle()
                }
                maxScore = bestTriFile.compare(compareChunk)
            }
        }
    }

    fun isDone(ignoreAlpha: Boolean) = if (ignoreAlpha || !bestTriFile.hasAlpha()) {
        maxScore > 0.99 || (triangleMode == TriangleMode.REMOVE && bestTriFile.size == G.getTriangles())
    } else {
        false
    }

    fun getTriangles(): ArrayList<Triangle> = bestTriFile.triangles

    fun getImage(newBlockPixelSize: Dimension): BufferedImage = bestTriFile.getImage(newBlockPixelSize)

    fun getImage(): BufferedImage? = lastImgChunk

    // the current modify the block should make to a triangle
    private enum class TriangleMode {
        RANDOM, COLOR_10, SHAPE_FULL, SHAPE_10, REMOVE;

        // increases to the next type of TriangleMode
        fun next(): TriangleMode {
            return values()[(ordinal + 1) % 5]
        }
    }
}