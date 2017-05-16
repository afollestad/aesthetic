package com.afollestad.aesthetic;

import android.support.annotation.RestrictTo;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Action1;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
final class Rx {

  static Action1<Throwable> onErrorLogAndRethrow() {
    return new Action1<Throwable>() {
      @Override
      public void call(Throwable throwable) {
        throwable.printStackTrace();
        throw Exceptions.propagate(throwable);
      }
    };
  }

  static <T> Observable.Transformer<T, T> distinctToMainThread() {
    return new Observable.Transformer<T, T>() {
      @Override
      public Observable<T> call(Observable<T> obs) {
        return obs.observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged();
      }
    };
  }
}
