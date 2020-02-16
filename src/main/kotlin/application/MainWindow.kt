package application

import global.Global
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.awt.*
import javax.swing.JFrame

class MainWindow(
    converter: Converter
) : JFrame() {

    companion object {
        val SCREEN_SIZE = Dimension(600, 600)
        val SCREEN_OFFSET = Dimension(16, 53)
    }

    private val previewPanel = PreviewPanel(converter)

    private val loadingBar = LoadingBar(converter)

    private var paintPreviewPanelJob: Job? = null

    private var paintLoadingBarJob: Job? = null

    init {
        add(previewPanel)
        add(loadingBar, BorderLayout.SOUTH)
        setSize(
            SCREEN_SIZE.width + SCREEN_OFFSET.width,
            SCREEN_SIZE.height + SCREEN_OFFSET.height
        )
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
        isVisible = true
    }

    fun startDrawing() {
        paintPreviewPanelJob?.cancel()
        paintPreviewPanelJob = GlobalScope.async {
            while (true) {
                previewPanel.repaint()
                delay(Global.paintWait.toLong())
            }
        }

        paintLoadingBarJob?.cancel()
        paintLoadingBarJob = GlobalScope.async {
            while (true) {
                loadingBar.repaint()
                delay(15)
            }
        }
    }

    fun stopDrawing() {
        paintPreviewPanelJob?.cancel()
        paintLoadingBarJob?.cancel()
        repaint()
    }
}