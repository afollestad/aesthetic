/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aestheticsample

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

  private var snackbar: Snackbar? = null
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
          activityTheme(R.style.AppThemeDark)
          isDark(true)
          textColorPrimaryRes(R.color.text_color_primary_dark)
          textColorSecondaryRes(R.color.text_color_secondary_dark)
        }
      } else {
        Aesthetic.config {
          activityTheme(R.style.AppTheme)
          isDark(false)
          textColorPrimaryRes(R.color.text_color_primary)
          textColorSecondaryRes(R.color.text_color_secondary)
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
      snackbar?.dismiss()
      snackbar = Snackbar.make(view, R.string.hello_world, Snackbar.LENGTH_LONG)
      with(snackbar!!) {
        setAction(android.R.string.cancel) { _ -> }
        show()
      }
    }

    btn_black.setOnClickListener { onClickButton(it) }
    btn_red.setOnClickListener { onClickButton(it) }
    btn_purple.setOnClickListener { onClickButton(it) }
    btn_blue.setOnClickListener { onClickButton(it) }
    btn_green.setOnClickListener { onClickButton(it) }
    btn_white.setOnClickListener { onClickButton(it) }
  }

  override fun onDestroyView() {
    isDarkSubscription!!.dispose()
    super.onDestroyView()
  }

  private fun onClickButton(view: View) {
    when (view.id) {
      R.id.btn_black -> Aesthetic.config {
        colorPrimaryRes(R.color.text_color_primary)
        colorAccentRes(R.color.md_purple)
        colorStatusBarAuto()
        colorNavigationBarAuto()
        bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
        bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
        swipeRefreshLayoutColorsRes(R.color.md_purple)
        attributeRes(R.attr.my_custom_attr, R.color.md_amber)
      }
      R.id.btn_red -> Aesthetic.config {
        colorPrimaryRes(R.color.md_red)
        colorAccentRes(R.color.md_amber)
        colorStatusBarAuto()
        colorNavigationBarAuto()
        bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
        bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
        swipeRefreshLayoutColorsRes(R.color.md_red, R.color.md_amber)
        attributeRes(R.attr.my_custom_attr, R.color.md_blue)
      }
      R.id.btn_purple -> Aesthetic.config {
        colorPrimaryRes(R.color.md_purple)
        colorAccentRes(R.color.md_lime)
        colorStatusBarAuto()
        colorNavigationBarAuto()
        bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
        bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
        swipeRefreshLayoutColorsRes(R.color.md_purple, R.color.md_lime)
        attributeRes(R.attr.my_custom_attr, R.color.md_green)
      }
      R.id.btn_blue -> Aesthetic.config {
        colorPrimaryRes(R.color.md_blue)
        colorAccentRes(R.color.md_pink)
        colorStatusBarAuto()
        colorNavigationBarAuto()
        bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
        bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
        swipeRefreshLayoutColorsRes(R.color.md_blue, R.color.md_pink)
        attributeRes(R.attr.my_custom_attr, R.color.md_purple)
      }
      R.id.btn_green -> Aesthetic.config {
        colorPrimaryRes(R.color.md_green)
        colorAccentRes(R.color.md_blue_grey)
        colorStatusBarAuto()
        colorNavigationBarAuto()
        bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
        bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
        swipeRefreshLayoutColorsRes(R.color.md_green, R.color.md_blue_grey)
        attributeRes(R.attr.my_custom_attr, R.color.md_pink)
      }
      R.id.btn_white -> Aesthetic.config {
        colorPrimaryRes(R.color.md_white)
        colorAccentRes(R.color.md_blue)
        colorStatusBarAuto()
        colorNavigationBarAuto()
        bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY)
        bottomNavigationIconTextMode(BottomNavIconTextMode.SELECTED_ACCENT)
        swipeRefreshLayoutColorsRes(R.color.md_blue)
        attributeRes(R.attr.my_custom_attr, R.color.md_lime)
      }
    }
  }
}
