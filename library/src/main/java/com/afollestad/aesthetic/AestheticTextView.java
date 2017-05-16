package com.afollestad.aesthetic;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import rx.Observable;
import rx.Subscription;

import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.Util.resolveResId;

/** @author Aidan Follestad (afollestad) */
final class AestheticTextView extends AppCompatTextView {

  private Subscription subscription;
  private int textColorResId;

  public AestheticTextView(Context context) {
    super(context);
  }

  public AestheticTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public AestheticTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    if (attrs != null) {
      textColorResId = resolveResId(context, attrs, android.R.attr.textColor);
    }
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    Observable<Integer> obs =
        ViewUtil.getObservableForResId(
            getContext(), textColorResId, Aesthetic.get().textColorSecondary());
    //noinspection ConstantConditions
    subscription =
        obs.compose(distinctToMainThread()).subscribe(this::setTextColor, onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }
}
