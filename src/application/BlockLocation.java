package application;

import java.awt.Rectangle;

public class BlockLocation {

	Rectangle original;
	Rectangle first;
	Rectangle second;
	
	BlockLocation(Rectangle o, Rectangle f, Rectangle s) {
		original = o;
		first = f;
		second = s;
	}
}
