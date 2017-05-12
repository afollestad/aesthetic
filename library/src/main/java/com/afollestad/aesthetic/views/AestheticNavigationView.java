package com.afollestad.aesthetic.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.RestrictTo;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.NavigationViewMode;
import com.afollestad.aesthetic.R;
import com.afollestad.aesthetic.Util;

import rx.Observable;
import rx.Subscription;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
@SuppressWarnings("RestrictedApi")
public class AestheticNavigationView extends NavigationView {

  private Subscription modeSubscription;
  private Subscription colorSubscription;

  public AestheticNavigationView(Context context) {
    super(context);
  }

  public AestheticNavigationView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private void invalidateColors(Pair<Integer, Boolean> pair) {
    int selectedColor = pair.first;
    boolean isDark = pair.second;
    int baseColor = isDark ? Color.WHITE : Color.BLACK;
    int unselectedIconColor = Util.adjustAlpha(baseColor, .54f);
    int unselectedTextColor = Util.adjustAlpha(baseColor, .87f);
    int selectedItemBgColor =
        ContextCompat.getColor(
            getContext(),
            isDark
                ? R.color.ate_navigation_drawer_selected_dark
                : R.color.ate_navigation_drawer_selected_light);

    final ColorStateList iconSl =
        new ColorStateList(
            new int[][] {
              new int[] {-android.R.attr.state_checked}, new int[] {android.R.attr.state_checked}
            },
            new int[] {unselectedIconColor, selectedColor});
    final ColorStateList textSl =
        new ColorStateList(
            new int[][] {
              new int[] {-android.R.attr.state_checked}, new int[] {android.R.attr.state_checked}
            },
            new int[] {unselectedTextColor, selectedColor});
    setItemTextColor(textSl);
    setItemIconTintList(iconSl);

    StateListDrawable bgDrawable = new StateListDrawable();
    bgDrawable.addState(
        new int[] {android.R.attr.state_checked}, new ColorDrawable(selectedItemBgColor));
    setItemBackground(bgDrawable);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    modeSubscription =
        Aesthetic.get()
            .navViewMode()
            .compose(distinctToMainThread())
            .subscribe(
                mode -> {
                  switch (mode) {
                    case NavigationViewMode.SELECTED_PRIMARY:
                      colorSubscription =
                          Observable.combineLatest(
                                  Aesthetic.get().primaryColor(),
                                  Aesthetic.get().isDark(),
                                  Pair::create)
                              .compose(distinctToMainThread())
                              .subscribe(this::invalidateColors, onErrorLogAndRethrow());
                      break;
                    case NavigationViewMode.SELECTED_ACCENT:
                      colorSubscription =
                          Observable.combineLatest(
                                  Aesthetic.get().accentColor(),
                                  Aesthetic.get().isDark(),
                                  Pair::create)
                              .compose(distinctToMainThread())
                              .subscribe(this::invalidateColors, onErrorLogAndRethrow());
                      break;
                    default:
                      throw new IllegalStateException("Unknown nav view mode: " + mode);
                  }
                },
                onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    if (modeSubscription != null) {
      modeSubscription.unsubscribe();
    }
    if (colorSubscription != null) {
      colorSubscription.unsubscribe();
    }
    super.onDetachedFromWindow();
  }
}
