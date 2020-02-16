package global

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

object FileHandler {

    fun getImageFile(): File? {
        return File(Global.USER_DIR).listFiles()?.find { file ->
            file.isValidFile()
        }
    }

    fun getImage(file: File): BufferedImage? {
        return try {
            ImageIO.read(file)
        } catch (e: IOException) {
            println(Global.FILE_ERROR + file.name)
            null
        }
    }

    fun putImageInFile(
        f: File,
        folder: String,
        image: BufferedImage?,
        append: String
    ) {
        val fi = toFile(f, folder, append)
        try {
            if (!fi.exists()) {
                fi.mkdirs()
            }
            ImageIO.write(image, Global.PNG, fi)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun toFile(f: File, folder: String, append: String): File {
        return File(File(f.parent, folder), f.nameWithoutExtension + append + Global.DOT_PNG)
    }

    private fun File.isValidFile(): Boolean {
        return isFile && (
                extension.equals(Global.PNG, ignoreCase = true)
                        || extension.equals(Global.JPG, ignoreCase = true)
                        || extension.equals(Global.BMP, ignoreCase = true)
                )
    }
}