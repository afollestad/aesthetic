@file:Suppress("unused")

package com.afollestad.aesthetic

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.CheckResult
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.StyleRes
import android.support.v4.content.ContextCompat
import android.support.v4.util.Pair
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.afollestad.aesthetic.PrefNames.KEY_ACCENT_COLOR
import com.afollestad.aesthetic.PrefNames.KEY_ACTIVITY_THEME
import com.afollestad.aesthetic.PrefNames.KEY_BOTTOM_NAV_BG_MODE
import com.afollestad.aesthetic.PrefNames.KEY_BOTTOM_NAV_ICONTEXT_MODE
import com.afollestad.aesthetic.PrefNames.KEY_CARD_VIEW_BG_COLOR
import com.afollestad.aesthetic.PrefNames.KEY_FIRST_TIME
import com.afollestad.aesthetic.PrefNames.KEY_ICON_TITLE_ACTIVE_COLOR
import com.afollestad.aesthetic.PrefNames.KEY_ICON_TITLE_INACTIVE_COLOR
import com.afollestad.aesthetic.PrefNames.KEY_IS_DARK
import com.afollestad.aesthetic.PrefNames.KEY_LIGHT_STATUS_MODE
import com.afollestad.aesthetic.PrefNames.KEY_NAV_BAR_COLOR
import com.afollestad.aesthetic.PrefNames.KEY_NAV_VIEW_MODE
import com.afollestad.aesthetic.PrefNames.KEY_PRIMARY_COLOR
import com.afollestad.aesthetic.PrefNames.KEY_PRIMARY_DARK_COLOR
import com.afollestad.aesthetic.PrefNames.KEY_PRIMARY_TEXT_COLOR
import com.afollestad.aesthetic.PrefNames.KEY_PRIMARY_TEXT_INVERSE_COLOR
import com.afollestad.aesthetic.PrefNames.KEY_SECONDARY_TEXT_COLOR
import com.afollestad.aesthetic.PrefNames.KEY_SECONDARY_TEXT_INVERSE_COLOR
import com.afollestad.aesthetic.PrefNames.KEY_SNACKBAR_ACTION_TEXT
import com.afollestad.aesthetic.PrefNames.KEY_SNACKBAR_TEXT
import com.afollestad.aesthetic.PrefNames.KEY_STATUS_BAR_COLOR
import com.afollestad.aesthetic.PrefNames.KEY_SWIPEREFRESH_COLORS
import com.afollestad.aesthetic.PrefNames.KEY_TAB_LAYOUT_BG_MODE
import com.afollestad.aesthetic.PrefNames.KEY_TAB_LAYOUT_INDICATOR_MODE
import com.afollestad.aesthetic.PrefNames.KEY_WINDOW_BG_COLOR
import com.afollestad.aesthetic.PrefNames.PREFS_NAME
import com.afollestad.aesthetic.utils.color
import com.afollestad.aesthetic.utils.colorAttr
import com.afollestad.aesthetic.utils.darkenColor
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.getRootView
import com.afollestad.aesthetic.utils.isColorLight
import com.afollestad.aesthetic.utils.mutableArrayMapOf
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.plusAssign
import com.afollestad.aesthetic.utils.setInflaterFactory
import com.afollestad.aesthetic.utils.setLightStatusBarCompat
import com.afollestad.aesthetic.utils.setNavBarColorCompat
import com.afollestad.aesthetic.utils.setStatusBarColorCompat
import com.afollestad.aesthetic.utils.setTaskDescriptionColor
import com.afollestad.aesthetic.utils.splitToInts
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.f2prateek.rx.preferences2.RxSharedPreferences
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import java.lang.String.format

/** @author Aidan Follestad (afollestad) */
class Aesthetic private constructor(private var ctxt: AppCompatActivity?) {

  private val lastActivityThemes = mutableArrayMapOf<String, Int>(2)

  private var subs: CompositeDisposable? = null
  private var prefs: SharedPreferences? = null
  private var editor: SharedPreferences.Editor? = null
  private var rxPrefs: RxSharedPreferences? = null
  private var isResumed: Boolean = false

  init {
    initPrefs()
  }

  @SuppressLint("CommitPrefEdits")
  private fun initPrefs() {
    prefs = context
        .applicationContext
        .getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    editor = prefs!!.edit()
    rxPrefs = RxSharedPreferences.create(prefs!!)
  }

  private fun deinitPrefs() {
    prefs = null
    editor = null
    rxPrefs = null
  }

  internal val context
    @CheckResult
    get() = ctxt!!

  val isDark: Observable<Boolean>
    @CheckResult
    get() = rxPrefs!!.getBoolean(KEY_IS_DARK, false).asObservable()

  internal fun addBackgroundSubscriber(
    view: View,
    colorObservable: Observable<Int>
  ) {
    colorObservable
        .distinctToMainThread()
        .subscribeWith(ViewBackgroundSubscriber(view))
        .unsubscribeOnDetach(view)
  }

  @CheckResult
  fun activityTheme(@StyleRes theme: Int): Aesthetic {
    val key = format(KEY_ACTIVITY_THEME, key(context))
    editor!!.putInt(key, theme)
    return this
  }

  @CheckResult
  fun activityTheme(): Observable<Int> {
    val key = format(KEY_ACTIVITY_THEME, key(context))
    return rxPrefs!!
        .getInteger(key, 0)
        .asObservable()
        .filter { it != 0 && it != getLastActivityTheme(context) }
  }

  @CheckResult
  fun isDark(isDark: Boolean): Aesthetic {
    editor!!.putBoolean(KEY_IS_DARK, isDark)
        .commit()
    return this
  }

  @CheckResult
  fun colorPrimary(@ColorInt color: Int): Aesthetic {
    // needs to be committed immediately so that for statusBarColorAuto() and other auto methods
    editor!!.putInt(KEY_PRIMARY_COLOR, color)
        .commit()
    return this
  }

  @CheckResult
  fun colorPrimaryRes(@ColorRes color: Int): Aesthetic {
    return colorPrimary(context.color(color))
  }

  @CheckResult
  fun colorPrimary(): Observable<Int> {
    return rxPrefs!!
        .getInteger(KEY_PRIMARY_COLOR, context.colorAttr(R.attr.colorPrimary))
        .asObservable()
  }

  @CheckResult
  fun colorPrimaryDark(@ColorInt color: Int): Aesthetic {
    // needs to be committed immediately so that for statusBarColorAuto() and other auto methods
    editor!!.putInt(KEY_PRIMARY_DARK_COLOR, color)
        .commit()
    return this
  }

  @CheckResult
  fun colorPrimaryDarkRes(@ColorRes color: Int): Aesthetic {
    return colorPrimaryDark(context.color(color))
  }

  @CheckResult
  fun colorPrimaryDark(): Observable<Int> {
    return rxPrefs!!
        .getInteger(
            KEY_PRIMARY_DARK_COLOR, context.colorAttr(R.attr.colorPrimaryDark)
        )
        .asObservable()
  }

  @CheckResult
  fun colorAccent(@ColorInt color: Int): Aesthetic {
    editor!!.putInt(KEY_ACCENT_COLOR, color)
        .commit()
    return this
  }

  @CheckResult
  fun colorAccentRes(@ColorRes color: Int): Aesthetic {
    return colorAccent(context.color(color))
  }

  @CheckResult
  fun colorAccent(): Observable<Int> {
    return rxPrefs!!
        .getInteger(KEY_ACCENT_COLOR, context.colorAttr(R.attr.colorAccent))
        .asObservable()
  }

  @CheckResult
  fun textColorPrimary(@ColorInt color: Int): Aesthetic {
    editor!!.putInt(KEY_PRIMARY_TEXT_COLOR, color)
    return this
  }

  @CheckResult
  fun textColorPrimaryRes(@ColorRes color: Int): Aesthetic {
    return textColorPrimary(context.color(color))
  }

  @CheckResult
  fun textColorPrimary(): Observable<Int> {
    return rxPrefs!!
        .getInteger(
            KEY_PRIMARY_TEXT_COLOR,
            context.colorAttr(android.R.attr.textColorPrimary)
        )
        .asObservable()
  }

  @CheckResult
  fun textColorSecondary(@ColorInt color: Int): Aesthetic {
    editor!!.putInt(KEY_SECONDARY_TEXT_COLOR, color)
    return this
  }

  @CheckResult
  fun textColorSecondaryRes(@ColorRes color: Int): Aesthetic {
    return textColorSecondary(context.color(color))
  }

  @CheckResult
  fun textColorSecondary(): Observable<Int> {
    return rxPrefs!!
        .getInteger(
            KEY_SECONDARY_TEXT_COLOR,
            context.colorAttr(android.R.attr.textColorSecondary)
        )
        .asObservable()
  }

  @CheckResult
  fun textColorPrimaryInverse(@ColorInt color: Int): Aesthetic {
    editor!!.putInt(KEY_PRIMARY_TEXT_INVERSE_COLOR, color)
    return this
  }

  @CheckResult
  fun textColorPrimaryInverseRes(@ColorRes color: Int): Aesthetic {
    return textColorPrimaryInverse(context.color(color))
  }

  @CheckResult
  fun textColorPrimaryInverse(): Observable<Int> {
    return rxPrefs!!
        .getInteger(
            KEY_PRIMARY_TEXT_INVERSE_COLOR,
            context.colorAttr(android.R.attr.textColorPrimaryInverse)
        )
        .asObservable()
  }

  @CheckResult
  fun textColorSecondaryInverse(@ColorInt color: Int): Aesthetic {
    editor!!.putInt(KEY_SECONDARY_TEXT_INVERSE_COLOR, color)
    return this
  }

  @CheckResult
  fun textColorSecondaryInverseRes(@ColorRes color: Int): Aesthetic {
    return textColorSecondaryInverse(context.color(color))
  }

  @CheckResult
  fun textColorSecondaryInverse(): Observable<Int> {
    return rxPrefs!!
        .getInteger(
            KEY_SECONDARY_TEXT_INVERSE_COLOR,
            context.colorAttr(android.R.attr.textColorSecondaryInverse)
        )
        .asObservable()
  }

  @CheckResult
  fun colorWindowBackground(@ColorInt color: Int): Aesthetic {
    editor!!.putInt(KEY_WINDOW_BG_COLOR, color)
        .commit()
    return this
  }

  @CheckResult
  fun colorWindowBackgroundRes(@ColorRes color: Int): Aesthetic {
    return colorWindowBackground(context.color(color))
  }

  @CheckResult
  fun colorWindowBackground(): Observable<Int> {
    return rxPrefs!!
        .getInteger(
            KEY_WINDOW_BG_COLOR,
            context.colorAttr(android.R.attr.windowBackground)
        )
        .asObservable()
  }

  @CheckResult
  fun colorStatusBar(@ColorInt color: Int): Aesthetic {
    val key = format(KEY_STATUS_BAR_COLOR, key(context))
    editor!!.putInt(key, color)
    return this
  }

  @CheckResult
  fun colorStatusBarRes(@ColorRes color: Int): Aesthetic {
    return colorStatusBar(context.color(color))
  }

  @CheckResult
  fun colorStatusBarAuto(): Aesthetic {
    val key = format(KEY_STATUS_BAR_COLOR, key(context))
    editor!!.putInt(
        key,
        prefs!!.getInt(
            KEY_PRIMARY_COLOR, context.colorAttr(R.attr.colorPrimary)
        ).darkenColor()
    )
    return this
  }

  @CheckResult
  fun colorStatusBar(): Observable<Int> {
    return colorPrimaryDark().flatMap {
      val key = format(KEY_STATUS_BAR_COLOR, key(context))
      rxPrefs!!.getInteger(key, it)
          .asObservable()
    }
  }

  @CheckResult
  fun colorNavigationBar(@ColorInt color: Int): Aesthetic {
    val key = format(KEY_NAV_BAR_COLOR, key(context))
    editor!!.putInt(key, color)
    return this
  }

  @CheckResult
  fun colorNavigationBarRes(@ColorRes color: Int): Aesthetic {
    return colorNavigationBar(context.color(color))
  }

  @CheckResult
  fun colorNavigationBarAuto(): Aesthetic {
    val color =
      prefs!!.getInt(KEY_PRIMARY_COLOR, context.colorAttr(R.attr.colorPrimary))
    val key = format(KEY_NAV_BAR_COLOR, key(context))
    editor!!.putInt(key, if (color.isColorLight()) Color.BLACK else color)
    return this
  }

  @CheckResult
  fun colorNavigationBar(): Observable<Int> {
    val key = format(KEY_NAV_BAR_COLOR, key(context))
    return rxPrefs!!.getInteger(key, Color.BLACK)
        .asObservable()
  }

  @CheckResult
  fun lightStatusBarMode(mode: AutoSwitchMode): Aesthetic {
    editor!!.putInt(KEY_LIGHT_STATUS_MODE, mode.value)
    return this
  }

  @CheckResult
  fun lightStatusBarMode(): Observable<Int> {
    return rxPrefs!!
        .getInteger(KEY_LIGHT_STATUS_MODE, AutoSwitchMode.AUTO.value)
        .asObservable()
  }

  @CheckResult
  fun tabLayoutIndicatorMode(mode: TabLayoutIndicatorMode): Aesthetic {
    editor!!.putInt(KEY_TAB_LAYOUT_INDICATOR_MODE, mode.value)
        .commit()
    return this
  }

  @CheckResult
  fun tabLayoutIndicatorMode(): Observable<TabLayoutIndicatorMode> {
    return rxPrefs!!
        .getInteger(
            KEY_TAB_LAYOUT_INDICATOR_MODE, TabLayoutIndicatorMode.ACCENT.value
        )
        .asObservable()
        .map { TabLayoutIndicatorMode.fromInt(it) }
  }

  @CheckResult
  fun tabLayoutBackgroundMode(mode: TabLayoutBgMode): Aesthetic {
    editor!!.putInt(KEY_TAB_LAYOUT_BG_MODE, mode.value)
        .commit()
    return this
  }

  @CheckResult
  fun tabLayoutBackgroundMode(): Observable<TabLayoutBgMode> {
    return rxPrefs!!
        .getInteger(KEY_TAB_LAYOUT_BG_MODE, TabLayoutBgMode.PRIMARY.value)
        .asObservable()
        .map { TabLayoutBgMode.fromInt(it) }
  }

  @CheckResult
  fun navigationViewMode(mode: NavigationViewMode): Aesthetic {
    editor!!.putInt(KEY_NAV_VIEW_MODE, mode.value)
        .commit()
    return this
  }

  @CheckResult
  fun navigationViewMode(): Observable<NavigationViewMode> {
    return rxPrefs!!
        .getInteger(KEY_NAV_VIEW_MODE, NavigationViewMode.SELECTED_PRIMARY.value)
        .asObservable()
        .map { NavigationViewMode.fromInt(it) }
  }

  @CheckResult
  fun bottomNavigationBackgroundMode(mode: BottomNavBgMode): Aesthetic {
    editor!!.putInt(KEY_BOTTOM_NAV_BG_MODE, mode.value)
        .commit()
    return this
  }

  @CheckResult
  fun bottomNavigationBackgroundMode(): Observable<BottomNavBgMode> {
    return rxPrefs!!
        .getInteger(KEY_BOTTOM_NAV_BG_MODE, BottomNavBgMode.BLACK_WHITE_AUTO.value)
        .asObservable()
        .map { BottomNavBgMode.fromInt(it) }
  }

  @CheckResult
  fun bottomNavigationIconTextMode(mode: BottomNavIconTextMode): Aesthetic {
    editor!!.putInt(KEY_BOTTOM_NAV_ICONTEXT_MODE, mode.value)
        .commit()
    return this
  }

  @CheckResult
  fun bottomNavigationIconTextMode(): Observable<BottomNavIconTextMode> {
    return rxPrefs!!
        .getInteger(
            KEY_BOTTOM_NAV_ICONTEXT_MODE,
            BottomNavIconTextMode.SELECTED_ACCENT.value
        )
        .asObservable()
        .map { BottomNavIconTextMode.fromInt(it) }
  }

  @CheckResult
  fun colorCardViewBackground(): Observable<Int> {
    return isDark.flatMap {
      rxPrefs!!
          .getInteger(
              KEY_CARD_VIEW_BG_COLOR,
              ContextCompat.getColor(
                  context,
                  if (it)
                    R.color.ate_cardview_bg_dark
                  else
                    R.color.ate_cardview_bg_light
              )
          )
          .asObservable()
    }
  }

  @CheckResult
  fun colorCardViewBackground(@ColorInt color: Int): Aesthetic {
    editor!!.putInt(KEY_CARD_VIEW_BG_COLOR, color)
    return this
  }

  @CheckResult
  fun colorCardViewBackgroundRes(@ColorRes color: Int): Aesthetic {
    return colorCardViewBackground(context.color(color))
  }

  @CheckResult
  fun colorIconTitle(
    requestedBackgroundObservable: Observable<Int>?
  ): Observable<ActiveInactiveColors> {
    var backgroundObservable = requestedBackgroundObservable
    if (backgroundObservable == null) {
      backgroundObservable = Aesthetic.get()
          .colorPrimary()
    }
    return backgroundObservable.flatMap {
      val isDark = !it.isColorLight()
      Observable.zip(
          rxPrefs!!
              .getInteger(
                  KEY_ICON_TITLE_ACTIVE_COLOR,
                  ContextCompat.getColor(
                      context, if (isDark) R.color.ate_icon_dark else R.color.ate_icon_light
                  )
              )
              .asObservable(),
          rxPrefs!!
              .getInteger(
                  KEY_ICON_TITLE_INACTIVE_COLOR,
                  ContextCompat.getColor(
                      context,
                      if (isDark)
                        R.color.ate_icon_dark_inactive
                      else
                        R.color.ate_icon_light_inactive
                  )
              )
              .asObservable(),
          BiFunction<Int, Int, ActiveInactiveColors> { active, inactive ->
            ActiveInactiveColors(
                active, inactive
            )
          })
    }
  }

  @CheckResult
  fun colorIconTitleActive(@ColorInt color: Int): Aesthetic {
    editor!!.putInt(KEY_ICON_TITLE_ACTIVE_COLOR, color)
    return this
  }

  @CheckResult
  fun colorIconTitleActiveRes(@ColorRes color: Int): Aesthetic {
    return colorIconTitleActive(context.color(color))
  }

  @CheckResult
  fun colorIconTitleInactive(@ColorInt color: Int): Aesthetic {
    editor!!.putInt(KEY_ICON_TITLE_INACTIVE_COLOR, color)
    return this
  }

  @CheckResult
  fun colorIconTitleInactiveRes(@ColorRes color: Int): Aesthetic {
    return colorIconTitleActive(context.color(color))
  }

  @CheckResult
  fun snackbarTextColor(): Observable<Int> {
    return isDark
        .flatMap { isDark ->
          if (isDark) {
            textColorPrimary()
          } else {
            textColorPrimaryInverse()
                .flatMap { defaultTextColor ->
                  rxPrefs!!.getInteger(
                      KEY_SNACKBAR_TEXT, defaultTextColor
                  )
                      .asObservable()
                }
          }
        }
  }

  @CheckResult
  fun snackbarTextColor(@ColorInt color: Int): Aesthetic {
    editor!!.putInt(KEY_SNACKBAR_TEXT, color)
    return this
  }

  @CheckResult
  fun snackbarTextColorRes(@ColorRes color: Int): Aesthetic {
    return colorCardViewBackground(context.color(color))
  }

  @CheckResult
  fun snackbarActionTextColor(): Observable<Int> {
    return colorAccent()
        .flatMap {
          rxPrefs!!
              .getInteger(KEY_SNACKBAR_ACTION_TEXT, it)
              .asObservable()
        }
  }

  @CheckResult
  fun snackbarActionTextColor(@ColorInt color: Int): Aesthetic {
    editor!!.putInt(KEY_SNACKBAR_ACTION_TEXT, color)
    return this
  }

  @CheckResult
  fun snackbarActionTextColorRes(@ColorRes color: Int): Aesthetic {
    return colorCardViewBackground(context.color(color))
  }

  @CheckResult
  fun swipeRefreshLayoutColors(): Observable<IntArray> {
    return colorAccent()
        .flatMap {
          rxPrefs!!
              .getString(KEY_SWIPEREFRESH_COLORS, "$it")
              .asObservable()
              .map { it.splitToInts() }
        }
  }

  @CheckResult
  fun swipeRefreshLayoutColors(@ColorInt vararg colors: Int): Aesthetic {
    editor!!.putString(KEY_SWIPEREFRESH_COLORS, colors.joinToString(","))
    return this
  }

  @CheckResult
  fun swipeRefreshLayoutColorsRes(@ColorRes vararg colorsRes: Int): Aesthetic {
    editor!!.putString(
        KEY_SWIPEREFRESH_COLORS, colorsRes.map { context.color(it) }.joinToString(",")
    )
    return this
  }

  /** Notifies all listening views that theme properties have been updated.  */
  fun apply() {
    editor!!.commit()
  }

  private fun invalidateStatusBar() {
    val key = format(KEY_STATUS_BAR_COLOR, key(context))
    val color = prefs!!.getInt(key, context.colorAttr(R.attr.colorPrimaryDark))

    val rootView = context.getRootView()
    if (rootView is DrawerLayout) {
      // Color is set to DrawerLayout, Activity gets transparent status bar
      context.setLightStatusBarCompat(false)
      context.setStatusBarColorCompat(Color.TRANSPARENT)
      rootView.setStatusBarBackgroundColor(color)
    } else {
      context.setStatusBarColorCompat(color)
    }

    val mode = AutoSwitchMode.fromInt(
        prefs!!.getInt(KEY_LIGHT_STATUS_MODE, AutoSwitchMode.AUTO.value)
    )
    when (mode) {
      AutoSwitchMode.OFF -> context.setLightStatusBarCompat(false)
      AutoSwitchMode.ON -> context.setLightStatusBarCompat(true)
      else -> context.setLightStatusBarCompat(color.isColorLight())
    }
  }

  companion object {

    @SuppressLint("StaticFieldLeak")
    private var instance: Aesthetic? = null

    private fun key(activity: AppCompatActivity?): String {
      var key: String?
      key = if (activity is AestheticKeyProvider) {
        (activity as AestheticKeyProvider).key()
      } else {
        "default"
      }
      if (key == null) {
        key = "default"
      }
      return key
    }

    /** Should be called before super.onCreate() in each Activity.  */
    fun attach(activity: AppCompatActivity): Aesthetic {
      if (instance == null) {
        instance = Aesthetic(activity)
      }
      with(instance!!) {
        isResumed = false
        ctxt = activity
        initPrefs()

        val li = activity.layoutInflater
        activity.setInflaterFactory(li)

        val activityThemeKey = format(KEY_ACTIVITY_THEME, key(activity))
        val latestActivityTheme = prefs!!.getInt(activityThemeKey, 0)
        lastActivityThemes[context.javaClass.name] = latestActivityTheme
        if (latestActivityTheme != 0) {
          activity.setTheme(latestActivityTheme)
        }

        return this
      }
    }

    private fun getLastActivityTheme(forContext: Context?): Int {
      return if (forContext == null || instance == null) {
        0
      } else instance!!.lastActivityThemes[forContext.javaClass.name] ?: return 0
    }

    @CheckResult
    fun get() = instance ?: throw IllegalStateException("Not attached")

    inline fun config(func: Aesthetic.() -> Unit) {
      val instance = get()
      instance.func()
      instance.apply()
    }

    /** Should be called in onPause() of each Activity.  */
    fun pause(activity: AppCompatActivity) {
      with(instance ?: return) {
        isResumed = false
        subs?.clear()
        if (activity.isFinishing) {
          if (context.javaClass.name == activity.javaClass.name) {
            ctxt = null
            deinitPrefs()
          }
        }
      }
    }

    /** Should be called in onResume() of each Activity.  */
    fun resume(activity: AppCompatActivity) {
      with(instance ?: throw IllegalStateException("Not attached")) {
        if (isResumed)
          throw IllegalStateException("Already resumed!")

        ctxt = activity
        initPrefs()
        isResumed = true

        subs = CompositeDisposable()
        subs!! +=
            instance!!
                .colorPrimary()
                .distinctToMainThread()
                .subscribe(
                    Consumer { context.setTaskDescriptionColor(it) },
                    onErrorLogAndRethrow()
                )
        subs!! +=
            instance!!
                .activityTheme()
                .distinctToMainThread()
                .filter { getLastActivityTheme(context) != it }
                .subscribe(
                    Consumer {
                      lastActivityThemes[context.javaClass.name] = it
                      context.recreate()
                    },
                    onErrorLogAndRethrow()
                )

        subs!! +=
            Observable.combineLatest<Int, Int, Pair<Int, Int>>(
                colorStatusBar(), lightStatusBarMode(),
                BiFunction<Int, Int, Pair<Int, Int>> { a, b -> Pair.create(a, b) })
                .distinctToMainThread()
                .subscribe(
                    Consumer { invalidateStatusBar() },
                    onErrorLogAndRethrow()
                )
        subs!! +=
            instance!!
                .colorNavigationBar()
                .distinctToMainThread()
                .subscribe(
                    Consumer { context.setNavBarColorCompat(it) },
                    onErrorLogAndRethrow()
                )
        subs!! +=
            instance!!
                .colorWindowBackground()
                .distinctToMainThread()
                .subscribe(
                    Consumer {
                      context.window?.setBackgroundDrawable(ColorDrawable(it))
                    },
                    onErrorLogAndRethrow()
                )
      }
    }

    /** Returns true if this field has never been accessed before.  */
    val isFirstTime: Boolean
      get() {
        with(instance ?: throw IllegalStateException("Not attached")) {
          val firstTime = prefs!!.getBoolean(KEY_FIRST_TIME, true)
          editor!!.putBoolean(KEY_FIRST_TIME, false)
              .apply()
          return firstTime
        }
      }
  }
}
