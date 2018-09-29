package com.evastos.music.ui.base

import android.arch.lifecycle.ViewModel
import com.evastos.music.data.rx.applySchedulers
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {

    protected val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    open fun onCreate(
        networkConnectivityObservable: Observable<Boolean>?,
        onConnectivityChange: (Boolean) -> Unit
    ) {
        networkConnectivityObservable?.let {
            disposables.add(it
                    .distinctUntilChanged()
                    .applySchedulers()
                    .subscribe({ isConnected ->
                        onConnectivityChange(isConnected)
                    }, { throwable ->
                        Timber.e(throwable)
                    })
            )
        }
    }
}
