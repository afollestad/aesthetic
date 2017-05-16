package com.afollestad.aesthetic;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.Util.resolveResId;

/** @author Aidan Follestad (afollestad) */
final class AestheticTextInputEditText extends TextInputEditText {

  private CompositeSubscription subs;
  private int backgroundResId;
  private ColorIsDarkState lastState;

  public AestheticTextInputEditText(Context context) {
    super(context);
  }

  public AestheticTextInputEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public AestheticTextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    if (attrs != null) {
      backgroundResId = resolveResId(context, attrs, android.R.attr.background);
    }
  }

  private void invalidateColors(ColorIsDarkState state) {
    this.lastState = state;
    TintHelper.setTintAuto(this, state.color(), true, state.isDark());
    TintHelper.setCursorTint(this, state.color());
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subs = new CompositeSubscription();
    subs.add(
        Aesthetic.get()
            .textColorPrimary()
            .compose(distinctToMainThread())
            .subscribe(this::setTextColor, onErrorLogAndRethrow()));
    subs.add(
        Aesthetic.get()
            .textColorSecondary()
            .compose(distinctToMainThread())
            .subscribe(this::setHintTextColor, onErrorLogAndRethrow()));
    subs.add(
        Observable.combineLatest(
                ViewUtil.getObservableForResId(
                    getContext(), backgroundResId, Aesthetic.get().colorAccent()),
                Aesthetic.get().isDark(),
                ColorIsDarkState::create)
            .compose(distinctToMainThread())
            .subscribe(this::invalidateColors, onErrorLogAndRethrow()));
  }

  @Override
  protected void onDetachedFromWindow() {
    subs.unsubscribe();
    super.onDetachedFromWindow();
  }

  @Override
  public void refreshDrawableState() {
    super.refreshDrawableState();
    if (lastState != null) {
      post(() -> invalidateColors(lastState));
    }
  }
}
