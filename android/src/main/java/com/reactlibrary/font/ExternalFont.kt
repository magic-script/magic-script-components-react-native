package com.reactlibrary.font

abstract class ExternalFont(val baseName: String) {
    abstract fun getFileName(fontWeight: FontWeight, fontStyle: FontStyle): String
}