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
package com.afollestad.aestheticsample.appcompat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.BottomNavBgMode
import com.afollestad.aesthetic.BottomNavIconTextMode
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_main.btn_black
import kotlinx.android.synthetic.main.fragment_main.btn_blue
import kotlinx.android.synthetic.main.fragment_main.btn_green
import kotlinx.android.synthetic.main.fragment_main.btn_purple
import kotlinx.android.synthetic.main.fragment_main.btn_red
import kotlinx.android.synthetic.main.fragment_main.btn_white
import kotlinx.android.synthetic.main.fragment_main.view.btn_dialog
import kotlinx.android.synthetic.main.fragment_main.view.fab
import kotlinx.android.synthetic.main.fragment_main.view.spinner
import kotlinx.android.synthetic.main.fragment_main.view.switch_theme

/** @author Aidan Follestad (afollestad) */
class MainFragment : Fragment() {

  private var snackBar: Snackbar? = null
  private var isDarkSubscription: Disposable? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_main, container, false)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    // Update the dark theme switch to the last saved isDark value.
    isDarkSubscription = Aesthetic.get()
        .isDark.subscribe { view.switch_theme.isChecked = it }

    // Further view setup
    val spinnerAdapter = ArrayAdapter(
        context!!,
        R.layout.list_item_spinner,
        arrayOf(
            "Spinner One", "Spinner Two", "Spinner Three",
            "Spinner Four", "Spinner Five", "Spinner Six"
        )
    )
    spinnerAdapter.setDropDownViewResource(R.layout.list_item_spinner_dropdown)
    view.spinner.adapter = spinnerAdapter

    view.switch_theme.setOnClickListener {
      if (view.switch_theme.isChecked) {
        Aesthetic.config {
          activityTheme(R.style.AppCompatDemoThemeDark)
          isDark(true)
          textColorPrimary(res = R.color.text_color_primary_dark)
          textColorSecondary(res = R.color.text_color_secondary_dark)
        }
      } else {
        Aesthetic.config {
          activityTheme(R.style.AppCompatDemoTheme)
          isDark(false)
          textColorPrimary(res = R.color.text_color_primary)
          textColorSecondary(res = R.color.text_color_secondary)
        }
      }
    }

    view.btn_dialog.setOnClickListener {
      AlertDialog.Builder(activity!!)
          .setTitle(R.string.hello_world)
          .setMessage(R.string.lorem_ipsum)
          .setPositiveButton(android.R.string.ok) { _, _ -> }
          .setNegativeButton(android.R.string.cancel) { _, _ -> }
          .show()
    }

    view.fab.setOnClickListener {
      snackBar?.dismiss()
      snackBar = Snackbar.make(view, R.string.hello_world, Snackbar.LENGTH_LONG)
      with(snackBar!!) {
        setAction(android.R.string.cancel) { }
        show()
      }
    }

    listOf(btn_black, btn_red, btn_purple, btn_blue, btn_green, btn_white)
        .forEach { it.setOnClickListener { btn -> onClickButton(btn) } }
  }

  override fun onDestroyView() {
    isDarkSubscription!!.dispose()
    super.onDestroyView()
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
