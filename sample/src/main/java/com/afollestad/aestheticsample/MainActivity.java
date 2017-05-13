package com.afollestad.aestheticsample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.AestheticActivity;
import com.afollestad.aesthetic.BottomNavBgMode;
import com.afollestad.aesthetic.BottomNavIconTextMode;
import com.afollestad.aesthetic.NavigationViewMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/** @author Aidan Follestad (afollestad) */
public class MainActivity extends AestheticActivity {

  private Unbinder unbinder;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.pager)
  ViewPager pager;

  @BindView(R.id.tabs)
  TabLayout tabs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    unbinder = ButterKnife.bind(this);

    toolbar.inflateMenu(R.menu.main);

    // If we haven't set any defaults, do that now
    if (Aesthetic.isFirstTime()) {
      Aesthetic.get()
          .activityTheme(R.style.AppTheme)
          .primaryTextColorRes(R.color.text_color_primary)
          .secondaryTextColorRes(R.color.text_color_secondary)
          .primaryColorRes(R.color.md_white)
          .accentColorRes(R.color.md_blue)
          .statusBarColorAuto()
          .navBarColorAuto()
          .primaryTextColor(Color.BLACK)
          .navViewMode(NavigationViewMode.SELECTED_ACCENT)
          .bottomNavBgMode(BottomNavBgMode.PRIMARY)
          .bottomNavIconTextMode(BottomNavIconTextMode.SELECTED_ACCENT)
          .apply();
    }

    pager.setAdapter(new MainPagerAdapter(this, getSupportFragmentManager()));
    tabs.setupWithViewPager(pager);
  }

  @Override
  protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }
}
