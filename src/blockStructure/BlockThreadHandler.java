package blockStructure;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import application.Block;
import global.G;
import triangleStructure.TrianglesFile;

public abstract class BlockThreadHandler {
	
	private static final Color PINK = new Color(255, 0, 255);
	
	private BufferedImage originalImg; // original image being compared to
	private BufferedImage newImg; // image being changed
	private BT[] BTArray;
	protected boolean allowAlpha;
	private long startTime = -1;
	
	BlockThreadHandler(BufferedImage originalImg, BufferedImage newImg) {
		this.originalImg = originalImg;
		this.newImg = newImg;
		BTArray = new BT[G.getThreadCount()];
	}

	abstract boolean isDone();
	abstract BlockLocation getNewBlockLocation();
	abstract void removeBlockLocation(BlockLocation blockLocation);
	public abstract double getPercent();
	
	public String getPercentDone() {
		return String.format("%s %02.0f%%", getClass().getSimpleName(), getPercent() * 100);
	}
	
	private long getSecondsFromStart() {
		return (System.currentTimeMillis() - startTime) / 1000;
	}
	
	public String getRunTime() {
		long time = getSecondsFromStart();
		return String.format("%01d:%02d:%02d", time / 3600, (time / 60) % 60, time % 60);
	}
	
	public String getEstimatedEndTime() {
		long runtime = getSecondsFromStart();
		long endTime = (long) (runtime / getPercent()) - runtime;
		return String.format("%01d:%02d:%02d", endTime / 3600, (endTime / 60) % 60, endTime % 60);
	}

	public void startConversion() {
        for (int i = 0; i < BTArray.length; i++) {
           	BTArray[i] = new BT("" + i);
        }
        startTime = System.currentTimeMillis();
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
	
	private void paintTo(BufferedImage b, Rectangle rect) {
		synchronized (newImg) {
			newImg.createGraphics().drawImage(b, rect.x, rect.y, rect.width, rect.height, null);
		}
	}

	private static BufferedImage getSubImage(BufferedImage b, Rectangle r) {
		return b.getSubimage(r.x, r.y, r.width, r.height);
	}
	
	private class BT extends Thread {

		private BufferedImage currentTestImage;
		private BlockLocation blockLocation;
		private boolean active;
		
		public void run() {
			while (!isDone()) {
				
				Block bestBlock = null;
				blockLocation = getNewBlockLocation();
				active = true;
				BufferedImage compareImage = getSubImage(originalImg, blockLocation.original);
				BufferedImage baseImg = getSubImage(newImg, blockLocation.third);
				if (blockLocation == null) {
					active = false;
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
				if (!allowAlpha || bestBlock.getMaxScore() >= TrianglesFile.compare(compareImage, baseImg)) {
					paintTo(bestBlock.getImage(blockLocation.third.getSize()), blockLocation.third);
				}
				active = false;
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
			active = false;
		}
		
		private void paint(Graphics2D g, int origW, int origH, Dimension windowSize) {
			if (active && currentTestImage != null) {
				
				Rectangle rect = new Rectangle(
						blockLocation.third.x * windowSize.width / origW,
						blockLocation.third.y * windowSize.height / origH,
						blockLocation.third.width * windowSize.width / origW,
						blockLocation.third.height * windowSize.height / origH);
				
				g.drawImage(currentTestImage,
						rect.x + 1, rect.y + 1,
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
}