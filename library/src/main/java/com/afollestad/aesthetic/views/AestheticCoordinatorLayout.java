package com.afollestad.aesthetic.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.View;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.TintHelper;
import com.afollestad.aesthetic.Util;

import java.lang.reflect.Field;

import rx.Subscription;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
public class AestheticCoordinatorLayout extends CoordinatorLayout
    implements AppBarLayout.OnOffsetChangedListener {

  private Subscription statusBarColorSubscription;
  private AppBarLayout appBarLayout;
  private View colorView;
  private AestheticToolbar toolbar;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private int toolbarColor;
  private int lastOffset = -1;

  public AestheticCoordinatorLayout(Context context) {
    super(context);
  }

  public AestheticCoordinatorLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @SuppressWarnings("unchecked")
  private static void tintMenu(
      @NonNull AestheticToolbar toolbar, @Nullable Menu menu, final @ColorInt int color) {
    if (toolbar.getNavigationIcon() != null) {
      toolbar.setNavigationIcon(toolbar.getNavigationIcon(), color);
    }
    Util.setOverflowButtonColor(toolbar, color);

    try {
      final Field field = Toolbar.class.getDeclaredField("mCollapseIcon");
      field.setAccessible(true);
      Drawable collapseIcon = (Drawable) field.get(toolbar);
      if (collapseIcon != null) {
        field.set(toolbar, TintHelper.createTintedDrawable(collapseIcon, color));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    final PorterDuffColorFilter colorFilter =
        new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
    for (int i = 0; i < toolbar.getChildCount(); i++) {
      final View v = toolbar.getChildAt(i);
      // We can't iterate through the toolbar.getMenu() here, because we need the ActionMenuItemView.
      if (v instanceof ActionMenuView) {
        for (int j = 0; j < ((ActionMenuView) v).getChildCount(); j++) {
          final View innerView = ((ActionMenuView) v).getChildAt(j);
          if (innerView instanceof ActionMenuItemView) {
            int drawablesCount = ((ActionMenuItemView) innerView).getCompoundDrawables().length;
            for (int k = 0; k < drawablesCount; k++) {
              if (((ActionMenuItemView) innerView).getCompoundDrawables()[k] != null) {
                ((ActionMenuItemView) innerView)
                    .getCompoundDrawables()[k].setColorFilter(colorFilter);
              }
            }
          }
        }
      }
    }

    if (menu == null) {
      menu = toolbar.getMenu();
    }
    ViewUtil.tintToolbarMenu(toolbar, menu, color);
  }

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();

    // Find the toolbar and color view used to blend the scroll transition
    if (getChildCount() > 0 && getChildAt(0) instanceof AppBarLayout) {
      appBarLayout = (AppBarLayout) getChildAt(0);
      if (appBarLayout.getChildCount() > 0
          && appBarLayout.getChildAt(0) instanceof CollapsingToolbarLayout) {
        collapsingToolbarLayout = (CollapsingToolbarLayout) appBarLayout.getChildAt(0);
        for (int i = 0; i < collapsingToolbarLayout.getChildCount(); i++) {
          if (this.toolbar != null && this.colorView != null) {
            break;
          }
          View child = collapsingToolbarLayout.getChildAt(i);
          if (child instanceof AestheticToolbar) {
            this.toolbar = (AestheticToolbar) child;
          } else if (child.getBackground() != null
              && child.getBackground() instanceof ColorDrawable) {
            this.colorView = child;
          }
        }
      }
    }

    if (toolbar != null && colorView != null) {
      this.appBarLayout.addOnOffsetChangedListener(this);
      toolbar
          .colorUpdated()
          .compose(distinctToMainThread())
          .subscribe(
              color -> {
                toolbarColor = color;
                invalidateColors();
              },
              onErrorLogAndRethrow());
    }

    statusBarColorSubscription =
        Aesthetic.get()
            .statusBarColor()
            .compose(distinctToMainThread())
            .subscribe(
                color -> {
                  collapsingToolbarLayout.setContentScrimColor(color);
                  collapsingToolbarLayout.setStatusBarScrimColor(color);
                },
                onErrorLogAndRethrow());
  }

  @Override
  public void onDetachedFromWindow() {
    statusBarColorSubscription.unsubscribe();
    this.appBarLayout.removeOnOffsetChangedListener(this);
    this.appBarLayout = null;
    this.toolbar = null;
    this.colorView = null;
    super.onDetachedFromWindow();
  }

  @Override
  public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
    if (lastOffset == Math.abs(verticalOffset)) {
      return;
    }
    lastOffset = Math.abs(verticalOffset);
    invalidateColors();
  }

  private void invalidateColors() {
    final int maxOffset = appBarLayout.getMeasuredHeight() - toolbar.getMeasuredHeight();
    final float ratio = (float) lastOffset / (float) maxOffset;

    final int colorViewColor = ((ColorDrawable) colorView.getBackground()).getColor();
    final int blendedColor = Util.blendColors(colorViewColor, toolbarColor, ratio);
    final int collapsedTitleColor = Util.isColorLight(toolbarColor) ? Color.BLACK : Color.WHITE;
    final int expandedTitleColor = Util.isColorLight(colorViewColor) ? Color.BLACK : Color.WHITE;
    final int blendedTitleColor = Util.blendColors(expandedTitleColor, collapsedTitleColor, ratio);

    toolbar.setBackgroundColor(blendedColor);

    collapsingToolbarLayout.setCollapsedTitleTextColor(collapsedTitleColor);
    collapsingToolbarLayout.setExpandedTitleColor(expandedTitleColor);

    tintMenu(toolbar, toolbar.getMenu(), blendedTitleColor);
  }
}
