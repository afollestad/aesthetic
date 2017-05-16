package com.afollestad.aesthetic;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

import rx.Observable;
import rx.Subscription;

import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.Util.resolveResId;

/** @author Aidan Follestad (afollestad) */
final class AestheticImageButton extends AppCompatImageButton {

  private Subscription bgSubscription;
  private int backgroundResId;

  public AestheticImageButton(Context context) {
    super(context);
  }

  public AestheticImageButton(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public AestheticImageButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    if (attrs != null) {
      backgroundResId = resolveResId(context, attrs, android.R.attr.background);
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
