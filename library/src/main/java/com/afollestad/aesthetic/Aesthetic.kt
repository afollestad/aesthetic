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
import android.graphics.Color.BLACK
import android.graphics.Color.TRANSPARENT
import android.graphics.Color.WHITE
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.os.Build.VERSION_CODES.O
import androidx.annotation.AttrRes
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.aesthetic.AutoSwitchMode.OFF
import com.afollestad.aesthetic.internal.InflationDelegate
import com.afollestad.aesthetic.internal.KEY_ACTIVITY_THEME
import com.afollestad.aesthetic.internal.KEY_BOTTOM_NAV_BG_MODE
import com.afollestad.aesthetic.internal.KEY_BOTTOM_NAV_ICONTEXT_MODE
import com.afollestad.aesthetic.internal.KEY_CARD_VIEW_BG_COLOR
import com.afollestad.aesthetic.internal.KEY_FIRST_TIME
import com.afollestad.aesthetic.internal.KEY_IS_DARK
import com.afollestad.aesthetic.internal.KEY_LIGHT_NAV_MODE
import com.afollestad.aesthetic.internal.KEY_LIGHT_STATUS_MODE
import com.afollestad.aesthetic.internal.KEY_NAV_VIEW_MODE
import com.afollestad.aesthetic.internal.KEY_SNACKBAR_ACTION_TEXT
import com.afollestad.aesthetic.internal.KEY_SNACKBAR_BG_COLOR
import com.afollestad.aesthetic.internal.KEY_SNACKBAR_TEXT
import com.afollestad.aesthetic.internal.KEY_SWIPEREFRESH_COLORS
import com.afollestad.aesthetic.internal.KEY_TAB_LAYOUT_BG_MODE
import com.afollestad.aesthetic.internal.KEY_TAB_LAYOUT_INDICATOR_MODE
import com.afollestad.aesthetic.internal.KEY_TOOLBAR_ICON_COLOR
import com.afollestad.aesthetic.internal.KEY_TOOLBAR_SUBTITLE_COLOR
import com.afollestad.aesthetic.internal.KEY_TOOLBAR_TITLE_COLOR
import com.afollestad.aesthetic.internal.attrKey
import com.afollestad.aesthetic.internal.deInitPrefs
import com.afollestad.aesthetic.internal.initPrefs
import com.afollestad.aesthetic.internal.invalidateStatusBar
import com.afollestad.aesthetic.internal.navBarColorKey
import com.afollestad.aesthetic.internal.statusBarColorKey
import com.afollestad.aesthetic.internal.waitForAttach
import com.afollestad.aesthetic.utils.adjustAlpha
import com.afollestad.aesthetic.utils.color
import com.afollestad.aesthetic.utils.colorAttr
import com.afollestad.aesthetic.utils.combine
import com.afollestad.aesthetic.utils.darkenColor
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.isColorLight
import com.afollestad.aesthetic.utils.kFlatMap
import com.afollestad.aesthetic.utils.mutableArrayMap
import com.afollestad.aesthetic.utils.plusAssign
import com.afollestad.aesthetic.utils.save
import com.afollestad.aesthetic.utils.setInflaterFactory
import com.afollestad.aesthetic.utils.setLightNavBarCompat
import com.afollestad.aesthetic.utils.setNavBarColorCompat
import com.afollestad.aesthetic.utils.setTaskDescriptionColor
import com.afollestad.aesthetic.utils.splitToInts
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.rxkprefs.RxkPrefs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

/** @author Aidan Follestad (afollestad) */
class Aesthetic private constructor(private var context: Context?) {

  internal val onAttached = BehaviorSubject.create<Boolean>()
  internal var rxkPrefs: RxkPrefs? = null
  internal var prefs: SharedPreferences? = null
  internal var editor: SharedPreferences.Editor? = null
  internal var inflationDelegate: InflationDelegate? = null

  private val lastActivityThemes = mutableArrayMap<String, Int>(2)

  private var subs: CompositeDisposable? = null
  private var isResumed: Boolean = false

  init {
    initPrefs()
  }

  // The 4 fields below allow us to avoid using !!, and provide indication if we access them
  // before we should.
  internal val safeContext
    @CheckResult
    get() = context ?: blowUp()
  internal val safePrefs
    @CheckResult
    get() = prefs ?: blowUp()
  private val safePrefsEditor
    @CheckResult
    get() = editor ?: blowUp()
  private val safeRxkPrefs
    @CheckResult
    get() = rxkPrefs ?: blowUp()

  val isDark: Observable<Boolean>
    @CheckResult
    get() = textColorPrimary().kFlatMap {
      safeRxkPrefs.boolean(KEY_IS_DARK, it.isColorLight())
          .observe()
    }

  @CheckResult fun isDark(isDark: Boolean): Aesthetic {
    safePrefsEditor.save { putBoolean(KEY_IS_DARK, isDark) }
    return this
  }

  @CheckResult fun activityTheme(@StyleRes theme: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_ACTIVITY_THEME, theme)
    return this
  }

  @CheckResult fun activityTheme() = waitForAttach().kFlatMap { rxPrefs ->
    rxPrefs
        .integer(KEY_ACTIVITY_THEME, 0)
        .observe()
        .filter { it != 0 && it != getLastActivityTheme(safeContext) }
  }

  @CheckResult
  fun attribute(@AttrRes attrId: Int, @ColorInt color: Int, applyNow: Boolean = false): Aesthetic {
    safePrefsEditor.putInt(attrKey(attrId), color)
    if (applyNow) safePrefsEditor.commit()
    return this
  }

  @CheckResult fun attributeRes(@AttrRes attrId: Int, @ColorRes color: Int) =
    attribute(attrId, safeContext.color(color))

  @CheckResult fun attribute(@AttrRes attrId: Int) = waitForAttach().kFlatMap { rxPrefs ->
    val defaultValue = safeContext.colorAttr(attr = attrId)
    rxPrefs
        .integer(attrKey(attrId), defaultValue)
        .observe()
  }

  @CheckResult internal fun attribute(name: String) = waitForAttach().kFlatMap { rxPrefs ->
    val key = attrKey(name)
    rxPrefs
        .integer(key, 0)
        .observe()
        .filter { safePrefs.contains(key) }
  }

  @CheckResult fun lightStatusBarMode(mode: AutoSwitchMode): Aesthetic {
    safePrefsEditor.putInt(KEY_LIGHT_STATUS_MODE, mode.value)
    return this
  }

  @CheckResult fun lightStatusBarMode() = waitForAttach().kFlatMap { rxPrefs ->
    rxPrefs
        .integer(KEY_LIGHT_STATUS_MODE, AutoSwitchMode.AUTO.value)
        .observe()
  }

  @CheckResult fun lightNavigationBarMode(mode: AutoSwitchMode): Aesthetic {
    safePrefsEditor.putInt(KEY_LIGHT_NAV_MODE, mode.value)
        .commit()
    return this
  }

  @CheckResult fun lightNavigationBarMode() = waitForAttach().kFlatMap { rxPrefs ->
    rxPrefs
        .integer(KEY_LIGHT_NAV_MODE, AutoSwitchMode.AUTO.value)
        .observe()
  }

  // Main Colors

  @SuppressLint("CheckResult")
  @CheckResult
  fun colorPrimary(@ColorInt color: Int): Aesthetic {
    attribute(R.attr.colorPrimary, color, applyNow = true)
    if (!safePrefs.contains(attrKey(R.attr.colorPrimaryDark))) {
      colorPrimaryDark(color.darkenColor())
    }
    return this
  }

  @SuppressLint("CheckResult")
  @CheckResult
  fun colorPrimaryRes(@ColorRes color: Int): Aesthetic {
    val primary = safeContext.color(color)
    colorPrimary(primary)
    colorPrimaryDark(primary.darkenColor())
    return this
  }

  @CheckResult fun colorPrimary() = attribute(R.attr.colorPrimary)

  @SuppressLint("CheckResult")
  @CheckResult
  fun colorPrimaryDark(@ColorInt color: Int): Aesthetic {
    attribute(R.attr.colorPrimaryDark, color, applyNow = true)
    if (!safePrefs.contains(statusBarColorKey())) {
      colorStatusBar(color.darkenColor())
    }
    return this
  }

  @CheckResult fun colorPrimaryDarkRes(@ColorRes color: Int) =
    colorPrimaryDark(safeContext.color(color))

  @CheckResult fun colorPrimaryDark() = attribute(R.attr.colorPrimaryDark)

  @CheckResult fun colorAccent(@ColorInt color: Int) =
    attribute(R.attr.colorAccent, color)

  @CheckResult fun colorAccentRes(@ColorRes color: Int) =
    colorAccent(safeContext.color(color))

  @CheckResult fun colorAccent() = attribute(R.attr.colorAccent)

  @CheckResult fun colorStatusBar(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(statusBarColorKey(), color)
    return this
  }

  @CheckResult fun colorStatusBarRes(@ColorRes color: Int) =
    colorStatusBar(safeContext.color(color))

  @CheckResult fun colorStatusBarAuto(): Aesthetic {
    val defaultValue = safeContext.colorAttr(R.attr.colorPrimaryDark)
    val primaryDark = safePrefs.getInt(attrKey(R.attr.colorPrimaryDark), defaultValue)
    safePrefsEditor.putInt(statusBarColorKey(), primaryDark)
    return this
  }

  @CheckResult fun colorStatusBar() = colorPrimaryDark().kFlatMap {
    safeRxkPrefs
        .integer(statusBarColorKey(), it)
        .observe()
  }

  @CheckResult fun colorNavigationBar(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(navBarColorKey(), color)
    return this
  }

  @CheckResult fun colorNavigationBarRes(@ColorRes color: Int) =
    colorNavigationBar(safeContext.color(color))

  @CheckResult fun colorNavigationBarAuto(): Aesthetic {
    val defaultValue = safeContext.colorAttr(attr = R.attr.colorPrimaryDark)
    val primaryDark = safePrefs.getInt(attrKey(R.attr.colorPrimaryDark), defaultValue)
    val navBarMode =
      AutoSwitchMode.fromInt(safePrefs.getInt(KEY_LIGHT_NAV_MODE, AutoSwitchMode.AUTO.value))
    val canUseLightMode = SDK_INT >= O && navBarMode != OFF
    safePrefsEditor.putInt(
        navBarColorKey(),
        if (!canUseLightMode && primaryDark.isColorLight()) BLACK else primaryDark
    )
    return this
  }

  @CheckResult fun colorNavigationBar() = waitForAttach().kFlatMap { rxPrefs ->
    val defaultValue =
      if (SDK_INT >= LOLLIPOP) safeContext.colorAttr(android.R.attr.navigationBarColor)
      else BLACK
    rxPrefs
        .integer(navBarColorKey(), defaultValue)
        .observe()
  }

  @CheckResult fun colorWindowBackground(@ColorInt color: Int) =
    attribute(android.R.attr.windowBackground, color)

  @CheckResult fun colorWindowBackgroundRes(@ColorRes color: Int) =
    colorWindowBackground(safeContext.color(color))

  @CheckResult fun colorWindowBackground() = attribute(android.R.attr.windowBackground)

  // Text Colors

  @CheckResult fun textColorPrimary(@ColorInt color: Int) =
    attribute(android.R.attr.textColorPrimary, color)

  @CheckResult fun textColorPrimaryRes(@ColorRes color: Int) =
    textColorPrimary(safeContext.color(color))

  @CheckResult fun textColorPrimary() = attribute(android.R.attr.textColorPrimary)

  @CheckResult fun textColorSecondary(@ColorInt color: Int) =
    attribute(android.R.attr.textColorSecondary, color)

  @CheckResult fun textColorSecondaryRes(@ColorRes color: Int) =
    textColorSecondary(safeContext.color(color))

  @CheckResult fun textColorSecondary() = attribute(android.R.attr.textColorSecondary)

  @CheckResult fun textColorPrimaryInverse(@ColorInt color: Int) =
    attribute(android.R.attr.textColorPrimaryInverse, color)

  @CheckResult fun textColorPrimaryInverseRes(@ColorRes color: Int) =
    textColorPrimaryInverse(safeContext.color(color))

  @CheckResult fun textColorPrimaryInverse() =
    attribute(android.R.attr.textColorPrimaryInverse)

  @CheckResult fun textColorSecondaryInverse(@ColorInt color: Int) =
    attribute(android.R.attr.textColorSecondaryInverse, color)

  @CheckResult fun textColorSecondaryInverseRes(@ColorRes color: Int) =
    textColorSecondaryInverse(safeContext.color(color))

  @CheckResult fun textColorSecondaryInverse() =
    attribute(android.R.attr.textColorSecondaryInverse)

  // View Support

  @CheckResult fun toolbarIconColor(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_TOOLBAR_ICON_COLOR, color)
    return this
  }

  @CheckResult fun toolbarIconColorRes(@ColorRes color: Int) =
    toolbarIconColor(safeContext.color(color))

  @CheckResult fun toolbarIconColor() = colorPrimary().kFlatMap {
    val defaultValue = if (it.isColorLight()) BLACK else WHITE
    safeRxkPrefs
        .integer(KEY_TOOLBAR_ICON_COLOR, defaultValue)
        .observe()
  }

  @CheckResult fun toolbarTitleColor(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_TOOLBAR_TITLE_COLOR, color)
    return this
  }

  @CheckResult fun toolbarTitleColorRes(@ColorRes color: Int) =
    toolbarTitleColor(safeContext.color(color))

  @CheckResult fun toolbarTitleColor() = colorPrimary().kFlatMap {
    val defaultValue = if (it.isColorLight()) BLACK else WHITE
    safeRxkPrefs
        .integer(KEY_TOOLBAR_TITLE_COLOR, defaultValue)
        .observe()
  }

  @CheckResult fun toolbarSubtitleColor(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_TOOLBAR_SUBTITLE_COLOR, color)
    return this
  }

  @CheckResult fun toolbarSubtitleColorRes(@ColorRes color: Int) =
    toolbarSubtitleColor(safeContext.color(color))

  @CheckResult fun toolbarSubtitleColor() = toolbarTitleColor().kFlatMap {
    safeRxkPrefs
        .integer(KEY_TOOLBAR_SUBTITLE_COLOR, it.adjustAlpha(.87f))
        .observe()
  }

  @CheckResult fun snackbarTextColor() = isDark.kFlatMap { isDark ->
    if (isDark) {
      textColorPrimary().kFlatMap {
        safeRxkPrefs.integer(KEY_SNACKBAR_TEXT, it)
            .observe()
      }
    } else {
      textColorPrimaryInverse().kFlatMap {
        safeRxkPrefs.integer(KEY_SNACKBAR_TEXT, it)
            .observe()
      }
    }
  }

  @CheckResult fun snackbarTextColorDefault(): Aesthetic {
    safePrefsEditor.remove(KEY_SNACKBAR_TEXT)
    return this
  }

  @CheckResult fun snackbarTextColor(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_SNACKBAR_TEXT, color)
    return this
  }

  @CheckResult fun snackbarTextColorRes(@ColorRes color: Int) =
    snackbarTextColor(safeContext.color(color))

  @CheckResult fun snackbarActionTextColor() = colorAccent().kFlatMap {
    safeRxkPrefs
        .integer(KEY_SNACKBAR_ACTION_TEXT, it)
        .observe()
  }

  @CheckResult fun snackbarActionTextColor(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_SNACKBAR_ACTION_TEXT, color)
    return this
  }

  @CheckResult fun snackbarActionTextColorRes(@ColorRes color: Int) =
    snackbarActionTextColor(safeContext.color(color))

  @CheckResult fun snackbarBackgroundColor() = waitForAttach().kFlatMap { rxPrefs ->
    rxPrefs
        .integer(KEY_SNACKBAR_BG_COLOR, TRANSPARENT)
        .observe()
        .filter { it != TRANSPARENT }
  }

  @CheckResult fun snackbarBackgroundColorDefault(): Aesthetic {
    safePrefsEditor.remove(KEY_SNACKBAR_BG_COLOR)
    return this
  }

  @CheckResult fun snackbarBackgroundColor(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_SNACKBAR_BG_COLOR, color)
    return this
  }

  @CheckResult fun snackbarBackgroundColorRes(@ColorRes color: Int) =
    snackbarBackgroundColor(safeContext.color(color))

  @CheckResult fun colorCardViewBackground() = isDark.kFlatMap { dark ->
    val cardBackgroundDefault = safeContext.color(
        if (dark) R.color.ate_cardview_bg_dark
        else R.color.ate_cardview_bg_light
    )
    safeRxkPrefs
        .integer(KEY_CARD_VIEW_BG_COLOR, cardBackgroundDefault)
        .observe()
  }

  @CheckResult fun colorCardViewBackground(@ColorInt color: Int): Aesthetic {
    safePrefsEditor.putInt(KEY_CARD_VIEW_BG_COLOR, color)
    return this
  }

  @CheckResult fun colorCardViewBackgroundRes(@ColorRes color: Int) =
    colorCardViewBackground(safeContext.color(color))

  @CheckResult fun tabLayoutIndicatorMode(mode: ColorMode): Aesthetic {
    safePrefsEditor.save { putInt(KEY_TAB_LAYOUT_INDICATOR_MODE, mode.value) }
    return this
  }

  @CheckResult fun tabLayoutIndicatorMode() = waitForAttach().kFlatMap { rxPrefs ->
    rxPrefs
        .integer(KEY_TAB_LAYOUT_INDICATOR_MODE, ColorMode.ACCENT.value)
        .observe()
        .map { ColorMode.fromInt(it) }
  }

  @CheckResult fun tabLayoutBackgroundMode(mode: ColorMode): Aesthetic {
    safePrefsEditor.save { putInt(KEY_TAB_LAYOUT_BG_MODE, mode.value) }
    return this
  }

  @CheckResult fun tabLayoutBackgroundMode() = waitForAttach().kFlatMap { rxPrefs ->
    rxPrefs
        .integer(KEY_TAB_LAYOUT_BG_MODE, ColorMode.PRIMARY.value)
        .observe()
        .map { ColorMode.fromInt(it) }
  }

  @CheckResult fun bottomNavigationBackgroundMode(mode: BottomNavBgMode): Aesthetic {
    safePrefsEditor.save { putInt(KEY_BOTTOM_NAV_BG_MODE, mode.value) }
    return this
  }

  @CheckResult fun bottomNavigationBackgroundMode() = waitForAttach().kFlatMap { rxPrefs ->
    rxPrefs
        .integer(KEY_BOTTOM_NAV_BG_MODE, BottomNavBgMode.BLACK_WHITE_AUTO.value)
        .observe()
        .map { BottomNavBgMode.fromInt(it) }
  }

  @CheckResult fun bottomNavigationIconTextMode(mode: BottomNavIconTextMode): Aesthetic {
    safePrefsEditor.save { putInt(KEY_BOTTOM_NAV_ICONTEXT_MODE, mode.value) }
    return this
  }

  @CheckResult fun bottomNavigationIconTextMode() = waitForAttach().kFlatMap { rxPrefs ->
    rxPrefs
        .integer(KEY_BOTTOM_NAV_ICONTEXT_MODE, BottomNavIconTextMode.SELECTED_ACCENT.value)
        .observe()
        .map { BottomNavIconTextMode.fromInt(it) }
  }

  @CheckResult fun navigationViewMode(mode: NavigationViewMode): Aesthetic {
    safePrefsEditor.save { putInt(KEY_NAV_VIEW_MODE, mode.value) }
    return this
  }

  @CheckResult fun navigationViewMode() = waitForAttach().kFlatMap { rxPrefs ->
    rxPrefs
        .integer(KEY_NAV_VIEW_MODE, NavigationViewMode.SELECTED_PRIMARY.value)
        .observe()
        .map { NavigationViewMode.fromInt(it) }
  }

  @CheckResult fun swipeRefreshLayoutColors() = colorAccent().kFlatMap { accent ->
    safeRxkPrefs
        .string(KEY_SWIPEREFRESH_COLORS, "$accent")
        .observe()
        .map { it.splitToInts() }
  }

  @CheckResult fun swipeRefreshLayoutColors(@ColorInt vararg colors: Int): Aesthetic {
    safePrefsEditor.putString(KEY_SWIPEREFRESH_COLORS, colors.joinToString(","))
    return this
  }

  @CheckResult fun swipeRefreshLayoutColorsRes(@ColorRes vararg colorsRes: Int): Aesthetic {
    safePrefsEditor.putString(
        KEY_SWIPEREFRESH_COLORS,
        colorsRes.map { safeContext.color(it) }.joinToString(",")
    )
    return this
  }

  // Etc.

  /** Notifies all listening views that theme properties have been updated.  */
  fun apply() = safePrefsEditor.putBoolean(KEY_FIRST_TIME, false).apply()

  companion object {

    @SuppressLint("StaticFieldLeak")
    private var instance: Aesthetic? = null

    /** Should be called before super.onCreate() in each Activity.  */
    @JvmStatic
    fun attach(whereAmI: Context): Aesthetic {
      if (instance == null) {
        instance = Aesthetic(whereAmI)
      }
      with(instance ?: blowUp("This is impossible")) {
        isResumed = false
        context = whereAmI
        initPrefs()

        with(whereAmI as? Activity ?: return this) {
          val li = layoutInflater
          (this as? AppCompatActivity)?.setInflaterFactory(li)

          val latestActivityTheme = safePrefs.getInt(KEY_ACTIVITY_THEME, 0)
          lastActivityThemes[safeContext.javaClass.name] = latestActivityTheme
          if (latestActivityTheme != 0) {
            setTheme(latestActivityTheme)
          }
        }

        return this
      }
    }

    @CheckResult
    @JvmStatic
    fun get() = instance ?: blowUp()

    inline fun config(func: Aesthetic.() -> Unit) {
      val instance = get()
      instance.func()
      instance.apply()
    }

    /** Should be called in onPause() of each Activity or Service.  */
    @JvmStatic
    fun pause(whereAmI: Context) {
      with(instance ?: return) {
        isResumed = false
        subs?.clear()
        if (whereAmI is Activity &&
            whereAmI.isFinishing &&
            safeContext == whereAmI
        ) {
          context = null
          deInitPrefs()
        }
      }
    }

    /** Should be called in onResume() of each Activity.  */
    @JvmStatic
    fun resume(whereAmI: Context) {
      with(instance ?: blowUp()) {
        if (isResumed)
          throw IllegalStateException("Already resumed")

        context = whereAmI
        initPrefs()
        isResumed = true

        subs = CompositeDisposable()
        if (safeContext is Activity) {
          subs += colorPrimary()
              .distinctToMainThread()
              .subscribeTo {
                (safeContext as? Activity)?.setTaskDescriptionColor(it)
              }
          subs += activityTheme()
              .distinctToMainThread()
              .subscribeTo {
                lastActivityThemes[safeContext.javaClass.name] = it
                (safeContext as? Activity)?.recreate()
              }
          subs += combine(
              colorStatusBar().distinctToMainThread(),
              lightStatusBarMode().distinctToMainThread()
          )
              .subscribeTo { invalidateStatusBar() }
          subs += combine(
              colorNavigationBar().distinctToMainThread(),
              lightNavigationBarMode().distinctUntilChanged().map { AutoSwitchMode.fromInt(it) }
          ) { color, mode -> Pair(color, mode) }
              .distinctToMainThread()
              .subscribeTo {
                (safeContext as? Activity)?.setNavBarColorCompat(it.first)
                val useLightMode = when (it.second) {
                  AutoSwitchMode.ON -> true
                  AutoSwitchMode.OFF -> false
                  else -> it.first.isColorLight()
                }
                (safeContext as? Activity)?.setLightNavBarCompat(useLightMode)
              }
        }
        subs += colorWindowBackground()
            .distinctToMainThread()
            .subscribeTo {
              (safeContext as? Activity)?.window?.setBackgroundDrawable(ColorDrawable(it))
            }
      }
    }

    /** Returns true if Aesthetic has not been configured before. */
    val isFirstTime: Boolean
      @CheckResult
      get() = with(instance ?: throw IllegalStateException("Not attached")) {
        return safePrefs.getBoolean(KEY_FIRST_TIME, true)
      }

    /**
     * Sets an interface which can be used to auto swap views that are not swapped internally
     * by Aesthetic, such as custom views from other libraries.
     *
     * You do not need to do this if you're just concerned about text color, background color, or
     * tint, since that is handled by Aesthetic without view swapping.
     */
    @JvmStatic
    fun setInflationDelegate(inflationDelegate: InflationDelegate) {
      get().inflationDelegate = inflationDelegate
    }

    private fun getLastActivityTheme(forContext: Context?): Int {
      return instance?.lastActivityThemes?.get(forContext?.javaClass?.name ?: "") ?: return 0
    }
  }
}

@Throws(IllegalStateException::class)
internal fun <T> blowUp(msg: String = "Not attached"): T {
  throw IllegalStateException(msg)
}
