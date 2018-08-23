/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import java.lang.reflect.Field

internal fun <T> Class<T>.findField(vararg nameOptions: String): Field {
  for (name in nameOptions) {
    try {
      return getDeclaredField(name)
    } catch (_: NoSuchFieldException) {
    }
  }
  throw IllegalStateException("Unable to find any of fields $nameOptions")
}
