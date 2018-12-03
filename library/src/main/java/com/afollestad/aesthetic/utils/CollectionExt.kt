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
