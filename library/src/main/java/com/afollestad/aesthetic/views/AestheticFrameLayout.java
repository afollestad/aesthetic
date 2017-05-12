package com.afollestad.aesthetic.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import rx.Observable;
import rx.Subscription;

import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

public class AestheticFrameLayout extends FrameLayout {

  private Subscription bgSubscription;
  private int backgroundResId;

  public AestheticFrameLayout(Context context) {
    super(context);
  }

  public AestheticFrameLayout(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public AestheticFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    if (attrs != null) {
      int[] attrsArray = new int[] {android.R.attr.background};
      TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
      backgroundResId = ta.getResourceId(0, 0);
      ta.recycle();
    }
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    Observable<Integer> obs = ViewUtil.getObservableForResId(getContext(), backgroundResId, null);
    if (obs != null) {
      bgSubscription =
          obs.compose(distinctToMainThread())
              .subscribe(this::setBackgroundColor, onErrorLogAndRethrow());
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    if (bgSubscription != null) {
      bgSubscription.unsubscribe();
    }
    super.onDetachedFromWindow();
  }
}
