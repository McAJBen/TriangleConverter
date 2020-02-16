package application

import global.FileHandler
import global.G
import global.Settings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

fun main() {

    Settings.load()
    val conversion = Conversion()
    val mainWindow = MainWindow(conversion)

    GlobalScope.launch {
        while (true) {
            mainWindow.title = G.FINDING_FILE

            val file: File? = FileHandler.getFile()
            if (file != null) {
                for (attempt in 1..G.getMaxAttempts()) {
                    G.reset()
                    mainWindow.title = G.getTitle(attempt)
                    conversion.startConversion(file)
                }
                val originalImg = FileHandler.getImage(file)
                file.delete()
                FileHandler.putImageInFile(file, G.ORIGINAL, originalImg, G.BLANK)
            } else {
                delay(1_000)
            }
        }
    }
}