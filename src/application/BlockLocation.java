package application;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class BlockLocation {

	Rectangle original;
	Rectangle scaled;
	
	/*Dimension blockSize;
	Point blockPosition;
	Dimension scaledBlockSize;
	Point scaledBlockPosition;*/
	
	BlockLocation(Dimension blockSize,
						Point blockPosition,
						Dimension scaledBlockSize,
						Point scaledBlockPosition)
	{
		original = new Rectangle(blockPosition, blockSize);
		scaled = new Rectangle(scaledBlockPosition, scaledBlockSize);
	}
}
