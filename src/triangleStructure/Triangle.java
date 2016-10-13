package triangleStructure;

import java.awt.Color;
import java.awt.Polygon;

import global.G;

public class Triangle {
	private static final int SIDES = 3;
	private float[] Xpoints = new float[SIDES];
	private float[] Ypoints = new float[SIDES];
	private Color color;
	
	Triangle() {
		for (int i = 0; i < SIDES; i++) {
			Xpoints[i] = G.getRandFloat();
			Ypoints[i] = G.getRandFloat();
		}
		color = new Color(G.getRandInt(256), G.getRandInt(256), G.getRandInt(256));
	}
	
	Triangle(float[] px, float[] py, Color c) {
		Xpoints = px;
		Ypoints = py;
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
			xp[i] = (int) (Xpoints[i] * width);
			yp[i] = (int) (Ypoints[i] * height);
		}
		return new Polygon(xp, yp, SIDES);
	}
	
	float[] getXpoints() {
		return Xpoints.clone();
	}
	
	float[] getYpoints() {
		return Ypoints.clone();
	}
	
	int[] getColorArray() {
		return new int[] {color.getRed(), color.getGreen(), color.getBlue()};
	}
	
	protected Triangle clone() {
		return new Triangle(Xpoints.clone(), Ypoints.clone(), new Color(color.getRed(), color.getGreen(), color.getBlue()));
	}
}