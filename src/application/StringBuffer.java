package application;

import java.awt.Point;
import java.util.ArrayList;

public class StringBuffer {
	private String string;
	private Point point;
	
	public StringBuffer(String s, Point p) {
		this.string = s;
		this.point = p;
	}
	public Point getPoint() {
		return point;
	}
	public String getString() {
		return string;
	}
	@Override
	public String toString() {
		return point.x + ", " + point.y + ", " + string;
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
