package com.afollestad.aesthetic;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import rx.Subscription;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/** @author Aidan Follestad (afollestad) */
final class AestheticSnackBarTextView extends AppCompatTextView {

  private Subscription subscription;

  public AestheticSnackBarTextView(Context context) {
    super(context);
  }

  public AestheticSnackBarTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticSnackBarTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subscription =
        Aesthetic.get()
            .snackbarTextColor()
            .compose(Rx.<Integer>distinctToMainThread())
            .subscribe(ViewTextColorAction.create(this), onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }
}
