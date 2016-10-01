package triangleStructure;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import global.G;

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
	
	public TrianglesFile(TrianglesFile tf) {
		this(tf.getTriangles(), tf.imageSize, tf.baseImg);
	}
	
	public TrianglesFile(ArrayList<Triangle> trArray, Dimension dimension) {
		for (int i = 0; i < trArray.size(); i++) {
			this.triangles.add(trArray.get(i));
		}
		imageSize = dimension.getSize();
		totalPossibleScore = MAX_SCORE * imageSize.getWidth() * imageSize.getHeight();
		baseImg = null;
	}
	
	public TrianglesFile(ArrayList<Triangle> trArray, Dimension dimension, BufferedImage baseChunk) {
		for (int i = 0; i < trArray.size(); i++) {
			this.triangles.add(trArray.get(i));
		}
		imageSize = dimension.getSize();
		totalPossibleScore = MAX_SCORE * imageSize.getWidth() * imageSize.getHeight();
		baseImg = baseChunk;
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
	
	private double compareTotal(BufferedImage original, BufferedImage newImg) {
		double score = 0;
		
		if (baseImg == null) {
			int[] newImgCol = new int[newImg.getWidth() * newImg.getHeight() * 4];
			int[] originCol = new int[newImgCol.length];
			newImg.getRaster().getPixels(0, 0, newImg.getWidth(), newImg.getHeight(), newImgCol);
			original.getRaster().getPixels(0, 0, newImg.getWidth(), newImg.getHeight(), originCol);
			
			for (int i = 0; i < newImgCol.length; i += 4) {
				if (newImgCol[i + 3] != 255 && baseImg == null) {
					score += MAX_SCORE;
				}
				else {
					score += Math.sqrt(
						Math.pow(originCol[i] - newImgCol[i], 2) +
						Math.pow(originCol[i + 1] - newImgCol[i + 1], 2) +
						Math.pow(originCol[i + 2] - newImgCol[i + 2], 2));
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
				score += Math.sqrt(
					Math.pow(originCol[i] - newImgCol[i], 2) +
					Math.pow(originCol[i + 1] - newImgCol[i + 1], 2) +
					Math.pow(originCol[i + 2] - newImgCol[i + 2], 2));
			}
			return score;
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
		ArrayList<Triangle> tr = new ArrayList<Triangle>();
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
	    
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
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
