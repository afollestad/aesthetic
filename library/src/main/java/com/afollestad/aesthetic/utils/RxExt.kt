/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afollestad.aesthetic.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.cardview.widget.CardView
import com.afollestad.aesthetic.blowUp
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables.empty
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function3

typealias KotlinSubscriber<T> = (T) -> Unit

internal typealias RxMapper<T, R> = (T) -> ObservableSource<out R>

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

internal fun <T> Observable<T>.toMainThread(): Observable<T> {
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

internal inline fun <T1, T2, R> combine(
  source1: Observable<T1>,
  source2: Observable<T2>,
  crossinline combineFunction: (T1, T2) -> R
) = combineLatest(source1, source2, BiFunction<T1, T2, R> { t1, t2 -> combineFunction(t1, t2) })!!

internal fun <T1, T2> combine(
  source1: Observable<T1>,
  source2: Observable<T2>
) = combineLatest(source1, source2, BiFunction<T1, T2, Pair<T1, T2>> { t1, t2 -> t1 to t2 })!!

inline fun <T1, T2, T3, R> combine(
  source1: Observable<T1>,
  source2: Observable<T2>,
  source3: Observable<T3>,
  crossinline combineFunction: (T1, T2, T3) -> R
) = combineLatest(source1, source2, source3,
    Function3 { t1: T1, t2: T2, t3: T3 -> combineFunction(t1, t2, t3) })!!

fun Observable<Int>.subscribeBackgroundColor(view: View): Disposable {
  return subscribeTo {
    when (view) {
      is CardView -> view.setCardBackgroundColor(it)
      else -> view.setBackgroundColor(it)
    }
  }
}

fun Observable<Int>.subscribeTextColor(view: View): Disposable {
  if (view !is TextView) return empty()
  return subscribeTo(view::setTextColor)
}

fun Observable<Int>.subscribeHintTextColor(view: View): Disposable {
  if (view !is TextView) return empty()
  return subscribeTo(view::setHintTextColor)
}

fun Observable<Int>.subscribeImageViewTint(view: View): Disposable {
  if (view !is ImageView) return empty()
  return subscribeTo(view::setColorFilter)
}

/**
 * We use this to we don't get lint warnings when using flatMap. Since Observable.flatMap is
 * a Java function and does not have nullability annotations, it can "possibly be null"
 * (it really cannot be).
 */
@CheckResult
internal fun <T, R> Observable<T>.kFlatMap(mapper: RxMapper<T, R>) = flatMap(mapper) ?: blowUp()
