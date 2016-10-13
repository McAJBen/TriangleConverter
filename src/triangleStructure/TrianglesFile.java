package triangleStructure;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import global.G;

public class TrianglesFile {
	
	private static final int FACT = 3;
	private static final double FACT_INVERSE = 1.0 / FACT;
	private static final double	MAX_SCORE_TRUE = Math.pow(195075, 0.5);
	private static final double	MAX_SCORE_FALSE = 765;
	private ArrayList<Triangle> triangles;
	private BufferedImage image;
	private BufferedImage baseImg;
	private Dimension imageSize;
	private double totalPossibleScore;
	private boolean imageMade = false;
	
	public TrianglesFile(ArrayList<Triangle> trArray, Dimension dimension) {
		triangles = new ArrayList<Triangle>(G.getTriangles());
		for (int i = 0; i < trArray.size(); i++) {
			this.triangles.add(trArray.get(i));
		}
		imageSize = dimension.getSize();
		
		totalPossibleScore = getTotalPossibleScore(imageSize.width, imageSize.height);
		baseImg = null;
	}

	public TrianglesFile(TrianglesFile tf) {
		this(tf.getTriangles(), tf.imageSize, tf.baseImg);
	}
	
	public TrianglesFile(ArrayList<Triangle> trArray, Dimension dimension, BufferedImage baseChunk) {
		this(trArray, dimension);
		baseImg = baseChunk;
	}
	
	private static double getTotalPossibleScore(int width, int height) {
		return (G.getTrueColor() ? MAX_SCORE_TRUE : MAX_SCORE_FALSE) * width * height;
	}
	
	public void modifyRandom() {
		imageMade = false;
		if (triangles.size() <= 0) {
			return;
		}
		int i = getRandomTri();
		triangles.remove(i);
		triangles.add(new Triangle());
	}
	
	public void modifyShape10() {
		imageMade = false;
		if (triangles.size() <= 0) {
			return;
		}
		int i = getRandomTri();
		float xp[] = triangles.get(i).getXpoints();
		float yp[] = triangles.get(i).getYpoints();
		for (int j = 0; j < 3; j++) {
			xp[j] += G.getRandDouble() / 5 - 0.1;
			yp[j] += G.getRandDouble() / 5 - 0.1;
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
		int i = getRandomTri();
		float xp[] = triangles.get(i).getXpoints();
		float yp[] = triangles.get(i).getYpoints();
		for (int j = 0; j < 3; j++) {
			xp[j] = G.getRandFloat();
			yp[j] = G.getRandFloat();
		}
		triangles.set(i, new Triangle(xp, yp, triangles.get(i).getColor()));
	}
	
	public void modifyColor10() {
		imageMade = false;
		if (triangles.size() <= 0) {
			return;
		}
		int i = getRandomTri();
		int[] col = triangles.get(i).getColorArray();
		for (int j = 0; j < col.length; j++) {
			col[j] += G.getRandInt(51) - 25;
			col[j] = checkBounds(col[j], 255);
		}
		triangles.set(i, new Triangle(triangles.get(i).getXpoints(), triangles.get(i).getYpoints(), new Color(col[0], col[1], col[2])));
	}
	
	public void modifyRemove() {
		imageMade = false;
		if (triangles.size() > 2) {
			triangles.remove(G.getRandInt(triangles.size()));
		}
	}
	
	public double compare(BufferedImage img) {
		createImg();
		double score = compareTotal(img, image);
		score /= totalPossibleScore;
		return 1-score;
	}
	
	public static double compare(BufferedImage original, BufferedImage newImg) {
		
		BufferedImage compareChunk = new BufferedImage(newImg.getWidth(), newImg.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		compareChunk.createGraphics().drawImage(original, 0, 0, newImg.getWidth(), newImg.getHeight(), null);
		
		double score = 0;
		int[] newImgCol = new int[newImg.getWidth() * newImg.getHeight() * 3];
		int[] originCol = new int[newImgCol.length];
		newImg.getRaster().getPixels(0, 0, newImg.getWidth(), newImg.getHeight(), newImgCol);
		compareChunk.getRaster().getPixels(0, 0, newImg.getWidth(), newImg.getHeight(), originCol);
		for (int i = 0; i < newImgCol.length; i += 3) {
			score += toScore(i, originCol, newImgCol);
		}
		return 1 - (score / getTotalPossibleScore(compareChunk.getWidth(), compareChunk.getHeight()));
	}
	
	private double compareTotal(BufferedImage original, BufferedImage newImg) {
		double score = 0;
		
		if (baseImg == null) {
			int[] newImgCol = new int[newImg.getWidth() * newImg.getHeight() * 4];
			int[] originCol = new int[newImgCol.length];
			newImg.getRaster().getPixels(0, 0, newImg.getWidth(), newImg.getHeight(), newImgCol);
			original.getRaster().getPixels(0, 0, newImg.getWidth(), newImg.getHeight(), originCol);
			for (int i = 0; i < newImgCol.length; i += 4) {
				if (newImgCol[i + 3] != 255) {
					score += G.getTrueColor() ? MAX_SCORE_TRUE : MAX_SCORE_FALSE;
				}
				else {
					score += toScore(i, originCol, newImgCol);
				}
			}
			return score;
		}
		else {
			int[] newImgCol = new int[newImg.getWidth() * newImg.getHeight() * 3];
			int[] originCol = new int[newImgCol.length];
			newImg.getRaster().getPixels(0, 0, newImg.getWidth(), newImg.getHeight(), newImgCol);
			original.getRaster().getPixels(0, 0, newImg.getWidth(), newImg.getHeight(), originCol);
			for (int i = 0; i < newImgCol.length; i += 3) {
				score += toScore(i, originCol, newImgCol);
			}
			return score;
		}
	}
	
	private static double toScore(int i, int[] a, int[] b) {
		if (G.getTrueColor()) {
			// square root(r^2 + g^2 + b^2)
			return	Math.sqrt(Math.pow(a[i] - b[i], 2) + Math.pow(a[i + 1] - b[i + 1], 2) + Math.pow(a[i + 2] - b[i + 2], 2));
		}
		else {
			// |r| + |g| + |b|
			return	Math.abs(a[i] - b[i]) + Math.abs(a[i + 1] - b[i + 1]) + Math.abs(a[i + 2] - b[i + 2]);
		}
	}
	
	private boolean hasAlpha(BufferedImage b) {
		for (int i = 0; i < b.getWidth(); i++) {
			for (int j = 0; j < b.getHeight(); j++) {
				if (b.getRGB(i, j) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasAlpha() {
		if (baseImg == null) {
			createImg();
			return hasAlpha(image);
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
		ArrayList<Triangle> tr = new ArrayList<Triangle>(G.getTriangles());
		for (int i = 0; i < triangles.size(); i++) {
			tr.add(triangles.get(i).clone());
		}
		return triangles;
	}
	
	public BufferedImage getImage(Dimension newBlockPixelSize) {
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
		BufferedImage img = baseImg == null ?
				new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR):
				new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
	    Graphics2D g2d = img.createGraphics();
	    if (baseImg != null) {
	    	g2d.drawImage(baseImg, 0, 0, width, height, null);
	    }
		for (int i = 0; i < triangles.size(); i++) {
			g2d.setColor(triangles.get(i).getColor());
			g2d.fillPolygon(triangles.get(i).getPolygon(width, height));
		}
	    g2d.dispose();
	    return img;
	}
	
	private int getRandomTri() {
		return (int) Math.pow(G.getRandInt((int) Math.pow(triangles.size(), FACT)), FACT_INVERSE);
	}
	
	private float checkBounds(float n, int max) {
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
