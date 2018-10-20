/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.internal

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.R
import com.afollestad.aesthetic.R.id
import com.afollestad.aesthetic.utils.fixedLayoutInflater
import com.afollestad.aesthetic.utils.observableForAttrName
import com.afollestad.aesthetic.utils.resId
import com.afollestad.aesthetic.views.AestheticActionMenuItemView
import com.afollestad.aesthetic.views.AestheticBorderlessButton
import com.afollestad.aesthetic.views.AestheticBottomNavigationView
import com.afollestad.aesthetic.views.AestheticButton
import com.afollestad.aesthetic.views.AestheticCardView
import com.afollestad.aesthetic.views.AestheticCheckBox
import com.afollestad.aesthetic.views.AestheticCheckedTextView
import com.afollestad.aesthetic.views.AestheticCoordinatorLayout
import com.afollestad.aesthetic.views.AestheticDialogButton
import com.afollestad.aesthetic.views.AestheticDrawerLayout
import com.afollestad.aesthetic.views.AestheticEditText
import com.afollestad.aesthetic.views.AestheticFab
import com.afollestad.aesthetic.views.AestheticListView
import com.afollestad.aesthetic.views.AestheticNavigationView
import com.afollestad.aesthetic.views.AestheticNestedScrollView
import com.afollestad.aesthetic.views.AestheticProgressBar
import com.afollestad.aesthetic.views.AestheticRadioButton
import com.afollestad.aesthetic.views.AestheticRecyclerView
import com.afollestad.aesthetic.views.AestheticScrollView
import com.afollestad.aesthetic.views.AestheticSeekBar
import com.afollestad.aesthetic.views.AestheticSnackBarButton
import com.afollestad.aesthetic.views.AestheticSnackBarTextView
import com.afollestad.aesthetic.views.AestheticSpinner
import com.afollestad.aesthetic.views.AestheticSwipeRefreshLayout
import com.afollestad.aesthetic.views.AestheticSwitch
import com.afollestad.aesthetic.views.AestheticSwitchCompat
import com.afollestad.aesthetic.views.AestheticTabLayout
import com.afollestad.aesthetic.views.AestheticTextInputEditText
import com.afollestad.aesthetic.views.AestheticTextInputLayout
import com.afollestad.aesthetic.views.AestheticToolbar
import com.afollestad.aesthetic.views.AestheticViewPager
import com.google.android.material.internal.NavigationMenuItemView

/** @author Aidan Follestad (afollestad) */
internal class InflationInterceptor(
  private val activity: AppCompatActivity,
  private val delegate: AppCompatDelegate?
) : LayoutInflater.Factory2 {

  companion object {

    private const val LOGGING_ENABLED = false

    private const val ANDROIDX_WIDGET = "androidx.core.widget"
    private const val APPCOMPAT_WIDGET = "androidx.appcompat.widget"
    private const val APPCOMPAT_VIEW = "androidx.appcompat.view"
    private const val GOOGLE_MATERIAL = "com.google.android.material"

    @Suppress("ConstantConditionIf")
    private fun log(msg: String) {
      if (!LOGGING_ENABLED) return
      Log.d("InflationInterceptor", msg)
    }

    private fun isBorderlessButton(
      context: Context?,
      attrs: AttributeSet?
    ): Boolean {
      if (context == null || attrs == null) {
        return false
      }
      val backgroundRes = context.resId(attrs, android.R.attr.background)
      if (backgroundRes == 0) {
        return false
      }
      val resName = context.resources.getResourceEntryName(backgroundRes)
      return resName.endsWith("btn_borderless_material")
    }

    private fun getViewPrefix(name: String): String {
      if (name.contains(".")) {
        // We have a full class, don't need a prefix
        return ""
      }
      // Else we have a framework class
      return when (name) {
        "View", "ViewStub", "SurfaceView", "TextureView" -> "android.view."
        "WebView" -> "android.webkit."
        else -> "android.widget."
      }
    }
  }

  override fun onCreateView(
    name: String?,
    context: Context?,
    attrs: AttributeSet?
  ): View {
    return onCreateView(name, context, attrs)
  }

  @SuppressLint("RestrictedApi")
  override fun onCreateView(
    parent: View?,
    name: String,
    context: Context,
    attrs: AttributeSet?
  ): View? {
    val viewId = context.resId(attrs, android.R.attr.id)
    var view = name.viewForName(context, attrs, viewId, parent)

    var viewBackgroundValue = ""
    var textColorValue = ""
    var hintTextColorValue = ""
    var tintValue = ""

    if (view.shouldIgnore()) {
      // Set view back to null so we can let AndroidX handle this view instead.
      view = null
    } else if (attrs != null) {
      val wizard = AttrWizard(context, attrs)
      viewBackgroundValue = wizard.getRawValue(android.R.attr.background)
      textColorValue = wizard.getRawValue(android.R.attr.textColor)
      hintTextColorValue = wizard.getRawValue(android.R.attr.textColorHint)
      tintValue = wizard.getRawValue(R.attr.tint)
    }

    // If view is null, let the activity try to create it
    if (view == null) {
      try {
        view = activity.onCreateView(parent, name, context, attrs)
        if (view == null) {
          view = activity.onCreateView(name, context, attrs)
        }
      } catch (e: Throwable) {
        throw IllegalStateException("Unable to delegate inflation of $name to your Activity.", e)
      }
    }
    // If it's still null, try the AppCompat delegate
    if (view == null && delegate != null && attrs != null) {
      try {
        view = delegate.createView(parent, name, context, attrs)
      } catch (e: Throwable) {
        throw IllegalStateException("Unable to delegate inflation of $name to AppCompat.", e)
      }
    }
    // If it's still null, use the LayoutInflater directly
    if (view == null) {
      try {
        val layoutInflater = context.fixedLayoutInflater()
        view = layoutInflater.createView(name, getViewPrefix(name), attrs)
      } catch (e: Throwable) {
        throw IllegalStateException(
            "Unable to delegate inflation of $name to normal LayoutInflater.", e
        )
      }
    }
    // If it's still null, explode
    if (view == null) {
      throw IllegalStateException("Unable to inflate $name! Please report as a GitHub issue.")
    }

    // If the view is blacklisted for apply, don't try to apply background theming, etc.
    if (name.isBlackListedForApply()) {
      return view
    }

    if (viewBackgroundValue.isNotEmpty()) {
      addBackgroundSubscriber(view, get().observableForAttrName(viewBackgroundValue))
    }
    if (view is TextView) {
      val textColorObs = get().observableForAttrName(
          name = textColorValue,
          fallback = get().textColorPrimary()
      )
      if (textColorValue.isNotEmpty()) {
        addTextColorSubscriber(view, textColorObs)
      }
      if (hintTextColorValue.isNotEmpty()) {
        addHintTextColorSubscriber(view, get().observableForAttrName(hintTextColorValue))
      }
    }
    if (tintValue.isNotEmpty()) {
      addImageTintSubscriber(view, get().observableForAttrName(tintValue))
    }

    var idName = ""
    try {
      idName = "${context.resources.getResourceName(view.id)} "
    } catch (ignored: Throwable) {
    }

    log("Inflated -> $idName${view.javaClass.name}")

    return view
  }

  private fun String.viewForName(
    context: Context,
    attrs: AttributeSet?,
    @IdRes viewId: Int,
    parent: View?
  ): View? = when (this) {
    "androidx.drawerlayout.widget.DrawerLayout" ->
      AestheticDrawerLayout(context, attrs)
    "Toolbar", "$APPCOMPAT_WIDGET.Toolbar" ->
      AestheticToolbar(context, attrs)

    "$APPCOMPAT_WIDGET.AppCompatTextView", "TextView" ->
      if (viewId == id.snackbar_text) {
        AestheticSnackBarTextView(context, attrs)
      } else {
        null
      }

    "Button", "$APPCOMPAT_WIDGET.AppCompatButton" ->
      if (viewId == android.R.id.button1 ||
          viewId == android.R.id.button2 ||
          viewId == android.R.id.button3
      ) {
        AestheticDialogButton(context, attrs)
      } else if (viewId == id.snackbar_action) {
        AestheticSnackBarButton(context, attrs)
      } else if (isBorderlessButton(context, attrs)) {
        AestheticBorderlessButton(context, attrs)
      } else {
        AestheticButton(context, attrs)
      }

    "$APPCOMPAT_WIDGET.AppCompatCheckBox", "CheckBox" ->
      AestheticCheckBox(context, attrs)
    "$APPCOMPAT_WIDGET.AppCompatRadioButton", "RadioButton" ->
      AestheticRadioButton(context, attrs)
    "$APPCOMPAT_WIDGET.AppCompatEditText", "EditText" ->
      AestheticEditText(context, attrs)
    "Switch" ->
      AestheticSwitch(context, attrs)
    "$APPCOMPAT_WIDGET.SwitchCompat" ->
      AestheticSwitchCompat(context, attrs)
    "$APPCOMPAT_WIDGET.AppCompatSeekBar", "SeekBar" ->
      AestheticSeekBar(context, attrs)
    "ProgressBar" ->
      AestheticProgressBar(context, attrs)
    "$APPCOMPAT_VIEW.ActionMenuItemView" ->
      AestheticActionMenuItemView(context, attrs)
    "CheckedTextView", "$APPCOMPAT_WIDGET.AppCompatCheckedTextView" -> {
      if (parent is NavigationMenuItemView) null
      else AestheticCheckedTextView(context, attrs)
    }

    "$APPCOMPAT_WIDGET.RecyclerView" ->
      AestheticRecyclerView(context, attrs)
    "$ANDROIDX_WIDGET.NestedScrollView" ->
      AestheticNestedScrollView(context, attrs)
    "ListView" ->
      AestheticListView(context, attrs)
    "ScrollView" ->
      AestheticScrollView(context, attrs)
    "androidx.viewpager.widget.ViewPager" ->
      AestheticViewPager(context, attrs)

    "Spinner", "$APPCOMPAT_WIDGET.AppCompatSpinner" ->
      AestheticSpinner(context, attrs)

    "$GOOGLE_MATERIAL.textfield.TextInputLayout" ->
      AestheticTextInputLayout(context, attrs)
    "$GOOGLE_MATERIAL.textfield.TextInputEditText" ->
      AestheticTextInputEditText(context, attrs)

    "$APPCOMPAT_WIDGET.CardView" ->
      AestheticCardView(context, attrs)
    "$GOOGLE_MATERIAL.tabs.TabLayout" ->
      AestheticTabLayout(context, attrs)
    "$GOOGLE_MATERIAL.navigation.NavigationView" ->
      AestheticNavigationView(context, attrs)
    "$GOOGLE_MATERIAL.bottomnavigation.BottomNavigationView" ->
      AestheticBottomNavigationView(context, attrs)
    "$GOOGLE_MATERIAL.floatingactionbutton.FloatingActionButton" ->
      AestheticFab(context, attrs)

    "androidx.coordinatorlayout.widget.CoordinatorLayout" ->
      AestheticCoordinatorLayout(context, attrs)
    "androidx.swiperefreshlayout.widget.SwipeRefreshLayout" ->
      AestheticSwipeRefreshLayout(context, attrs)

    else -> get().inflationDelegate?.createView(context, attrs, this@viewForName, viewId)
  }

  private fun View?.shouldIgnore() =
    this != null && (":aesthetic_ignore" == tag || getTag(R.id.aesthetic_ignore) != null)

  private fun String.isBlackListedForApply() =
    ("com.google.android.material.internal.NavigationMenuItemView" == this ||
        "ViewStub" == this ||
        "fragment" == this ||
        "include" == this)
}
