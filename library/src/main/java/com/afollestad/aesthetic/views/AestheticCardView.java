package com.afollestad.aesthetic.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.R;

import rx.Observable;
import rx.Subscription;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
public class AestheticCardView extends CardView {

  private Subscription bgSubscription;
  private int backgroundResId;

  public AestheticCardView(Context context) {
    super(context);
  }

  public AestheticCardView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public AestheticCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    if (attrs != null) {
      TypedArray ta = context.obtainStyledAttributes(attrs, new int[] {android.R.attr.background});
      backgroundResId = ta.getResourceId(0, 0);
      ta.recycle();
      if (backgroundResId == 0) {
        ta = context.obtainStyledAttributes(attrs, new int[] {R.attr.cardBackgroundColor});
        backgroundResId = ta.getResourceId(0, 0);
        ta.recycle();
      }
    }
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    Observable<Integer> obs =
        ViewUtil.getObservableForResId(
            getContext(), backgroundResId, Aesthetic.get().colorCardViewBackground());
    if (obs != null) {
      bgSubscription =
          obs.compose(distinctToMainThread())
              .subscribe(this::setCardBackgroundColor, onErrorLogAndRethrow());
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    if (bgSubscription != null) {
      bgSubscription.unsubscribe();
    }
    super.onDetachedFromWindow();
  }
}
