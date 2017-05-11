package com.afollestad.aesthetic.views;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;

import rx.Subscription;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

@RestrictTo(LIBRARY_GROUP)
public class AestheticRecyclerView extends RecyclerView {

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
            .accentColor()
            .compose(distinctToMainThread())
            .subscribe(this::invalidateColors, onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }
}
