package blockStructure;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import application.Block;
import global.G;

public abstract class BlockThreadHandler {
	
	private static final Color PINK = new Color(255, 0, 255);
	
	private BufferedImage
			originalImg, // original image being compared to
			newImg; // image being changed
	
	private BT[] BTArray;
	
	protected boolean allowAlpha;
	
	BlockThreadHandler(BufferedImage originalImg, BufferedImage newImg) {
		this.originalImg = originalImg;
		this.newImg = newImg;
		BTArray = new BT[G.getThreadCount()];
	}

	abstract boolean isDone();
	abstract BlockLocation getNewBlockLocation();
	abstract void removeBlockLocation(BlockLocation blockLocation);
	abstract boolean usePreviousImage();
	public abstract double getPercent();
	
	public String getPercentDone() {
		return String.format("%s %2.0f %%", getClass().getSimpleName(), getPercent() * 100);
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
	
	public void paint(Graphics2D g2d, Dimension size) {
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
				BufferedImage compareImage = getSubImage(originalImg, blockLocation.original);
				BufferedImage baseImg = getSubImage(newImg, blockLocation.third);
				if (blockLocation == null) {
					break;
				}
				{
					double bestScore = 0;
					for (int sample = 0; sample < G.getMaxSamples(); sample++) {
						Block block = allowAlpha ? 
								new Block(compareImage, baseImg, blockLocation.first.getSize()):
								new Block(compareImage, blockLocation.first.getSize());
						
						compute(block);
						if (bestScore < block.getMaxScore()) {
							bestBlock = block;
							bestScore = block.getMaxScore();
						}
					}
				}
				if (G.getPostProcessing()) {
					Block block = allowAlpha ? 
							new Block(compareImage, baseImg, blockLocation.second.getSize(), bestBlock.getTriangles()):
							new Block(compareImage, blockLocation.second.getSize(), bestBlock.getTriangles());
					compute(block);
					bestBlock = block;
				}
				
				
				paintTo(bestBlock.getImage(blockLocation.third.getSize()), blockLocation.third);
				removeBlockLocation(blockLocation);
			}
		}
		
		private void compute(Block block) {
			while (!block.isDone()) {
				block.move();
				if (G.getPreDraw()) {
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
						blockLocation.third.x * windowSize.width / origW,
						blockLocation.third.y * windowSize.height / origH,
						blockLocation.third.width * windowSize.width / origW,
						blockLocation.third.height * windowSize.height / origH);
				
				g.drawImage(currentTestImage,
						rect.x, rect.y,
						rect.width, rect.height, null);
				
				g.setColor(PINK);
				
				g.drawString(getName(),
						rect.x + 1, 
						rect.y + 11);
				
				g.drawRect(
						rect.x, rect.y,
						rect.width, rect.height);
			}
		}
	}

	public int getPercent(int width) {
		return (int) (width * getPercent());
	}
}