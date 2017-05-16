package com.afollestad.aesthetic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import rx.Subscription;
import rx.functions.Action1;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/** @author Aidan Follestad (afollestad) */
final class AestheticRecyclerView extends RecyclerView {

  private Subscription subscription;

  public AestheticRecyclerView(Context context) {
    super(context);
  }

  public AestheticRecyclerView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private void invalidateColors(int color) {
    EdgeGlowUtil.setEdgeGlowColor(this, color, null);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subscription =
        Aesthetic.get()
            .colorAccent()
            .compose(Rx.<Integer>distinctToMainThread())
            .subscribe(
                new Action1<Integer>() {
                  @Override
                  public void call(Integer color) {
                    invalidateColors(color);
                  }
                },
                onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }
}
