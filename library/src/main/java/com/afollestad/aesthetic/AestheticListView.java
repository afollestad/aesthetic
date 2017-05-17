package com.afollestad.aesthetic;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import rx.Subscription;
import rx.functions.Action1;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/** @author Aidan Follestad (afollestad) */
public class AestheticListView extends ListView {

  private Subscription subscription;

  public AestheticListView(Context context) {
    super(context);
  }

  public AestheticListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private void invalidateColors(int color) {
    EdgeGlowUtil.setEdgeGlowColor(this, color);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subscription =
        Aesthetic.get()
            .colorAccent()
            .compose(Rx.<Integer>distinctToMainThread())
            .subscribe(
                new Action1<Integer>() {
                  @Override
                  public void call(Integer color) {
                    invalidateColors(color);
                  }
                },
                onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }
}
