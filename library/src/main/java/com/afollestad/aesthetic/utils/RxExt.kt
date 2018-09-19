/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.Consumer

typealias KotlinSubscriber<T> = (T) -> Unit

internal fun <T> Observable<T>.distinctToMainThread(): Observable<T> {
  return observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged()
}

internal operator fun CompositeDisposable?.plusAssign(disposable: Disposable) {
  this?.add(disposable)
}

internal fun onErrorLogAndRethrow(): Consumer<Throwable> {
  return Consumer { throwable ->
    throwable.printStackTrace()
    throw Exceptions.propagate(throwable)
  }
}

internal fun <T> Observable<T>.one(): Observable<T> {
  return take(1)
}

internal fun <T> Observable<T>.onMainThread(): Observable<T> {
  return observeOn(AndroidSchedulers.mainThread())
}

internal inline fun <T> Observable<T>.subscribeWith(
  crossinline subscriber: KotlinSubscriber<T>
): Disposable {
  return this.subscribe(
      Consumer { subscriber(it) },
      onErrorLogAndRethrow()
  )
}
