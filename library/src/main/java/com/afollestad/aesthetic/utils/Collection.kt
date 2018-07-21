package com.afollestad.aesthetic.utils

internal fun String.splitToInts(delimiter: String = ","): IntArray {
  return split(delimiter).map { it.toInt() }
      .toIntArray()
}