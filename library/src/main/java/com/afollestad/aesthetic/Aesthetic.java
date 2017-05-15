package com.afollestad.aesthetic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.afollestad.aesthetic.views.ActiveInactiveColors;
import com.f2prateek.rx.preferences.RxSharedPreferences;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.Util.isColorLight;
import static com.afollestad.aesthetic.Util.resolveColor;
import static com.afollestad.aesthetic.Util.setLightStatusBarCompat;
import static com.afollestad.aesthetic.Util.setNavBarColorCompat;

/** @author Aidan Follestad (afollestad) */
public class Aesthetic {

  private static final String PREFS_NAME = "[aesthetic-prefs]";
  private static final String KEY_FIRST_TIME = "first_time";
  private static final String KEY_ACTIVITY_THEME = "activity_theme_%s";
  private static final String KEY_IS_DARK = "is_dark";
  private static final String KEY_PRIMARY_COLOR = "primary_color";
  private static final String KEY_ACCENT_COLOR = "accent_color";
  private static final String KEY_PRIMARY_TEXT_COLOR = "primary_text";
  private static final String KEY_SECONDARY_TEXT_COLOR = "secondary_text";
  private static final String KEY_PRIMARY_TEXT_INVERSE_COLOR = "primary_text_inverse";
  private static final String KEY_SECONDARY_TEXT_INVERSE_COLOR = "secondary_text_inverse";
  private static final String KEY_WINDOW_BG_COLOR = "window_bg_color";
  private static final String KEY_STATUS_BAR_COLOR = "status_bar_color_%s";
  private static final String KEY_NAV_BAR_COLOR = "nav_bar_color_%S";
  private static final String KEY_LIGHT_STATUS_MODE = "light_status_mode";
  private static final String KEY_TAB_LAYOUT_BG_MODE = "tab_layout_bg_mode";
  private static final String KEY_TAB_LAYOUT_INDICATOR_MODE = "tab_layout_indicator_mode";
  private static final String KEY_NAV_VIEW_MODE = "nav_view_mode";
  private static final String KEY_BOTTOM_NAV_BG_MODE = "bottom_nav_bg_mode";
  private static final String KEY_BOTTOM_NAV_ICONTEXT_MODE = "bottom_nav_icontext_mode";
  private static final String KEY_CARD_VIEW_BG_COLOR = "card_view_bg_color";
  private static final String KEY_ICON_TITLE_ACTIVE_COLOR = "icon_title_active_color";
  private static final String KEY_ICON_TITLE_INACTIVE_COLOR = "icon_title_inactive_color";

  @SuppressLint("StaticFieldLeak")
  private static Aesthetic instance;

  private AppCompatActivity context;
  private SharedPreferences prefs;
  private SharedPreferences.Editor editor;
  private RxSharedPreferences rxPrefs;
  private CompositeSubscription subs;
  private int lastActivityTheme;

  @SuppressLint("CommitPrefEdits")
  private Aesthetic(AppCompatActivity context) {
    this.context = context;
    prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    editor = prefs.edit();
    rxPrefs = RxSharedPreferences.create(prefs);
  }

  private static String key(@Nullable AppCompatActivity activity) {
    String key;
    if (activity instanceof AestheticKeyProvider) {
      key = ((AestheticKeyProvider) activity).key();
    } else {
      key = "default";
    }
    if (key == null) {
      key = "default";
    }
    return key;
  }

  /** Should be called before super.onCreate() in each Activity. */
  @NonNull
  public static Aesthetic attach(@NonNull AppCompatActivity activity) {
    if (instance == null) {
      instance = new Aesthetic(activity);
    }
    instance.context = activity;
    LayoutInflater li = activity.getLayoutInflater();
    Util.setInflaterFactory(li, activity);
    String activityThemeKey = String.format(KEY_ACTIVITY_THEME, key(activity));
    instance.lastActivityTheme = instance.prefs.getInt(activityThemeKey, 0);
    if (instance.lastActivityTheme != 0) {
      activity.setTheme(instance.lastActivityTheme);
    }
    return instance;
  }

  @NonNull
  @CheckResult
  public static Aesthetic get() {
    if (instance == null) {
      throw new IllegalStateException("Not attached!");
    }
    return instance;
  }

  /** Should be called in onPause() of each Activity. */
  public static void pause(@NonNull AppCompatActivity activity) {
    if (instance == null) {
      return;
    }
    if (instance.subs != null) {
      instance.subs.unsubscribe();
    }
    if (instance.context != null
        && instance.context.getClass().getName().equals(activity.getClass().getName())) {
      Log.d("Aesthetic", "Pause " + instance.context.getClass().getName());
      instance.context = null;
    }
  }

  /** Should be called in onResume() of each Activity. */
  public static void resume(@NonNull AppCompatActivity activity) {
    if (instance == null) {
      return;
    }
    instance.context = activity;
    if (instance.subs != null) {
      instance.subs.unsubscribe();
    }
    instance.subs = new CompositeSubscription();
    instance.subs.add(
        instance
            .colorPrimary()
            .compose(distinctToMainThread())
            .subscribe(
                color -> Util.setTaskDescriptionColor(instance.context, color),
                onErrorLogAndRethrow()));
    instance.subs.add(
        instance
            .activityTheme()
            .compose(distinctToMainThread())
            .subscribe(
                themeId -> {
                  if (instance.lastActivityTheme == themeId) {
                    return;
                  }
                  instance.lastActivityTheme = themeId;
                  instance.context.recreate();
                },
                onErrorLogAndRethrow()));
    instance.subs.add(
        instance
            .colorStatusBar()
            .compose(distinctToMainThread())
            .subscribe(color -> instance.invalidateStatusBar(), onErrorLogAndRethrow()));
    instance.subs.add(
        instance
            .colorNavigationBar()
            .compose(distinctToMainThread())
            .subscribe(
                color -> setNavBarColorCompat(instance.context, color), onErrorLogAndRethrow()));
    instance.subs.add(
        instance
            .colorWindowBackground()
            .compose(distinctToMainThread())
            .subscribe(
                color ->
                    instance.context.getWindow().setBackgroundDrawable(new ColorDrawable(color)),
                onErrorLogAndRethrow()));
    instance.subs.add(
        instance
            .lightStatusBarMode()
            .compose(distinctToMainThread())
            .subscribe(color -> instance.invalidateStatusBar(), onErrorLogAndRethrow()));
  }

  public static boolean isFirstTime() {
    boolean firstTime = instance.prefs.getBoolean(KEY_FIRST_TIME, true);
    instance.editor.putBoolean(KEY_FIRST_TIME, false).commit();
    return firstTime;
  }

  //
  /////// GETTERS AND SETTERS OF THEME PROPERTIES
  //

  private void invalidateStatusBar() {
    String key = String.format(KEY_STATUS_BAR_COLOR, key(context));
    final int color = prefs.getInt(key, resolveColor(context, R.attr.colorPrimaryDark));

    ViewGroup rootView = Util.getRootView(context);
    if (rootView instanceof DrawerLayout) {
      // Color is set to DrawerLayout, Activity gets transparent status bar
      setLightStatusBarCompat(context, false);
      Util.setStatusBarColorCompat(
          context, ContextCompat.getColor(context, android.R.color.transparent));
      ((DrawerLayout) rootView).setStatusBarBackgroundColor(color);
    } else {
      Util.setStatusBarColorCompat(context, color);
    }

    final int mode = prefs.getInt(KEY_LIGHT_STATUS_MODE, AutoSwitchMode.AUTO);
    switch (mode) {
      case AutoSwitchMode.OFF:
        setLightStatusBarCompat(context, false);
        break;
      case AutoSwitchMode.ON:
        setLightStatusBarCompat(context, true);
        break;
      default:
        setLightStatusBarCompat(context, isColorLight(color));
        break;
    }
  }

  @CheckResult
  public Aesthetic activityTheme(@StyleRes int theme) {
    String key = String.format(KEY_ACTIVITY_THEME, key(context));
    editor.putInt(key, theme);
    return this;
  }

  @CheckResult
  public Observable<Integer> activityTheme() {
    String key = String.format(KEY_ACTIVITY_THEME, key(context));
    return rxPrefs
        .getInteger(key, 0)
        .asObservable()
        .filter(next -> next != 0 && next != lastActivityTheme);
  }

  @CheckResult
  public Aesthetic isDark(boolean isDark) {
    editor.putBoolean(KEY_IS_DARK, isDark);
    return this;
  }

  @CheckResult
  public Observable<Boolean> isDark() {
    return rxPrefs.getBoolean(KEY_IS_DARK, false).asObservable();
  }

  @CheckResult
  public Aesthetic colorPrimary(@ColorInt int color) {
    // needs to be committed immediately so that for statusBarColorAuto() and other auto methods
    editor.putInt(KEY_PRIMARY_COLOR, color).commit();
    return this;
  }

  @CheckResult
  public Aesthetic colorPrimaryRes(@ColorRes int color) {
    return colorPrimary(ContextCompat.getColor(context, color));
  }

  @CheckResult
  public Observable<Integer> colorPrimary() {
    return rxPrefs
        .getInteger(KEY_PRIMARY_COLOR, resolveColor(context, R.attr.colorPrimary))
        .asObservable();
  }

  @CheckResult
  public Aesthetic colorAccent(@ColorInt int color) {
    editor.putInt(KEY_ACCENT_COLOR, color).commit();
    return this;
  }

  @CheckResult
  public Aesthetic colorAccentRes(@ColorRes int color) {
    return colorAccent(ContextCompat.getColor(context, color));
  }

  @CheckResult
  public Observable<Integer> colorAccent() {
    return rxPrefs
        .getInteger(KEY_ACCENT_COLOR, resolveColor(context, R.attr.colorAccent))
        .asObservable();
  }

  @CheckResult
  public Aesthetic textColorPrimary(@ColorInt int color) {
    editor.putInt(KEY_PRIMARY_TEXT_COLOR, color);
    return this;
  }

  @CheckResult
  public Aesthetic textColorPrimaryRes(@ColorRes int color) {
    return textColorPrimary(ContextCompat.getColor(context, color));
  }

  @CheckResult
  public Observable<Integer> textColorPrimary() {
    return rxPrefs
        .getInteger(KEY_PRIMARY_TEXT_COLOR, resolveColor(context, android.R.attr.textColorPrimary))
        .asObservable();
  }

  @CheckResult
  public Aesthetic textColorSecondary(@ColorInt int color) {
    editor.putInt(KEY_SECONDARY_TEXT_COLOR, color);
    return this;
  }

  @CheckResult
  public Aesthetic textColorSecondaryRes(@ColorRes int color) {
    return textColorSecondary(ContextCompat.getColor(context, color));
  }

  @CheckResult
  public Observable<Integer> textColorSecondary() {
    return rxPrefs
        .getInteger(
            KEY_SECONDARY_TEXT_COLOR, resolveColor(context, android.R.attr.textColorSecondary))
        .asObservable();
  }

  @CheckResult
  public Aesthetic textColorPrimaryInverse(@ColorInt int color) {
    editor.putInt(KEY_PRIMARY_TEXT_INVERSE_COLOR, color);
    return this;
  }

  @CheckResult
  public Aesthetic textColorPrimaryInverseRes(@ColorRes int color) {
    return textColorPrimaryInverse(ContextCompat.getColor(context, color));
  }

  @CheckResult
  public Observable<Integer> textColorPrimaryInverse() {
    return rxPrefs
        .getInteger(
            KEY_PRIMARY_TEXT_INVERSE_COLOR,
            resolveColor(context, android.R.attr.textColorPrimaryInverse))
        .asObservable();
  }

  @CheckResult
  public Aesthetic textColorSecondaryInverse(@ColorInt int color) {
    editor.putInt(KEY_SECONDARY_TEXT_INVERSE_COLOR, color);
    return this;
  }

  @CheckResult
  public Aesthetic textColorSecondaryInverseRes(@ColorRes int color) {
    return textColorSecondaryInverse(ContextCompat.getColor(context, color));
  }

  @CheckResult
  public Observable<Integer> textColorSecondaryInverse() {
    return rxPrefs
        .getInteger(
            KEY_SECONDARY_TEXT_INVERSE_COLOR,
            resolveColor(context, android.R.attr.textColorSecondaryInverse))
        .asObservable();
  }

  @CheckResult
  public Aesthetic colorWindowBackground(@ColorInt int color) {
    editor.putInt(KEY_WINDOW_BG_COLOR, color).commit();
    return this;
  }

  @CheckResult
  public Aesthetic colorWindowBackgroundRes(@ColorRes int color) {
    return colorWindowBackground(ContextCompat.getColor(context, color));
  }

  @CheckResult
  public Observable<Integer> colorWindowBackground() {
    return rxPrefs
        .getInteger(KEY_WINDOW_BG_COLOR, resolveColor(context, android.R.attr.windowBackground))
        .asObservable();
  }

  @CheckResult
  public Aesthetic colorStatusBar(@ColorInt int color) {
    String key = String.format(KEY_STATUS_BAR_COLOR, key(context));
    editor.putInt(key, color);
    return this;
  }

  @CheckResult
  public Aesthetic colorStatusBarRes(@ColorRes int color) {
    return colorStatusBar(ContextCompat.getColor(context, color));
  }

  @CheckResult
  public Aesthetic colorStatusBarAuto() {
    String key = String.format(KEY_STATUS_BAR_COLOR, key(context));
    editor.putInt(
        key,
        Util.darkenColor(
            prefs.getInt(KEY_PRIMARY_COLOR, resolveColor(context, R.attr.colorPrimary))));
    return this;
  }

  @CheckResult
  public Observable<Integer> colorStatusBar() {
    String key = String.format(KEY_STATUS_BAR_COLOR, key(context));
    return rxPrefs.getInteger(key, resolveColor(context, R.attr.colorPrimaryDark)).asObservable();
  }

  @CheckResult
  public Aesthetic colorNavigationBar(@ColorInt int color) {
    String key = String.format(KEY_NAV_BAR_COLOR, key(context));
    editor.putInt(key, color);
    return this;
  }

  @CheckResult
  public Aesthetic colorNavigationBarRes(@ColorRes int color) {
    return colorNavigationBar(ContextCompat.getColor(context, color));
  }

  @CheckResult
  public Aesthetic colorNavigationBarAuto() {
    int color = prefs.getInt(KEY_PRIMARY_COLOR, resolveColor(context, R.attr.colorPrimary));
    String key = String.format(KEY_NAV_BAR_COLOR, key(context));
    editor.putInt(key, isColorLight(color) ? Color.BLACK : color);
    return this;
  }

  @CheckResult
  public Observable<Integer> colorNavigationBar() {
    String key = String.format(KEY_NAV_BAR_COLOR, key(context));
    return rxPrefs.getInteger(key, Color.BLACK).asObservable();
  }

  @CheckResult
  public Aesthetic lightStatusBarMode(@AutoSwitchMode int mode) {
    editor.putInt(KEY_LIGHT_STATUS_MODE, mode);
    return this;
  }

  @CheckResult
  public Observable<Integer> lightStatusBarMode() {
    return rxPrefs.getInteger(KEY_LIGHT_STATUS_MODE, AutoSwitchMode.AUTO).asObservable();
  }

  @CheckResult
  public Aesthetic tabLayoutIndicatorMode(@TabLayoutIndicatorMode int mode) {
    editor.putInt(KEY_TAB_LAYOUT_INDICATOR_MODE, mode).commit();
    return this;
  }

  @CheckResult
  public Observable<Integer> tabLayoutIndicatorMode() {
    return rxPrefs
        .getInteger(KEY_TAB_LAYOUT_INDICATOR_MODE, TabLayoutIndicatorMode.ACCENT)
        .asObservable();
  }

  @CheckResult
  public Aesthetic tabLayoutBackgroundMode(@TabLayoutBgMode int mode) {
    editor.putInt(KEY_TAB_LAYOUT_BG_MODE, mode).commit();
    return this;
  }

  @CheckResult
  public Observable<Integer> tabLayoutBackgroundMode() {
    return rxPrefs.getInteger(KEY_TAB_LAYOUT_BG_MODE, TabLayoutBgMode.PRIMARY).asObservable();
  }

  @CheckResult
  public Aesthetic navigationViewMode(@NavigationViewMode int mode) {
    editor.putInt(KEY_NAV_VIEW_MODE, mode).commit();
    return this;
  }

  @CheckResult
  public Observable<Integer> navigationViewMode() {
    return rxPrefs
        .getInteger(KEY_NAV_VIEW_MODE, NavigationViewMode.SELECTED_PRIMARY)
        .asObservable();
  }

  @CheckResult
  public Aesthetic bottomNavigationBackgroundMode(@BottomNavBgMode int mode) {
    editor.putInt(KEY_BOTTOM_NAV_BG_MODE, mode).commit();
    return this;
  }

  @CheckResult
  public Observable<Integer> bottomNavigationBackgroundMode() {
    return rxPrefs
        .getInteger(KEY_BOTTOM_NAV_BG_MODE, BottomNavBgMode.BLACK_WHITE_AUTO)
        .asObservable();
  }

  @CheckResult
  public Aesthetic bottomNavigationIconTextMode(@BottomNavIconTextMode int mode) {
    editor.putInt(KEY_BOTTOM_NAV_ICONTEXT_MODE, mode).commit();
    return this;
  }

  @CheckResult
  public Observable<Integer> bottomNavigationIconTextMode() {
    return rxPrefs
        .getInteger(KEY_BOTTOM_NAV_ICONTEXT_MODE, BottomNavIconTextMode.SELECTED_ACCENT)
        .asObservable();
  }

  @CheckResult
  public Observable<Integer> colorCardViewBackground() {
    return isDark()
        .take(1)
        .flatMap(
            isDark ->
                rxPrefs
                    .getInteger(
                        KEY_CARD_VIEW_BG_COLOR,
                        ContextCompat.getColor(
                            context,
                            isDark ? R.color.ate_cardview_bg_dark : R.color.ate_cardview_bg_light))
                    .asObservable());
  }

  @CheckResult
  public Aesthetic colorCardViewBackground(@ColorInt int color) {
    editor.putInt(KEY_CARD_VIEW_BG_COLOR, color);
    return this;
  }

  @CheckResult
  public Aesthetic colorCardViewBackgroundRes(@ColorRes int color) {
    return colorCardViewBackground(ContextCompat.getColor(context, color));
  }

  @CheckResult
  public Observable<ActiveInactiveColors> colorIconTitle() {
    return isDark()
        .take(1)
        .flatMap(
            isDark ->
                Observable.zip(
                    rxPrefs
                        .getInteger(
                            KEY_ICON_TITLE_ACTIVE_COLOR,
                            ContextCompat.getColor(
                                context, isDark ? R.color.ate_icon_dark : R.color.ate_icon_light))
                        .asObservable(),
                    rxPrefs
                        .getInteger(
                            KEY_ICON_TITLE_INACTIVE_COLOR,
                            ContextCompat.getColor(
                                context,
                                isDark
                                    ? R.color.ate_icon_dark_inactive
                                    : R.color.ate_icon_light_inactive))
                        .asObservable(),
                    ActiveInactiveColors::create));
  }

  @CheckResult
  public Aesthetic colorIconTitleActive(@ColorInt int color) {
    editor.putInt(KEY_ICON_TITLE_ACTIVE_COLOR, color);
    return this;
  }

  @CheckResult
  public Aesthetic colorIconTitleActiveRes(@ColorRes int color) {
    return colorIconTitleActive(ContextCompat.getColor(context, color));
  }

  @CheckResult
  public Aesthetic colorIconTitleInactive(@ColorInt int color) {
    editor.putInt(KEY_ICON_TITLE_INACTIVE_COLOR, color);
    return this;
  }

  @CheckResult
  public Aesthetic colorIconTitleInactiveRes(@ColorRes int color) {
    return colorIconTitleActive(ContextCompat.getColor(context, color));
  }

  public void apply() {
    editor.commit();
  }
}
