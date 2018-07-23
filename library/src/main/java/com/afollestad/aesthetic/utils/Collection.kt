package com.afollestad.aesthetic.utils

import android.support.v4.util.ArrayMap

internal fun String.splitToInts(delimiter: String = ","): IntArray {
  return split(delimiter).map { it.toInt() }
      .toIntArray()
}

internal fun <K, V> mutableArrayMapOf(initialCapacity: Int = 0): MutableMap<K, V> {
  return ArrayMap<K, V>(initialCapacity)
}