package com.afollestad.aesthetic;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.Util.resolveResId;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Switch;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/** @author Aidan Follestad (afollestad) */
public class AestheticSwitch extends Switch {

  private Disposable subscription;
  private int backgroundResId;

  public AestheticSwitch(Context context) {
    super(context);
  }

  public AestheticSwitch(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public AestheticSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
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
    //noinspection ConstantConditions
    subscription =
        Observable.combineLatest(
                ViewUtil.getObservableForResId(
                    getContext(), backgroundResId, Aesthetic.get().colorAccent()),
                Aesthetic.get().isDark(),
                ColorIsDarkState.creator())
            .compose(Rx.<ColorIsDarkState>distinctToMainThread())
            .subscribe(
                new Consumer<ColorIsDarkState>() {
                  @Override
                  public void accept(@NonNull ColorIsDarkState state) {
                    invalidateColors(state);
                  }
                },
                onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    subscription.dispose();
    super.onDetachedFromWindow();
  }
}
