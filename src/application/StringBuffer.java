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
	
	public static String combineStrings(ArrayList<StringBuffer> sb, int blockSize) {
		ArrayList<StringBuffer> newsb = new ArrayList<StringBuffer>();
		
		for (int i = 0; i < blockSize; i++) {
			for (int j = 0; j < blockSize; j++) {
				for (int k = 0; k < sb.size(); k++) {
					
					// TODO case where a stringbuffer in the array has been set to null instead of aguments
					
					if (sb.get(k).getPoint().equals(new Point(i, j))) {
						newsb.add(sb.get(k));
						sb.remove(k);
						break;
					}
				}
			}
		}
		String s = "\n";
		for (StringBuffer b: newsb) {
			s = s.concat(b.getString());
		}
		
		return s;
	}
	
	
	
}
