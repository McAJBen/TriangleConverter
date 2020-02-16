
fun Float.boundTo(min: Float, max: Float): Float {
    return when {
        this > max -> max
        this < min -> min
        else -> this
    }
}

fun Int.boundTo(min: Int, max: Int): Int {
    return when {
        this > max -> max
        this < min -> min
        else -> this
    }
}