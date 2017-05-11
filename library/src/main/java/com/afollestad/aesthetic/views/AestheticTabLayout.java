package com.afollestad.aesthetic.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.TabLayoutBgMode;
import com.afollestad.aesthetic.TabLayoutIndicatorMode;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;
import static com.afollestad.aesthetic.Util.adjustAlpha;
import static com.afollestad.aesthetic.Util.isColorLight;

public class AestheticTabLayout extends TabLayout {

  private static final float UNFOCUSED_ALPHA = 0.5f;
  private CompositeSubscription subs;

  public AestheticTabLayout(Context context) {
    super(context);
  }

  public AestheticTabLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private Observable<Integer> bgColorObservable() {
    return Aesthetic.get()
        .tabLayoutBgMode()
        .flatMap(
            mode -> {
              switch (mode) {
                case TabLayoutBgMode.PRIMARY:
                  return Aesthetic.get().primaryColor();
                case TabLayoutBgMode.ACCENT:
                  return Aesthetic.get().accentColor();
                default:
                  return Aesthetic.get().windowBgColor();
              }
            })
        .compose(distinctToMainThread());
  }

  private void invalidateBackground() {
    bgColorObservable().take(1).subscribe(this::setBackgroundColor, onErrorLogAndRethrow());
  }

  private void invalidateIcons(int color) {
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

  private void invalidateIndicator() {
    Aesthetic.get()
        .tabLayoutIndicatorMode()
        .take(1)
        .subscribe(
            mode -> {
              switch (mode) {
                case TabLayoutIndicatorMode.PRIMARY:
                  {
                    Aesthetic.get()
                        .primaryColor()
                        .compose(distinctToMainThread())
                        .take(1)
                        .subscribe(
                            color -> {
                              int iconTextColor = isColorLight(color) ? Color.BLACK : Color.WHITE;
                              setTabTextColors(
                                  adjustAlpha(iconTextColor, UNFOCUSED_ALPHA), iconTextColor);
                              setSelectedTabIndicatorColor(color);
                              invalidateIcons(iconTextColor);
                            },
                            onErrorLogAndRethrow());
                    break;
                  }
                case TabLayoutIndicatorMode.ACCENT:
                  {
                    Aesthetic.get()
                        .accentColor()
                        .compose(distinctToMainThread())
                        .take(1)
                        .subscribe(
                            color -> {
                              int iconTextColor = isColorLight(color) ? Color.BLACK : Color.WHITE;
                              setTabTextColors(
                                  adjustAlpha(iconTextColor, UNFOCUSED_ALPHA), iconTextColor);
                              setSelectedTabIndicatorColor(color);
                              invalidateIcons(iconTextColor);
                            },
                            onErrorLogAndRethrow());
                    break;
                  }
                default:
                  { // BLACK_WHITE_AUTO
                    bgColorObservable()
                        .take(1)
                        .subscribe(
                            color -> {
                              int iconTextColor = isColorLight(color) ? Color.BLACK : Color.WHITE;
                              setTabTextColors(adjustAlpha(color, UNFOCUSED_ALPHA), iconTextColor);
                              setSelectedTabIndicatorColor(iconTextColor);
                              invalidateIcons(iconTextColor);
                            },
                            onErrorLogAndRethrow());
                    break;
                  }
              }
            },
            onErrorLogAndRethrow());
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subs = new CompositeSubscription();
    subs.add(
        Aesthetic.get()
            .tabLayoutBgMode()
            .compose(distinctToMainThread())
            .subscribe(ignored -> invalidateBackground(), onErrorLogAndRethrow()));
    subs.add(
        Aesthetic.get()
            .tabLayoutIndicatorMode()
            .compose(distinctToMainThread())
            .subscribe(ignored -> invalidateIndicator(), onErrorLogAndRethrow()));
    subs.add(
        Observable.merge(
                Aesthetic.get().primaryColor(),
                Aesthetic.get().accentColor(),
                Aesthetic.get().windowBgColor())
            .compose(distinctToMainThread())
            .subscribe(
                color -> {
                  invalidateBackground();
                  invalidateIndicator();
                },
                onErrorLogAndRethrow()));
  }

  @Override
  protected void onDetachedFromWindow() {
    subs.unsubscribe();
    super.onDetachedFromWindow();
  }
}
