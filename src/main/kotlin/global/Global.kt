package global

import java.io.File

object Global {

    const val BLANK = ""
    const val ORIGINAL = "Original"
    const val NEW = "New"
    const val SPACE = " "
    const val RUN_TIME = " Run Time: "
    const val END = " End?: "
    const val FINDING_FILE = "Finding File ..."
    const val OUT_OF_MEMORY = "Out of memory, not able to display"
    const val FILE_ERROR = "ERROR: Could not read file "
    const val PNG = "png"
    const val DOT_PNG = ".$PNG"
    const val JPG = "jpg"
    const val BMP = "bmp"
    const val AUTO = "AUTO"

    val USER_DIR = File(System.getProperty("user.dir"))
    val SETTINGS_FILE = File(USER_DIR, "TriangleConverter.settings")

    var preDraw = true
    var preDrawOutline = false
    var preDrawShowBest = true
    var allowCollision = true
    var trueColor = false
    var transparentTriangles = true
    var blocksWide = 10
    var triangles = 2
    var maxSamples = 1
    var threadCount = Runtime.getRuntime().availableProcessors()
    var paintWait = 250
    var maxAttempts = 10
    var randomBlocks = 0
    var randomBlockMult = 0
    var scale = 0.5
    var postScale = 2.0
    var sequential = false
    private var seqCount = 0

    fun reset() {
        if (sequential) {
            Settings.reset(seqCount++)
            if (seqCount >= Settings.seqSize()) {
                seqCount = 0
            }
        }
        randomBlocks = blocksWide * blocksWide * randomBlockMult
    }

    val totalScale: Double
        get() = scale * postScale

    fun getTitle(attempt: Int): String {
        return "TC Wi:" + blocksWide +
                " Tr:" + triangles +
                " Sa:" + maxSamples +
                " Th:" + threadCount +
                " At:" + attempt + "/" + maxAttempts +
                " Sc:" + scale +
                " > " + postScale +
                " RB:" + randomBlocks +
                (if (trueColor) " TruCol" else " LinCol") +
                if (transparentTriangles) " Transp" else " Opaq"
    }

    val shortTitle: String
        get() = "_" + blocksWide +
                "_" + triangles +
                "_" + maxSamples +
                "_" + scale +
                "_" + postScale +
                "_" + randomBlocks +
                "_" + (if (trueColor) "T" else "F") +
                if (transparentTriangles) "T" else "F"
}