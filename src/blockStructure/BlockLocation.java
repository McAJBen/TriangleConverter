package blockStructure;

import java.awt.Rectangle;

public class BlockLocation {

	Rectangle original;
	Rectangle first;
	Rectangle second;
	Rectangle third;
	
	BlockLocation(Rectangle o, Rectangle f, Rectangle s, Rectangle t) {
		original = o;
		first = f;
		second = s;
		third = t;
	}
}
