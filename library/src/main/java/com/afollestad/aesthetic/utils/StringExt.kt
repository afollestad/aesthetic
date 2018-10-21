/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import androidx.annotation.CheckResult

@CheckResult
internal fun String.isNumber() = all { it.isDigit() }
