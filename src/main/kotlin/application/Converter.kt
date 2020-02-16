package application

import blockStructure.BTGrid
import blockStructure.BTRandom
import blockStructure.BlockThreadHandler
import global.FileHandler
import global.Global
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File

class Converter : ConverterInterface {

    companion object {
        private fun getNew4Byte(width: Int, height: Int): BufferedImage {
            val image = BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
            val blank = IntArray(width * height * 4)
            image.raster.setPixels(0, 0, image.width, image.height, blank)
            return image
        }

        private fun to4Byte(image: BufferedImage): BufferedImage {
            return if (image.type == BufferedImage.TYPE_4BYTE_ABGR) {
                image
            } else {
                val b2 = BufferedImage(image.width, image.height, BufferedImage.TYPE_4BYTE_ABGR)
                b2.graphics.drawImage(image, 0, 0, null)
                b2
            }
        }
    }

    private var file: File? = null

    private var newImg: BufferedImage? = null

    private var blockThread: BlockThreadHandler? = null

    fun startConversion(file: File) {
        this.file = file

        val originalImg = to4Byte(FileHandler.getImage(file)!!)
        newImg = getNew4Byte(
            (originalImg.width * Global.totalScale).toInt(),
            (originalImg.height * Global.totalScale).toInt()
        )

        blockThread = BTGrid(originalImg, newImg!!)
        blockThread?.execute()
        blockThread = BTRandom(originalImg, newImg!!)
        blockThread?.execute()

        FileHandler.putImageInFile(file, Global.NEW, newImg, Global.shortTitle)
        blockThread = null
        this.file = null
    }


    override fun hasImage(): Boolean {
        return blockThread != null
    }

    override fun getNewImage(): BufferedImage? {
        return newImg
    }

    override fun drawBlockThread(g2d: Graphics2D, size: Dimension) {
        blockThread?.paint(g2d, size)
    }

    override fun getPercent(): Double {
        return blockThread?.percent ?: 0.0
    }

    override fun getInfo(): String = try {
        String.format("%03.0f%%", blockThread!!.percent * 100) +
                Global.RUN_TIME + blockThread!!.runTime +
                Global.END + blockThread!!.estimatedEndTime +
                Global.SPACE + file!!.name
    } catch (e: NullPointerException) {
        "No Info"
    }
}