package application

import global.Global
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

class PreviewPanel(
    private val converter: ConverterInterface
) : JPanel() {

    override fun paint(g: Graphics) {
        super.paint(g)

        if (converter.hasImage()) {
            try {
                val g2d = g as Graphics2D
                g2d.drawImage(
                    converter.getNewImage(),
                    0,
                    0,
                    size.width,
                    size.height,
                    null
                )

                if (Global.preDraw) {
                    converter.drawBlockThread(g2d, size)
                    g2d.color = Color.BLACK
                }
            } catch (e: OutOfMemoryError) {
                g.drawString(Global.OUT_OF_MEMORY, 5, 15)
                g.drawString(String.format("%03.0f%%", converter.getPercent() * 100), 5, 30)
            }
        } else {
            g.color = Color.BLACK
            g.drawString(Global.FINDING_FILE, 1, size.height - 3)
        }
    }
}