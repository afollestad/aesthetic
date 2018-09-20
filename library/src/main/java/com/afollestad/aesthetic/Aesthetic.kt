/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
@file:Suppress("unused")

package com.afollestad.aesthetic

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.CheckResult
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.StyleRes
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.afollestad.aesthetic.internal.KEY_ACCENT_COLOR
import com.afollestad.aesthetic.internal.KEY_ACTIVITY_THEME
import com.afollestad.aesthetic.internal.KEY_BOTTOM_NAV_BG_MODE
import com.afollestad.aesthetic.internal.KEY_BOTTOM_NAV_ICONTEXT_MODE
import com.afollestad.aesthetic.internal.KEY_CARD_VIEW_BG_COLOR
import com.afollestad.aesthetic.internal.KEY_FIRST_TIME
import com.afollestad.aesthetic.internal.KEY_ICON_TITLE_ACTIVE_COLOR
import com.afollestad.aesthetic.internal.KEY_ICON_TITLE_INACTIVE_COLOR
import com.afollestad.aesthetic.internal.KEY_IS_DARK
import com.afollestad.aesthetic.internal.KEY_LIGHT_STATUS_MODE
import com.afollestad.aesthetic.internal.KEY_NAV_BAR_COLOR
import com.afollestad.aesthetic.internal.KEY_NAV_VIEW_MODE
import com.afollestad.aesthetic.internal.KEY_PRIMARY_COLOR
import com.afollestad.aesthetic.internal.KEY_PRIMARY_DARK_COLOR
import com.afollestad.aesthetic.internal.KEY_PRIMARY_TEXT_COLOR
import com.afollestad.aesthetic.internal.KEY_PRIMARY_TEXT_INVERSE_COLOR
import com.afollestad.aesthetic.internal.KEY_SECONDARY_TEXT_COLOR
import com.afollestad.aesthetic.internal.KEY_SECONDARY_TEXT_INVERSE_COLOR
import com.afollestad.aesthetic.internal.KEY_SNACKBAR_ACTION_TEXT
import com.afollestad.aesthetic.internal.KEY_SNACKBAR_TEXT
import com.afollestad.aesthetic.internal.KEY_STATUS_BAR_COLOR
import com.afollestad.aesthetic.internal.KEY_SWIPEREFRESH_COLORS
import com.afollestad.aesthetic.internal.KEY_TAB_LAYOUT_BG_MODE
import com.afollestad.aesthetic.internal.KEY_TAB_LAYOUT_INDICATOR_MODE
import com.afollestad.aesthetic.internal.KEY_WINDOW_BG_COLOR
import com.afollestad.aesthetic.internal.PREFS_NAME
import com.afollestad.aesthetic.utils.color
import com.afollestad.aesthetic.utils.colorAttr
import com.afollestad.aesthetic.utils.darkenColor
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.getRootView
import com.afollestad.aesthetic.utils.isColorLight
import com.afollestad.aesthetic.utils.mutableArrayMap
import com.afollestad.aesthetic.utils.plusAssign
import com.afollestad.aesthetic.utils.save
import com.afollestad.aesthetic.utils.setInflaterFactory
import com.afollestad.aesthetic.utils.setLightStatusBarCompat
import com.afollestad.aesthetic.utils.setNavBarColorCompat
import com.afollestad.aesthetic.utils.setStatusBarColorCompat
import com.afollestad.aesthetic.utils.setTaskDescriptionColor
import com.afollestad.aesthetic.utils.splitToInts
import com.afollestad.aesthetic.utils.subscribeBackgroundColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.afollestad.rxkprefs.RxkPrefs
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.Observable.zip
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import java.lang.String.format

/** @author Aidan Follestad (afollestad) */
class Aesthetic private constructor(private var ctxt: Context?) {

  private val lastActivityThemes = mutableArrayMap<String, Int>(2)
  private val onAttached = BehaviorSubject.create<Boolean>()

  private var subs: CompositeDisposable? = null
  private var prefs: SharedPreferences? = null
  private var editor: SharedPreferences.Editor? = null
  private var rxkPrefs: RxkPrefs? = null
  private var isResumed: Boolean = false

  init {
    initPrefs()
  }

  @SuppressLint("CommitPrefEdits")
  private fun initPrefs() {
    rxkPrefs = RxkPrefs(context, PREFS_NAME)
    prefs = rxkPrefs!!.getSharedPrefs()
    editor = safePrefs.edit()
    onAttached.onNext(true)
  }

  private fun deInitPrefs() {
    onAttached.onNext(false)
    prefs = null
    editor = null
    rxkPrefs = null
  }

  /**
   * Emits the current reactive shared preferences value if and when the instance is attached to
   * an Activity, when the preferences are actually initialized and populated. Without this,
   * we can get Kotlin null exceptions due the instance being unexpectedly null.
   */
  private fun waitForAttach() = onAttached.filter { it }.map { rxkPrefs!! }

  // The 4 fields below allow us to avoid using !!, and provide indication if we access them
  // before we should.
  internal val context
    @CheckResult
    get() = ctxt ?: throw IllegalStateException("Not attached")
  private val safePrefs
    @CheckResult
    get() = prefs ?: throw IllegalStateException("Not attached")
  private val safePrefsEditor
    @CheckResult
    get() = editor ?: throw IllegalStateException("Not attached")

  val isDark: Observable<Boolean>
    @CheckResult
    get() = waitForAttach().flatMap {
      it.boolean(KEY_IS_DARK)
          .asObservable()
    }

  @CheckResult fun activityTheme(@StyleRes theme: Int): Aesthetic {
    val key = format(KEY_ACTIVITY_THEME, key(context))
    safePrefsEditor.putInt(key, theme)
    return this
  }

  @CheckResult fun activityTheme(): Observable<Int> {
    val key = format(KEY_ACTIVITY_THEME, key(context))
    return waitForAttach().flatMap { rxPrefs ->
      rxPrefs
          .integer(key, 0)
          .asObservable()
          .filter { it != 0 && it != getLastActivityTheme(context) }
    }
  }

  @CheckResult fun isDark(isDark: Boolean): Aesthetic {
    safePrefsEditor.save { putBoolean(KEY_IS_DARK, isDark) }
    return this
  }

  @CheckResult fun colorPrimary(@ColorInt color: Int): Aesthetic {
    // needs to be committed immediately so that for statusBarColorAuto() and other auto methods
    safePrefsEditor.save { putInt(KEY_PRIMARY_COLOR, color) }
    return this
  }

  @CheckResult fun colorPrimaryRes(@ColorRes color: Int): Aesthetic {
    return colorPrimary(context.color(color))
  }

  @CheckResult fun colorPrimary(): Observable<Int> {
    return waitForAttach().flatMap { rxPrefs ->
      rxPrefs
          .integer(
              KEY_PRIMARY_COLOR,
              context.colorAttr(R.attr.colorPrimary)
          )
          .asObservable()
    }
  }

  @CheckResult fun colorPrimaryDark(@ColorInt color: Int): Aesthetic {
    // needs to be committed immediately so that for statusBarColorAuto() and other auto methods
    safePrefsEditor.save { putInt(KEY_PRIMARY_DARK_COLOR, color) }
    return this
  }

  @CheckResult fun colorPrimaryDarkRes(@ColorRes color: Int): Aesthetic {
    return colorPrimaryDark(context.color(color))
  }

  @CheckResult fun colorPrimaryDark(): Observable<Int> {
    return colorPrimary().flatMap { primary ->
      rxkPrefs!!
          .integer(
              KEY_PRIMARY_DARK_COLOR,
              primary.darkenColor()
          )
          .asObservable()
    }
  }

  @CheckResult fun colorAccent(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.save { putInt(KEY_ACCENT_COLOR, color) }
    return this
  }

  @CheckResult fun colorAccentRes(@ColorRes color: Int): Aesthetic {
    return colorAccent(context.color(color))
  }

  @CheckResult fun colorAccent(): Observable<Int> {
    return waitForAttach().flatMap { rxPrefs ->
      rxPrefs
          .integer(
              KEY_ACCENT_COLOR,
              context.colorAttr(R.attr.colorAccent)
          )
          .asObservable()
    }
  }

  @CheckResult fun textColorPrimary(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_PRIMARY_TEXT_COLOR, color)
    return this
  }

  @CheckResult fun textColorPrimaryRes(@ColorRes color: Int): Aesthetic {
    return textColorPrimary(context.color(color))
  }

  @CheckResult fun textColorPrimary(): Observable<Int> {
    return waitForAttach().flatMap { rxPrefs ->
      rxPrefs
          .integer(
              KEY_PRIMARY_TEXT_COLOR,
              context.colorAttr(android.R.attr.textColorPrimary)
          )
          .asObservable()
    }
  }

  @CheckResult fun textColorSecondary(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_SECONDARY_TEXT_COLOR, color)
    return this
  }

  @CheckResult fun textColorSecondaryRes(@ColorRes color: Int): Aesthetic {
    return textColorSecondary(context.color(color))
  }

  @CheckResult fun textColorSecondary(): Observable<Int> {
    return waitForAttach().flatMap { rxPrefs ->
      rxPrefs
          .integer(
              KEY_SECONDARY_TEXT_COLOR,
              context.colorAttr(android.R.attr.textColorSecondary)
          )
          .asObservable()
    }
  }

  @CheckResult fun textColorPrimaryInverse(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_PRIMARY_TEXT_INVERSE_COLOR, color)
    return this
  }

  @CheckResult fun textColorPrimaryInverseRes(@ColorRes color: Int): Aesthetic {
    return textColorPrimaryInverse(context.color(color))
  }

  @CheckResult fun textColorPrimaryInverse(): Observable<Int> {
    return waitForAttach().flatMap { rxPrefs ->
      rxPrefs
          .integer(
              KEY_PRIMARY_TEXT_INVERSE_COLOR,
              context.colorAttr(android.R.attr.textColorPrimaryInverse)
          )
          .asObservable()
    }
  }

  @CheckResult fun textColorSecondaryInverse(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_SECONDARY_TEXT_INVERSE_COLOR, color)
    return this
  }

  @CheckResult fun textColorSecondaryInverseRes(@ColorRes color: Int): Aesthetic {
    return textColorSecondaryInverse(context.color(color))
  }

  @CheckResult fun textColorSecondaryInverse(): Observable<Int> {
    return waitForAttach().flatMap { rxPrefs ->
      rxPrefs
          .integer(
              KEY_SECONDARY_TEXT_INVERSE_COLOR,
              context.colorAttr(android.R.attr.textColorSecondaryInverse)
          )
          .asObservable()
    }
  }

  @CheckResult fun colorWindowBackground(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.save { putInt(KEY_WINDOW_BG_COLOR, color) }
    return this
  }

  @CheckResult fun colorWindowBackgroundRes(@ColorRes color: Int): Aesthetic {
    return colorWindowBackground(context.color(color))
  }

  @CheckResult fun colorWindowBackground(): Observable<Int> {
    return waitForAttach().flatMap { rxPrefs ->
      rxPrefs
          .integer(
              KEY_WINDOW_BG_COLOR,
              context.colorAttr(android.R.attr.windowBackground)
          )
          .asObservable()
    }
  }

  @CheckResult fun colorStatusBar(@ColorInt color: Int): Aesthetic {
    val key = format(KEY_STATUS_BAR_COLOR, key(context))
    safePrefsEditor.putInt(key, color)
    return this
  }

  @CheckResult fun colorStatusBarRes(@ColorRes color: Int): Aesthetic {
    return colorStatusBar(context.color(color))
  }

  @CheckResult fun colorStatusBarAuto(): Aesthetic {
    val key = format(KEY_STATUS_BAR_COLOR, key(context))
    safePrefsEditor.putInt(
        key,
        safePrefs.getInt(
            KEY_PRIMARY_COLOR,
            context.colorAttr(R.attr.colorPrimary)
        ).darkenColor()
    )
    return this
  }

  @CheckResult fun colorStatusBar(): Observable<Int> {
    return colorPrimaryDark().flatMap {
      val key = format(KEY_STATUS_BAR_COLOR, key(context))
      rxkPrefs!!.integer(key, it)
          .asObservable()
    }
  }

  @CheckResult fun colorNavigationBar(@ColorInt color: Int): Aesthetic {
    val key = format(KEY_NAV_BAR_COLOR, key(context))
    safePrefsEditor.putInt(key, color)
    return this
  }

  @CheckResult fun colorNavigationBarRes(@ColorRes color: Int): Aesthetic {
    return colorNavigationBar(context.color(color))
  }

  @CheckResult fun colorNavigationBarAuto(): Aesthetic {
    val color =
      safePrefs.getInt(KEY_PRIMARY_COLOR, context.colorAttr(R.attr.colorPrimary))
    val key = format(KEY_NAV_BAR_COLOR, key(context))
    safePrefsEditor.putInt(
        key,
        if (color.isColorLight()) Color.BLACK else color
    )
    return this
  }

  @CheckResult fun colorNavigationBar(): Observable<Int> {
    val key = format(KEY_NAV_BAR_COLOR, key(context))
    return waitForAttach().flatMap { rxPrefs ->
      rxPrefs.integer(key, Color.BLACK)
          .asObservable()
    }
  }

  @CheckResult fun lightStatusBarMode(mode: AutoSwitchMode): Aesthetic {
    safePrefsEditor.putInt(KEY_LIGHT_STATUS_MODE, mode.value)
    return this
  }

  @CheckResult fun lightStatusBarMode(): Observable<Int> {
    return waitForAttach().flatMap { rxPrefs ->
      rxPrefs
          .integer(
              KEY_LIGHT_STATUS_MODE,
              AutoSwitchMode.AUTO.value
          )
          .asObservable()
    }
  }

  @CheckResult fun tabLayoutIndicatorMode(mode: TabLayoutIndicatorMode): Aesthetic {
    safePrefsEditor.save { putInt(KEY_TAB_LAYOUT_INDICATOR_MODE, mode.value) }
    return this
  }

  @CheckResult fun tabLayoutIndicatorMode(): Observable<TabLayoutIndicatorMode> {
    return waitForAttach().flatMap { rxPrefs ->
      rxPrefs
          .integer(
              KEY_TAB_LAYOUT_INDICATOR_MODE,
              TabLayoutIndicatorMode.ACCENT.value
          )
          .asObservable()
          .map { TabLayoutIndicatorMode.fromInt(it) }
    }
  }

  @CheckResult fun tabLayoutBackgroundMode(mode: TabLayoutBgMode): Aesthetic {
    safePrefsEditor.save { putInt(KEY_TAB_LAYOUT_BG_MODE, mode.value) }
    return this
  }

  @CheckResult fun tabLayoutBackgroundMode(): Observable<TabLayoutBgMode> {
    return waitForAttach().flatMap { rxPrefs ->
      rxPrefs
          .integer(
              KEY_TAB_LAYOUT_BG_MODE,
              TabLayoutBgMode.PRIMARY.value
          )
          .asObservable()
          .map { TabLayoutBgMode.fromInt(it) }
    }
  }

  @CheckResult fun navigationViewMode(mode: NavigationViewMode): Aesthetic {
    safePrefsEditor.save { putInt(KEY_NAV_VIEW_MODE, mode.value) }
    return this
  }

  @CheckResult fun navigationViewMode(): Observable<NavigationViewMode> {
    return waitForAttach().flatMap { rxPrefs ->
      rxPrefs
          .integer(
              KEY_NAV_VIEW_MODE,
              NavigationViewMode.SELECTED_PRIMARY.value
          )
          .asObservable()
          .map { NavigationViewMode.fromInt(it) }
    }
  }

  @CheckResult fun bottomNavigationBackgroundMode(mode: BottomNavBgMode): Aesthetic {
    safePrefsEditor.save { putInt(KEY_BOTTOM_NAV_BG_MODE, mode.value) }
    return this
  }

  @CheckResult fun bottomNavigationBackgroundMode(): Observable<BottomNavBgMode> {
    return waitForAttach().flatMap { rxPrefs ->
      rxPrefs
          .integer(
              KEY_BOTTOM_NAV_BG_MODE,
              BottomNavBgMode.BLACK_WHITE_AUTO.value
          )
          .asObservable()
          .map { BottomNavBgMode.fromInt(it) }
    }
  }

  @CheckResult fun bottomNavigationIconTextMode(mode: BottomNavIconTextMode): Aesthetic {
    safePrefsEditor.save { putInt(KEY_BOTTOM_NAV_ICONTEXT_MODE, mode.value) }
    return this
  }

  @CheckResult fun bottomNavigationIconTextMode(): Observable<BottomNavIconTextMode> {
    return waitForAttach().flatMap { rxPrefs ->
      rxPrefs
          .integer(
              KEY_BOTTOM_NAV_ICONTEXT_MODE,
              BottomNavIconTextMode.SELECTED_ACCENT.value
          )
          .asObservable()
          .map { BottomNavIconTextMode.fromInt(it) }
    }
  }

  @CheckResult fun colorCardViewBackground(): Observable<Int> {
    return isDark.flatMap { dark ->
      rxkPrefs!!
          .integer(
              KEY_CARD_VIEW_BG_COLOR,
              context.color(
                  if (dark) R.color.ate_cardview_bg_dark
                  else R.color.ate_cardview_bg_light
              )
          )
          .asObservable()
    }
  }

  @CheckResult fun colorCardViewBackground(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_CARD_VIEW_BG_COLOR, color)
    return this
  }

  @CheckResult fun colorCardViewBackgroundRes(@ColorRes color: Int): Aesthetic {
    return colorCardViewBackground(context.color(color))
  }

  @CheckResult fun colorIconTitle(
    requestedBackgroundObservable: Observable<Int>? = null
  ): Observable<ActiveInactiveColors> {
    val backgroundObservable = requestedBackgroundObservable ?: colorPrimary()
    val iconTitleObs = backgroundObservable.flatMap {
      val isDark = !it.isColorLight()
      zip(
          rxkPrefs!!
              .integer(
                  KEY_ICON_TITLE_ACTIVE_COLOR,
                  context.color(
                      if (isDark) R.color.ate_icon_dark
                      else R.color.ate_icon_light
                  )
              )
              .asObservable(),
          rxkPrefs!!
              .integer(
                  KEY_ICON_TITLE_INACTIVE_COLOR,
                  context.color(
                      if (isDark) R.color.ate_icon_dark_inactive
                      else R.color.ate_icon_light_inactive
                  )
              )
              .asObservable(),
          BiFunction<Int, Int, ActiveInactiveColors> { active, inactive ->
            ActiveInactiveColors(
                active, inactive
            )
          })
    }

    return waitForAttach().flatMap { iconTitleObs }
  }

  @CheckResult fun colorIconTitleActive(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_ICON_TITLE_ACTIVE_COLOR, color)
    return this
  }

  @CheckResult fun colorIconTitleActiveRes(@ColorRes color: Int): Aesthetic {
    return colorIconTitleActive(context.color(color))
  }

  @CheckResult fun colorIconTitleInactive(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_ICON_TITLE_INACTIVE_COLOR, color)
    return this
  }

  @CheckResult fun colorIconTitleInactiveRes(@ColorRes color: Int): Aesthetic {
    return colorIconTitleActive(context.color(color))
  }

  @CheckResult fun snackbarTextColor(): Observable<Int> {
    return isDark.flatMap { isDark ->
      if (isDark) {
        textColorPrimary()
      } else {
        textColorPrimaryInverse()
            .flatMap { defaultTextColor ->
              rxkPrefs!!.integer(
                  KEY_SNACKBAR_TEXT,
                  defaultTextColor
              )
                  .asObservable()
            }
      }
    }
  }

  @CheckResult fun snackbarTextColor(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_SNACKBAR_TEXT, color)
    return this
  }

  @CheckResult fun snackbarTextColorRes(@ColorRes color: Int): Aesthetic {
    return colorCardViewBackground(context.color(color))
  }

  @CheckResult fun snackbarActionTextColor(): Observable<Int> {
    return colorAccent().flatMap {
      rxkPrefs!!
          .integer(KEY_SNACKBAR_ACTION_TEXT, it)
          .asObservable()
    }
  }

  @CheckResult fun snackbarActionTextColor(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_SNACKBAR_ACTION_TEXT, color)
    return this
  }

  @CheckResult fun snackbarActionTextColorRes(@ColorRes color: Int): Aesthetic {
    return colorCardViewBackground(context.color(color))
  }

  @CheckResult fun swipeRefreshLayoutColors(): Observable<IntArray> {
    return colorAccent().flatMap { accent ->
      rxkPrefs!!
          .string(
              KEY_SWIPEREFRESH_COLORS,
              "$accent"
          )
          .asObservable()
          .map { it.splitToInts() }
    }
  }

  @CheckResult fun swipeRefreshLayoutColors(@ColorInt vararg colors: Int): Aesthetic {
    safePrefsEditor.putString(KEY_SWIPEREFRESH_COLORS, colors.joinToString(","))
    return this
  }

  @CheckResult fun swipeRefreshLayoutColorsRes(@ColorRes vararg colorsRes: Int): Aesthetic {
    safePrefsEditor.putString(
        KEY_SWIPEREFRESH_COLORS,
        colorsRes.map { context.color(it) }.joinToString(",")
    )
    return this
  }

  /** Notifies all listening views that theme properties have been updated.  */
  fun apply() {
    safePrefsEditor.apply()
  }

  internal fun addBackgroundSubscriber(
    view: View,
    colorObservable: Observable<Int>
  ) {
    colorObservable
        .distinctToMainThread()
        .subscribeBackgroundColor(view)
        .unsubscribeOnDetach(view)
  }

  private fun invalidateStatusBar() {
    with(context as? Activity ?: return) {
      val key = format(KEY_STATUS_BAR_COLOR, key(context))
      val color = safePrefs.getInt(key, context.colorAttr(R.attr.colorPrimaryDark))

      val rootView = getRootView()
      if (rootView is DrawerLayout) {
        // Color is set to DrawerLayout, Activity gets transparent status bar
        setLightStatusBarCompat(false)
        setStatusBarColorCompat(Color.TRANSPARENT)
        rootView.setStatusBarBackgroundColor(color)
      } else {
        setStatusBarColorCompat(color)
      }

      val mode = AutoSwitchMode.fromInt(
          safePrefs.getInt(
              KEY_LIGHT_STATUS_MODE,
              AutoSwitchMode.AUTO.value
          )
      )
      when (mode) {
        AutoSwitchMode.OFF -> setLightStatusBarCompat(false)
        AutoSwitchMode.ON -> setLightStatusBarCompat(true)
        else -> setLightStatusBarCompat(color.isColorLight())
      }
    }
  }

  companion object {

    @SuppressLint("StaticFieldLeak")
    private var instance: Aesthetic? = null

    /** Should be called before super.onCreate() in each Activity.  */
    fun attach(whereAmI: Context): Aesthetic {
      if (instance == null) {
        instance = Aesthetic(whereAmI)
      }
      with(instance!!) {
        isResumed = false
        ctxt = whereAmI
        initPrefs()

        with(whereAmI as? Activity ?: return this) {
          val li = layoutInflater
          (this as? AppCompatActivity)?.setInflaterFactory(li)

          val activityThemeKey = format(KEY_ACTIVITY_THEME, key(this))
          val latestActivityTheme = safePrefs.getInt(activityThemeKey, 0)
          lastActivityThemes[context.javaClass.name] = latestActivityTheme
          if (latestActivityTheme != 0) {
            setTheme(latestActivityTheme)
          }
        }

        return this
      }
    }

    @CheckResult
    fun get() = instance ?: throw IllegalStateException("Not attached")

    inline fun config(func: Aesthetic.() -> Unit) {
      val instance = get()
      instance.func()
      instance.apply()
    }

    /** Should be called in onPause() of each Activity or Service.  */
    fun pause(whereAmI: Context) {
      with(instance ?: return) {
        isResumed = false
        subs?.clear()
        if (whereAmI is Activity && whereAmI.isFinishing &&
            context.javaClass.name == whereAmI.javaClass.name
        ) {
          ctxt = null
          deInitPrefs()
        }
      }
    }

    /** Should be called in onResume() of each Activity.  */
    fun resume(whereAmI: Context) {
      with(instance ?: throw IllegalStateException("Not attached")) {
        if (isResumed)
          throw IllegalStateException("Already resumed")

        ctxt = whereAmI
        initPrefs()
        isResumed = true

        subs = CompositeDisposable()
        if (context is Activity) {
          subs += colorPrimary()
              .distinctToMainThread()
              .subscribeTo {
                (context as Activity).setTaskDescriptionColor(it)
              }
          subs += activityTheme()
              .distinctToMainThread()
              .subscribeTo {
                lastActivityThemes[context.javaClass.name] = it
                (context as Activity).recreate()
              }
          subs += combineLatest<Int, Int, Pair<Int, Int>>(
              colorStatusBar(), lightStatusBarMode(),
              BiFunction<Int, Int, Pair<Int, Int>> { a, b -> Pair(a, b) })
              .distinctToMainThread()
              .subscribeTo { invalidateStatusBar() }
          subs += colorNavigationBar()
              .distinctToMainThread()
              .subscribeTo {
                (context as Activity).setNavBarColorCompat(it)
              }
          subs += colorWindowBackground()
              .distinctToMainThread()
              .subscribeTo {
                (context as Activity).window?.setBackgroundDrawable(ColorDrawable(it))
              }
        }
      }
    }

    /** Returns true if this field has never been accessed before.  */
    val isFirstTime: Boolean
      get() {
        with(instance ?: throw IllegalStateException("Not attached")) {
          val firstTime = safePrefs.getBoolean(KEY_FIRST_TIME, true)
          safePrefsEditor.save { putBoolean(KEY_FIRST_TIME, false) }
          return firstTime
        }
      }

    private fun getLastActivityTheme(forContext: Context?): Int {
      return instance?.lastActivityThemes?.get(forContext?.javaClass?.name ?: "") ?: return 0
    }

    private fun key(whereAmI: Context?): String {
      return (whereAmI as? AestheticKeyProvider)?.key() ?: "default"
    }
  }
}
