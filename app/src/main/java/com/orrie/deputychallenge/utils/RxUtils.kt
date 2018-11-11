package com.orrie.deputychallenge.utils

import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Utilities for working with RxJava
 */

fun Disposable.autoDispose(disposeSignal: Observable<Unit>) {
    disposeSignal.take(1).subscribe { dispose() }
}

fun View.throttleClicks(): Observable<Unit> {
    return clicks().throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.subscribeAndObserveOnMainThread(onNext: (t: T) -> Unit): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
            .subscribe(onNext)
}

fun <T> SingleEmitter<T>.onSafeError(throwable: Throwable) {
    if (!isDisposed) onError(throwable)
}