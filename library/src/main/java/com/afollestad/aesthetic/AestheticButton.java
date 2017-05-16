package com.afollestad.aesthetic;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import rx.Observable;
import rx.Subscription;

import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/** @author Aidan Follestad (afollestad) */
final class AestheticButton extends AppCompatButton {

  private Subscription subscription;
  private int backgroundResId;

  public AestheticButton(Context context) {
    super(context);
  }

  public AestheticButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public AestheticButton(Context context, AttributeSet attrs, int defStyleAttr) {
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
    TintHelper.setTintAuto(this, state.color(), true, state.isDark());
    ColorStateList textColorSl =
        new ColorStateList(
            new int[][] {
              new int[] {android.R.attr.state_enabled}, new int[] {-android.R.attr.state_enabled}
            },
            new int[] {
              Util.isColorLight(state.color()) ? Color.BLACK : Color.WHITE,
              state.isDark() ? Color.WHITE : Color.BLACK
            });
    setTextColor(textColorSl);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subscription =
        Observable.combineLatest(
                ViewUtil.getObservableForResId(
                    getContext(), backgroundResId, Aesthetic.get().colorAccent()),
                Aesthetic.get().isDark(),
                ColorIsDarkState::create)
            .compose(distinctToMainThread())
            .subscribe(this::invalidateColors, onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }
}
