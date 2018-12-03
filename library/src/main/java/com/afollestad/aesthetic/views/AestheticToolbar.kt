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
package com.afollestad.aesthetic.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.R
import com.afollestad.aesthetic.internal.AttrWizard
import com.afollestad.aesthetic.utils.darkenColor
import com.afollestad.aesthetic.utils.observableForAttrName
import com.afollestad.aesthetic.utils.setOverflowButtonColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.tint
import com.afollestad.aesthetic.utils.tintMenu
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import io.reactivex.subjects.PublishSubject

/** @author Aidan Follestad (afollestad) */
class AestheticToolbar(
  context: Context,
  attrs: AttributeSet? = null
) : Toolbar(context, attrs) {

  private var onColorUpdated = PublishSubject.create<Int>()
  private var menuIconColor: Int? = null

  private val wizard = AttrWizard(context, attrs)
  private val backgroundColorValue = wizard.getRawValue(android.R.attr.background)
  private val titleTextColorValue = wizard.getRawValue(R.attr.titleTextColor)
  private val subtitleTextColorValue = wizard.getRawValue(R.attr.subtitleTextColor)

  fun colorUpdated() = onColorUpdated

  override fun setNavigationIcon(icon: Drawable?) {
    if (menuIconColor == null) {
      super.setNavigationIcon(icon)
      return
    }
    super.setNavigationIcon(icon.tint(menuIconColor!!))
  }

  fun setNavigationIcon(icon: Drawable?, @ColorInt color: Int) {
    if (menuIconColor == null) {
      super.setNavigationIcon(icon)
      return
    }
    super.setNavigationIcon(icon.tint(color))
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    get().observableForAttrName(
        backgroundColorValue,
        get().colorPrimary()
    )!!
        .distinctUntilChanged()
        .doOnNext { onColorUpdated.onNext(it) }
        .subscribeTo(::setBackgroundColor)
        .unsubscribeOnDetach(this)

    get().toolbarIconColor()
        .distinctUntilChanged()
        .subscribeTo(::invalidateColors)
        .unsubscribeOnDetach(this)

    get().observableForAttrName(
        titleTextColorValue,
        get().toolbarTitleColor()
    )
        ?.distinctUntilChanged()
        ?.subscribeTo(::setTitleTextColor)
        ?.unsubscribeOnDetach(this)

    get().observableForAttrName(
        subtitleTextColorValue,
        get().toolbarSubtitleColor()
    )
        ?.distinctUntilChanged()
        ?.subscribeTo(::setSubtitleTextColor)
        ?.unsubscribeOnDetach(this)
  }

  private fun invalidateColors(color: Int) {
    this.menuIconColor = color
    setOverflowButtonColor(color)
    tintMenu(menu, color, color.darkenColor())
    if (navigationIcon != null) {
      this.navigationIcon = navigationIcon
    }
  }
}
