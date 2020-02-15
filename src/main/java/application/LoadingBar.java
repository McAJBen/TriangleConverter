package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class LoadingBar extends JPanel {

    Conversion conversion;


    public LoadingBar(Conversion conversion) {
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 14));
        revalidate();
        this.conversion = conversion;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Dimension size = getSize();
        g.setColor(Color.WHITE);
        g.fillRect(0, size.height - 14, size.width, 14);
        g.setColor(Color.GREEN);
        g.fillRect(0, size.height - 14, conversion.getPercent(size.width), 14);
        g.setColor(Color.BLACK);
        g.drawString(conversion.getInfo(), 1, size.height - 3);
    }
}