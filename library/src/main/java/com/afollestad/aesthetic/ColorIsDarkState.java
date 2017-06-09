package com.afollestad.aesthetic;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

import android.support.annotation.ColorInt;
import android.support.annotation.RestrictTo;
import io.reactivex.functions.BiFunction;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
final class ColorIsDarkState {

  @ColorInt private final int color;
  private final boolean isDark;

  private ColorIsDarkState(@ColorInt int color, boolean isDark) {
    this.color = color;
    this.isDark = isDark;
  }

  static ColorIsDarkState create(@ColorInt int color, boolean isDark) {
    return new ColorIsDarkState(color, isDark);
  }

  static BiFunction<Integer, Boolean, ColorIsDarkState> creator() {
    return new BiFunction<Integer, Boolean, ColorIsDarkState>() {
      @Override
      public ColorIsDarkState apply(Integer integer, Boolean aBoolean) {
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
