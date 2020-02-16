package global

import java.util.*

object Global {
    const val BLANK = ""
    const val ORIGINAL = "Original"
    const val NEW = "New"
    const val SPACE = " "
    const val RUN_TIME = SPACE + "Run Time:" + SPACE
    const val END = SPACE + "End?:" + SPACE
    const val FINDING_FILE = "Finding File ..."
    const val OUT_OF_MEMORY = "Out of memory, not able to display"
    const val PAINT_THREAD = "paintThread"
    const val LOAD_THREAD = "LoadingThread"
    @JvmField
    val USER_DIR = System.getProperty("user.dir")
    const val FILE_ERROR = "ERROR: Could not read file$SPACE"
    const val PNG = "png"
    const val DOT_PNG = ".$PNG"
    const val JPG = "jpg"
    const val BMP = "bmp"
    const val SETTINGS_FILE = "TriangleConverter.settings"
    const val NO_SETTINGS_FILE = "Settings File does not exist"
    const val AUTO = "AUTO"
    @JvmField
    var preDraw = true
    @JvmField
    var preDrawOutline = false
    @JvmField
    var preDrawShowBest = true
    @JvmField
    var allowCollision = true
    @JvmField
    var trueColor = false
    @JvmField
    var transparentTriangles = true
    @JvmField
    var blocksWide = 10
    @JvmField
    var triangles = 2
    @JvmStatic
    var maxSamples = 1
    @JvmField
    var threadCount = Runtime.getRuntime().availableProcessors()
    @JvmStatic
    var paintWait = 250
    @JvmStatic
    var maxAttempts = 10
    @JvmStatic
    var randomBlocks = 0
    @JvmField
    var randomBlockMult = 0
    @JvmField
    var scale = 0.5
    @JvmField
    var postScale = 2.0
    @JvmField
    var sequential = false
    var seqCount = 0
    private val RANDOM = Random()
    fun reset() {
        if (sequential) {
            Settings.reset(seqCount++)
            if (seqCount >= Settings.seqSize()) {
                seqCount = 0
            }
        }
        randomBlocks = blocksWide * blocksWide * randomBlockMult
    }

    @JvmStatic
    val randDouble: Double
        get() = RANDOM.nextDouble()

    val randFloat: Float
        get() = RANDOM.nextFloat()

    @JvmStatic
    fun getRandInt(i: Int): Int {
        return RANDOM.nextInt(i)
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