/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import java.lang.reflect.Field
import kotlin.reflect.KClass

internal fun <T : Any> KClass<T>.findField(vararg nameOptions: String): Field = with(java) {
  for (name in nameOptions) {
    try {
      val field = getDeclaredField(name)
      field.isAccessible = true
      return field
    } catch (_: NoSuchFieldException) {
    }
  }
  throw IllegalArgumentException(
      "Unable to find any of fields ${nameOptions.toList()} in ${this.name}"
  )
}
