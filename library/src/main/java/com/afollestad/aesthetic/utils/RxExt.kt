/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function3

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

internal inline fun <T> Observable<T>.subscribeTo(
  crossinline subscriber: KotlinSubscriber<T>
): Disposable {
  return this.subscribe(
      Consumer { subscriber(it) },
      onErrorLogAndRethrow()
  )
}

internal fun Observable<Int>.subscribeBackgroundColor(view: View): Disposable {
  return subscribeTo {
    when (view) {
      is CardView -> view.setCardBackgroundColor(it)
      else -> view.setBackgroundColor(it)
    }
  }
}

internal fun Observable<Int>.subscribeTextColor(view: TextView): Disposable {
  return subscribeTo(view::setTextColor)
}

internal fun Observable<Int>.subscribeHintTextColor(view: TextView): Disposable {
  return subscribeTo(view::setHintTextColor)
}

internal fun Observable<Int>.subscribeImageViewTint(view: ImageView): Disposable {
  return subscribeTo(view::setColorFilter)
}

internal inline fun <T1, T2, R> allOf(
  source1: Observable<T1>,
  source2: Observable<T2>,
  crossinline combineFunction: (T1, T2) -> R
) = combineLatest(source1, source2, BiFunction<T1, T2, R> { t1, t2 -> combineFunction(t1, t2) })!!

internal fun <T1, T2> allOf(
  source1: Observable<T1>,
  source2: Observable<T2>
) = combineLatest(source1, source2, BiFunction<T1, T2, Pair<T1, T2>> { t1, t2 -> t1 to t2 })!!

inline fun <T1, T2, T3, R> allOf(
  source1: Observable<T1>,
  source2: Observable<T2>,
  source3: Observable<T3>,
  crossinline combineFunction: (T1, T2, T3) -> R
) = combineLatest(source1, source2, source3,
    Function3 { t1: T1, t2: T2, t3: T3 -> combineFunction(t1, t2, t3) })!!
