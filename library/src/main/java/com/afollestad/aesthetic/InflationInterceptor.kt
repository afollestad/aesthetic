package com.afollestad.aesthetic

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.LayoutInflaterFactory
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.view.ContextThemeWrapper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.afollestad.aesthetic.utils.ViewUtil
import com.afollestad.aesthetic.utils.resId
import io.reactivex.Observable
import java.lang.reflect.Field
import java.lang.reflect.Method

/** @author Aidan Follestad (afollestad) */
internal class InflationInterceptor(
  private val keyContext: AppCompatActivity?,
  private val layoutInflater: LayoutInflater,
  private val delegate: AppCompatDelegate?
) : LayoutInflaterFactory {

  private val onCreateViewMethod: Method
  private val createViewMethod: Method
  private val constructorArgsField: Field
  private var attrsTheme: IntArray? = null

  init {

    try {
      onCreateViewMethod = LayoutInflater::class.java.getDeclaredMethod(
          "onCreateView", View::class.java, String::class.java, AttributeSet::class.java
      )
    } catch (e: NoSuchMethodException) {
      throw IllegalStateException("Failed to retrieve the onCreateView method.", e)
    }

    try {
      createViewMethod = LayoutInflater::class.java.getDeclaredMethod(
          "createView", String::class.java, String::class.java, AttributeSet::class.java
      )
    } catch (e: NoSuchMethodException) {
      throw IllegalStateException("Failed to retrieve the createView method.", e)
    }

    try {
      constructorArgsField = LayoutInflater::class.java.getDeclaredField("mConstructorArgs")
    } catch (e: NoSuchFieldException) {
      throw IllegalStateException("Failed to retrieve the mConstructorArgs field.", e)
    }

    try {
      val attrsThemeField = LayoutInflater::class.java.getDeclaredField("attrsTheme")
      attrsThemeField.isAccessible = true
      attrsTheme = attrsThemeField.get(null) as IntArray
    } catch (t: Throwable) {
      t.printStackTrace()
      Log.d(
          "InflationInterceptor",
          "Failed to get the value of static field attrsTheme: " + t.message
      )
    }

    onCreateViewMethod.isAccessible = true
    createViewMethod.isAccessible = true
    constructorArgsField.isAccessible = true
  }

  private fun log(msg: String) {
    Log.d("InflationInterceptor", msg)
  }

  private fun isBlackListedForApply(name: String): Boolean {
    return ("android.support.design.internal.NavigationMenuItemView" == name
        || "ViewStub" == name
        || "fragment" == name
        || "include" == name)
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
      "ImageView", "android.support.v7.widget.AppCompatImageView" -> view =
          AestheticImageView(context, attrs)
      "ImageButton", "android.support.v7.widget.AppCompatImageButton" -> view =
          AestheticImageButton(context, attrs)

      "android.support.v4.widget.DrawerLayout" -> view = AestheticDrawerLayout(context, attrs)
      "Toolbar", "android.support.v7.widget.Toolbar" -> view = AestheticToolbar(context, attrs)

      "android.support.v7.widget.AppCompatTextView", "TextView" -> if (viewId == R.id.snackbar_text) {
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
            if (viewId == android.R.id.button1
                || viewId == android.R.id.button2
                || viewId == android.R.id.button3
            ) {
              AestheticDialogButton(context, attrs)
            } else if (viewId == R.id.snackbar_action) {
              AestheticSnackBarButton(context, attrs)
            } else {
              AestheticButton(context, attrs)
            }
      "android.support.v7.widget.AppCompatCheckBox", "CheckBox" -> view =
          AestheticCheckBox(context, attrs)
      "android.support.v7.widget.AppCompatRadioButton", "RadioButton" -> view =
          AestheticRadioButton(context, attrs)
      "android.support.v7.widget.AppCompatEditText", "EditText" -> view =
          AestheticEditText(context, attrs)
      "Switch" -> view = AestheticSwitch(context, attrs)
      "android.support.v7.widget.SwitchCompat" -> view = AestheticSwitchCompat(context, attrs)
      "android.support.v7.widget.AppCompatSeekBar", "SeekBar" -> view =
          AestheticSeekBar(context, attrs)
      "ProgressBar", "me.zhanghai.android.materialprogressbar.MaterialProgressBar" -> view =
          AestheticProgressBar(context, attrs)
      "android.support.v7.view.menu.ActionMenuItemView" -> view =
          AestheticActionMenuItemView(context, attrs)

      "android.support.v7.widget.RecyclerView" -> view = AestheticRecyclerView(context, attrs)
      "android.support.v4.widget.NestedScrollView" -> view =
          AestheticNestedScrollView(context, attrs)
      "ListView" -> view = AestheticListView(context, attrs)
      "ScrollView" -> view = AestheticScrollView(context, attrs)
      "android.support.v4.view.ViewPager" -> view = AestheticViewPager(context, attrs)

      "Spinner", "android.support.v7.widget.AppCompatSpinner" -> view =
          AestheticSpinner(context, attrs)

      "android.support.design.widget.TextInputLayout" -> view =
          AestheticTextInputLayout(context, attrs)
      "android.support.design.widget.TextInputEditText" -> view =
          AestheticTextInputEditText(context, attrs)

      "android.support.v7.widget.CardView" -> view = AestheticCardView(context, attrs)
      "android.support.design.widget.TabLayout" -> view = AestheticTabLayout(context, attrs)
      "android.support.design.widget.NavigationView" -> view =
          AestheticNavigationView(context, attrs)
      "android.support.design.widget.BottomNavigationView" -> view =
          AestheticBottomNavigationView(context, attrs)
      "android.support.design.widget.FloatingActionButton" -> view = AestheticFab(context, attrs)
      "android.support.design.widget.CoordinatorLayout" -> view =
          AestheticCoordinatorLayout(context, attrs)
    }

    var viewBackgroundRes = 0

    if (view != null && view.tag != null && ":aesthetic_ignore" == view.tag) {
      // Set view back to null so we can let AppCompat handle this view instead.
      view = null
    } else if (attrs != null) {
      viewBackgroundRes = context.resId(attrs, android.R.attr.background)
    }

    if (view == null) {
      // First, check if the AppCompatDelegate will give us a view, usually (maybe always) null.
      if (delegate != null) {
        view = delegate.createView(parent, name, context, attrs!!)
        if (view == null) {
          view = keyContext!!.onCreateView(parent, name, context, attrs)
        } else {
          view = null
        }
      } else {
        view = null
      }

      if (isBlackListedForApply(name)) {
        return view
      }

      // Mimic code of LayoutInflater using reflection tricks (this would normally be run when this
      // factory returns null).
      // We need to intercept the default behavior rather than allowing the LayoutInflater to handle
      // it after this method returns.
      if (view == null) {
        try {
          var viewContext = layoutInflater.context
          // Apply a theme wrapper, if requested.
          if (attrsTheme != null) {
            val ta = viewContext.obtainStyledAttributes(attrs, attrsTheme)
            val themeResId = ta.getResourceId(0, 0)
            if (themeResId != 0) {

              viewContext = ContextThemeWrapper(viewContext, themeResId)
            }
            ta.recycle()
          }

          val constructorArgs: Array<Any>
          try {
            constructorArgs = constructorArgsField.get(layoutInflater) as Array<Any>
          } catch (e: IllegalAccessException) {
            throw IllegalStateException(
                "Failed to retrieve the mConstructorArgsField field.", e
            )
          }

          val lastContext = constructorArgs[0]
          constructorArgs[0] = viewContext
          try {
            view = if (-1 == name.indexOf('.')) {
              onCreateViewMethod.invoke(layoutInflater, parent, name, attrs) as View
            } else {
              createViewMethod.invoke(layoutInflater, name, null, attrs) as View
            }
          } catch (e: Exception) {
            log("Failed to inflate $name: ${e.message}")
            e.printStackTrace()
          } finally {
            constructorArgs[0] = lastContext
          }
        } catch (t: Throwable) {
          throw RuntimeException(
              "An error occurred while inflating View $name: ${t.message}", t
          )
        }

      }
    }

    if (view != null) {
      if (viewBackgroundRes != 0) {
        val obs: Observable<Int>? =
          ViewUtil.getObservableForResId(view.context, viewBackgroundRes, null)
        if (obs != null) {
          Aesthetic.get().addBackgroundSubscriber(view, obs)
        }
      }

      var idName = ""
      try {
        idName = context.resources.getResourceName(view.id) + " "
      } catch (ignored: Throwable) {
      }

      log("Inflated -> $idName${view.javaClass.name}")
    }

    return view
  }
}
