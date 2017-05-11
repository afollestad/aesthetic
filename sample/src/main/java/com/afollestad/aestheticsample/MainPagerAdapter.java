package com.afollestad.aestheticsample;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/** @author Aidan Follestad (afollestad) */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

  private final Context context;

  public MainPagerAdapter(Context context, FragmentManager fm) {
    super(fm);
    this.context = context;
  }

  @Override
  public Fragment getItem(int position) {
    return position == 0 ? new MainFragment() : new SecondaryFragment();
  }

  @Override
  public int getCount() {
    return 2;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return context.getString(position == 0 ? R.string.main : R.string.other);
  }
}
