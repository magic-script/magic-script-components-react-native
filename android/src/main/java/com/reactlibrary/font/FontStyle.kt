package com.reactlibrary.font

enum class FontStyle {
    NORMAL,
    ITALIC;

    companion object {
        fun fromName(string: String): FontStyle? {
            return when (string) {
                "normal" -> NORMAL
                "italic" -> ITALIC
                else -> null
            }
        }
    }
}