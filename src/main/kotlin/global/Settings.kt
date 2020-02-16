package global

import global.Global.maxAttempts
import global.Global.maxSamples
import global.Global.paintWait
import java.io.*
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
        val br: BufferedReader
        br = try {
            val settingsFile =
                File(Global.USER_DIR + File.separator + Global.SETTINGS_FILE)
            // check if settings exist and read first line
            if (settingsFile.exists()) {
                BufferedReader(FileReader(settingsFile))
            } else throw IOException(Global.NO_SETTINGS_FILE)
        } catch (e1: IOException) {
            createSettingsFile()
            return
        }

        while (true) { // read a line
            try {
                val settingsString = br.readLine() ?: throw IOException()
                setVar(settingsString)
            } catch (e: IOException) { // can't read another line, the file must be done
                break
            }
        }
        if (blocksWide.isEmpty()) {
            blocksWide.add(Global.blocksWide)
        }
        if (maxTriangles.isEmpty()) {
            maxTriangles.add(Global.triangles)
        }
        if (samples.isEmpty()) {
            samples.add(maxSamples)
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
            maxAttempts = seqSize()
        }
    }

    fun seqSize(): Int {
        return blocksWide.size * maxTriangles.size * samples.size *
                randomBlocks.size * scales.size * postScales.size
    }

    // return true if correctly dealt with line
// return false if sequential is starting
    private fun setVar(line: String) {
        if (!line.startsWith(COMMENT_SYMBOL)) {
            val split = line.split(ID_SYMBOL.toRegex()).toTypedArray()
            // if line does not have identifier ignore it
            if (split.size == 2) {
                when (Setting.valueOf(split[0])) {
                    Setting.PREDRAW -> Global.preDraw = split[1].toBoolean()
                    Setting.PREDRAW_OUTLINE -> Global.preDrawOutline = split[1].toBoolean()
                    Setting.PREDRAW_SHOW_BEST -> Global.preDrawShowBest = split[1].toBoolean()
                    Setting.ALLOW_COLLISION -> Global.allowCollision = split[1].toBoolean()
                    Setting.TRUE_COLOR -> Global.trueColor = split[1].toBoolean()
                    Setting.TRANSPARENT_TRIANGLES -> Global.transparentTriangles = split[1].toBoolean()
                    Setting.SEQUENTIAL -> Global.sequential = split[1].toBoolean()
                    Setting.BLOCKS_WIDE -> {
                        Global.blocksWide = split[1].toInt()
                        blocksWide.add(Global.blocksWide)
                    }
                    Setting.MAX_TRIANGLES -> {
                        Global.triangles = split[1].toInt()
                        maxTriangles.add(Global.triangles)
                    }
                    Setting.SAMPLES -> {
                        maxSamples = split[1].toInt()
                        samples.add(maxSamples)
                    }
                    Setting.THREAD_COUNT -> if (split[1].equals(Global.AUTO, ignoreCase = true)) {
                        Global.threadCount = Runtime.getRuntime().availableProcessors()
                    } else {
                        Global.threadCount = split[1].toInt()
                    }
                    Setting.RANDOM_BLOCKS -> {
                        Global.randomBlockMult = split[1].toInt()
                        randomBlocks.add(Global.randomBlockMult)
                    }
                    Setting.REPAINT_WAIT_MS -> paintWait = split[1].toInt()
                    Setting.ATTEMPTS -> maxAttempts = split[1].toInt()
                    Setting.SCALE -> {
                        Global.scale = split[1].toDouble()
                        scales.add(Global.scale)
                    }
                    Setting.POST_SCALE -> {
                        Global.postScale = split[1].toDouble()
                        postScales.add(Global.postScale)
                    }
                }
            }
        }
    }

    private fun createSettingsFile() { // create default settings strings
        val settingsString =
            COMMENT_SYMBOL + "All Comments must begin with " + COMMENT_SYMBOL + "\n\n" +
                    COMMENT_SYMBOL + "Thread count can be set to 'AUTO'\n" +
                    Setting.THREAD_COUNT + ID_SYMBOL + "AUTO" + "\n\n" +
                    Setting.REPAINT_WAIT_MS + ID_SYMBOL + paintWait + "\n" +
                    Setting.ATTEMPTS + ID_SYMBOL + maxAttempts + "\n\n" +
                    COMMENT_SYMBOL + "Boolean variables\n" +
                    Setting.PREDRAW + ID_SYMBOL + Global.preDraw + "\n" +
                    Setting.PREDRAW_OUTLINE + ID_SYMBOL + Global.preDrawOutline + "\n" +
                    Setting.PREDRAW_SHOW_BEST + ID_SYMBOL + Global.preDrawShowBest + "\n" +
                    Setting.ALLOW_COLLISION + ID_SYMBOL + Global.allowCollision + "\n" +
                    Setting.TRUE_COLOR + ID_SYMBOL + Global.trueColor + "\n" +
                    Setting.TRANSPARENT_TRIANGLES + ID_SYMBOL + Global.transparentTriangles + "\n" +
                    Setting.SEQUENTIAL + ID_SYMBOL + Global.sequential + "\n\n" +
                    COMMENT_SYMBOL + "Start of sequential operations...\n\n" +
                    Setting.BLOCKS_WIDE + ID_SYMBOL + Global.blocksWide + "\n" +
                    Setting.MAX_TRIANGLES + ID_SYMBOL + Global.triangles + "\n" +
                    Setting.SAMPLES + ID_SYMBOL + maxSamples + "\n\n" +
                    Setting.RANDOM_BLOCKS + ID_SYMBOL + Global.randomBlockMult + "\n\n" +
                    Setting.SCALE + ID_SYMBOL + Global.scale + "\n" +
                    Setting.POST_SCALE + ID_SYMBOL + Global.postScale
        // write default settings to file
        try {
            val settingsFile =
                File(Global.USER_DIR + File.separator + Global.SETTINGS_FILE)
            val writer = BufferedWriter(FileWriter(settingsFile))
            writer.write(settingsString)
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
            // can't create the settings file?
        }
    }

    fun reset(i: Int) {
        var i = i
        Global.randomBlockMult = randomBlocks[i % randomBlocks.size]
        i /= randomBlocks.size
        Global.postScale = postScales[i % postScales.size]
        i /= postScales.size
        Global.scale = scales[i % scales.size]
        i /= scales.size
        maxSamples = samples[i % samples.size]
        i /= samples.size
        Global.triangles = maxTriangles[i % maxTriangles.size]
        i /= maxTriangles.size
        Global.blocksWide = blocksWide[i % blocksWide.size]
    }
}