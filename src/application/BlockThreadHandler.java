package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public abstract class BlockThreadHandler {
	
	private BufferedImage
			originalImg, // original image being compared to
			newImg; // image being changed
	
	private BT[] BTArray;
	
	public BlockThreadHandler(BufferedImage originalImg, BufferedImage newImg) {
		
		this.originalImg = originalImg;
		this.newImg = newImg;
		BTArray = new BT[G.threadCount];
	}
	
	public void startConversion() {
		
        for (int i = 0; i < BTArray.length; i++) {
           	BTArray[i] = new BT("" + i);
        }
        
        for (BT b: BTArray) {
        	b.start();
        }
        for (BT b: BTArray) {
        	try {
				b.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
	}
	
	private class BT extends Thread {

		private BufferedImage currentTestImage;
		private BlockLocation blockLocation;
		
		public BT(String string) {
			super(string);
			currentTestImage = null;
		}
		@Override
		public void run() {
			while (!isDone()) {
				double bestScore = Double.MIN_VALUE;
				Block bestBlock = null;
				
				blockLocation = getNewBlockLocation();
				
				if (blockLocation == null) {
					break;
				}
				
				for (int sample = 0; sample < G.samples; sample++) {
					Block block = new Block(blockLocation.blockPosition, blockLocation.blockSize, originalImg);
					while (!block.isDone()) {
						block.move();
						if (G.preDraw) {
							currentTestImage = block.getImage();
						}
					}
					currentTestImage = block.getImage();
					if (bestScore < block.getMaxScore()) {
						bestBlock = block;
						bestScore = block.getMaxScore();
					}
				}
				if (G.postProcessing) {
					Block block = new Block(blockLocation.scaledBlockPosition, blockLocation.scaledBlockSize, originalImg, newImg, bestBlock.getTriangles());
					while (!block.isDone()) {
						block.move();
						if (G.preDraw) {
							currentTestImage = block.getImage();
						}
					}
					currentTestImage = block.getImage();
					bestBlock = block;
				}
				paintTo(bestBlock.getImage(blockLocation.scaledBlockSize), blockLocation.scaledBlockPosition, blockLocation.scaledBlockSize);
				removeBlockLocation(blockLocation);
			}
		}
		
		public void paint(Graphics2D g, int origW, int origH, Dimension windowSize) {
			if (currentTestImage != null && blockLocation != null) {
				
				Point blPos = new Point(
						blockLocation.scaledBlockPosition.x * windowSize.width / origW,
						blockLocation.scaledBlockPosition.y * windowSize.height / origH);
				Dimension blSize = new Dimension(
						blockLocation.scaledBlockSize.width * windowSize.width / origW,
						blockLocation.scaledBlockSize.height * windowSize.height / origH);
				
				g.drawImage(currentTestImage,
						blPos.x, blPos.y,
						blSize.width, blSize.height, null);
				
				g.setColor(Color.YELLOW);
				
				g.drawString(getName() + "",
						blPos.x + 1, 
						blPos.y + 11);
				g.drawRect(
						blPos.x, blPos.y,
						blSize.width, blSize.height);
			}
		}
	}

	public abstract boolean isDone();
	public abstract BlockLocation getNewBlockLocation();
	public abstract void removeBlockLocation(BlockLocation blockLocation);

	
	public void paint(Graphics2D g2d, Dimension size) {
		if (BTArray != null) {
			for (BT b: BTArray) {
				if (b != null) {
					b.paint(g2d, newImg.getWidth(), newImg.getHeight(), size);
				}
			}
		}
	}
	
	private synchronized void paintTo(BufferedImage b, Point p, Dimension size) {
		newImg.createGraphics().drawImage(b, p.x, p.y, size.width, size.height, null);
	}
}
