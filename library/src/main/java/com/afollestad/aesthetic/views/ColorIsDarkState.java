package com.afollestad.aesthetic.views;

import android.support.annotation.ColorInt;
import android.support.annotation.RestrictTo;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
final class ColorIsDarkState {

  @ColorInt public final int color;
  public final boolean isDark;

  private ColorIsDarkState(int color, boolean isDark) {
    this.color = color;
    this.isDark = isDark;
  }

  static ColorIsDarkState create(int color, boolean isDark) {
    return new ColorIsDarkState(color, isDark);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof ColorIsDarkState
        && color == ((ColorIsDarkState) obj).color
        && isDark == ((ColorIsDarkState) obj).isDark;
  }
}
