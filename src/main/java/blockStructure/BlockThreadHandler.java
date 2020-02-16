package blockStructure;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import global.Global;
import triangleStructure.Block;
import triangleStructure.TrianglesFile;

public abstract class BlockThreadHandler {

    private static final Color PINK = new Color(255, 0, 255);
    private final BT[] BTArray;
    private final long startTime;
    private final boolean ignoreAlpha;
    private BufferedImage originalImg; // original image being compared to
    private BufferedImage newImg; // image being changed

    public abstract double getPercent();

    public String getPercentDone() {
        return String.format("%03.0f%%", getPercent() * 100);
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

    public void paint(Graphics2D g2d, Dimension size) {
        if (BTArray != null) {
            for (BT b: BTArray) {
                if (b != null) {
                    double xScale = size.getWidth() / newImg.getWidth();
                    double yScale = size.getHeight() / newImg.getHeight();

                    b.paint(g2d, xScale, yScale);
                }
            }
        }
    }

    public void start() {
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

    BlockThreadHandler(BufferedImage originalImg, BufferedImage newImg) {
        this.originalImg = originalImg;
        this.newImg = newImg;
        ignoreAlpha = !hasAlpha(newImg);
        BTArray = new BT[Global.threadCount];
        for (int i = 0; i < BTArray.length; i++) {
            BTArray[i] = new BT(i);
        }
        startTime = System.currentTimeMillis();
    }

    abstract boolean isDone();
    abstract BlockLocation getNewBlockLocation();
    abstract void removeBlockLocation(BlockLocation blockLocation);
    abstract void addCompleted();

    private static BufferedImage getSubImage(BufferedImage b, Rectangle r) {
        return b.getSubimage(r.x, r.y, r.width, r.height);
    }

    private boolean hasAlpha(BufferedImage b) {
        if (ignoreAlpha) {
            return false;
        }
        for (int i = 0; i < b.getWidth(); i++) {
            for (int j = 0; j < b.getHeight(); j++) {
                if (b.getRGB(i, j) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private long getSecondsFromStart() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    private void paintTo(BufferedImage b, Rectangle rect) {
        synchronized (newImg) {
            newImg.createGraphics().drawImage(b, rect.x, rect.y, rect.width, rect.height, null);
        }
    }

    private class BT extends Thread {

        private final String index;
        private BufferedImage currentTestImage;
        private BlockLocation blockLocation;
        private boolean active;
        private boolean ignoreAlphaChunk;

        public void run() {
            while (!isDone()) {
                blockLocation = getNewBlockLocation();
                currentTestImage = null;
                active = true;
                if (blockLocation == null) {
                    active = false;
                    break;
                }
                BufferedImage compareImage = getSubImage(originalImg, blockLocation.getOriginal());
                BufferedImage baseImg = getSubImage(newImg, blockLocation.getPost());
                ignoreAlphaChunk = !hasAlpha(baseImg);

                double bestScore = 0;
                Block bestBlock = null;
                for (int sample = 0; sample < Global.getMaxSamples(); sample++) {
                    Block block = new Block(compareImage, baseImg, blockLocation.getScaled().getSize());
                    compute(block);
                    if (bestScore < block.getMaxScore()) {
                        bestBlock = block;
                        bestScore = block.getMaxScore();
                    }
                }
                if (Global.postScale != 1.0) {
                    Block block = new Block(compareImage, baseImg, blockLocation.getPost().getSize(), bestBlock.getTriangles());
                    compute(block);
                    bestBlock = block;
                }
                // if (first drawing || better than last drawing)
                if (!ignoreAlpha || bestBlock.getMaxScore() >= TrianglesFile.Companion.compare(compareImage, baseImg)) {
                    paintTo(bestBlock.getImage(blockLocation.getPost().getSize()), blockLocation.getPost());
                    addCompleted();
                }
                active = false;
                removeBlockLocation(blockLocation);
            }
        }

        private BT(int index) {
            super("BT:" + index);
            this.index = "" + index;
            currentTestImage = null;
            active = false;
        }

        private void compute(Block block) {
            while (!block.isDone(ignoreAlphaChunk)) {
                block.move();
                if (Global.preDraw) {
                    currentTestImage = block.getImage();
                }
            }
        }

        private void paint(Graphics2D g, double xScale, double yScale) {
            if (active) {

                Rectangle rect = new Rectangle(
                        (int)(blockLocation.getPost().x * xScale),
                        (int)(blockLocation.getPost().y * yScale),
                        (int)(blockLocation.getPost().width * xScale),
                        (int)(blockLocation.getPost().height * yScale));

                if (currentTestImage != null) {
                    g.drawImage(currentTestImage,
                            rect.x, rect.y,
                            rect.width, rect.height, null);
                }
                if (Global.preDrawOutline) {
                    g.setColor(PINK);

                    g.drawString(index,
                            rect.x + 1,
                            rect.y + 11);

                    g.drawRect(
                            rect.x, rect.y,
                            rect.width - 1, rect.height - 1);
                }
            }
        }
    }
}