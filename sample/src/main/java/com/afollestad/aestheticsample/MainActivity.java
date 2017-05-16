package com.afollestad.aestheticsample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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

  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.pager)
  ViewPager pager;
  @BindView(R.id.tabs)
  TabLayout tabs;
  private Unbinder unbinder;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    unbinder = ButterKnife.bind(this);

    toolbar.inflateMenu(R.menu.main);
    final MenuItem searchItem = toolbar.getMenu().findItem(R.id.search);
    final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    searchView.setQueryHint(getString(R.string.search_view_example));

    // If we haven't set any defaults, do that now
    if (Aesthetic.isFirstTime()) {
      Aesthetic.get()
          .activityTheme(R.style.AppTheme)
          .textColorPrimaryRes(R.color.text_color_primary)
          .textColorSecondaryRes(R.color.text_color_secondary)
          .colorPrimaryRes(R.color.md_white)
          .colorAccentRes(R.color.md_blue)
          .colorStatusBarAuto()
          .colorNavigationBarAuto()
          .textColorPrimary(Color.BLACK)
          .navigationViewMode(NavigationViewMode.SELECTED_ACCENT)
          .bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY)
          .bottomNavigationIconTextMode(BottomNavIconTextMode.SELECTED_ACCENT)
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
