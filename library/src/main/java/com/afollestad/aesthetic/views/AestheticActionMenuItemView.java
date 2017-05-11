package com.afollestad.aesthetic.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;

import rx.Subscription;

import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;
import static com.afollestad.aesthetic.Util.isColorLight;

@SuppressWarnings("RestrictedApi")
public class AestheticActionMenuItemView extends ActionMenuItemView {

  private int iconColor;
  private Drawable ogIcon;
  private Drawable icon;
  private Subscription subscription;

  public AestheticActionMenuItemView(Context context) {
    super(context);
  }

  public AestheticActionMenuItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticActionMenuItemView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  private void invalidateColors(int color) {
    this.iconColor = isColorLight(color) ? Color.BLACK : Color.WHITE;
    if (ogIcon != null) {
      setIcon(ogIcon);
    }
  }

  @Override
  public void setIcon(Drawable icon) {
    if (ogIcon != icon) {
      ogIcon = icon;
    }
    this.icon = createTintedDrawable(ogIcon, iconColor);
    super.setIcon(this.icon);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subscription =
        Aesthetic.get()
            .primaryColor()
            .compose(distinctToMainThread())
            .subscribe(this::invalidateColors, onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }
}
