package application

import global.FileHandler
import global.Global
import global.Settings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {

    Settings.load()
    val conversion = Conversion()
    val mainWindow = MainWindow(conversion)

    GlobalScope.launch {
        while (true) {
            mainWindow.title = Global.FINDING_FILE

            val file = FileHandler.getImageFile()
            if (file != null) {
                for (attempt in 1..Global.maxAttempts) {
                    Global.reset()
                    mainWindow.title = Global.getTitle(attempt)
                    conversion.startConversion(file)
                }
                val originalImg = FileHandler.getImage(file)
                file.delete()
                FileHandler.putImageInFile(file, Global.ORIGINAL, originalImg, Global.BLANK)
            } else {
                delay(1_000)
            }
        }
    }
}