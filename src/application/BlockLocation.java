package application;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

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

	public Rectangle getRectangle() {
		return new Rectangle(blockPosition.x, blockPosition.y, blockSize.width, blockSize.height);
	}
}
