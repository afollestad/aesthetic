package com.afollestad.aesthetic.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.R;
import com.afollestad.aesthetic.TintHelper;

import java.lang.reflect.Field;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;
import static com.afollestad.aesthetic.Util.isColorLight;
import static com.afollestad.aesthetic.Util.setOverflowButtonColor;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
public class AestheticToolbar extends Toolbar {

  private int titleIconColor;
  private int backgroundResId;
  private Subscription subscription;
  private PublishSubject<Integer> onColorUpdated;

  public AestheticToolbar(Context context) {
    super(context);
    init(context, null);
  }

  public AestheticToolbar(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public AestheticToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    onColorUpdated = PublishSubject.create();
    if (context == null) {
      return;
    }
    if (attrs != null) {
      int[] attrsArray = new int[] {android.R.attr.background};
      TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
      backgroundResId = ta.getResourceId(0, 0);
      ta.recycle();
    }
  }

  private void invalidateColors(int color) {
    setBackgroundColor(color);
    this.titleIconColor = isColorLight(color) ? Color.BLACK : Color.WHITE;
    setTitleTextColor(titleIconColor);
    setOverflowButtonColor(this, titleIconColor);
    if (getNavigationIcon() != null) {
      setNavigationIcon(getNavigationIcon());
    }
    onColorUpdated.onNext(color);

    // The collapse icon displays when action views are expanded (e.g. SearchView)
    try {
      final Field field = Toolbar.class.getDeclaredField("mCollapseIcon");
      field.setAccessible(true);
      Drawable collapseIcon = (Drawable) field.get(this);
      if (collapseIcon != null)
        field.set(this, TintHelper.createTintedDrawable(collapseIcon, titleIconColor));
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Theme menu action views
    for (int i = 0; i < getMenu().size(); i++) {
      MenuItem item = getMenu().getItem(i);
      if (item.getActionView() instanceof SearchView) {
        themeSearchView(titleIconColor, (SearchView) item.getActionView());
      }
    }
  }

  private void themeSearchView(int tintColor, SearchView view) {
    final Class<?> cls = view.getClass();
    try {
      final Field mSearchSrcTextViewField = cls.getDeclaredField("mSearchSrcTextView");
      mSearchSrcTextViewField.setAccessible(true);
      final EditText mSearchSrcTextView = (EditText) mSearchSrcTextViewField.get(view);
      mSearchSrcTextView.setTextColor(tintColor);
      mSearchSrcTextView.setHintTextColor(
          ContextCompat.getColor(
              getContext(),
              isColorLight(tintColor)
                  ? R.color.ate_text_disabled_dark
                  : R.color.ate_text_disabled_light));
      TintHelper.setCursorTint(mSearchSrcTextView, tintColor);

      Field field = cls.getDeclaredField("mSearchButton");
      tintImageView(view, field, tintColor);
      field = cls.getDeclaredField("mGoButton");
      tintImageView(view, field, tintColor);
      field = cls.getDeclaredField("mCloseButton");
      tintImageView(view, field, tintColor);
      field = cls.getDeclaredField("mVoiceButton");
      tintImageView(view, field, tintColor);

      field = cls.getDeclaredField("mSearchPlate");
      field.setAccessible(true);
      TintHelper.setTintAuto((View) field.get(view), tintColor, true, !isColorLight(tintColor));

      field = cls.getDeclaredField("mSearchHintIcon");
      field.setAccessible(true);
      field.set(view, createTintedDrawable((Drawable) field.get(view), tintColor));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void tintImageView(Object target, Field field, int tintColor) throws Exception {
    field.setAccessible(true);
    final ImageView imageView = (ImageView) field.get(target);
    if (imageView.getDrawable() != null) {
      imageView.setImageDrawable(
          TintHelper.createTintedDrawable(imageView.getDrawable(), tintColor));
    }
  }

  public Observable<Integer> colorUpdated() {
    return onColorUpdated.asObservable();
  }

  @Override
  public void setNavigationIcon(@Nullable Drawable icon) {
    super.setNavigationIcon(createTintedDrawable(icon, titleIconColor));
  }

  public void setNavigationIcon(@Nullable Drawable icon, @ColorInt int color) {
    super.setNavigationIcon(createTintedDrawable(icon, color));
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subscription =
        ViewUtil.getObservableForResId(
                getContext(), backgroundResId, Aesthetic.get().primaryColor())
            .compose(distinctToMainThread())
            .subscribe(this::invalidateColors, onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    onColorUpdated = null;
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }
}
