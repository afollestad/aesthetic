package com.afollestad.aesthetic.views;

import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.annotation.RestrictTo;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
public final class ActiveInactiveColors {

  private final int activeColor;
  private final int inactiveColor;

  @ColorInt
  public int activeColor() {
    return activeColor;
  }

  @ColorInt
  public int inactiveColor() {
    return inactiveColor;
  }

  private ActiveInactiveColors(int activeColor, int inactiveColor) {
    this.activeColor = activeColor;
    this.inactiveColor = inactiveColor;
  }

  public static ActiveInactiveColors create(int activeColor, int inactiveColor) {
    return new ActiveInactiveColors(activeColor, inactiveColor);
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
