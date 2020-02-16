package application

import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.image.BufferedImage

interface ConverterInterface {
    fun hasImage(): Boolean
    fun getNewImage(): BufferedImage?
    fun drawBlockThread(g2d: Graphics2D, size: Dimension)
    fun getPercent(): Double
    fun getInfo(): String
}