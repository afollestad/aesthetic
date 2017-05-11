package com.afollestad.aesthetic;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.aesthetic.views.AestheticActionMenuItemView;
import com.afollestad.aesthetic.views.AestheticButton;
import com.afollestad.aesthetic.views.AestheticCheckBox;
import com.afollestad.aesthetic.views.AestheticEditText;
import com.afollestad.aesthetic.views.AestheticProgressBar;
import com.afollestad.aesthetic.views.AestheticRadioButton;
import com.afollestad.aesthetic.views.AestheticSeekBar;
import com.afollestad.aesthetic.views.AestheticSwitch;
import com.afollestad.aesthetic.views.AestheticSwitchCompat;
import com.afollestad.aesthetic.views.AestheticTextView;
import com.afollestad.aesthetic.views.AestheticToolbar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
final class InflationInterceptor implements LayoutInflaterFactory {

  private static final boolean LOGGING_ENABLED = true;

  private static void LOG(String msg, Object... args) {
    //noinspection PointlessBooleanExpression
    if (!LOGGING_ENABLED) return;
    if (args != null) {
      Log.d("InflationInterceptor", String.format(msg, args));
    } else {
      Log.d("InflationInterceptor", msg);
    }
  }

  private final AppCompatActivity keyContext;
  @NonNull private final LayoutInflater layoutInflater;
  @Nullable private AppCompatDelegate delegate;
  private static Method onCreateViewMethod;
  private static Method createViewMethod;
  private static Field constructorArgsField;
  private static int[] ATTRS_THEME;

  InflationInterceptor(
      @Nullable AppCompatActivity keyContext,
      @NonNull LayoutInflater li,
      @Nullable AppCompatDelegate delegate) {
    this.keyContext = keyContext;
    layoutInflater = li;
    this.delegate = delegate;
    if (onCreateViewMethod == null) {
      try {
        onCreateViewMethod =
            LayoutInflater.class.getDeclaredMethod(
                "onCreateView", View.class, String.class, AttributeSet.class);
      } catch (NoSuchMethodException e) {
        throw new RuntimeException("Failed to retrieve the onCreateView method.", e);
      }
    }
    if (createViewMethod == null) {
      try {
        createViewMethod =
            LayoutInflater.class.getDeclaredMethod(
                "createView", String.class, String.class, AttributeSet.class);
      } catch (NoSuchMethodException e) {
        throw new RuntimeException("Failed to retrieve the createView method.", e);
      }
    }
    if (constructorArgsField == null) {
      try {
        constructorArgsField = LayoutInflater.class.getDeclaredField("mConstructorArgs");
      } catch (NoSuchFieldException e) {
        throw new RuntimeException("Failed to retrieve the mConstructorArgs field.", e);
      }
    }
    if (ATTRS_THEME == null) {
      try {
        final Field attrsThemeField = LayoutInflater.class.getDeclaredField("ATTRS_THEME");
        attrsThemeField.setAccessible(true);
        ATTRS_THEME = (int[]) attrsThemeField.get(null);
      } catch (Throwable t) {
        t.printStackTrace();
        Log.d(
            "InflationInterceptor",
            "Failed to get the value of static field ATTRS_THEME: " + t.getMessage());
      }
    }
    onCreateViewMethod.setAccessible(true);
    createViewMethod.setAccessible(true);
    constructorArgsField.setAccessible(true);
  }

  private boolean isBlackListedForApply(String name) {
    return name.equals("android.support.design.internal.NavigationMenuItemView")
        || name.equals("ViewStub")
        || name.equals("fragment")
        || name.equals("include")
        || name.equals("android.support.design.internal.NavigationMenuItemView");
  }

  @Override
  public View onCreateView(View parent, final String name, Context context, AttributeSet attrs) {
    View view = null;

    switch (name) {
      case "android.support.v7.widget.AppCompatTextView":
      case "TextView":
        view = new AestheticTextView(context, attrs);
        if (parent instanceof LinearLayout && view.getId() == android.R.id.message) {
          // This is for a toast message
          view = null;
        }
        break;
      case "Toolbar":
      case "android.support.v7.widget.Toolbar":
        view = new AestheticToolbar(context, attrs);
        break;
      case "Button":
      case "android.support.v7.widget.AppCompatButton":
        view = new AestheticButton(context, attrs);
        break;
      case "android.support.v7.widget.AppCompatCheckBox":
      case "CheckBox":
        view = new AestheticCheckBox(context, attrs);
        break;
      case "android.support.v7.widget.AppCompatRadioButton":
      case "RadioButton":
        view = new AestheticRadioButton(context, attrs);
        break;
      case "android.support.v7.widget.AppCompatEditText":
      case "EditText":
        view = new AestheticEditText(context, attrs);
        break;
      case "Switch":
        view = new AestheticSwitch(context, attrs);
        break;
      case "android.support.v7.widget.SwitchCompat":
        view = new AestheticSwitchCompat(context, attrs);
        break;
      case "android.support.v7.widget.AppCompatSeekBar":
      case "SeekBar":
        view = new AestheticSeekBar(context, attrs);
        break;
      case "ProgressBar":
        view = new AestheticProgressBar(context, attrs);
        break;
      case "android.support.v7.view.menu.ActionMenuItemView":
        view = new AestheticActionMenuItemView(context, attrs);
        break;

        //      case "android.support.v7.widget.AppCompatAutoCompleteTextView":
        //      case "AutoCompleteTextView":
        //        view =
        //            new ATEAutoCompleteTextView(
        //                context, attrs, keyContext, parent != null && parent instanceof TextInputLayout);
        //        break;
        //      case "android.support.v7.widget.AppCompatMultiAutoCompleteTextView":
        //      case "MultiAutoCompleteTextView":
        //        view =
        //            new ATEMultiAutoCompleteTextView(
        //                context, attrs, keyContext, parent != null && parent instanceof TextInputLayout);
        //        break;
        //      case "ListView":
        //        view = new ATEListView(context, attrs, keyContext);
        //        break;
        //      case "ScrollView":
        //        view = new ATEScrollView(context, attrs, keyContext);
        //        break;
        //      case "Spinner":
        //        view = new ATEStockSpinner(context, attrs, keyContext);
        //        break;
        //      case "android.support.v7.widget.AppCompatSpinner":
        //        view = new ATESpinner(context, attrs, keyContext);
        //        break;
        //      case "android.support.design.widget.FloatingActionButton":
        //        view = new ATEFloatingActionButton(context, attrs, keyContext);
        //        break;
        //      case EdgeGlowTagProcessor.RECYCLERVIEW_CLASS:
        //        view = new ATERecyclerView(context, attrs, keyContext);
        //        break;
        //      case EdgeGlowTagProcessor.NESTEDSCROLLVIEW_CLASS:
        //        view = new ATENestedScrollView(context, attrs, keyContext);
        //        break;
        //      case "android.support.v4.widget.DrawerLayout":
        //        view = new ATEDrawerLayout(context, attrs, keyContext);
        //        break;
        //      case NavigationViewProcessor.MAIN_CLASS:
        //        view = new ATENavigationView(context, attrs, keyContext);
        //        break;
        //      case TabLayoutTagProcessor.MAIN_CLASS:
        //        view = new ATETabLayout(context, attrs, keyContext);
        //        break;
        //      case EdgeGlowTagProcessor.VIEWPAGER_CLASS:
        //        view = new ATEViewPager(context, attrs, keyContext);
        //        break;
        //      case "android.support.design.widget.CoordinatorLayout":
        //        view = new ATECoordinatorLayout(context, attrs, keyContext);
        //        break;
        //      case "android.support.v7.widget.SearchView$SearchAutoComplete":
        //        view = new ATESearchViewAutoComplete(context, attrs, keyContext);
        //        break;
        //      case "CheckedTextView":
        //        view = new ATECheckedTextView(context, attrs, keyContext);
        //        break;
    }

    if (view == null) {
      // First, check if the AppCompatDelegate will give us a view, usually (maybe always) null.
      if (delegate != null) {
        view = delegate.createView(parent, name, context, attrs);
        if (view == null) {
          view = keyContext.onCreateView(parent, name, context, attrs);
        } else {
          view = null;
        }
      } else {
        view = null;
      }

      if (isBlackListedForApply(name)) {
        return view;
      }

      // Mimic code of LayoutInflater using reflection tricks (this would normally be run when this factory returns null).
      // We need to intercept the default behavior rather than allowing the LayoutInflater to handle it after this method returns.
      if (view == null) {
        try {
          Context viewContext;
          final boolean inheritContext = false; // TODO will this ever need to be true?
          //noinspection PointlessBooleanExpression,ConstantConditions
          if (parent != null && inheritContext) {
            viewContext = parent.getContext();
          } else {
            viewContext = layoutInflater.getContext();
          }
          // Apply a theme wrapper, if requested.
          if (ATTRS_THEME != null) {
            final TypedArray ta = viewContext.obtainStyledAttributes(attrs, ATTRS_THEME);
            final int themeResId = ta.getResourceId(0, 0);
            if (themeResId != 0) {
              //noinspection RestrictedApi
              viewContext = new ContextThemeWrapper(viewContext, themeResId);
            }
            ta.recycle();
          }

          Object[] mConstructorArgs;
          try {
            mConstructorArgs = (Object[]) constructorArgsField.get(layoutInflater);
          } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to retrieve the mConstructorArgsField field.", e);
          }

          final Object lastContext = mConstructorArgs[0];
          mConstructorArgs[0] = viewContext;
          try {
            if (-1 == name.indexOf('.')) {
              view = (View) onCreateViewMethod.invoke(layoutInflater, parent, name, attrs);
            } else {
              view = (View) createViewMethod.invoke(layoutInflater, name, null, attrs);
            }
          } catch (Exception e) {
            LOG("Failed to inflate %s: %s", name, e.getMessage());
            e.printStackTrace();
          } finally {
            mConstructorArgs[0] = lastContext;
          }
        } catch (Throwable t) {
          throw new RuntimeException(
              String.format("An error occurred while inflating View %s: %s", name, t.getMessage()),
              t);
        }
      }
    }

    Resources res = context.getResources();
    //noinspection ResourceType
    String idName =
        view != null && view.getId() != 0 && view.getId() != 0xffffffff
            ? " " + res.getResourceName(view.getId()) + " "
            : "";
    LOG("%s inflated to ->%s%s", name, idName, view != null ? view.getClass().getName() : "(null)");

    return view;
  }
}
