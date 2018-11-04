/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import androidx.collection.ArrayMap
import io.reactivex.Observable

internal fun String.splitToInts(delimiter: String = ","): IntArray {
  return split(delimiter).map { it.toInt() }
      .toIntArray()
}

internal fun Observable<String>.mapToIntArray(delimiter: String = ","): Observable<IntArray> {
  return map { it.splitToInts(delimiter) }
}

internal fun <K, V> mutableArrayMap(initialCapacity: Int = 0): MutableMap<K, V> {
  return ArrayMap<K, V>(initialCapacity)
}
