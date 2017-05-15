package com.afollestad.aesthetic.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.R;
import com.afollestad.aesthetic.TintHelper;
import com.afollestad.aesthetic.Util;

import java.lang.reflect.Field;

import rx.Observable;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;
import static com.afollestad.aesthetic.Util.isColorLight;

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

  static void tintToolbarMenu(
      @NonNull Toolbar toolbar, @NonNull Menu menu, ActiveInactiveColors titleIconColors) {
    // The collapse icon displays when action views are expanded (e.g. SearchView)
    try {
      final Field field = Toolbar.class.getDeclaredField("mCollapseIcon");
      field.setAccessible(true);
      Drawable collapseIcon = (Drawable) field.get(toolbar);
      if (collapseIcon != null)
        field.set(toolbar, createTintedDrawable(collapseIcon, titleIconColors.toEnabledSl()));
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Theme menu action views
    for (int i = 0; i < menu.size(); i++) {
      MenuItem item = menu.getItem(i);
      if (item.getActionView() instanceof SearchView) {
        themeSearchView(titleIconColors, (SearchView) item.getActionView());
      }
    }
  }

  private static void themeSearchView(ActiveInactiveColors tintColors, SearchView view) {
    final Class<?> cls = view.getClass();
    try {
      final Field mSearchSrcTextViewField = cls.getDeclaredField("mSearchSrcTextView");
      mSearchSrcTextViewField.setAccessible(true);
      final EditText mSearchSrcTextView = (EditText) mSearchSrcTextViewField.get(view);
      mSearchSrcTextView.setTextColor(tintColors.activeColor());
      mSearchSrcTextView.setHintTextColor(tintColors.inactiveColor());
      TintHelper.setCursorTint(mSearchSrcTextView, tintColors.activeColor());

      Field field = cls.getDeclaredField("mSearchButton");
      tintImageView(view, field, tintColors);
      field = cls.getDeclaredField("mGoButton");
      tintImageView(view, field, tintColors);
      field = cls.getDeclaredField("mCloseButton");
      tintImageView(view, field, tintColors);
      field = cls.getDeclaredField("mVoiceButton");
      tintImageView(view, field, tintColors);

      field = cls.getDeclaredField("mSearchPlate");
      field.setAccessible(true);
      TintHelper.setTintAuto(
          (View) field.get(view),
          tintColors.activeColor(),
          true,
          !isColorLight(tintColors.activeColor()));

      field = cls.getDeclaredField("mSearchHintIcon");
      field.setAccessible(true);
      field.set(view, createTintedDrawable((Drawable) field.get(view), tintColors.toEnabledSl()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void tintImageView(Object target, Field field, ActiveInactiveColors tintColors)
      throws Exception {
    field.setAccessible(true);
    final ImageView imageView = (ImageView) field.get(target);
    if (imageView.getDrawable() != null) {
      imageView.setImageDrawable(
          createTintedDrawable(imageView.getDrawable(), tintColors.toEnabledSl()));
    }
  }
}
