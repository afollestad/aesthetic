/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afollestad.aestheticsample.materialcomponents

import android.os.Bundle
import android.view.View
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.AestheticActivity
import com.afollestad.aesthetic.BottomNavBgMode
import com.afollestad.aesthetic.BottomNavIconTextMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import kotlinx.android.synthetic.main.activity_demo_materialcomponents.bar
import kotlinx.android.synthetic.main.activity_demo_materialcomponents.bottom_drawer
import kotlinx.android.synthetic.main.main_content.btn_black
import kotlinx.android.synthetic.main.main_content.btn_blue
import kotlinx.android.synthetic.main.main_content.btn_green
import kotlinx.android.synthetic.main.main_content.btn_purple
import kotlinx.android.synthetic.main.main_content.btn_red
import kotlinx.android.synthetic.main.main_content.btn_white

/** @author Aidan Follestad (afollestad) */
class MaterialComponentsDemoActivity : AestheticActivity() {

  private var bottomDrawerBehavior: BottomSheetBehavior<View>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_demo_materialcomponents)

    // If we haven't set any defaults, do that now
    if (Aesthetic.isFirstTime) {
      Aesthetic.config {
        activityTheme(R.style.MaterialComponentsDemoTheme)

        attribute(R.attr.my_custom_attr, res = R.color.md_red)
      }
    }

    setUpBottomDrawer()

    listOf(btn_black, btn_red, btn_purple, btn_blue, btn_green, btn_white)
        .forEach { it.setOnClickListener { btn -> onClickButton(btn) } }
  }

  override fun onBackPressed() {
    if (bottomDrawerBehavior?.state != STATE_HIDDEN) {
      bottomDrawerBehavior?.state = STATE_HIDDEN
      return
    }
    super.onBackPressed()
  }

  private fun setUpBottomDrawer() {
    bottomDrawerBehavior = BottomSheetBehavior.from(bottom_drawer)
    bottomDrawerBehavior!!.state = STATE_HIDDEN

    bar.setNavigationOnClickListener {
      bottomDrawerBehavior?.state = STATE_HALF_EXPANDED
    }
    bar.setNavigationIcon(R.drawable.ic_drawer_menu_24px)
    bar.replaceMenu(R.menu.demo_primary)
  }

  private fun onClickButton(view: View) {
    when (view.id) {
      R.id.btn_black -> Aesthetic.config {
        colorPrimary(res = R.color.text_color_primary)
        colorAccent(res = R.color.md_purple)
        colorStatusBarAuto()
        colorNavigationBarAuto()
        bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
        bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
        swipeRefreshLayoutColorsRes(R.color.md_purple)
        attribute(R.attr.my_custom_attr, res = R.color.md_amber)

        snackbarBackgroundColorDefault()
        snackbarTextColorDefault()
      }
      R.id.btn_red -> Aesthetic.config {
        colorPrimary(res = R.color.md_red)
        colorAccent(res = R.color.md_amber)
        colorStatusBarAuto()
        colorNavigationBarAuto()
        bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
        bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
        swipeRefreshLayoutColorsRes(
            R.color.md_red,
            R.color.md_amber
        )
        attribute(R.attr.my_custom_attr, res = R.color.md_blue)

        snackbarBackgroundColorDefault()
        snackbarTextColorDefault()
      }
      R.id.btn_purple -> Aesthetic.config {
        colorPrimary(res = R.color.md_purple)
        colorAccent(res = R.color.md_lime)
        colorStatusBarAuto()
        colorNavigationBarAuto()
        bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
        bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
        swipeRefreshLayoutColorsRes(
            R.color.md_purple,
            R.color.md_lime
        )
        attribute(R.attr.my_custom_attr, res = R.color.md_green)

        snackbarBackgroundColorDefault()
        snackbarTextColorDefault()
      }
      R.id.btn_blue -> Aesthetic.config {
        colorPrimary(res = R.color.md_blue)
        colorAccent(res = R.color.md_pink)
        colorStatusBarAuto()
        colorNavigationBarAuto()
        bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
        bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
        swipeRefreshLayoutColorsRes(
            R.color.md_blue,
            R.color.md_pink
        )
        attribute(R.attr.my_custom_attr, res = R.color.md_purple)

        snackbarBackgroundColorDefault()
        snackbarTextColorDefault()
      }
      R.id.btn_green -> Aesthetic.config {
        colorPrimary(res = R.color.md_green)
        colorAccent(res = R.color.md_blue_grey)
        colorStatusBarAuto()
        colorNavigationBarAuto()
        bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
        bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
        swipeRefreshLayoutColorsRes(
            R.color.md_green,
            R.color.md_blue_grey
        )
        attribute(R.attr.my_custom_attr, res = R.color.md_pink)

        snackbarBackgroundColorDefault()
        snackbarTextColorDefault()
      }
      R.id.btn_white -> Aesthetic.config {
        colorPrimary(res = R.color.md_white)
        colorAccent(res = R.color.md_blue)
        colorStatusBarAuto()
        colorNavigationBarAuto()
        bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY)
        bottomNavigationIconTextMode(BottomNavIconTextMode.SELECTED_ACCENT)
        swipeRefreshLayoutColorsRes(R.color.md_blue)
        attribute(R.attr.my_custom_attr, res = R.color.md_lime)

        snackbarBackgroundColor(res = R.color.md_white)
        snackbarTextColor(res = android.R.color.black)
      }
    }
  }
}
