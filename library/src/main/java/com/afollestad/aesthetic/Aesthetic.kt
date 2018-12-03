/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.afollestad.aesthetic.internal.invalidateNavBar
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
import com.afollestad.aesthetic.utils.mapToIntArray
import com.afollestad.aesthetic.utils.mutableArrayMap
import com.afollestad.aesthetic.utils.plusAssign
import com.afollestad.aesthetic.utils.save
import com.afollestad.aesthetic.utils.setInflaterFactory
import com.afollestad.aesthetic.utils.setTaskDescriptionColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.rxkprefs.RxkPrefs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

/** @author Aidan Follestad (afollestad) */
class Aesthetic private constructor(private var context: Context?) {

  internal val onAttached = BehaviorSubject.create<Boolean>()
  internal var rPrefs: RxkPrefs? = null
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
    get() = rPrefs ?: blowUp()

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
  fun attribute(
    @AttrRes attrId: Int,
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null,
    applyNow: Boolean = false
  ): Aesthetic {
    val color = givenColor(literal, res)
    safePrefsEditor.putInt(attrKey(attrId), color)
    if (applyNow) safePrefsEditor.commit()
    return this
  }

  @Deprecated(
      message = "Use attribute() method with the res parameter instead,",
      replaceWith = ReplaceWith("attribute(attrId, res = color)")
  )
  fun attributeRes(@AttrRes attrId: Int, @ColorRes color: Int) = attribute(attrId, res = color)

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
        .map { AutoSwitchMode.fromInt(it) }
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
        .map { AutoSwitchMode.fromInt(it) }
  }

  // Main Colors

  @CheckResult
  fun colorPrimary(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ) = attribute(R.attr.colorPrimary, literal = literal, res = res)

  @Deprecated(
      message = "Use colorPrimary() method with the res parameter instead,",
      replaceWith = ReplaceWith("colorPrimary(res = color)")
  )
  fun colorPrimaryRes(@ColorRes color: Int) = colorPrimary(res = color)

  @CheckResult fun colorPrimary() = attribute(R.attr.colorPrimary)

  @CheckResult
  fun colorPrimaryDark(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ) = attribute(R.attr.colorPrimaryDark, literal = literal, res = res)

  @CheckResult fun colorPrimaryDark() = colorPrimary().kFlatMap { primary ->
    val defaultValue = primary.darkenColor()
    safeRxkPrefs
        .integer(attrKey(R.attr.colorPrimaryDark), defaultValue)
        .observe()
  }

  @Deprecated(
      message = "Use colorPrimaryDark() method with the res parameter instead,",
      replaceWith = ReplaceWith("colorPrimaryDark(res = color)")
  )
  fun colorPrimaryDarkRes(@ColorRes color: Int) = colorPrimaryDark(res = color)

  @CheckResult fun colorAccent(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ) = attribute(R.attr.colorAccent, literal = literal, res = res)

  @Deprecated(
      message = "Use colorAccent() method with the res parameter instead,",
      replaceWith = ReplaceWith("colorAccent(res = color)")
  )
  fun colorAccentRes(@ColorRes color: Int) = colorAccent(res = color)

  @CheckResult fun colorAccent() = attribute(R.attr.colorAccent)

  @CheckResult fun colorStatusBar(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ): Aesthetic {
    val color = givenColor(literal, res)
    safePrefsEditor.putInt(statusBarColorKey(), color)
    return this
  }

  @Deprecated(
      message = "Use colorStatusBar() method with the res parameter instead,",
      replaceWith = ReplaceWith("colorStatusBar(res = color)")
  )
  fun colorStatusBarRes(@ColorRes color: Int) = colorStatusBar(res = color)

  @CheckResult fun colorStatusBarAuto(): Aesthetic {
    safePrefsEditor.remove(statusBarColorKey())
    return this
  }

  @CheckResult fun colorStatusBar() = colorPrimaryDark().kFlatMap {
    safeRxkPrefs
        .integer(statusBarColorKey(), it)
        .observe()
  }

  @CheckResult fun colorNavigationBar(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ): Aesthetic {
    val color = givenColor(literal, res)
    safePrefsEditor.putInt(navBarColorKey(), color)
    return this
  }

  @Deprecated(
      message = "Use colorNavigationBar() method with the res parameter instead,",
      replaceWith = ReplaceWith("colorNavigationBar(res = color)")
  )
  fun colorNavigationBarRes(@ColorRes color: Int) = colorNavigationBar(res = color)

  @CheckResult fun colorNavigationBarAuto(): Aesthetic {
    safePrefsEditor.remove(navBarColorKey())
    return this
  }

  @CheckResult fun colorNavigationBar() = combine(colorPrimaryDark(), lightNavigationBarMode())
      .kFlatMap { primaryDarkAndLightMode ->
        val primaryDark = primaryDarkAndLightMode.first
        val navBarMode = primaryDarkAndLightMode.second
        val canUseLightMode = SDK_INT >= O && navBarMode != OFF
        val defaultValue = if (!canUseLightMode && primaryDark.isColorLight()) {
          BLACK
        } else {
          primaryDark
        }
        safeRxkPrefs
            .integer(navBarColorKey(), defaultValue)
            .observe()
      }

  @CheckResult fun colorWindowBackground(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ) = attribute(android.R.attr.windowBackground, literal = literal, res = res)

  @Deprecated(
      message = "Use colorWindowBackground() method with the res parameter instead,",
      replaceWith = ReplaceWith("colorWindowBackground(res = color)")
  )
  fun colorWindowBackgroundRes(@ColorRes color: Int) = colorWindowBackground(res = color)

  @CheckResult fun colorWindowBackground() = attribute(android.R.attr.windowBackground)

  // Text Colors

  @CheckResult fun textColorPrimary(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ) = attribute(android.R.attr.textColorPrimary, literal = literal, res = res)

  @Deprecated(
      message = "Use textColorPrimary() method with the res parameter instead,",
      replaceWith = ReplaceWith("textColorPrimary(res = color)")
  )
  fun textColorPrimaryRes(@ColorRes color: Int) = textColorPrimary(res = color)

  @CheckResult fun textColorPrimary() = attribute(android.R.attr.textColorPrimary)

  @CheckResult fun textColorSecondary(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ) = attribute(android.R.attr.textColorSecondary, literal = literal, res = res)

  @Deprecated(
      message = "Use textColorSecondary() method with the res parameter instead,",
      replaceWith = ReplaceWith("textColorSecondary(res = color)")
  )
  fun textColorSecondaryRes(@ColorRes color: Int) = textColorSecondary(res = color)

  @CheckResult fun textColorSecondary() = attribute(android.R.attr.textColorSecondary)

  @CheckResult fun textColorPrimaryInverse(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ) = attribute(android.R.attr.textColorPrimaryInverse, literal = literal, res = res)

  @Deprecated(
      message = "Use textColorPrimaryInverse() method with the res parameter instead,",
      replaceWith = ReplaceWith("textColorPrimaryInverse(res = color)")
  )
  fun textColorPrimaryInverseRes(@ColorRes color: Int) = textColorPrimaryInverse(res = color)

  @CheckResult fun textColorPrimaryInverse() = attribute(android.R.attr.textColorPrimaryInverse)

  @CheckResult fun textColorSecondaryInverse(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ) = attribute(android.R.attr.textColorSecondaryInverse, literal = literal, res = res)

  @Deprecated(
      message = "Use textColorSecondaryInverse() method with the res parameter instead,",
      replaceWith = ReplaceWith("textColorSecondaryInverse(res = color)")
  )
  fun textColorSecondaryInverseRes(@ColorRes color: Int) = textColorSecondaryInverse(res = color)

  @CheckResult fun textColorSecondaryInverse() = attribute(android.R.attr.textColorSecondaryInverse)

  // View Support

  @CheckResult fun toolbarIconColor(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ): Aesthetic {
    val color = givenColor(literal, res)
    safePrefsEditor.putInt(KEY_TOOLBAR_ICON_COLOR, color)
    return this
  }

  @Deprecated(
      message = "Use toolbarIconColor() method with the res parameter instead,",
      replaceWith = ReplaceWith("toolbarIconColor(res = color)")
  )
  fun toolbarIconColorRes(@ColorRes color: Int) = toolbarIconColor(res = color)

  @CheckResult fun toolbarIconColor() = colorPrimary().kFlatMap {
    val defaultValue = if (it.isColorLight()) BLACK else WHITE
    safeRxkPrefs
        .integer(KEY_TOOLBAR_ICON_COLOR, defaultValue)
        .observe()
  }

  @CheckResult fun toolbarTitleColor(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ): Aesthetic {
    val color = givenColor(literal, res)
    safePrefsEditor.putInt(KEY_TOOLBAR_TITLE_COLOR, color)
    return this
  }

  @Deprecated(
      message = "Use toolbarTitleColor() method with the res parameter instead,",
      replaceWith = ReplaceWith("toolbarTitleColor(res = color)")
  )
  fun toolbarTitleColorRes(@ColorRes color: Int) = toolbarTitleColor(res = color)

  @CheckResult fun toolbarTitleColor() = colorPrimary().kFlatMap { primary ->
    val defaultValue = if (primary.isColorLight()) BLACK else WHITE
    safeRxkPrefs
        .integer(KEY_TOOLBAR_TITLE_COLOR, defaultValue)
        .observe()
  }

  @CheckResult fun toolbarSubtitleColor(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ): Aesthetic {
    val color = givenColor(literal, res)
    safePrefsEditor.putInt(KEY_TOOLBAR_SUBTITLE_COLOR, color)
    return this
  }

  @Deprecated(
      message = "Use toolbarSubtitleColor() method with the res parameter instead,",
      replaceWith = ReplaceWith("toolbarSubtitleColor(res = color)")
  )
  fun toolbarSubtitleColorRes(@ColorRes color: Int) = toolbarSubtitleColor(res = color)

  @CheckResult fun toolbarSubtitleColor() = toolbarTitleColor().kFlatMap { titleColor ->
    safeRxkPrefs
        .integer(KEY_TOOLBAR_SUBTITLE_COLOR, titleColor.adjustAlpha(.87f))
        .observe()
  }

  @CheckResult fun snackbarTextColor() = isDark.kFlatMap { isDark ->
    if (isDark) {
      textColorPrimary().kFlatMap { primary ->
        safeRxkPrefs.integer(KEY_SNACKBAR_TEXT, primary)
            .observe()
      }
    } else {
      textColorPrimaryInverse().kFlatMap { primaryInverse ->
        safeRxkPrefs.integer(KEY_SNACKBAR_TEXT, primaryInverse)
            .observe()
      }
    }
  }

  @CheckResult fun snackbarTextColorDefault(): Aesthetic {
    safePrefsEditor.remove(KEY_SNACKBAR_TEXT)
    return this
  }

  @CheckResult fun snackbarTextColor(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ): Aesthetic {
    val color = givenColor(literal, res)
    safePrefsEditor.putInt(KEY_SNACKBAR_TEXT, color)
    return this
  }

  @Deprecated(
      message = "Use snackbarTextColor() method with the res parameter instead,",
      replaceWith = ReplaceWith("snackbarTextColor(res = color)")
  )
  fun snackbarTextColorRes(@ColorRes color: Int) = snackbarTextColor(res = color)

  @CheckResult fun snackbarActionTextColor() = colorAccent().kFlatMap { accent ->
    safeRxkPrefs
        .integer(KEY_SNACKBAR_ACTION_TEXT, accent)
        .observe()
  }

  @CheckResult fun snackbarActionTextColor(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ): Aesthetic {
    val color = givenColor(literal, res)
    safePrefsEditor.putInt(KEY_SNACKBAR_ACTION_TEXT, color)
    return this
  }

  @Deprecated(
      message = "Use snackbarActionTextColor() method with the res parameter instead,",
      replaceWith = ReplaceWith("snackbarActionTextColor(res = color)")
  )
  fun snackbarActionTextColorRes(@ColorRes color: Int) = snackbarActionTextColor(res = color)

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

  @CheckResult fun snackbarBackgroundColor(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ): Aesthetic {
    val color = givenColor(literal, res)
    safePrefsEditor.putInt(KEY_SNACKBAR_BG_COLOR, color)
    return this
  }

  @Deprecated(
      message = "Use snackbarBackgroundColor() method with the res parameter instead,",
      replaceWith = ReplaceWith("snackbarBackgroundColor(res = color)")
  )
  fun snackbarBackgroundColorRes(@ColorRes color: Int) = snackbarBackgroundColor(res = color)

  @CheckResult fun colorCardViewBackground() = isDark.kFlatMap { dark ->
    val cardBackgroundDefault = safeContext.color(
        if (dark) {
          R.color.ate_cardview_bg_dark
        } else {
          R.color.ate_cardview_bg_light
        }
    )
    safeRxkPrefs
        .integer(KEY_CARD_VIEW_BG_COLOR, cardBackgroundDefault)
        .observe()
  }

  @CheckResult fun colorCardViewBackground(
    @ColorInt literal: Int? = null,
    @ColorRes res: Int? = null
  ): Aesthetic {
    val color = givenColor(literal, res)
    safePrefsEditor.putInt(KEY_CARD_VIEW_BG_COLOR, color)
    return this
  }

  @Deprecated(
      message = "Use colorCardViewBackground() method with the res parameter instead,",
      replaceWith = ReplaceWith("colorCardViewBackground(res = color)")
  )
  fun colorCardViewBackgroundRes(@ColorRes color: Int) = colorCardViewBackground(res = color)

  @CheckResult fun tabLayoutIndicatorMode(mode: ColorMode): Aesthetic {
    safePrefsEditor.save { putInt(KEY_TAB_LAYOUT_INDICATOR_MODE, mode.value) }
    return this
  }

  @CheckResult fun tabLayoutIndicatorMode() = waitForAttach().kFlatMap { rxPrefs ->
    rxPrefs
        .integer(KEY_TAB_LAYOUT_INDICATOR_MODE, ColorMode.ACCENT.value)
        .observe()
        .mapToColorMode()
  }

  @CheckResult fun tabLayoutBackgroundMode(mode: ColorMode): Aesthetic {
    safePrefsEditor.save { putInt(KEY_TAB_LAYOUT_BG_MODE, mode.value) }
    return this
  }

  @CheckResult fun tabLayoutBackgroundMode() = waitForAttach().kFlatMap { rxPrefs ->
    rxPrefs
        .integer(KEY_TAB_LAYOUT_BG_MODE, ColorMode.PRIMARY.value)
        .observe()
        .mapToColorMode()
  }

  @CheckResult fun bottomNavigationBackgroundMode(mode: BottomNavBgMode): Aesthetic {
    safePrefsEditor.save { putInt(KEY_BOTTOM_NAV_BG_MODE, mode.value) }
    return this
  }

  @CheckResult fun bottomNavigationBackgroundMode() = waitForAttach().kFlatMap { rxPrefs ->
    rxPrefs
        .integer(KEY_BOTTOM_NAV_BG_MODE, BottomNavBgMode.BLACK_WHITE_AUTO.value)
        .observe()
        .mapToBottomNavBgMode()
  }

  @CheckResult fun bottomNavigationIconTextMode(mode: BottomNavIconTextMode): Aesthetic {
    safePrefsEditor.save { putInt(KEY_BOTTOM_NAV_ICONTEXT_MODE, mode.value) }
    return this
  }

  @CheckResult fun bottomNavigationIconTextMode() = waitForAttach().kFlatMap { rxPrefs ->
    rxPrefs
        .integer(KEY_BOTTOM_NAV_ICONTEXT_MODE, BottomNavIconTextMode.SELECTED_ACCENT.value)
        .observe()
        .mapToBottomNavIconTextMode()
  }

  @CheckResult fun navigationViewMode(mode: NavigationViewMode): Aesthetic {
    safePrefsEditor.save { putInt(KEY_NAV_VIEW_MODE, mode.value) }
    return this
  }

  @CheckResult fun navigationViewMode() = waitForAttach().kFlatMap { rxPrefs ->
    rxPrefs
        .integer(KEY_NAV_VIEW_MODE, NavigationViewMode.SELECTED_PRIMARY.value)
        .observe()
        .mapToNavigationViewMode()
  }

  @CheckResult fun swipeRefreshLayoutColors() = colorAccent().kFlatMap { accent ->
    safeRxkPrefs
        .string(KEY_SWIPEREFRESH_COLORS, "$accent")
        .observe()
        .mapToIntArray()
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
    @JvmOverloads
    fun attach(
      whereAmI: Context,
      inflationDelegate: InflationDelegate? = null
    ): Aesthetic {
      if (instance == null) {
        instance = Aesthetic(whereAmI)
      }
      with(instance ?: blowUp("This is impossible")) {
        isResumed = false
        context = whereAmI
        initPrefs()

        with(whereAmI as? Activity ?: return this) {
          (this as? AppCompatActivity)?.setInflaterFactory(layoutInflater, inflationDelegate)
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
    @JvmStatic fun pause(whereAmI: Context) {
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
    @JvmStatic fun resume(whereAmI: Context) {
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
              .distinctToMainThread()
              .subscribe(::invalidateStatusBar)
          subs += combine(
              colorNavigationBar().distinctToMainThread(),
              lightNavigationBarMode().distinctUntilChanged()
          )
              .distinctToMainThread()
              .subscribe(::invalidateNavBar)
          subs += colorWindowBackground()
              .distinctToMainThread()
              .subscribeTo {
                (safeContext as? Activity)?.window?.setBackgroundDrawable(ColorDrawable(it))
              }
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
    @JvmStatic fun setInflationDelegate(inflationDelegate: InflationDelegate) {
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

internal fun Aesthetic.givenColor(@ColorInt literal: Int?, @ColorRes res: Int?): Int {
  if (literal != null) {
    return literal
  } else if (res != null) {
    return safeContext.color(res)
  }
  throw IllegalArgumentException("Expected literal or res parameter to be non-null.")
}
