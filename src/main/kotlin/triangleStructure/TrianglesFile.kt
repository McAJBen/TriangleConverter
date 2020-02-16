package triangleStructure

import boundTo
import global.Global
import java.awt.Color
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class TrianglesFile(
    trArray: ArrayList<Triangle>,
    dimension: Dimension,
    baseChunk: BufferedImage?
) {

    companion object {

        private const val FACT = 3

        private const val FACT_INVERSE = 1.0 / FACT

        private val MAX_SCORE_TRUE = sqrt(195075.0)

        private const val MAX_SCORE_FALSE = 765.0

        fun compare(original: BufferedImage, newImg: BufferedImage): Double {

            val compareChunk = BufferedImage(
                newImg.width,
                newImg.height,
                BufferedImage.TYPE_4BYTE_ABGR
            ).apply {
                createGraphics().drawImage(original, 0, 0, newImg.width, newImg.height, null)
            }

            val newImgCol = IntArray(newImg.width * newImg.height * 4)
            newImg.raster.getPixels(0, 0, newImg.width, newImg.height, newImgCol)

            val originCol = IntArray(newImgCol.size)
            compareChunk.raster.getPixels(0, 0, newImg.width, newImg.height, originCol)

            val score = (newImgCol.indices step 4).map {
                toScore(it, originCol, newImgCol)
            }.sum()

            return 1 - score / getTotalPossibleScore(
                compareChunk.width,
                compareChunk.height
            )
        }

        private fun getTotalPossibleScore(width: Int, height: Int): Double {
            return width * height * if (Global.trueColor) MAX_SCORE_TRUE else MAX_SCORE_FALSE
        }

        private fun toScore(i: Int, a: IntArray, b: IntArray): Double {

            val deltaR = a[i] - b[i]
            val deltaG = a[i + 1] - b[i + 1]
            val deltaB = a[i + 2] - b[i + 2]

            return if (Global.trueColor) {
                sqrt(deltaR.toDouble().pow(2) + deltaG.toDouble().pow(2) + deltaB.toDouble().pow(2))
            } else {
                (abs(deltaR) + abs(deltaG) + abs(deltaB)).toDouble()
            }
        }
    }

    private val imageSize: Dimension

    val triangles: ArrayList<Triangle> = ArrayList(Global.triangles)

    private var image: BufferedImage? = null

    private val baseImg: BufferedImage?

    private val totalPossibleScore: Double

    val size: Int
        get() = triangles.size

    constructor(tf: TrianglesFile) : this(tf.triangles, tf.imageSize, tf.baseImg)

    init {
        triangles.addAll(trArray)
        imageSize = dimension.size
        totalPossibleScore = getTotalPossibleScore(imageSize.width, imageSize.height)
        baseImg = baseChunk
    }

    fun modifyRandom() {
        image = null
        if (triangles.size <= 0) {
            return
        }
        val i = getRandomTri()
        triangles.removeAt(i)
        triangles.add(Triangle())
    }

    fun modifyShape10() {
        image = null
        if (triangles.size <= 0) {
            return
        }
        val i = getRandomTri()
        val xp = triangles[i].getX()
        val yp = triangles[i].getY()
        for (j in 0..2) {
            xp[j] += (Global.randDouble / 5 - 0.1).toFloat()
            yp[j] += (Global.randDouble / 5 - 0.1).toFloat()
            xp[j] = xp[j].boundTo(0F, 1F)
            yp[j] = yp[j].boundTo(0F, 1F)
        }
        triangles[i] = Triangle(xp, yp, triangles[i].color)
    }

    fun modifyShapeFull() {
        image = null
        if (triangles.size <= 0) {
            return
        }
        val i = getRandomTri()
        val xp = triangles[i].getX()
        val yp = triangles[i].getY()
        for (j in 0..2) {
            xp[j] = Global.randFloat
            yp[j] = Global.randFloat
        }
        triangles[i] = Triangle(xp, yp, triangles[i].color)
    }

    fun modifyColor10() {
        image = null
        if (triangles.size <= 0) {
            return
        }
        val i = getRandomTri()
        val col = triangles[i].getColorArray()
        for (j in col.indices) {
            col[j] += Global.getRandInt(51) - 25
            col[j] = col[j].boundTo(0, 255)
        }
        triangles[i] = Triangle(
            triangles[i].getX(),
            triangles[i].getY(),
            Color(col[0], col[1], col[2])
        )
    }

    fun modifyRemove() {
        image = null
        if (triangles.size > 2) {
            triangles.removeAt(Global.getRandInt(triangles.size))
        }
    }

    fun compare(img: BufferedImage): Double {
        var score = compareTotal(img, createImg())
        score /= totalPossibleScore
        return 1 - score
    }

    fun hasAlpha(): Boolean {
        return hasAlpha(createImg())
    }

    fun getImage(): BufferedImage? {
        return createImg()
    }

    fun addTriangle() {
        image = null
        triangles.add(Triangle())
    }

    fun removeBackTriangle() {
        image = null
        if (size > 0) {
            triangles.removeAt(0)
        }
    }

    fun getImage(newBlockPixelSize: Dimension): BufferedImage {
        return makeImg(newBlockPixelSize.width, newBlockPixelSize.height)
    }

    private fun compareTotal(original: BufferedImage, newImg: BufferedImage): Double {
        var score = 0.0
        val newImgCol = IntArray(newImg.width * newImg.height * 4)
        val originCol = IntArray(newImgCol.size)
        newImg.raster.getPixels(0, 0, newImg.width, newImg.height, newImgCol)
        original.raster.getPixels(0, 0, newImg.width, newImg.height, originCol)
        var i = 0
        while (i < newImgCol.size) {
            score += if (newImgCol[i + 3] != 255) {
                if (Global.trueColor) MAX_SCORE_TRUE else MAX_SCORE_FALSE
            } else {
                toScore(i, originCol, newImgCol)
            }
            i += 4
        }
        return score
    }

    private fun hasAlpha(b: BufferedImage): Boolean {
        for (i in 0 until b.width) {
            for (j in 0 until b.height) {
                if (b.getRGB(i, j) == 0) {
                    return true
                }
            }
        }
        return false
    }

    private fun createImg(): BufferedImage {
        image?.let { return it }

        makeImg(imageSize.width, imageSize.height).let {
            image = it
            return it
        }
    }

    private fun makeImg(width: Int, height: Int): BufferedImage {
        val img = BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
        val g2d = img.createGraphics()
        if (baseImg != null) {
            g2d.drawImage(baseImg, 0, 0, width, height, null)
        }
        for (triangle in triangles) {
            g2d.color = triangle.color
            g2d.fillPolygon(triangle.getPolygon(width, height))
        }
        g2d.dispose()
        return img
    }

    private fun getRandomTri(): Int {
        return (Global.randDouble * triangles.size.toDouble().pow(FACT.toDouble())).pow(FACT_INVERSE).toInt()
    }
}