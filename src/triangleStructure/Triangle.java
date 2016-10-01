package triangleStructure;

import java.awt.Color;
import java.awt.Polygon;

import global.G;

public class Triangle {
	private static final int SIDES = 3;
	private float[] Xpoints = new float[SIDES];
	private float[] Ypoints = new float[SIDES];
	private Color color;
	
	public Triangle() {
		for (int i = 0; i < SIDES; i++) {
			Xpoints[i] = G.getRandFloat();
			Ypoints[i] = G.getRandFloat();
		}
		color = new Color(G.getRandInt(256), G.getRandInt(256), G.getRandInt(256));
	}
	
	Triangle(float[] px, float[] py, Color c) {
		Xpoints = px.clone();
		Ypoints = py.clone();
		setColor(c);
	}
	
	Color getColor() {
		return new Color(color.getRed(), color.getGreen(), color.getBlue());
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
	
	@Override
	public Triangle clone() {
		return new Triangle(Xpoints.clone(), Ypoints.clone(), getColor());
	}

	private void setColor(Color c) {
		color = new Color(c.getRed(), c.getGreen(), c.getBlue());
	}
}