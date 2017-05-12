package com.afollestad.aesthetic.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.RestrictTo;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.TabLayoutIndicatorMode;

import rx.Subscription;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;
import static com.afollestad.aesthetic.Util.adjustAlpha;
import static com.afollestad.aesthetic.Util.isColorLight;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
public class AestheticTabLayout extends TabLayout {

  private static final float UNFOCUSED_ALPHA = 0.5f;
  private Subscription indicatorModeSubscription;
  private Subscription bgModeSubscription;
  private Subscription indicatorColorSubscription;
  private Subscription bgColorSubscription;

  public AestheticTabLayout(Context context) {
    super(context);
  }

  public AestheticTabLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private void setIconsColor(int color) {
    final ColorStateList sl =
        new ColorStateList(
            new int[][] {
              new int[] {-android.R.attr.state_selected}, new int[] {android.R.attr.state_selected}
            },
            new int[] {adjustAlpha(color, UNFOCUSED_ALPHA), color});
    for (int i = 0; i < getTabCount(); i++) {
      final TabLayout.Tab tab = getTabAt(i);
      if (tab != null && tab.getIcon() != null) {
        tab.setIcon(createTintedDrawable(tab.getIcon(), sl));
      }
    }
  }

  @Override
  public void setBackgroundColor(@ColorInt int color) {
    super.setBackgroundColor(color);
    int iconTextColor = isColorLight(color) ? Color.BLACK : Color.WHITE;
    setIconsColor(iconTextColor);
    setTabTextColors(adjustAlpha(iconTextColor, UNFOCUSED_ALPHA), iconTextColor);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    bgModeSubscription =
        Aesthetic.get()
            .tabLayoutBgMode()
            .compose(distinctToMainThread())
            .subscribe(
                mode -> {
                  if (bgColorSubscription != null) {
                    bgColorSubscription.unsubscribe();
                  }
                  switch (mode) {
                    case TabLayoutIndicatorMode.PRIMARY:
                      bgColorSubscription =
                          Aesthetic.get()
                              .primaryColor()
                              .compose(distinctToMainThread())
                              .subscribe(this::setBackgroundColor, onErrorLogAndRethrow());
                      break;
                    case TabLayoutIndicatorMode.ACCENT:
                      bgColorSubscription =
                          Aesthetic.get()
                              .accentColor()
                              .compose(distinctToMainThread())
                              .subscribe(this::setBackgroundColor, onErrorLogAndRethrow());
                      break;
                    default:
                      throw new IllegalStateException("Unimplemented bg mode: " + mode);
                  }
                },
                onErrorLogAndRethrow());

    indicatorModeSubscription =
        Aesthetic.get()
            .tabLayoutIndicatorMode()
            .compose(distinctToMainThread())
            .subscribe(
                mode -> {
                  if (indicatorColorSubscription != null) {
                    indicatorColorSubscription.unsubscribe();
                  }
                  switch (mode) {
                    case TabLayoutIndicatorMode.PRIMARY:
                      indicatorColorSubscription =
                          Aesthetic.get()
                              .primaryColor()
                              .compose(distinctToMainThread())
                              .subscribe(
                                  this::setSelectedTabIndicatorColor, onErrorLogAndRethrow());
                      break;
                    case TabLayoutIndicatorMode.ACCENT:
                      indicatorColorSubscription =
                          Aesthetic.get()
                              .accentColor()
                              .compose(distinctToMainThread())
                              .subscribe(
                                  this::setSelectedTabIndicatorColor, onErrorLogAndRethrow());
                      break;
                    default:
                      throw new IllegalStateException("Unimplemented bg mode: " + mode);
                  }
                },
                onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    if (bgModeSubscription != null) {
      bgModeSubscription.unsubscribe();
    }
    if (indicatorModeSubscription != null) {
      indicatorModeSubscription.unsubscribe();
    }
    if (bgColorSubscription != null) {
      bgColorSubscription.unsubscribe();
    }
    if (indicatorColorSubscription != null) {
      indicatorColorSubscription.unsubscribe();
    }
    super.onDetachedFromWindow();
  }
}
