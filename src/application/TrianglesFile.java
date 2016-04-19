package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TrianglesFile {
	
	private static final int FACT = 3;
	private static final double FACT_INVERSE = 1.0 / FACT, 
			MAX_SCORE = Math.pow(195075, 0.5);
	
	private Dimension imageSize;
	private ArrayList<Triangle> triangles = new ArrayList<Triangle>();
	private BufferedImage
					image,
					baseImg;
	private boolean imageMade = false;
	private double totalPossibleScore;
	
	TrianglesFile(TrianglesFile tf) {
		this(tf.getTriangles(), tf.imageSize, tf.baseImg);
	}
	
	TrianglesFile(ArrayList<Triangle> trArray, Dimension dimension) {
		for (int i = 0; i < trArray.size(); i++) {
			this.triangles.add(trArray.get(i));
		}
		imageSize = dimension.getSize();
		totalPossibleScore = MAX_SCORE * imageSize.getWidth() * imageSize.getHeight();
		baseImg = null;
	}
	
	TrianglesFile(ArrayList<Triangle> trArray, Dimension dimension, BufferedImage baseChunk) {
		for (int i = 0; i < trArray.size(); i++) {
			this.triangles.add(trArray.get(i));
		}
		imageSize = dimension.getSize();
		totalPossibleScore = MAX_SCORE * imageSize.getWidth() * imageSize.getHeight();
		baseImg = baseChunk;
	}
	
	void modifyRandom() {
		imageMade = false;
		if (triangles.size() <= 0) {
			return;
		}
		int i = getRandom();
		triangles.remove(i);
		triangles.add(new Triangle());
	}
	
	void modifyShape10() {
		imageMade = false;
		if (triangles.size() <= 0) {
			return;
		}
		int i = getRandom();
		double xp[] = triangles.get(i).getXpoints();
		double yp[] = triangles.get(i).getYpoints();
		for (int j = 0; j < 3; j++) {
			xp[j] += G.RANDOM.nextDouble() / 5 - 0.1;
			yp[j] += G.RANDOM.nextDouble() / 5 - 0.1;
			
			xp[j] = checkBounds(xp[j], 1);
			yp[j] = checkBounds(yp[j], 1);
		}
		triangles.set(i, new Triangle(xp, yp, triangles.get(i).getColor()));
	}
	
	void modifyShapeFull() {
		imageMade = false;
		if (triangles.size() <= 0) {
			return;
		}
		int i = getRandom();
		double xp[] = triangles.get(i).getXpoints();
		double yp[] = triangles.get(i).getYpoints();
		for (int j = 0; j < 3; j++) {
			xp[j] = G.RANDOM.nextDouble();
			yp[j] = G.RANDOM.nextDouble();
		}
		triangles.set(i, new Triangle(xp, yp, triangles.get(i).getColor()));
	}
	
	void modifyColor10() {
		imageMade = false;
		if (triangles.size() <= 0) {
			return;
		}
		int i = getRandom();
		int[] col = triangles.get(i).getColorArray();
		for (int j = 0; j < col.length; j++) {
			col[j] += G.RANDOM.nextInt(51) - 25;
			col[j] = checkBounds(col[j], 255);
		}
		triangles.set(i, new Triangle(triangles.get(i).getXpoints(), triangles.get(i).getYpoints(), new Color(col[0], col[1], col[2])));
	}
	
	void modifyRemove() {
		imageMade = false;
		if (triangles.size() > 2) {
			triangles.remove(G.RANDOM.nextInt(triangles.size()));
		}
	}
	
	double compare(BufferedImage img) {
		createImg();
		double score = compareTotal(img, image);
		score /= totalPossibleScore;
		return 1-score;
	}
	
	static double compare(BufferedImage original, BufferedImage newImg) {
		double score = compareTotal(original, newImg);
		score /= (newImg.getWidth() * newImg.getHeight() * MAX_SCORE);
		return 1-score;
	}
	
	private static double compareTotal(BufferedImage original, BufferedImage newImg) {
		double score = 0;
		for (int i = 0; i < newImg.getWidth(); i++) {
			for (int j = 0; j < newImg.getHeight(); j++) {
				Color b = new Color(newImg.getRGB(i, j), true);
				if (b.getAlpha() != 255) {
					score += MAX_SCORE;
				}
				else {
					Color a = new Color(original.getRGB(i, j));
					score += Math.sqrt(
						Math.pow(a.getRed() - b.getRed(), 2) +
						Math.pow(a.getGreen()-b.getGreen(), 2) +
						Math.pow(a.getBlue()-b.getBlue(), 2));
				}
			}
		}
		return score;
	}

	boolean hasAlpha() {
		createImg();
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				if (image.getRGB(i, j) == 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	BufferedImage getImage() {
		createImg();
		return image;
	}
	
	int getSize() {
		return triangles.size();
	}
	
	void addTriangle() {
		imageMade = false;
		triangles.add(new Triangle());
	}
	
	void removeBackTriangle() {
		imageMade = false;
		if (getSize() > 0) {
			triangles.remove(0);
		}
	}
	
	ArrayList<Triangle> getTriangles() {
		ArrayList<Triangle> tr = new ArrayList<Triangle>();
		for (int i = 0; i < triangles.size(); i++) {
			tr.add(triangles.get(i).clone());
		}
		return triangles;
	}
	
	BufferedImage getImage(Dimension newBlockPixelSize) {
		return makeImg(newBlockPixelSize.width, newBlockPixelSize.height);
	}

	private void createImg() {
		if (imageMade) {
			return;
		}
		image = makeImg(imageSize.width, imageSize.height);
		imageMade = true;
	}
	
	private BufferedImage makeImg(int width, int height) {
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = img.createGraphics();
	    
	    if (baseImg != null) {
	    	g2d.drawImage(baseImg, 0, 0, width, height, null);
	    }
	    
		
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		for (int i = 0; i < triangles.size(); i++) {
			g2d.setColor(triangles.get(i).getColor());
			g2d.fillPolygon(triangles.get(i).getPolygon(width, height));
		}
	    g2d.dispose();
	    return img;
	}
	
	private int getRandom() {
		return (int) Math.pow(G.RANDOM.nextInt((int) Math.pow(triangles.size(), FACT)), FACT_INVERSE);
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
}
