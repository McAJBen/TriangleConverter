package triangleStructure;

import java.awt.Color;
import java.awt.Polygon;

import global.G;

public class Triangle {

    private static final int SIDES = 3;
    private float[] x = new float[SIDES];
    private float[] y = new float[SIDES];
    private Color color;

    Triangle() {
        for (int i = 0; i < SIDES; i++) {
            x[i] = G.getRandFloat();
            y[i] = G.getRandFloat();
        }
        if (G.getTransparentTriangles()) {
            color = new Color(G.getRandInt(256), G.getRandInt(256), G.getRandInt(256), G.getRandInt(256));
        }
        else {
            color = new Color(G.getRandInt(256), G.getRandInt(256), G.getRandInt(256));
        }
    }

    Triangle(float[] px, float[] py, Color c) {
        x = px;
        y = py;
        color = c;
    }

    Color getColor() {
        return color;
    }

    Polygon getPolygon(int width, int height) {
        width++;
        height++;
        int[] xp = new int[SIDES];
        int[] yp = new int[SIDES];
        for (int i = 0; i < SIDES; i++) {
            xp[i] = (int) (x[i] * width);
            yp[i] = (int) (y[i] * height);
        }
        return new Polygon(xp, yp, SIDES);
    }

    float[] getX() {
        return x.clone();
    }

    float[] getY() {
        return y.clone();
    }

    int[] getColorArray() {
        if (G.getTransparentTriangles()) {
            return new int[] {color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()};
        }
        else {
            return new int[] {color.getRed(), color.getGreen(), color.getBlue()};
        }
    }

    protected Triangle clone() {
        return new Triangle(getX(), getY(), new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
    }
}