package com.afollestad.aesthetic;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.AttributeSet;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/** @author Aidan Follestad (afollestad) */
@SuppressWarnings("RestrictedApi")
final class AestheticActionMenuItemView extends ActionMenuItemView {

  private Drawable icon;
  private Disposable subscription;

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
    super.setIcon(icon);

    // We need to retrieve the color again here.
    // For some reason, without this, a transparent color is used and the icon disappears
    // when the overflow menu opens.
    Aesthetic.get()
        .colorIconTitle(null)
        .observeOn(AndroidSchedulers.mainThread())
        .take(1)
        .subscribe(
            new Consumer<ActiveInactiveColors>() {
              @Override
              public void accept(@NonNull ActiveInactiveColors colors) {
                setIcon(icon, colors.toEnabledSl());
              }
            },
            onErrorLogAndRethrow());
  }

  public void setIcon(final Drawable icon, ColorStateList colors) {
    this.icon = icon;
    super.setIcon(createTintedDrawable(icon, colors));
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subscription =
        Aesthetic.get()
            .colorIconTitle(null)
            .compose(Rx.<ActiveInactiveColors>distinctToMainThread())
            .subscribe(
                new Consumer<ActiveInactiveColors>() {
                  @Override
                  public void accept(@NonNull ActiveInactiveColors colors) {
                    if (icon != null) {
                      setIcon(icon, colors.toEnabledSl());
                    }
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
