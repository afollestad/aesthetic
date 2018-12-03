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
package com.afollestad.aesthetic.internal

// Base
const val PREFS_NAME = "[aesthetic-prefs]"
const val KEY_FIRST_TIME = "first_time"
const val KEY_ACTIVITY_THEME = "activity_theme_default"
const val KEY_IS_DARK = "is_dark"
const val KEY_ATTRIBUTE = "ate_attribute.%s"

// Legacy Attributes
@Deprecated("Legacy attribute")
const val KEY_PRIMARY_COLOR = "primary_color"
@Deprecated("Legacy attribute")
const val KEY_PRIMARY_DARK_COLOR = "primary_dark_color"
@Deprecated("Legacy attribute")
const val KEY_ACCENT_COLOR = "accent_color"
@Deprecated("Legacy attribute")
const val KEY_PRIMARY_TEXT_COLOR = "primary_text"
@Deprecated("Legacy attribute")
const val KEY_SECONDARY_TEXT_COLOR = "secondary_text"
@Deprecated("Legacy attribute")
const val KEY_PRIMARY_TEXT_INVERSE_COLOR = "primary_text_inverse"
@Deprecated("Legacy attribute")
const val KEY_SECONDARY_TEXT_INVERSE_COLOR = "secondary_text_inverse"
@Deprecated("Legacy attribute")
const val KEY_WINDOW_BG_COLOR = "window_bg_color"
@Deprecated("Legacy attribute")
const val KEY_ICON_TITLE_ACTIVE_COLOR = "icon_title_active_color"
@Deprecated("Legacy attribute")
const val KEY_ICON_TITLE_INACTIVE_COLOR = "icon_title_inactive_color"

// Window/System
const val KEY_STATUS_BAR_COLOR = "status_bar_color_default"
const val KEY_NAV_BAR_COLOR = "nav_bar_color_default"
const val KEY_LIGHT_STATUS_MODE = "light_status_mode"
const val KEY_LIGHT_NAV_MODE = "light_navigation_bar_mode"

// Custom Views
const val KEY_TOOLBAR_ICON_COLOR = "toolbar_icon_color"
const val KEY_TOOLBAR_TITLE_COLOR = "toolbar_title_color"
const val KEY_TOOLBAR_SUBTITLE_COLOR = "toolbar_subtitle_color"
const val KEY_TAB_LAYOUT_BG_MODE = "tab_layout_bg_mode"
const val KEY_TAB_LAYOUT_INDICATOR_MODE = "tab_layout_indicator_mode"
const val KEY_NAV_VIEW_MODE = "nav_view_mode"
const val KEY_BOTTOM_NAV_BG_MODE = "bottom_nav_bg_mode"
const val KEY_BOTTOM_NAV_ICONTEXT_MODE = "bottom_nav_icontext_mode"
const val KEY_CARD_VIEW_BG_COLOR = "card_view_bg_color"
const val KEY_SNACKBAR_TEXT = "snackbar_text_color"
const val KEY_SNACKBAR_ACTION_TEXT = "snackbar_action_text_color"
const val KEY_SNACKBAR_BG_COLOR = "snackbar_bg_color"
const val KEY_SWIPEREFRESH_COLORS = "swiperefreshlayout_colors"
