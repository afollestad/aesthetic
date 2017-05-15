package com.afollestad.aesthetic.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;
import static com.afollestad.aesthetic.Util.setOverflowButtonColor;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
public class AestheticToolbar extends Toolbar {

  private int backgroundResId;
  private BgIconColorState lastState;
  private Subscription subscription;
  private PublishSubject<Integer> onColorUpdated;

  public AestheticToolbar(Context context) {
    super(context);
    init(context, null);
  }

  public AestheticToolbar(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public AestheticToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    onColorUpdated = PublishSubject.create();
    if (context == null) {
      return;
    }
    if (attrs != null) {
      int[] attrsArray = new int[] {android.R.attr.background};
      TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
      backgroundResId = ta.getResourceId(0, 0);
      ta.recycle();
    }
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
                ViewUtil.getObservableForResId(
                    getContext(), backgroundResId, Aesthetic.get().colorPrimary()),
                Aesthetic.get().colorIconTitle(),
                BgIconColorState::create)
            .compose(distinctToMainThread())
            .subscribe(this::invalidateColors, onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    lastState = null;
    onColorUpdated = null;
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }
}
