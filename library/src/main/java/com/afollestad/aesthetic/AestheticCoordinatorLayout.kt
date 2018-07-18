package com.afollestad.aesthetic

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.util.Pair
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.ActionMenuView
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import com.afollestad.aesthetic.utils.TintHelper
import com.afollestad.aesthetic.utils.adjustAlpha
import com.afollestad.aesthetic.utils.blendWith
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.isColorLight
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.setOverflowButtonColor
import com.afollestad.aesthetic.utils.tintMenu
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
class AestheticCoordinatorLayout(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr), AppBarLayout.OnOffsetChangedListener {

  private var toolbarColorSubscription: Disposable? = null
  private var statusBarColorSubscription: Disposable? = null
  private var appBarLayout: AppBarLayout? = null
  private var colorView: View? = null
  private var toolbar: AestheticToolbar? = null
  private var collapsingToolbarLayout: CollapsingToolbarLayout? = null

  private var toolbarColor: Int = 0
  private var iconTextColors: ActiveInactiveColors? = null
  private var lastOffset = -1

  private fun tintMenu(
    toolbar: AestheticToolbar,
    menu: Menu?,
    colors: ActiveInactiveColors
  ) {
    if (toolbar.navigationIcon != null) {
      toolbar.setNavigationIcon(toolbar.navigationIcon, colors.activeColor)
    }
    toolbar.setOverflowButtonColor(colors.activeColor)

    try {
      val field = Toolbar::class.java.getDeclaredField("mCollapseIcon")
      field.isAccessible = true
      val collapseIcon = field.get(toolbar) as? Drawable
      if (collapseIcon != null) {
        field.set(toolbar, TintHelper.createTintedDrawable(collapseIcon, colors.toEnabledSl()))
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

    val colorFilter = PorterDuffColorFilter(colors.activeColor, PorterDuff.Mode.SRC_IN)
    for (i in 0 until toolbar.childCount) {
      val v = toolbar.getChildAt(i)
      // We can't iterate through the toolbar.getMenu() here, because we need the
      // ActionMenuItemView.
      if (v is ActionMenuView) {
        for (j in 0 until v.childCount) {
          val innerView = v.getChildAt(j)
          if (innerView is ActionMenuItemView) {
            val drawablesCount = innerView.compoundDrawables.size
            for (k in 0 until drawablesCount) {
              if (innerView.compoundDrawables[k] != null) {
                innerView
                    .compoundDrawables[k].colorFilter = colorFilter
              }
            }
          }
        }
      }
    }
    toolbar.tintMenu(menu ?: toolbar.menu, colors)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    // Find the toolbar and color view used to blend the scroll transition
    if (childCount > 0 && getChildAt(0) is AppBarLayout) {
      appBarLayout = getChildAt(0) as AppBarLayout
      if (appBarLayout!!.childCount > 0 && appBarLayout!!.getChildAt(
              0
          ) is CollapsingToolbarLayout
      ) {
        collapsingToolbarLayout = appBarLayout!!.getChildAt(0) as CollapsingToolbarLayout
        for (i in 0 until collapsingToolbarLayout!!.childCount) {
          if (this.toolbar != null && this.colorView != null) {
            break
          }
          val child = collapsingToolbarLayout!!.getChildAt(i)
          if (child is AestheticToolbar) {
            this.toolbar = child
          } else if (child.background != null && child.background is ColorDrawable) {
            this.colorView = child
          }
        }
      }
    }

    if (toolbar != null && colorView != null) {
      this.appBarLayout!!.addOnOffsetChangedListener(this)
      toolbarColorSubscription =
          Observable.combineLatest<Int, ActiveInactiveColors, Pair<Int, ActiveInactiveColors>>(
              toolbar!!.colorUpdated(),
              Aesthetic.get().colorIconTitle(toolbar!!.colorUpdated()),
              BiFunction<Int, ActiveInactiveColors, Pair<Int, ActiveInactiveColors>> { a, b ->
                Pair.create(
                    a, b
                )
              })
              .distinctToMainThread()
              .subscribe(
                  Consumer {
                    toolbarColor = it.first!!
                    iconTextColors = it.second!!
                    invalidateColors()
                  },
                  onErrorLogAndRethrow()
              )
    }

    if (collapsingToolbarLayout != null) {
      statusBarColorSubscription = Aesthetic.get()
          .colorStatusBar()
          .distinctToMainThread()
          .subscribe(
              Consumer {
                collapsingToolbarLayout!!.setContentScrimColor(it)
                collapsingToolbarLayout!!.setStatusBarScrimColor(it)
              },
              onErrorLogAndRethrow()
          )
    }
  }

  override fun onDetachedFromWindow() {
    toolbarColorSubscription?.dispose()
    statusBarColorSubscription?.dispose()
    this.appBarLayout?.removeOnOffsetChangedListener(this)
    this.appBarLayout = null
    this.toolbar = null
    this.colorView = null
    super.onDetachedFromWindow()
  }

  override fun onOffsetChanged(
    appBarLayout: AppBarLayout,
    verticalOffset: Int
  ) {
    if (lastOffset == Math.abs(verticalOffset)) {
      return
    }
    lastOffset = Math.abs(verticalOffset)
    invalidateColors()
  }

  private fun invalidateColors() {
    if (iconTextColors == null) {
      return
    }

    val maxOffset = appBarLayout!!.measuredHeight - toolbar!!.measuredHeight
    val ratio = lastOffset.toFloat() / maxOffset.toFloat()

    val colorViewColor = (colorView!!.background as ColorDrawable).color
    val blendedColor = colorViewColor.blendWith(toolbarColor, ratio)
    val collapsedTitleColor = iconTextColors!!.activeColor
    val expandedTitleColor = if (colorViewColor.isColorLight()) Color.BLACK else Color.WHITE
    val blendedTitleColor = expandedTitleColor.blendWith(collapsedTitleColor, ratio)

    toolbar!!.setBackgroundColor(blendedColor)

    collapsingToolbarLayout!!.setCollapsedTitleTextColor(collapsedTitleColor)
    collapsingToolbarLayout!!.setExpandedTitleColor(expandedTitleColor)

    tintMenu(
        toolbar!!,
        toolbar!!.menu,
        ActiveInactiveColors(
            blendedTitleColor, blendedColor.adjustAlpha(0.7f)
        )
    )
  }
}
