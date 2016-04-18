package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class BlockThreadHandler {
	
	private static final Color PINK = new Color(255, 0, 255);
	
	private BufferedImage
			originalImg, // original image being compared to
			newImg; // image being changed
	
	private BT[] BTArray;
	
	BlockThreadHandler(BufferedImage originalImg, BufferedImage newImg) {
		this.originalImg = originalImg;
		this.newImg = newImg;
		BTArray = new BT[G.threadCount];
	}

	abstract boolean isDone();
	abstract BlockLocation getNewBlockLocation();
	abstract void removeBlockLocation(BlockLocation blockLocation);
	abstract boolean usePreviousImage();

	void startConversion() {
		
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
	
	void paint(Graphics2D g2d, Dimension size) {
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
	
	private class BT extends Thread {

		private BufferedImage currentTestImage;
		private BlockLocation blockLocation;
		
		public void run() {
			while (!isDone()) {
				double bestScore = 0;
				Block bestBlock = null;
				
				blockLocation = getNewBlockLocation();
				BufferedImage subImage = newImg.getSubimage(blockLocation.second.x, blockLocation.second.y, blockLocation.second.width, blockLocation.second.height);
				double prevScore = -1;
				if (usePreviousImage()){
					BufferedImage baseSub = new BufferedImage(blockLocation.second.width, blockLocation.second.height, BufferedImage.TYPE_INT_RGB);
					baseSub.createGraphics().drawImage(originalImg.getSubimage(blockLocation.original.x, blockLocation.original.y, blockLocation.original.width, blockLocation.original.height),
							0, 0, blockLocation.second.width, blockLocation.second.height, null);
					prevScore = TrianglesFile.compare(baseSub, subImage);
				}
				
				if (blockLocation == null) {
					break;
				}
				
				for (int sample = 0; sample < G.samples; sample++) {
					Block block = new Block(blockLocation.original, blockLocation.first, originalImg, subImage);
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
					Block block = new Block(blockLocation.original, blockLocation.second, originalImg, subImage, bestBlock.getTriangles());
					while (!block.isDone()) {
						block.move();
						if (G.preDraw) {
							currentTestImage = block.getImage();
						}
					}
					currentTestImage = block.getImage();
					bestBlock = block;
					bestScore = bestBlock.getMaxScore();
				}
				
				if (bestScore > prevScore) {
					paintTo(bestBlock.getImage(blockLocation.second.getSize()), blockLocation.second.getLocation(), blockLocation.second.getSize());
				}
				else {
					System.out.println();
				}
				removeBlockLocation(blockLocation);
			}
		}
		
		private BT(String string) {
			super(string);
			currentTestImage = null;
		}
		
		private void paint(Graphics2D g, int origW, int origH, Dimension windowSize) {
			if (currentTestImage != null && blockLocation != null) {
				
				Rectangle rect = new Rectangle(
						blockLocation.second.x * windowSize.width / origW,
						blockLocation.second.y * windowSize.height / origH,
						blockLocation.second.width * windowSize.width / origW,
						blockLocation.second.height * windowSize.height / origH);
				
				g.drawImage(currentTestImage,
						rect.x, rect.y,
						rect.width, rect.height, null);
				
				g.setColor(PINK);
				
				g.drawString(getName() + "",
						rect.x + 1, 
						rect.y + 11);
				
				g.drawRect(
						rect.x, rect.y,
						rect.width, rect.height);
			}
		}
	}
}