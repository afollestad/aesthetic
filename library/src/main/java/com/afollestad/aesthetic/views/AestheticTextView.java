package com.afollestad.aesthetic.views;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;

import rx.Observable;
import rx.Subscription;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

@RestrictTo(LIBRARY_GROUP)
public class AestheticTextView extends AppCompatTextView {

  private Subscription subscription;

  public AestheticTextView(Context context) {
    super(context);
  }

  public AestheticTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    String idName;
    try {
      idName = getResources().getResourceName(getId());
    } catch (Exception e) {
      idName = null;
    }

    // TODO when to use inverse colors?

    Observable<Integer> obs;
    if (idName == null || idName.contains("title") || idName.contains("header")) {
      obs = Aesthetic.get().primaryTextColor();
    } else {
      obs = Aesthetic.get().secondaryTextColor();
    }
    subscription =
        obs.compose(distinctToMainThread()).subscribe(this::setTextColor, onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }
}
