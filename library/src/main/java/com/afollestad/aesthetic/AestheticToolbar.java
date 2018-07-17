package com.afollestad.aesthetic;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;
import static com.afollestad.aesthetic.Util.setOverflowButtonColor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

/** @author Aidan Follestad (afollestad) */
public class AestheticToolbar extends Toolbar {

  private BgIconColorState lastState;
  private Disposable subscription;
  private PublishSubject<Integer> onColorUpdated;

  public AestheticToolbar(Context context) {
    super(context);
  }

  public AestheticToolbar(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private void invalidateColors(BgIconColorState state) {
    lastState = state;
    setBackgroundColor(state.bgColor());
    final ActiveInactiveColors iconTitleColors = state.iconTitleColor();
    if (iconTitleColors != null) {
      setTitleTextColor(iconTitleColors.activeColor());
      setOverflowButtonColor(this, iconTitleColors.activeColor());
    }
    if (getNavigationIcon() != null) {
      setNavigationIcon(getNavigationIcon());
    }
    onColorUpdated.onNext(state.bgColor());
    ViewUtil.tintToolbarMenu(this, getMenu(), iconTitleColors);
  }

  public Observable<Integer> colorUpdated() {
    return onColorUpdated;
  }

  @Override
  public void setNavigationIcon(@Nullable Drawable icon) {
    if (lastState == null) {
      super.setNavigationIcon(icon);
      return;
    }
    final ActiveInactiveColors iconTitleColors = lastState.iconTitleColor();
    if (iconTitleColors != null) {
      super.setNavigationIcon(createTintedDrawable(icon, iconTitleColors.toEnabledSl()));
    } else {
      super.setNavigationIcon(icon);
    }
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
    onColorUpdated = PublishSubject.create();
    subscription =
        Observable.combineLatest(
                Aesthetic.get().colorPrimary(),
                Aesthetic.get().colorIconTitle(null),
                BgIconColorState.creator())
            .compose(Rx.<BgIconColorState>distinctToMainThread())
            .subscribe(this::invalidateColors, onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    lastState = null;
    onColorUpdated = null;
    subscription.dispose();
    super.onDetachedFromWindow();
  }
}
