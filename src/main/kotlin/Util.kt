import global.Global
import java.io.File

fun Long.toTimeString(): String {
    return String.format(
        "%01d:%02d:%02d",
        this / 3600,
        this / 60 % 60,
        this % 60
    )
}

fun File.isImageFile(): Boolean {
    return isFile && (
            extension.equals(Global.PNG, ignoreCase = true)
                    || extension.equals(Global.JPG, ignoreCase = true)
                    || extension.equals(Global.BMP, ignoreCase = true)
            )
}