package com.afollestad.aesthetic.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;
import static com.afollestad.aesthetic.Util.isColorLight;

@SuppressWarnings("RestrictedApi")
@RestrictTo(LIBRARY_GROUP)
public class AestheticActionMenuItemView extends ActionMenuItemView {

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

  @Override
  public void setIcon(final Drawable icon) {
    // We need to retrieve the color again here.
    // For some reason, without this, a transparent color is used and the icon disappears
    // when the overflow menu opens.
    Aesthetic.get()
        .primaryColor()
        .observeOn(AndroidSchedulers.mainThread())
        .take(1)
        .subscribe(
            color -> {
              int iconColor = isColorLight(color) ? Color.BLACK : Color.WHITE;
              this.icon = createTintedDrawable(icon, iconColor);
              super.setIcon(this.icon);
            },
            onErrorLogAndRethrow());
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subscription =
        Aesthetic.get()
            .primaryColor()
            .compose(distinctToMainThread())
            .subscribe(
                color -> {
                  if (icon != null) {
                    setIcon(icon);
                  }
                },
                onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }
}
