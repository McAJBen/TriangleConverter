package blockStructure;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import global.Global;

public class btRandom extends BlockThreadHandler {

    private int randomPlacementsLeft;
    private final Dimension defaultSize;
    private final Dimension imageSize;
    private ArrayList<Rectangle> alreadyTakenBlocks;

    public btRandom(BufferedImage originalImg, BufferedImage newImg) {
        super(originalImg, newImg);
        randomPlacementsLeft = Global.getRandomBlocks();
        imageSize = new Dimension(originalImg.getWidth(), originalImg.getHeight());
        defaultSize = new Dimension(imageSize.width / Global.blocksWide, imageSize.height / Global.blocksWide);
        alreadyTakenBlocks = new ArrayList<>(Global.threadCount);
    }

    public boolean isDone() {
        synchronized (alreadyTakenBlocks) {
            return randomPlacementsLeft <= 0;
        }
    }

    public double getPercent() {
        return 1.0 - ((double)randomPlacementsLeft / Global.getRandomBlocks());
    }

    BlockLocation getNewBlockLocation() {
        synchronized (alreadyTakenBlocks) {
            if (randomPlacementsLeft <= alreadyTakenBlocks.size()) {
                return null;
            }
            Rectangle orig,
                    scaled,
                    post;
            BlockLocation bl;
            do {
                orig = getValidRect();
                scaled = toRectangle(orig, Global.scale);
                post = toRectangle(orig, Global.scale * Global.postScale);

                bl = new BlockLocation(orig, scaled, post);

            } while (
                    scaled.width <= 0 || scaled.height <= 0 ||
                            post.width <= 0 || post.height <= 0);

            alreadyTakenBlocks.add(orig);
            return bl;
        }
    }

    private Rectangle getValidRect() {
        while (true) {
            Dimension size = getBlock();
            for (int i = 0; i < 100; i++) {
                Rectangle orig = getRandomRect(size);
                if (orig.width > 0 && orig.height > 0) {
                    if (Global.allowCollision || !collides(orig)) {
                        return orig;
                    }
                }
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void removeBlockLocation(BlockLocation blockLocation) {
        synchronized(alreadyTakenBlocks) {
            alreadyTakenBlocks.remove(blockLocation.getOriginal());
        }
    }

    private static Rectangle toRectangle(Rectangle r, double scale) {
        return new Rectangle(
                (int)(r.x * scale),
                (int)(r.y * scale),
                (int)(r.width * scale),
                (int)(r.height * scale));
    }

    private Rectangle getRandomRect(Dimension size) {
        return new Rectangle(
                Global.getRandInt(imageSize.width - size.width),
                Global.getRandInt(imageSize.height - size.height),
                size.width,
                size.height);
    }

    private Dimension getBlock() {
        Dimension r;
        do {
            r = defaultSize.getSize();
            r.width *= 0.9 + (Global.getRandDouble() * 0.2);
            r.height *= 0.9 + (Global.getRandDouble() * 0.2);
        } while (r.width <= 0 || r.width >= defaultSize.width || r.height <= 0 || r.height >= defaultSize.height);

        return r;
    }

    private boolean collides(Rectangle rect) {
        for (Rectangle bl: alreadyTakenBlocks) {
            if (bl.intersects(rect)) {
                return true;
            }
        }
        return false;
    }

    @Override
    void addCompleted() {
        randomPlacementsLeft--;
        if (randomPlacementsLeft < 0) {
            System.out.println(randomPlacementsLeft);
        }
    }
}