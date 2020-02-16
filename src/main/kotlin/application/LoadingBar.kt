package application

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JPanel

class LoadingBar(
    private val converter: ConverterInterface
) : JPanel() {

    init {
        preferredSize = Dimension(Int.MAX_VALUE, 14)
        revalidate()
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        g.color = Color.WHITE
        g.fillRect(0, size.height - 14, size.width, 14)
        g.color = Color.GREEN
        g.fillRect(0, size.height - 14, (converter.getPercent() * size.width).toInt(), 14)
        g.color = Color.BLACK
        g.drawString(converter.getInfo(), 1, size.height - 3)
    }
}