package application;

import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Random;

public class Triangle {
	private static final int SIDES = 3;
	private static final Random RAND = new Random();
	private double[] Xpoints = new double[SIDES];
	private double[] Ypoints = new double[SIDES];
	private Color color;
	
	Triangle() {
		for (int i = 0; i < SIDES; i++) {
			Xpoints[i] = RAND.nextDouble();
			Ypoints[i] = RAND.nextDouble();
		}
		color = new Color(RAND.nextInt(256), RAND.nextInt(256), RAND.nextInt(256));
	}
	
	Triangle(double[] px, double[] py, Color c) {
		Xpoints = px.clone();
		Ypoints = py.clone();
		setColor(c);
	}
	
	Triangle(ArrayList<Double> px, ArrayList<Double> py, Color c) {
		Xpoints = new double[SIDES];
		Ypoints = new double[SIDES];
		for (int i = 0; i < SIDES; i++) {
			Xpoints[i] = px.get(i);
			Ypoints[i] = py.get(i);
		}
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
	
	double[] getXpoints() {
		return Xpoints.clone();
	}
	
	double[] getYpoints() {
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