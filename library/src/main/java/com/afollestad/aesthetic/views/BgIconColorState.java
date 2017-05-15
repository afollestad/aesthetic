package com.afollestad.aesthetic.views;

import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import com.google.auto.value.AutoValue;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
@AutoValue
abstract class BgIconColorState {

  static BgIconColorState create(int color, ActiveInactiveColors iconTitleColors) {
    return new AutoValue_BgIconColorState(color, iconTitleColors);
  }

  @ColorInt
  abstract int bgColor();

  @Nullable
  abstract ActiveInactiveColors iconTitleColor();
}
