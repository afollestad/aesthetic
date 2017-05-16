package com.afollestad.aesthetic;

import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import rx.functions.Func2;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
final class BgIconColorState {

  private final int bgColor;
  private final ActiveInactiveColors iconTitleColor;

  private BgIconColorState(int bgColor, ActiveInactiveColors iconTitleColor) {
    this.bgColor = bgColor;
    this.iconTitleColor = iconTitleColor;
  }

  static BgIconColorState create(int color, ActiveInactiveColors iconTitleColors) {
    return new BgIconColorState(color, iconTitleColors);
  }

  @ColorInt
  int bgColor() {
    return bgColor;
  }

  @Nullable
  ActiveInactiveColors iconTitleColor() {
    return iconTitleColor;
  }

  static Func2<Integer, ActiveInactiveColors, BgIconColorState> creator() {
    return new Func2<Integer, ActiveInactiveColors, BgIconColorState>() {
      @Override
      public BgIconColorState call(Integer integer, ActiveInactiveColors activeInactiveColors) {
        return BgIconColorState.create(integer, activeInactiveColors);
      }
    };
  }
}
