package com.evastos.music.data.rx

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

private const val DELAY_ERROR_MILLIS = 400L

fun <T> Single<T>.applySchedulers(): Single<T> {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.applySchedulers(): Observable<T> {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T> Flowable<T>.applySchedulers(): Flowable<T> {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

//fun <T, E : Throwable> Single<T>.mapException(exceptionMapper: ExceptionMapper<E>): Single<T> {
//    return retryWhen {
//        return@retryWhen it.flatMap { throwable ->
//            Flowable.error<Throwable> {
//                exceptionMapper.map(throwable)
//            }
//        }
//    }
//}

fun <T> Single<T>.delayError(): Single<T> =
        this.retryWhen {
            it.delay(DELAY_ERROR_MILLIS, TimeUnit.MILLISECONDS, Schedulers.computation())
                    .flatMapSingle { error -> Single.error<Unit>(error) }
        }
