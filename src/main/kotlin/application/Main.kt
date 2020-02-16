package application

import global.FileHandler
import global.Global
import global.Settings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {

    Settings.load()

    val converter = Converter()

    val mainWindow = MainWindow(converter)

    GlobalScope.launch {
        while (true) {
            mainWindow.title = Global.FINDING_FILE

            val file = FileHandler.getImageFile()
            if (file != null) {
                for (attempt in 1..Global.maxAttempts) {
                    Global.reset()
                    mainWindow.title = Global.getTitle(attempt)

                    mainWindow.startDrawing()
                    converter.startConversion(file)
                    mainWindow.stopDrawing()
                }
                val originalImg = FileHandler.getImage(file)
                file.delete()
                FileHandler.putImageInFile(file, Global.ORIGINAL, originalImg, Global.BLANK)
            } else {
                delay(1000)
            }
        }
    }
}