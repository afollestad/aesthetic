package com.afollestad.aesthetic;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import rx.Subscription;

import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/** @author Aidan Follestad (afollestad) */
final class AestheticListView extends ListView {

  private Subscription subscription;

  public AestheticListView(Context context) {
    super(context);
  }

  public AestheticListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private void invalidateColors(int color) {
    EdgeGlowUtil.setEdgeGlowColor(this, color);
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
