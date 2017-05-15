package com.afollestad.aesthetic.views;

import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.annotation.RestrictTo;

import com.google.auto.value.AutoValue;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
@AutoValue
public abstract class ActiveInactiveColors {

  @ColorInt
  abstract int activeColor();

  @ColorInt
  abstract int inactiveColor();

  public static ActiveInactiveColors create(int activeColor, int inactiveColor) {
    return new AutoValue_ActiveInactiveColors(activeColor, inactiveColor);
  }

  public ColorStateList toEnabledSl() {
    return new ColorStateList(
        new int[][] {
          new int[] {android.R.attr.state_enabled}, new int[] {-android.R.attr.state_enabled}
        },
        new int[] {activeColor(), inactiveColor()});
  }

  public ColorStateList toCheckedSl() {
    return new ColorStateList(
        new int[][] {
          new int[] {android.R.attr.state_checked}, new int[] {-android.R.attr.state_checked}
        },
        new int[] {activeColor(), inactiveColor()});
  }
}
