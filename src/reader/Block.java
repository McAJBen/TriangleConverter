package reader;

import java.awt.Dimension;
import java.util.ArrayList;

import application.Triangle;
import application.TrianglesFile;

public class Block {
	private TrianglesFile bestTriFile;
	private double maxScore,
			score;
	
	
	public Block(Dimension size, ArrayList<Triangle> triangles) {
		bestTriFile = new TrianglesFile(triangles, size);
		maxScore = bestTriFile.compare();
	}
	public void move() {
		TrianglesFile modifyTriFile = new TrianglesFile(bestTriFile);
		
		for (int i = 0; i < 10; i++) {
			modifyTriFile.modifyShape();
		}
		score = modifyTriFile.compare();
		
		if (score >= maxScore) {
			if (score > maxScore) {
				maxScore = score;
				System.out.println(maxScore);
			}
			bestTriFile = modifyTriFile;
		}
	}
	
	public TrianglesFile getTriangleFile() {
		return bestTriFile;
	}
	
	public boolean isDone() {
		return !bestTriFile.hasAlpha() || maxScore > 0.99;
	}
	
	public String getText(double x, double y, double size) {
		return bestTriFile.getText(x, y, size);
	}
	public double getMaxScore() {
		return maxScore;
	}
}