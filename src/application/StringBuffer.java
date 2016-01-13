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
	
	public static String combineStrings(ArrayList<StringBuffer> sb, int blockSize) {
		ArrayList<StringBuffer> newsb = new ArrayList<StringBuffer>();
		
		for (int i = 0; i < blockSize; i++) {
			for (int j = 0; j < blockSize; j++) {
				for (int k = 0; k < sb.size(); k++) {
					if (sb.get(k).getPoint().equals(new Point(i, j))) {
						newsb.add(sb.get(k));
						sb.remove(k);
						break;
					}
				}
			}
		}
		String s = "";
		for (StringBuffer b: newsb) {
			s = s.concat(b.getString());
		}
		
		return s;
	}
	
	
	
}
