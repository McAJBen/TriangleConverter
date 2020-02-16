package blockStructure;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import global.Global;

public class btGrid extends BlockThreadHandler {

    private final D2D originalSize;
    private final D2D scaledSize;
    private final D2D postSize;

    private Point nextPos;

    public btGrid(BufferedImage originalImg, BufferedImage newImg) {
        super(originalImg, newImg);

        originalSize = new D2D(
                (double)originalImg.getWidth() / Global.blocksWide,
                (double)originalImg.getHeight() / Global.blocksWide);

        scaledSize = new D2D(
                originalSize.getWidth() * Global.scale,
                originalSize.getHeight() * Global.scale);

        postSize = new D2D(
                scaledSize.getWidth() * Global.postScale,
                scaledSize.getHeight() * Global.postScale);

        nextPos = new Point(0, 0);
    }

    public double getPercent() {
        return (nextPos.getX() / Global.blocksWide + nextPos.y) / Global.blocksWide;
    }

    boolean isDone() {
        synchronized (nextPos) {
            return nextPos.y >= Global.blocksWide;
        }
    }

    BlockLocation getNewBlockLocation() {

        Rectangle orig;
        Rectangle scaled;
        Rectangle post;

        do {
            Point position = getNextPos();
            if (position == null || position.y >= Global.blocksWide) {
                return null;
            }
            orig = toRectangle(position, originalSize);
            scaled = toRectangle(position, scaledSize);
            post = toRectangle(position, postSize);

        } while (orig.width <= 0 || orig.height <= 0 ||
                scaled.width <= 0 || scaled.height <= 0 ||
                post.width <= 0 || post.height <= 0);
        return new BlockLocation(orig, scaled, post);
    }

    void removeBlockLocation(BlockLocation blockLocation) {}

    private static Rectangle toRectangle(Point pos, D2D size) {
        Rectangle r = new Rectangle();

        r.setLocation(
                (int)(pos.x * size.getWidth()),
                (int)(pos.y * size.getHeight()));
        r.setSize(
                (int)((pos.x + 1) * size.getWidth()) - r.x,
                (int)((pos.y + 1) * size.getHeight()) - r.y);

        return r;
    }

    private Point getNextPos() {
        synchronized (nextPos) {
            Point p = (Point) nextPos.clone();
            nextPos.x++;
            if (nextPos.x >= Global.blocksWide) {
                nextPos.y++;
                nextPos.x = 0;
            }
            return p;
        }
    }

    private class D2D {
        private final double width;
        private final double height;

        public D2D(double width, double height) {
            this.width = width;
            this.height = height;
        }

        public double getHeight() {
            return height;
        }

        public double getWidth() {
            return width;
        }
    }

    @Override
    void addCompleted() {

    }
}