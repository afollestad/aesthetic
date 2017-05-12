package com.afollestad.aesthetic.views;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.R;
import com.afollestad.aesthetic.Util;

import rx.Observable;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
class ViewUtil {

  @Nullable
  static Observable<Integer> getObservableForResId(
      @NonNull Context context, @IdRes int resId, @Nullable Observable<Integer> fallback) {
    if (resId == 0) {
      return fallback;
    } else if (resId == Util.resolveResId(context, R.attr.colorPrimary, 0)) {
      return Aesthetic.get().primaryColor();
    } else if (resId == Util.resolveResId(context, R.attr.colorPrimaryDark, 0)) {
      return Aesthetic.get().statusBarColor();
    } else if (resId == Util.resolveResId(context, R.attr.colorAccent, 0)) {
      return Aesthetic.get().accentColor();
    } else if (resId == Util.resolveResId(context, android.R.attr.windowBackground, 0)) {
      return Aesthetic.get().windowBgColor();
    } else if (resId == Util.resolveResId(context, android.R.attr.textColorPrimary, 0)) {
      return Aesthetic.get().primaryTextColor();
    } else if (resId == Util.resolveResId(context, android.R.attr.textColorPrimaryInverse, 0)) {
      return Aesthetic.get().primaryTextInverseColor();
    } else if (resId == Util.resolveResId(context, android.R.attr.textColorSecondary, 0)) {
      return Aesthetic.get().secondaryTextColor();
    } else if (resId == Util.resolveResId(context, android.R.attr.textColorSecondaryInverse, 0)) {
      return Aesthetic.get().secondaryTextInverseColor();
    }
    return fallback;
  }
}
