package com.afollestad.aesthetic;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;
import static com.afollestad.aesthetic.Util.setOverflowButtonColor;

/** @author Aidan Follestad (afollestad) */
final class AestheticToolbar extends Toolbar {

  private BgIconColorState lastState;
  private Subscription subscription;
  private PublishSubject<Integer> onColorUpdated;

  public AestheticToolbar(Context context) {
    super(context);
    init();
  }

  public AestheticToolbar(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public AestheticToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    onColorUpdated = PublishSubject.create();
  }

  private void invalidateColors(BgIconColorState state) {
    lastState = state;
    setBackgroundColor(state.bgColor());
    setTitleTextColor(state.iconTitleColor().activeColor());
    setOverflowButtonColor(this, state.iconTitleColor().activeColor());
    if (getNavigationIcon() != null) {
      setNavigationIcon(getNavigationIcon());
    }
    onColorUpdated.onNext(state.bgColor());
    ViewUtil.tintToolbarMenu(this, getMenu(), state.iconTitleColor());
  }

  public Observable<Integer> colorUpdated() {
    return onColorUpdated.asObservable();
  }

  @Override
  public void setNavigationIcon(@Nullable Drawable icon) {
    if (lastState == null) {
      super.setNavigationIcon(icon);
      return;
    }
    super.setNavigationIcon(createTintedDrawable(icon, lastState.iconTitleColor().toEnabledSl()));
  }

  public void setNavigationIcon(@Nullable Drawable icon, @ColorInt int color) {
    if (lastState == null) {
      super.setNavigationIcon(icon);
      return;
    }
    super.setNavigationIcon(createTintedDrawable(icon, color));
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subscription =
        Observable.combineLatest(
                Aesthetic.get().colorPrimary(),
                Aesthetic.get().colorIconTitle(null),
                BgIconColorState.creator())
            .compose(Rx.<BgIconColorState>distinctToMainThread())
            .subscribe(
                new Action1<BgIconColorState>() {
                  @Override
                  public void call(BgIconColorState bgIconColorState) {
                    invalidateColors(bgIconColorState);
                  }
                },
                onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    lastState = null;
    onColorUpdated = null;
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }
}
