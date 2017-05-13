package com.afollestad.aestheticsample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.afollestad.aesthetic.AestheticActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/** @author Aidan Follestad (afollestad) */
public class CoordinatorLayoutActivity extends AestheticActivity {

  private Unbinder unbinder;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_collapsing_appbar);
    unbinder = ButterKnife.bind(this);

    toolbar.inflateMenu(R.menu.coordinatorlayout);
    toolbar.setNavigationOnClickListener(view -> finish());
  }

  @Override
  protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }
}
