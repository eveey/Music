package com.evastos.music.ui.base

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.evastos.music.data.rx.applySchedulers
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {

    protected val disposables: CompositeDisposable = CompositeDisposable()

    val networkConnectivityLiveData = MutableLiveData<Boolean>()

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    fun onCreate(networkConnectivityObservable: Observable<Boolean>) {
        disposables.add(networkConnectivityObservable
                .distinctUntilChanged()
                .applySchedulers()
                .subscribe({ isConnected ->
                    networkConnectivityLiveData.postValue(isConnected)
                }, {
                    Timber.e(it)
                }))
    }
}
