package com.afollestad.aesthetic.views;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.TintHelper;

import rx.subscriptions.CompositeSubscription;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
public class AestheticSwitchCompat extends SwitchCompat {

  private int color;
  private boolean isDark;
  private CompositeSubscription subs;

  public AestheticSwitchCompat(Context context) {
    super(context);
  }

  public AestheticSwitchCompat(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticSwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private void invalidateColors(int color) {
    this.color = color;
    TintHelper.setTint(this, color, isDark);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subs = new CompositeSubscription();
    subs.add(
        Aesthetic.get()
            .accentColor()
            .compose(distinctToMainThread())
            .subscribe(this::invalidateColors, onErrorLogAndRethrow()));
    subs.add(
        Aesthetic.get()
            .isDark()
            .compose(distinctToMainThread())
            .subscribe(
                isDark -> {
                  this.isDark = isDark;
                  invalidateColors(color);
                }));
  }

  @Override
  protected void onDetachedFromWindow() {
    subs.unsubscribe();
    super.onDetachedFromWindow();
  }
}
