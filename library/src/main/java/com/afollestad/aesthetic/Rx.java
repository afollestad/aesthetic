package com.afollestad.aesthetic;

import android.support.annotation.RestrictTo;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Action1;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
public class Rx {

  public static Action1<Throwable> onErrorLogAndRethrow() {
    return t -> {
      t.printStackTrace();
      throw Exceptions.propagate(t);
    };
  }

  public static <T> Observable.Transformer<T, T> distinctToMainThread() {
    return obs -> obs.observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged();
  }
}
