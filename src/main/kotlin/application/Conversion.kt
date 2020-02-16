package application

import blockStructure.BlockThreadHandler
import blockStructure.btGrid
import blockStructure.btRandom
import global.FileHandler
import global.Global
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.JPanel

class Conversion : JPanel() {

    companion object {
        private fun getNew4Byte(w: Int, h: Int): BufferedImage {
            val b = BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR)
            val blank = IntArray(w * h * 4)
            b.raster.setPixels(0, 0, b.width, b.height, blank)
            return b
        }

        private fun to4Byte(b: BufferedImage): BufferedImage {
            return if (b.type == BufferedImage.TYPE_4BYTE_ABGR) {
                b
            } else {
                val b2 = BufferedImage(b.width, b.height, BufferedImage.TYPE_4BYTE_ABGR)
                b2.graphics.drawImage(b, 0, 0, null)
                b2
            }
        }
    }

    private var file: File? = null

    private var newImg: BufferedImage? = null

    private var blockThread: BlockThreadHandler? = null

    val loadingBar: LoadingBar = LoadingBar(this)

    val info: String
        get() = try {
            blockThread!!.percentDone +
                    Global.RUN_TIME + blockThread!!.runTime +
                    Global.END + blockThread!!.estimatedEndTime +
                    Global.SPACE + file!!.name
        } catch (e: NullPointerException) {
            "No Info"
        }

    private val paintThread: Thread
        get() = object : Thread(Global.PAINT_THREAD) {
            override fun run() {
                while (!isInterrupted) {
                    repaint()
                    try {
                        sleep(Global.paintWait.toLong())
                    } catch (e: InterruptedException) {
                        break
                    }
                }
            }
        }

    private val loadThread: Thread
        get() = object : Thread(Global.LOAD_THREAD) {
            override fun run() {
                while (!isInterrupted) {
                    loadingBar.repaint()
                    try {
                        sleep(15)
                    } catch (e: InterruptedException) {
                        break
                    }
                }
            }
        }

    fun startConversion(file: File) {
        val repaintThread = paintThread
        val loadThread = loadThread
        this.file = file

        repaintThread.start()
        loadThread.start()

        val originalImg = to4Byte(FileHandler.getImage(file)!!)
        newImg = getNew4Byte(
            (originalImg.width * Global.totalScale).toInt(),
            (originalImg.height * Global.totalScale).toInt()
        )

        blockThread = btGrid(originalImg, newImg)
        blockThread?.start()
        blockThread = btRandom(originalImg, newImg)
        blockThread?.start()

        FileHandler.putImageInFile(file, Global.NEW, newImg, Global.shortTitle)
        blockThread = null
        this.file = null
        repaintThread.interrupt()
        loadThread.interrupt()
        repaint()
    }

    fun getPercent(width: Int): Int {
        return try {
            (blockThread!!.percent * width).toInt()
        } catch (e: NullPointerException) {
            0
        }
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        val size = size
        if (blockThread != null) {
            try {
                val g2d = g as Graphics2D
                g2d.drawImage(newImg, 0, 0, size.width, size.height, null)
                try {
                    if (Global.preDraw) {
                        blockThread!!.paint(g2d, size)
                        g2d.color = Color.BLACK
                    }
                } catch (e: NullPointerException) { // blockThread not created
                }
            } catch (e: OutOfMemoryError) {
                g.drawString(Global.OUT_OF_MEMORY, 5, 15)
                g.drawString(blockThread!!.percentDone, 5, 30)
            }
        } else {
            g.color = Color.BLACK
            g.drawString(Global.FINDING_FILE, 1, size.height - 3)
        }
    }
}