package com.afollestad.aesthetic;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;

/** @author Aidan Follestad (afollestad) */
public final class Rx {

  public static Consumer<Throwable> onErrorLogAndRethrow() {
    return throwable -> {
      throwable.printStackTrace();
      throw Exceptions.propagate(throwable);
    };
  }

  public static <T> ObservableTransformer<T, T> distinctToMainThread() {
    return obs -> obs.observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged();
  }
}
