package com.afollestad.aestheticsample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;

import com.afollestad.aesthetic.AestheticActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BottomNavActivity extends AestheticActivity {

  private Unbinder unbinder;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.bottom_tabs)
  BottomNavigationView bottomNavigationView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bottom_nav);
    unbinder = ButterKnife.bind(this);

    toolbar.setNavigationOnClickListener(view -> finish());
  }

  @Override
  protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }
}
