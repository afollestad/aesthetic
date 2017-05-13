package com.afollestad.aesthetic.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.RestrictTo;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.TintHelper;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
public class AestheticTextInputEditText extends TextInputEditText {

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
      int[] attrsArray = new int[] {android.R.attr.background};
      TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
      backgroundResId = ta.getResourceId(0, 0);
      ta.recycle();
    }
  }

  private void invalidateColors(ColorIsDarkState state) {
    this.lastState = state;
    TintHelper.setTintAuto(this, state.color, true, state.isDark);
    TintHelper.setCursorTint(this, state.color);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subs = new CompositeSubscription();
    subs.add(
        Aesthetic.get()
            .primaryTextColor()
            .compose(distinctToMainThread())
            .subscribe(this::setTextColor, onErrorLogAndRethrow()));
    subs.add(
        Aesthetic.get()
            .secondaryTextColor()
            .compose(distinctToMainThread())
            .subscribe(this::setHintTextColor, onErrorLogAndRethrow()));
    subs.add(
        Observable.combineLatest(
                ViewUtil.getObservableForResId(
                    getContext(), backgroundResId, Aesthetic.get().accentColor()),
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
