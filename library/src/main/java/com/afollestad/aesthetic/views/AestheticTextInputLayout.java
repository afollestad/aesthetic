package com.afollestad.aesthetic.views;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.TintHelper;

import rx.subscriptions.CompositeSubscription;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.Util.adjustAlpha;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
public class AestheticTextInputLayout extends TextInputLayout {

  private int color;
  private boolean isDark;
  private CompositeSubscription subs;

  public AestheticTextInputLayout(Context context) {
    super(context);
  }

  public AestheticTextInputLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private void invalidateColors(int color) {
    this.color = color;
    TextInputLayoutUtil.setAccent(this, color);
    TintHelper.setTintAuto(getEditText(), color, false, isDark);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subs = new CompositeSubscription();
    subs.add(
        Aesthetic.get()
            .primaryTextColor()
            .compose(distinctToMainThread())
            .subscribe(color -> getEditText().setTextColor(color), onErrorLogAndRethrow()));
    subs.add(
        Aesthetic.get()
            .secondaryTextColor()
            .compose(distinctToMainThread())
            .subscribe(
                color -> TextInputLayoutUtil.setHint(this, adjustAlpha(color, 0.7f)),
                onErrorLogAndRethrow()));
    subs.add(
        Aesthetic.get()
            .accentColor()
            .compose(distinctToMainThread())
            .subscribe(this::invalidateColors, onErrorLogAndRethrow()));
    subs.add(
        Aesthetic.get()
            .isDark()
            .compose(distinctToMainThread())
            .subscribe(
                isDark -> {
                  this.isDark = isDark;
                  invalidateColors(color);
                },
                onErrorLogAndRethrow()));
  }

  @Override
  protected void onDetachedFromWindow() {
    subs.unsubscribe();
    super.onDetachedFromWindow();
  }
}
