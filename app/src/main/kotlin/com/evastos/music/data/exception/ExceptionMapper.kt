package com.evastos.music.data.exception

interface ExceptionMapper<out Exception : Throwable> {
    fun map(throwable: Throwable): Exception
}
