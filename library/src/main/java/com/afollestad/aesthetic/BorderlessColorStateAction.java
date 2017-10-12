package com.afollestad.aesthetic;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.widget.TextView;

import io.reactivex.functions.Consumer;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/** @author Asen Lekov (L3K0V) */
@RestrictTo(LIBRARY_GROUP)
class BorderlessColorStateAction implements Consumer<Integer> {

  private final TextView view;

  private BorderlessColorStateAction(TextView view) {
    this.view = view;
  }

  public static BorderlessColorStateAction create(@NonNull TextView view) {
    return new BorderlessColorStateAction(view);
  }

  @Override
  public void accept(@io.reactivex.annotations.NonNull Integer color) {
    if (view != null) {

      final int disabledColor = Util.adjustAlpha(color, 0.70f);

      ColorStateList textColorSl =
          new ColorStateList(
              new int[][] {
                  new int[] {android.R.attr.state_enabled}, new int[] {-android.R.attr.state_enabled}
              },
              new int[] {color, disabledColor}
          );

      view.setTextColor(textColorSl);
    }
  }
}
