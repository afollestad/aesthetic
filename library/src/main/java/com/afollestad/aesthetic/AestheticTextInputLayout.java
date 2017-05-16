package com.afollestad.aesthetic;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.Util.adjustAlpha;
import static com.afollestad.aesthetic.Util.resolveResId;

/** @author Aidan Follestad (afollestad) */
final class AestheticTextInputLayout extends TextInputLayout {

  private CompositeSubscription subs;
  private int backgroundResId;

  public AestheticTextInputLayout(Context context) {
    super(context);
  }

  public AestheticTextInputLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public AestheticTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    if (attrs != null) {
      backgroundResId = resolveResId(context, attrs, android.R.attr.background);
    }
  }

  private void invalidateColors(int color) {
    TextInputLayoutUtil.setAccent(this, color);
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subs = new CompositeSubscription();
    subs.add(
        Aesthetic.get()
            .textColorSecondary()
            .compose(Rx.<Integer>distinctToMainThread())
            .subscribe(
                new Action1<Integer>() {
                  @Override
                  public void call(Integer color) {
                    TextInputLayoutUtil.setHint(
                        AestheticTextInputLayout.this, adjustAlpha(color, 0.7f));
                  }
                },
                onErrorLogAndRethrow()));
    subs.add(
        ViewUtil.getObservableForResId(getContext(), backgroundResId, Aesthetic.get().colorAccent())
            .compose(Rx.<Integer>distinctToMainThread())
            .subscribe(
                new Action1<Integer>() {
                  @Override
                  public void call(Integer color) {
                    invalidateColors(color);
                  }
                },
                onErrorLogAndRethrow()));
  }

  @Override
  protected void onDetachedFromWindow() {
    subs.unsubscribe();
    super.onDetachedFromWindow();
  }
}
