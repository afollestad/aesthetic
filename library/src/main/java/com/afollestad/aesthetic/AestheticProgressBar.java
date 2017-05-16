package com.afollestad.aesthetic;

import android.content.Context;
import android.util.AttributeSet;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.Subscription;

import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/** @author Aidan Follestad (afollestad) */
final class AestheticProgressBar extends MaterialProgressBar {

  private Subscription subscription;

  public AestheticProgressBar(Context context) {
    super(context);
  }

  public AestheticProgressBar(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private void invalidateColors(int color) {
    TintHelper.setTint(this, color);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subscription =
        Aesthetic.get()
            .colorAccent()
            .compose(distinctToMainThread())
            .subscribe(this::invalidateColors, onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }
}
