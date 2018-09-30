package com.evastos.music.ui.util.extensions

private val metaCharacters = setOf(
    "\\",
    "^",
    "$",
    "{", "}",
    "[", "]",
    "(", ")",
    ".",
    "*",
    "+",
    "?",
    "|",
    "<",
    ">",
    "-",
    "&",
    "%"
)

fun String.formatQuery(): String {
    forEach {
        if (it.toString() in metaCharacters) {
            replace(it.toString(), "\\$it")
        }
    }
    return this
}