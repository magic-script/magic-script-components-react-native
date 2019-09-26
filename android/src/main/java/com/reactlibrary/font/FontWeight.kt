package com.reactlibrary.font

enum class FontWeight {
    EXTRA_LIGHT,
    LIGHT,
    REGULAR,
    MEDIUM,
    BOLD,
    EXTRA_BOLD;

    companion object {
        fun fromName(string: String): FontWeight? {
            return when (string) {
                "extra-light" -> EXTRA_LIGHT
                "light" -> LIGHT
                "regular" -> REGULAR
                "medium" -> MEDIUM
                "bold" -> BOLD
                "extra-bold" -> EXTRA_BOLD
                else -> null
            }
        }
    }

}