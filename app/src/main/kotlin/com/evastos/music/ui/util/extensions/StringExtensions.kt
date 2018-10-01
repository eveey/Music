package com.evastos.music.ui.util.extensions

private val META_CHARACTERS = setOf(
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
        if (it.toString() in META_CHARACTERS) {
            replace(it.toString(), "\\$it")
        }
    }
    return "$this*"
}
