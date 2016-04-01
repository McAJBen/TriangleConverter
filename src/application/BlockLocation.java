package application;

import java.awt.Dimension;
import java.awt.Point;

public class BlockLocation {

	Dimension blockSize;
	Point blockPosition;
	Dimension scaledBlockSize;
	Point scaledBlockPosition;
	
	public BlockLocation(Dimension blockSize,
						Point blockPosition,
						Dimension scaledBlockSize,
						Point scaledBlockPosition)
	{
		this.blockSize = blockSize;
		this.blockPosition = blockPosition;
		this.scaledBlockSize = scaledBlockSize;
		this.scaledBlockPosition = scaledBlockPosition;
	}
}
