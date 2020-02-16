package global

import java.util.*

object Settings {

    private const val ID_SYMBOL = ":"
    private const val COMMENT_SYMBOL = "#"

    private var blocksWide = ArrayList<Int>()
    private var maxTriangles = ArrayList<Int>()
    private var samples = ArrayList<Int>()
    private var randomBlocks = ArrayList<Int>()
    private var scales = ArrayList<Double>()
    private var postScales = ArrayList<Double>()

    fun load() {
        if (!Global.SETTINGS_FILE.exists()) {
            createSettingsFile()
        }

        Global.SETTINGS_FILE.readLines().filter { line ->
            !line.startsWith(COMMENT_SYMBOL)
        }.forEach { line ->
            setVar(line)
        }

        if (blocksWide.isEmpty()) {
            blocksWide.add(Global.blocksWide)
        }
        if (maxTriangles.isEmpty()) {
            maxTriangles.add(Global.triangles)
        }
        if (samples.isEmpty()) {
            samples.add(Global.maxSamples)
        }
        if (randomBlocks.isEmpty()) {
            randomBlocks.add(Global.randomBlockMult)
        }
        if (scales.isEmpty()) {
            scales.add(Global.scale)
        }
        if (postScales.isEmpty()) {
            postScales.add(Global.postScale)
        }

        blocksWide.trimToSize()
        maxTriangles.trimToSize()
        samples.trimToSize()
        randomBlocks.trimToSize()
        scales.trimToSize()
        postScales.trimToSize()

        if (Global.sequential) {
            Global.maxAttempts = seqSize()
        }
    }

    fun seqSize(): Int {
        return blocksWide.size * maxTriangles.size * samples.size *
                randomBlocks.size * scales.size * postScales.size
    }

    fun reset(n: Int) {
        var i = n
        Global.randomBlockMult = randomBlocks[i % randomBlocks.size]
        i /= randomBlocks.size
        Global.postScale = postScales[i % postScales.size]
        i /= postScales.size
        Global.scale = scales[i % scales.size]
        i /= scales.size
        Global.maxSamples = samples[i % samples.size]
        i /= samples.size
        Global.triangles = maxTriangles[i % maxTriangles.size]
        i /= maxTriangles.size
        Global.blocksWide = blocksWide[i % blocksWide.size]
    }

    private fun setVar(line: String) {
        val split = line.split(ID_SYMBOL.toRegex())

        // if line does not have identifier ignore it
        if (split.size == 2) {

            val key = Setting.valueOf(split[0])
            val value = split[1]

            when (key) {
                Setting.PREDRAW -> Global.preDraw = value.toBoolean()
                Setting.PREDRAW_OUTLINE -> Global.preDrawOutline = value.toBoolean()
                Setting.PREDRAW_SHOW_BEST -> Global.preDrawShowBest = value.toBoolean()
                Setting.ALLOW_COLLISION -> Global.allowCollision = value.toBoolean()
                Setting.TRUE_COLOR -> Global.trueColor = value.toBoolean()
                Setting.TRANSPARENT_TRIANGLES -> Global.transparentTriangles = value.toBoolean()
                Setting.SEQUENTIAL -> Global.sequential = value.toBoolean()
                Setting.BLOCKS_WIDE -> {
                    Global.blocksWide = value.toInt()
                    blocksWide.add(Global.blocksWide)
                }
                Setting.MAX_TRIANGLES -> {
                    Global.triangles = value.toInt()
                    maxTriangles.add(Global.triangles)
                }
                Setting.SAMPLES -> {
                    Global.maxSamples = value.toInt()
                    samples.add(Global.maxSamples)
                }
                Setting.THREAD_COUNT -> {
                    Global.threadCount = if (value.equals(Global.AUTO, ignoreCase = true)) {
                        Runtime.getRuntime().availableProcessors()
                    } else {
                        value.toInt()
                    }
                }
                Setting.RANDOM_BLOCKS -> {
                    Global.randomBlockMult = value.toInt()
                    randomBlocks.add(Global.randomBlockMult)
                }
                Setting.REPAINT_WAIT_MS -> Global.paintWait = value.toInt()
                Setting.ATTEMPTS -> Global.maxAttempts = value.toInt()
                Setting.SCALE -> {
                    Global.scale = value.toDouble()
                    scales.add(Global.scale)
                }
                Setting.POST_SCALE -> {
                    Global.postScale = value.toDouble()
                    postScales.add(Global.postScale)
                }
            }
        }
    }

    private fun createSettingsFile() { // create default settings strings
        Global.SETTINGS_FILE.writeText("""
            ${COMMENT_SYMBOL}All Comments must begin with $COMMENT_SYMBOL

            ${COMMENT_SYMBOL}Thread count can be set to 'AUTO'
            ${Setting.THREAD_COUNT}${ID_SYMBOL}AUTO

            ${Setting.REPAINT_WAIT_MS}$ID_SYMBOL${Global.paintWait}
            ${Setting.ATTEMPTS}$ID_SYMBOL${Global.maxAttempts}

            ${COMMENT_SYMBOL}Boolean variables
            ${Setting.PREDRAW}$ID_SYMBOL${Global.preDraw}
            ${Setting.PREDRAW_OUTLINE}$ID_SYMBOL${Global.preDrawOutline}
            ${Setting.PREDRAW_SHOW_BEST}$ID_SYMBOL${Global.preDrawShowBest}
            ${Setting.ALLOW_COLLISION}$ID_SYMBOL${Global.allowCollision}
            ${Setting.TRUE_COLOR}$ID_SYMBOL${Global.trueColor}
            ${Setting.TRANSPARENT_TRIANGLES}$ID_SYMBOL${Global.transparentTriangles}
            ${Setting.SEQUENTIAL}$ID_SYMBOL${Global.sequential}

            ${COMMENT_SYMBOL}Start of sequential operations...

            ${Setting.BLOCKS_WIDE}$ID_SYMBOL${Global.blocksWide}
            ${Setting.MAX_TRIANGLES}$ID_SYMBOL${Global.triangles}
            ${Setting.SAMPLES}$ID_SYMBOL${Global.maxSamples}

            ${Setting.RANDOM_BLOCKS}$ID_SYMBOL${Global.randomBlockMult}

            ${Setting.SCALE}$ID_SYMBOL${Global.scale}
            ${Setting.POST_SCALE}$ID_SYMBOL${Global.postScale}

            """.trimIndent()
        )
    }
}