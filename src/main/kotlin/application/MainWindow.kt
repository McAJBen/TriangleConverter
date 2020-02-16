package application

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame

class MainWindow(conversion: Conversion) : JFrame() {

    companion object {
        val SCREEN_SIZE = Dimension(600, 600)
        val SCREEN_OFFSET = Dimension(16, 53);
    }

    init {
        add(conversion)
        add(conversion.loadingBar, BorderLayout.SOUTH)
        setSize(SCREEN_SIZE.width + SCREEN_OFFSET.width, SCREEN_SIZE.height + SCREEN_OFFSET.height)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
        isVisible = true
    }
}