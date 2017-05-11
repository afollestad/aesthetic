package com.afollestad.aesthetic.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.Util;

import rx.Subscription;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;
import static com.afollestad.aesthetic.Util.setOverflowButtonColor;

@RestrictTo(LIBRARY_GROUP)
public class AestheticToolbar extends Toolbar {

  private int titleIconColor;
  private Subscription subscription;

  public AestheticToolbar(Context context) {
    super(context);
  }

  public AestheticToolbar(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private void invalidateColors(int color) {
    setBackgroundColor(color);
    this.titleIconColor = Util.isColorLight(color) ? Color.BLACK : Color.WHITE;
    setTitleTextColor(titleIconColor);
    setOverflowButtonColor(this, titleIconColor);
    if (getNavigationIcon() != null) {
      setNavigationIcon(getNavigationIcon());
    }
  }

  @Override
  public void setNavigationIcon(@Nullable Drawable icon) {
    super.setNavigationIcon(createTintedDrawable(getNavigationIcon(), titleIconColor));
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
