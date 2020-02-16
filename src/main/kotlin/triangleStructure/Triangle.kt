package triangleStructure

import global.G
import java.awt.Color
import java.awt.Polygon

class Triangle {

    companion object {
        private const val SIDES = 3
    }

    private var x = FloatArray(SIDES)

    private var y = FloatArray(SIDES)

    val color: Color

    internal constructor() {
        for (i in 0 until SIDES) {
            x[i] = G.getRandFloat()
            y[i] = G.getRandFloat()
        }
        color = if (G.getTransparentTriangles()) {
            Color(
                G.getRandInt(256),
                G.getRandInt(256),
                G.getRandInt(256),
                G.getRandInt(256)
            )
        } else {
            Color(
                G.getRandInt(256),
                G.getRandInt(256),
                G.getRandInt(256)
            )
        }
    }

    internal constructor(px: FloatArray, py: FloatArray, c: Color) {
        x = px
        y = py
        color = c
    }

    fun getPolygon(width: Int, height: Int): Polygon {
        val width1 = width + 1
        val height1 = height + 1

        val xp = IntArray(SIDES)
        val yp = IntArray(SIDES)

        for (i in 0 until SIDES) {
            xp[i] = (x[i] * width1).toInt()
            yp[i] = (y[i] * height1).toInt()
        }

        return Polygon(xp, yp, SIDES)
    }

    fun getX() = x.clone()

    fun getY() = y.clone()

    fun getColorArray() = when {
        G.getTransparentTriangles() ->
            intArrayOf(
                color.red,
                color.green,
                color.blue,
                color.alpha
            )
        else -> intArrayOf(
            color.red,
            color.green,
            color.blue
        )
    }

    fun clone() = Triangle(
        getX(),
        getY(),
        Color(
            color.red,
            color.green,
            color.blue,
            color.alpha
        )
    )
}