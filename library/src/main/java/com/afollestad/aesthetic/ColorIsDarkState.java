package com.afollestad.aesthetic;

import android.support.annotation.ColorInt;
import android.support.annotation.RestrictTo;

import rx.functions.Func2;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
final class ColorIsDarkState {

  private final int color;
  private final boolean isDark;

  private ColorIsDarkState(int color, boolean isDark) {
    this.color = color;
    this.isDark = isDark;
  }

  static ColorIsDarkState create(int color, boolean isDark) {
    return new ColorIsDarkState(color, isDark);
  }

  static Func2<Integer, Boolean, ColorIsDarkState> creator() {
    return new Func2<Integer, Boolean, ColorIsDarkState>() {
      @Override
      public ColorIsDarkState call(Integer integer, Boolean aBoolean) {
        return ColorIsDarkState.create(integer, aBoolean);
      }
    };
  }

  @ColorInt
  int color() {
    return color;
  }

  boolean isDark() {
    return isDark;
  }
}
