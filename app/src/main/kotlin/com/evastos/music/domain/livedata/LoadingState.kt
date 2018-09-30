package com.evastos.music.domain.livedata

/**
 * Indicates the loading state of the UI.
 */
sealed class LoadingState {
    class Loading : LoadingState()
    class Success : LoadingState()
    class Error(val errorMessage: String?) : LoadingState()
}
