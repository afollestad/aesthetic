/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.internal

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.AestheticActionMenuItemView
import com.afollestad.aesthetic.AestheticBorderlessButton
import com.afollestad.aesthetic.AestheticBottomNavigationView
import com.afollestad.aesthetic.AestheticButton
import com.afollestad.aesthetic.AestheticCardView
import com.afollestad.aesthetic.AestheticCheckBox
import com.afollestad.aesthetic.AestheticCoordinatorLayout
import com.afollestad.aesthetic.AestheticDialogButton
import com.afollestad.aesthetic.AestheticDrawerLayout
import com.afollestad.aesthetic.AestheticEditText
import com.afollestad.aesthetic.AestheticFab
import com.afollestad.aesthetic.AestheticImageButton
import com.afollestad.aesthetic.AestheticImageView
import com.afollestad.aesthetic.AestheticListView
import com.afollestad.aesthetic.AestheticNavigationView
import com.afollestad.aesthetic.AestheticNestedScrollView
import com.afollestad.aesthetic.AestheticProgressBar
import com.afollestad.aesthetic.AestheticRadioButton
import com.afollestad.aesthetic.AestheticRecyclerView
import com.afollestad.aesthetic.AestheticScrollView
import com.afollestad.aesthetic.AestheticSeekBar
import com.afollestad.aesthetic.AestheticSnackBarButton
import com.afollestad.aesthetic.AestheticSnackBarTextView
import com.afollestad.aesthetic.AestheticSpinner
import com.afollestad.aesthetic.AestheticSwipeRefreshLayout
import com.afollestad.aesthetic.AestheticSwitch
import com.afollestad.aesthetic.AestheticSwitchCompat
import com.afollestad.aesthetic.AestheticTabLayout
import com.afollestad.aesthetic.AestheticTextInputEditText
import com.afollestad.aesthetic.AestheticTextInputLayout
import com.afollestad.aesthetic.AestheticTextView
import com.afollestad.aesthetic.AestheticToolbar
import com.afollestad.aesthetic.AestheticViewPager
import com.afollestad.aesthetic.R.id
import com.afollestad.aesthetic.utils.ViewUtil.getObservableForResId
import com.afollestad.aesthetic.utils.resId

/** @author Aidan Follestad (afollestad) */
internal class InflationInterceptor(
  private val activity: AppCompatActivity,
  private val delegate: AppCompatDelegate?
) : LayoutInflater.Factory2 {

  companion object {

    private const val LOGGING_ENABLED = true

    @Suppress("ConstantConditionIf")
    private fun log(msg: String) {
      if (!LOGGING_ENABLED) return
      Log.d("InflationInterceptor", msg)
    }

    private fun isBlackListedForApply(name: String): Boolean {
      return ("android.support.design.internal.NavigationMenuItemView" == name ||
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
      "ImageView", "android.support.v7.widget.AppCompatImageView" ->
        view = AestheticImageView(context, attrs)
      "ImageButton", "android.support.v7.widget.AppCompatImageButton" ->
        view = AestheticImageButton(context, attrs)

      "android.support.v4.widget.DrawerLayout" ->
        view = AestheticDrawerLayout(context, attrs)
      "Toolbar", "android.support.v7.widget.Toolbar" ->
        view = AestheticToolbar(context, attrs)

      "android.support.v7.widget.AppCompatTextView", "TextView" ->
        if (viewId == id.snackbar_text) {
          view = AestheticSnackBarTextView(context, attrs)
        } else {
          view = AestheticTextView(context, attrs)
          if (parent is LinearLayout && view.id == android.R.id.message) {
            // This is for a toast message
            view = null
          }
        }
      "Button", "android.support.v7.widget.AppCompatButton" ->
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
      "android.support.v7.widget.AppCompatCheckBox", "CheckBox" ->
        view = AestheticCheckBox(context, attrs)
      "android.support.v7.widget.AppCompatRadioButton", "RadioButton" ->
        view = AestheticRadioButton(context, attrs)
      "android.support.v7.widget.AppCompatEditText", "EditText" ->
        view = AestheticEditText(context, attrs)
      "Switch" -> view = AestheticSwitch(context, attrs)
      "android.support.v7.widget.SwitchCompat" -> view =
          AestheticSwitchCompat(context, attrs)
      "android.support.v7.widget.AppCompatSeekBar", "SeekBar" ->
        view = AestheticSeekBar(context, attrs)
      "ProgressBar", "me.zhanghai.android.materialprogressbar.MaterialProgressBar" ->
        view = AestheticProgressBar(context, attrs)
      "android.support.v7.view.menu.ActionMenuItemView" ->
        view = AestheticActionMenuItemView(context, attrs)

      "android.support.v7.widget.RecyclerView" ->
        view = AestheticRecyclerView(context, attrs)
      "android.support.v4.widget.NestedScrollView" ->
        view = AestheticNestedScrollView(context, attrs)
      "ListView" -> view = AestheticListView(context, attrs)
      "ScrollView" -> view = AestheticScrollView(context, attrs)
      "android.support.v4.view.ViewPager" -> view =
          AestheticViewPager(context, attrs)

      "Spinner", "android.support.v7.widget.AppCompatSpinner" ->
        view = AestheticSpinner(context, attrs)

      "android.support.design.widget.TextInputLayout" ->
        view = AestheticTextInputLayout(context, attrs)
      "android.support.design.widget.TextInputEditText" ->
        view = AestheticTextInputEditText(context, attrs)

      "android.support.v7.widget.CardView" -> view =
          AestheticCardView(context, attrs)
      "android.support.design.widget.TabLayout" -> view =
          AestheticTabLayout(context, attrs)
      "android.support.design.widget.NavigationView" ->
        view = AestheticNavigationView(context, attrs)
      "android.support.design.widget.BottomNavigationView" ->
        view = AestheticBottomNavigationView(context, attrs)
      "android.support.design.widget.FloatingActionButton" ->
        view = AestheticFab(context, attrs)
      "android.support.design.widget.CoordinatorLayout" ->
        view = AestheticCoordinatorLayout(context, attrs)
      "android.support.v4.widget.SwipeRefreshLayout" ->
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
        val layoutInflater = LayoutInflater.from(context)
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
      val obs = getObservableForResId(context, viewBackgroundRes, null)
      if (obs != null) {
        Aesthetic.get()
            .addBackgroundSubscriber(view, obs)
      }
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
