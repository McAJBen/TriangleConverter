package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
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
	
	private synchronized void paintTo(BufferedImage b, Rectangle rect) {
		newImg.createGraphics().drawImage(b, rect.x, rect.y, rect.width, rect.height, null);
	}
	
	private static BufferedImage getSubImage(BufferedImage b, Rectangle r) {
		return b.getSubimage(r.x, r.y, r.width, r.height);
	}
	
	private class BT extends Thread {

		private BufferedImage currentTestImage;
		private BlockLocation blockLocation;
		
		public void run() {
			while (!isDone()) {
				
				Block bestBlock = null;
				blockLocation = getNewBlockLocation();
				BufferedImage subImage = getSubImage(originalImg, blockLocation.original);
				if (blockLocation == null) {
					break;
				}
				{
					double bestScore = 0;
					for (int sample = 0; sample < G.samples; sample++) {
						Block block = new Block(subImage, blockLocation.first.getSize());
						compute(block);
						if (bestScore < block.getMaxScore()) {
							bestBlock = block;
							bestScore = block.getMaxScore();
						}
					}
				}
				if (G.postProcessing) {
					Block block = new Block(subImage, blockLocation.second.getSize(), bestBlock.getTriangles());
					compute(block);
					bestBlock = block;
				}
				paintTo(bestBlock.getImage(), blockLocation.second);
				removeBlockLocation(blockLocation);
			}
		}
		
		private void compute(Block block) {
			while (!block.isDone()) {
				block.move();
				if (G.preDraw) {
					currentTestImage = block.getImage();
				}
			}
			currentTestImage = block.getImage();
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