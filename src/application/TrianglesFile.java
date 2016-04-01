package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class TrianglesFile {
	
	private static final int FACT = 3;
	private static final double FACT_INVERSE = 1.0 / FACT, 
			MAX_SCORE = Math.pow(195075, 0.5);
	
	private static Random rand = new Random();
	
	private Dimension imageSize;
	private ArrayList<Triangle> triangles = new ArrayList<Triangle>();
	private BufferedImage
					image,
					baseImg;
	private boolean imageMade = false;
	
	public TrianglesFile(TrianglesFile tf) {
		this(tf.getTriangles(), tf.imageSize, tf.baseImg);
	}
	
	public TrianglesFile(ArrayList<Triangle> trArray, Dimension dimension) {
		for (int i = 0; i < trArray.size(); i++) {
			this.triangles.add(trArray.get(i));
		}
		imageSize = (Dimension) dimension.clone();
	}
	
	public TrianglesFile(ArrayList<Triangle> trArray, Dimension dimension, BufferedImage baseChunk) {
		this(trArray, dimension);
		baseImg = baseChunk;
	}

	private BufferedImage makeImg(int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = img.createGraphics();
	    g2d.drawImage(baseImg, 0, 0, width, height, null);
	    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		for (int i = 0; i < triangles.size(); i++) {
			g2d.setColor(triangles.get(i).getColor());
			g2d.fillPolygon(triangles.get(i).getPolygon(width, height));
		}
	    g2d.dispose();
	    return img;
	}
	
	public void modifyRandom() {
		imageMade = false;
		if (triangles.size() <= 0) {
			return;
		}
		int i = getRandom();
		triangles.remove(i);
		triangles.add(new Triangle());
	}
	
	public void modifyShape10() {
		imageMade = false;
		if (triangles.size() <= 0) {
			return;
		}
		int i = getRandom();
		double xp[] = triangles.get(i).getXpoints();
		double yp[] = triangles.get(i).getYpoints();
		for (int j = 0; j < 3; j++) {
			xp[j] += rand.nextDouble() / 5 - 0.1;
			yp[j] += rand.nextDouble() / 5 - 0.1;
			
			xp[j] = checkBounds(xp[j], 1);
			yp[j] = checkBounds(yp[j], 1);
		}
		triangles.set(i, new Triangle(xp, yp, triangles.get(i).getColor()));
	}
	
	public void modifyShapeFull() {
		imageMade = false;
		if (triangles.size() <= 0) {
			return;
		}
		int i = getRandom();
		double xp[] = triangles.get(i).getXpoints();
		double yp[] = triangles.get(i).getYpoints();
		for (int j = 0; j < 3; j++) {
			xp[j] = rand.nextDouble();
			yp[j] = rand.nextDouble();
		}
		triangles.set(i, new Triangle(xp, yp, triangles.get(i).getColor()));
	}
	
	public void modifyColor10() {
		imageMade = false;
		if (triangles.size() <= 0) {
			return;
		}
		int i = getRandom();
		int[] col = {triangles.get(i).getRed(),
				triangles.get(i).getGreen(),
				triangles.get(i).getBlue()};
		for (int j = 0; j < col.length; j++) {
			col[j] += rand.nextInt(51) - 25;
			col[j] = checkBounds(col[j], 255);
		}
		triangles.set(i, new Triangle(triangles.get(i).getXpoints(), triangles.get(i).getYpoints(), new Color(col[0], col[1], col[2])));
	}
	
	public void modifyRemove() {
		imageMade = false;
		if (triangles.size() > 2) {
			triangles.remove(rand.nextInt(triangles.size()));
		}
	}
	
	private int getRandom() {
		return (int) Math.pow(rand.nextInt((int) Math.pow(triangles.size(), FACT)), FACT_INVERSE);
	}
	
	private double checkBounds(double n, int max) {
		if (n > max) {
			return max;
		}
		else if (n < 0) {
			return 0;
		}
		return n;
	}
	
	private int checkBounds(int n, int max) {
		if (n > max) {
			return max;
		}
		else if (n < 0) {
			return 0;
		}
		return n;
	}
	
	public double compare(BufferedImage img) {
		createImg();
		double score = 0;
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				if (new Color(image.getRGB(i, j), true).getAlpha() != 255) {
					score += MAX_SCORE;
				}
				else {
					Color a = new Color(img.getRGB(i, j));
					Color b = new Color(image.getRGB(i, j));
					score += Math.sqrt(
						Math.pow(a.getRed() - b.getRed(), 2) +
						Math.pow(a.getGreen()-b.getGreen(), 2) +
						Math.pow(a.getBlue()-b.getBlue(), 2));
					
				}
			}
		}
		score /= MAX_SCORE;
		score /= (imageSize.getWidth() * imageSize.getHeight());
		return 1-score;
	}

	public boolean hasAlpha() {
		createImg();
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				if (new Color(image.getRGB(i, j), true).getAlpha() != 255) {
					return true;
				}
			}
		}
		return false;
	}
	
	public BufferedImage getImage() {
		createImg();
		return image;
	}
	
	public int getSize() {
		return triangles.size();
	}
	
	public void addTriangle() {
		imageMade = false;
		triangles.add(new Triangle());
	}
	
	public void removeBackTriangle() {
		imageMade = false;
		if (getSize() > 0) {
			triangles.remove(0);
		}
	}
	
	public ArrayList<Triangle> getTriangles() {
		ArrayList<Triangle> tr = new ArrayList<Triangle>();
		for (int i = 0; i < triangles.size(); i++) {
			tr.add(triangles.get(i).clone());
		}
		return triangles;
	}

	private void createImg() {
		if (imageMade) {
			return;
		}
		image = makeImg(imageSize.width, imageSize.height);
		imageMade = true;
	}

	public BufferedImage getImage(Dimension newBlockPixelSize) {
		return makeImg(newBlockPixelSize.width, newBlockPixelSize.height);
	}
}
