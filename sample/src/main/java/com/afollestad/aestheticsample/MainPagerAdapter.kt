package com.afollestad.aestheticsample

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/** @author Aidan Follestad (afollestad)
 */
internal class MainPagerAdapter(
  private val context: Context,
  fm: FragmentManager
) : FragmentStatePagerAdapter(fm) {

  override fun getItem(position: Int): Fragment {
    return if (position == 0) MainFragment() else SecondaryFragment()
  }

  override fun getCount(): Int {
    return 2
  }

  override fun getPageTitle(position: Int): CharSequence? {
    return context.getString(if (position == 0) R.string.main else R.string.other)
  }
}
