package application;

import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Random;

public class Triangle {
	private final static int SIDES = 3;
	private static Random rand = new Random();
	private double[] Xpoints = new double[SIDES];
	private double[] Ypoints = new double[SIDES];
	private Color color;
	
	Triangle() {
		for (int i = 0; i < SIDES; i++) {
			Xpoints[i] = rand.nextDouble();
			Ypoints[i] = rand.nextDouble();
		}
		color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
	}
	public Triangle(double[] px, double[] py, Color c) {
		Xpoints = px.clone();
		Ypoints = py.clone();
		setColor(c);
	}
	public Triangle(ArrayList<Double> px, ArrayList<Double> py, Color c) {
		Xpoints = new double[SIDES];
		Ypoints = new double[SIDES];
		for (int i = 0; i < SIDES; i++) {
			Xpoints[i] = px.get(i);
			Ypoints[i] = py.get(i);
		}
		setColor(c);
	}
	private void setColor(Color c) {
		color = new Color(c.getRed(), c.getGreen(), c.getBlue());
	}
	public Color getColor() {
		return new Color(color.getRed(), color.getGreen(), color.getBlue());
	}
	public Polygon getPolygon(int width, int height) {
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
	public double[] getXpoints() {
		return Xpoints.clone();
	}
	public double[] getYpoints() {
		return Ypoints.clone();
	}
	public int getRed() {
		return color.getRed();
	}
	public int getGreen() {
		return color.getGreen();
	}
	public int getBlue() {
		return color.getBlue();
	}
	@Override
	public Triangle clone() {
		return new Triangle(Xpoints.clone(), Ypoints.clone(), getColor());
	}
	@Override
	public String toString() {
		String s = color.toString();
		for (int i = 0; i < SIDES; i++) {
			s = s.concat("\t" + Xpoints[i] + "\t" + Ypoints[i]);
		}
		return  s;
	}
}