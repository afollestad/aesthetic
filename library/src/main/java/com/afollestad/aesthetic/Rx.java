package com.afollestad.aesthetic;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

import android.support.annotation.RestrictTo;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
final class Rx {

  static Consumer<Throwable> onErrorLogAndRethrow() {
    return new Consumer<Throwable>() {
      @Override
      public void accept(@NonNull Throwable throwable) throws Exception {
        throwable.printStackTrace();
        throw Exceptions.propagate(throwable);
      }
    };
  }

  static <T> ObservableTransformer<T, T> distinctToMainThread() {
    return new ObservableTransformer<T, T>() {
      @Override
      public ObservableSource<T> apply(@NonNull Observable<T> obs) {
        return obs.observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged();
      }
    };
  }
}
