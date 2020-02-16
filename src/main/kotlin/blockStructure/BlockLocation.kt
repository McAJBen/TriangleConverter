package blockStructure

import java.awt.Rectangle

data class BlockLocation(
    var original: Rectangle,
    var scaled: Rectangle,
    var post: Rectangle
)