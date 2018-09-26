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
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.afollestad.aesthetic.R.id
import com.afollestad.aesthetic.addBackgroundSubscriber
import com.afollestad.aesthetic.utils.fixedLayoutInflater
import com.afollestad.aesthetic.utils.resId
import com.afollestad.aesthetic.utils.watchColor
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
import com.afollestad.aesthetic.views.AestheticImageButton
import com.afollestad.aesthetic.views.AestheticImageView
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
import com.afollestad.aesthetic.views.AestheticTextView
import com.afollestad.aesthetic.views.AestheticToolbar
import com.afollestad.aesthetic.views.AestheticViewPager

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

    private fun isBlackListedForApply(name: String): Boolean {
      return ("com.google.android.material.internal.NavigationMenuItemView" == name ||
          "ViewStub" == name ||
          "fragment" == name ||
          "include" == name)
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
    var view: View? = null
    val viewId = context.resId(attrs, android.R.attr.id)

    when (name) {
      "ImageView", "$APPCOMPAT_WIDGET.AppCompatImageView" ->
        view = AestheticImageView(context, attrs)
      "ImageButton", "$APPCOMPAT_WIDGET.AppCompatImageButton" ->
        view = AestheticImageButton(context, attrs)

      "androidx.drawerlayout.widget.DrawerLayout" ->
        view = AestheticDrawerLayout(context, attrs)
      "Toolbar", "$APPCOMPAT_WIDGET.Toolbar" ->
        view = AestheticToolbar(context, attrs)

      "$APPCOMPAT_WIDGET.AppCompatTextView", "TextView" ->
        if (viewId == id.snackbar_text) {
          view = AestheticSnackBarTextView(context, attrs)
        } else {
          view = AestheticTextView(context, attrs)
          if (parent is LinearLayout && view.id == android.R.id.message) {
            // This is for a toast message
            view = null
          }
        }
      "Button", "$APPCOMPAT_WIDGET.AppCompatButton" ->
        view =
            if (viewId == android.R.id.button1 ||
                viewId == android.R.id.button2 ||
                viewId == android.R.id.button3
            ) {
              AestheticDialogButton(context, attrs)
            } else if (viewId == id.snackbar_action) {
              AestheticSnackBarButton(context, attrs)
            } else if (isBorderlessButton(
                    context, attrs
                )
            ) {
              AestheticBorderlessButton(context, attrs)
            } else {
              AestheticButton(context, attrs)
            }
      "$APPCOMPAT_WIDGET.AppCompatCheckBox", "CheckBox" ->
        view = AestheticCheckBox(context, attrs)
      "$APPCOMPAT_WIDGET.AppCompatRadioButton", "RadioButton" ->
        view = AestheticRadioButton(context, attrs)
      "$APPCOMPAT_WIDGET.AppCompatEditText", "EditText" ->
        view = AestheticEditText(context, attrs)
      "Switch" -> view = AestheticSwitch(context, attrs)
      "$APPCOMPAT_WIDGET.SwitchCompat" -> view =
          AestheticSwitchCompat(context, attrs)
      "$APPCOMPAT_WIDGET.AppCompatSeekBar", "SeekBar" ->
        view = AestheticSeekBar(context, attrs)
      "ProgressBar" ->
        view = AestheticProgressBar(context, attrs)
      "$APPCOMPAT_VIEW.ActionMenuItemView" ->
        view = AestheticActionMenuItemView(context, attrs)
      "CheckedTextView", "$APPCOMPAT_WIDGET.AppCompatCheckedTextView" ->
        view = AestheticCheckedTextView(context, attrs)

      "$APPCOMPAT_WIDGET.RecyclerView" ->
        view = AestheticRecyclerView(context, attrs)
      "$ANDROIDX_WIDGET.NestedScrollView" ->
        view = AestheticNestedScrollView(context, attrs)
      "ListView" -> view = AestheticListView(context, attrs)
      "ScrollView" -> view = AestheticScrollView(context, attrs)
      "androidx.viewpager.widget.ViewPager" -> view =
          AestheticViewPager(context, attrs)

      "Spinner", "$APPCOMPAT_WIDGET.AppCompatSpinner" ->
        view = AestheticSpinner(context, attrs)

      "$GOOGLE_MATERIAL.textfield.TextInputLayout" ->
        view = AestheticTextInputLayout(context, attrs)
      "$GOOGLE_MATERIAL.textfield.TextInputEditText" ->
        view = AestheticTextInputEditText(context, attrs)

      "$APPCOMPAT_WIDGET.CardView" -> view =
          AestheticCardView(context, attrs)
      "$GOOGLE_MATERIAL.tabs.TabLayout" -> view =
          AestheticTabLayout(context, attrs)
      "$GOOGLE_MATERIAL.navigation.NavigationView" ->
        view = AestheticNavigationView(context, attrs)
      "$GOOGLE_MATERIAL.bottomnavigation.BottomNavigationView" ->
        view = AestheticBottomNavigationView(context, attrs)
      "$GOOGLE_MATERIAL.floatingactionbutton.FloatingActionButton" ->
        view = AestheticFab(context, attrs)
      "androidx.coordinatorlayout.widget.CoordinatorLayout" ->
        view = AestheticCoordinatorLayout(context, attrs)
      "androidx.swiperefreshlayout.widget.SwipeRefreshLayout" ->
        view = AestheticSwipeRefreshLayout(context, attrs)
    }

    var viewBackgroundRes = 0
    if (view != null && view.tag != null && ":aesthetic_ignore" == view.tag) {
      // Set view back to null so we can let AppCompat handle this view instead.
      view = null
    } else if (attrs != null) {
      viewBackgroundRes = context.resId(attrs, android.R.attr.background)
    }

    // If view is null, let the activity try to create it
    if (view == null) {
      try {
        view = activity.onCreateView(
            parent,
            name,
            context,
            attrs
        )
        if (view == null) {
          view = activity.onCreateView(
              name,
              context,
              attrs
          )
        }
      } catch (e: Throwable) {
        throw IllegalStateException("Unable to delegate inflation of $name to your Activity.", e)
      }
    }
    // If it's still null, try the AppCompat delegate
    if (view == null && delegate != null && attrs != null) {
      try {
        view = delegate.createView(
            parent,
            name,
            context,
            attrs
        )
      } catch (e: Throwable) {
        throw IllegalStateException("Unable to delegate inflation of $name to AppCompat.", e)
      }
    }
    // If it's still null, use the LayoutInflater directly
    if (view == null) {
      try {
        val layoutInflater = context.fixedLayoutInflater()
        view = layoutInflater.createView(
            name,
            getViewPrefix(name),
            attrs
        )
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
    if (isBlackListedForApply(name)) {
      return view
    }

    if (viewBackgroundRes != 0) {
      addBackgroundSubscriber(
          view, watchColor(context, viewBackgroundRes)
      )
    }

    var idName = ""
    try {
      idName = "${context.resources.getResourceName(view.id)} "
    } catch (ignored: Throwable) {
    }

    log("Inflated -> $idName${view.javaClass.name}")

    return view
  }
}
