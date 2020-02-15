package blockStructure;

import java.awt.Rectangle;

public class BlockLocation {

    Rectangle original;
    Rectangle scaled;
    Rectangle post;

    BlockLocation(Rectangle r1, Rectangle r2, Rectangle r3) {
        original = r1;
        scaled = r2;
        post = r3;
    }
}
