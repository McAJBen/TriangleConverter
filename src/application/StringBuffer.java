package application;

import java.awt.Point;
import java.util.ArrayList;

public class StringBuffer {
	private String s;
	private Point p;
	
	public StringBuffer(String s, Point p) {
		this.s = s;
		this.p = p;
	}
	
	public Point getPoint() {
		return p;
	}
	
	public String getString() {
		return s;
	}
	
	@Override
	public String toString() {
		return p.x + ", " + p.y + ", " + s;
	}
	
	public static ArrayList<String> combineStrings(ArrayList<StringBuffer> sb, int blockSize) {
		ArrayList<String> newsb = new ArrayList<String>();
		
		for (int j = 0; j < blockSize; j++) {
			for (int i = 0; i < blockSize; i++) {
				for (int k = 0; k < sb.size(); k++) {
					if (sb.get(k).getPoint().equals(new Point(i, j))) {
						newsb.add(sb.get(k).getString());
						sb.remove(k);
						break;
					}
				}
			}
		}
		return newsb;
	}
	
	
	
}
