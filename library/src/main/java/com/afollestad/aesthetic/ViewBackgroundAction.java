package com.afollestad.aesthetic;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.view.View;

import rx.functions.Action1;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
class ViewBackgroundAction implements Action1<Integer> {

  private final View view;

  private ViewBackgroundAction(View view) {
    this.view = view;
  }

  public static ViewBackgroundAction create(@NonNull View view) {
    return new ViewBackgroundAction(view);
  }

  @Override
  public void call(Integer color) {
    if (view != null) {
      view.setBackgroundColor(color);
    }
  }
}
