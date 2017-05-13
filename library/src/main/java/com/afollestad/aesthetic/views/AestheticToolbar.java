package com.afollestad.aesthetic.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
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
import static com.afollestad.aesthetic.Util.isColorLight;
import static com.afollestad.aesthetic.Util.setOverflowButtonColor;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
public class AestheticToolbar extends Toolbar {

  private int titleIconColor;
  private int backgroundResId;
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

  private void invalidateColors(int color) {
    setBackgroundColor(color);
    this.titleIconColor = isColorLight(color) ? Color.BLACK : Color.WHITE;
    setTitleTextColor(titleIconColor);
    setOverflowButtonColor(this, titleIconColor);
    if (getNavigationIcon() != null) {
      setNavigationIcon(getNavigationIcon());
    }
    onColorUpdated.onNext(color);
    ViewUtil.tintToolbarMenu(this, getMenu(), titleIconColor);
  }

  public Observable<Integer> colorUpdated() {
    return onColorUpdated.asObservable();
  }

  @Override
  public void setNavigationIcon(@Nullable Drawable icon) {
    super.setNavigationIcon(createTintedDrawable(icon, titleIconColor));
  }

  public void setNavigationIcon(@Nullable Drawable icon, @ColorInt int color) {
    super.setNavigationIcon(createTintedDrawable(icon, color));
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subscription =
        ViewUtil.getObservableForResId(
                getContext(), backgroundResId, Aesthetic.get().primaryColor())
            .compose(distinctToMainThread())
            .subscribe(this::invalidateColors, onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    onColorUpdated = null;
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }
}
