package com.afollestad.aesthetic;

import static android.support.v7.appcompat.R.style.Widget_AppCompat_Button_Borderless;
import static android.support.v7.appcompat.R.style.Widget_AppCompat_Button_Borderless_Colored;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.Util.resolveResId;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/** @author Aidan Follestad (afollestad) */
public class AestheticButton extends AppCompatButton {

  private CompositeDisposable subscriptions;
  private int backgroundResId;
  private int styleResId;

  public AestheticButton(Context context) {
    super(context);
  }

  public AestheticButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public AestheticButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    if (attrs != null) {
      backgroundResId = resolveResId(context, attrs, android.R.attr.background);
      styleResId = attrs.getStyleAttribute();
    }
  }

  private void invalidateColors(ColorIsDarkState state) {

    final boolean isBorderless =
        styleResId == Widget_AppCompat_Button_Borderless_Colored
            || styleResId == Widget_AppCompat_Button_Borderless;

    TintHelper.setTintAuto(this, state.color(), !isBorderless, state.isDark());

    int enabled = Util.isColorLight(state.color()) ? Color.BLACK : Color.WHITE;
    int disabled = state.isDark() ? Color.WHITE : Color.BLACK;

    if (isBorderless) {
      // Invert of a normal/disabled control
      enabled =
          Util.stripAlpha(
              ContextCompat.getColor(
                  this.getContext(),
                  state.isDark()
                      ? R.color.ate_control_disabled_dark
                      : R.color.ate_control_disabled_light));
      disabled =
          ContextCompat.getColor(
              this.getContext(),
              state.isDark() ? R.color.ate_control_normal_dark : R.color.ate_control_normal_light);
    }

    ColorStateList textColorSl =
        new ColorStateList(
            new int[][] {
              new int[] {android.R.attr.state_enabled}, new int[] {-android.R.attr.state_enabled}
            },
            new int[] {enabled, disabled});

    setTextColor(textColorSl);

    // Hack around button color not updating
    setEnabled(!isEnabled());
    setEnabled(!isEnabled());
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subscriptions = new CompositeDisposable();
    //noinspection ConstantConditions
    subscriptions.add(
        Observable.combineLatest(
                ViewUtil.getObservableForResId(
                    getContext(), backgroundResId, Aesthetic.get().colorAccent()),
                Aesthetic.get().isDark(),
                ColorIsDarkState.creator())
            .compose(Rx.<ColorIsDarkState>distinctToMainThread())
            .subscribe(
                new Consumer<ColorIsDarkState>() {
                  @Override
                  public void accept(@NonNull ColorIsDarkState colorIsDarkState) {
                    invalidateColors(colorIsDarkState);
                  }
                },
                onErrorLogAndRethrow()));

    if (styleResId == Widget_AppCompat_Button_Borderless_Colored) {
      subscriptions.add(
          Aesthetic.get()
              .colorAccent()
              .compose(Rx.<Integer>distinctToMainThread())
              .subscribe(BorderlessColorStateAction.create(this)));
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    subscriptions.dispose();
    super.onDetachedFromWindow();
  }
}
