package com.afollestad.aestheticsample;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.afollestad.aesthetic.AestheticActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DrawerActivity extends AestheticActivity {

  private Unbinder unbinder;
  private ActionBarDrawerToggle drawerToggle;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.drawer_layout)
  DrawerLayout drawerLayout;

  @BindView(R.id.navigation_view)
  NavigationView navigationView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_drawer);
    unbinder = ButterKnife.bind(this);
    setSupportActionBar(toolbar);

    drawerToggle =
        new ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
    drawerLayout.addDrawerListener(drawerToggle);

    //noinspection ConstantConditions
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

    navigationView.post(() -> navigationView.setCheckedItem(R.id.item_three));
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    drawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    drawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
  }

  @Override
  protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }
}
