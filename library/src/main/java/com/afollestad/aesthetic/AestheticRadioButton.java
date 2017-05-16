package com.afollestad.aesthetic;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.Util.resolveResId;

/** @author Aidan Follestad (afollestad) */
final class AestheticRadioButton extends AppCompatRadioButton {

  private CompositeSubscription subscriptions;
  private int backgroundResId;

  public AestheticRadioButton(Context context) {
    super(context);
  }

  public AestheticRadioButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public AestheticRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    if (attrs != null) {
      backgroundResId = resolveResId(context, attrs, android.R.attr.background);
    }
  }

  private void invalidateColors(ColorIsDarkState state) {
    TintHelper.setTint(this, state.color(), state.isDark());
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subscriptions = new CompositeSubscription();
    subscriptions.add(
        Observable.combineLatest(
                ViewUtil.getObservableForResId(
                    getContext(), backgroundResId, Aesthetic.get().colorAccent()),
                Aesthetic.get().isDark(),
                ColorIsDarkState::create)
            .compose(distinctToMainThread())
            .subscribe(this::invalidateColors, onErrorLogAndRethrow()));
    subscriptions.add(
        Aesthetic.get()
            .textColorPrimary()
            .compose(distinctToMainThread())
            .subscribe(this::setTextColor));
  }

  @Override
  protected void onDetachedFromWindow() {
    subscriptions.unsubscribe();
    super.onDetachedFromWindow();
  }
}
