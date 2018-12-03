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

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import com.afollestad.aesthetic.utils.isNumber
import com.afollestad.aesthetic.utils.safeResourceName

/** @author Aidan Follestad (afollestad) */
internal class AttrWizard(
  private val context: Context,
  private val attrs: AttributeSet?
) {

  fun getRawValue(@AttrRes attrId: Int): String {
    if (attrs == null || attrId == 0) {
      return ""
    }

    val res = context.resources
    val attrName = res.safeResourceName(attrId)
    val attrIndex = attrs.indexOfAttr(context) { it == attrName }
    if (attrIndex == -1) {
      return ""
    }

    val attrValue = attrs.getAttributeValue(attrIndex)
    return when {
      attrValue.startsWith('@') || attrValue.startsWith('?') -> {
        val rawId = attrValue.substring(1)
        var resName = if (rawId.isNumber()) {
          val id = rawId.toInt()
          if (id == 0) {
            return ""
          }
          res.safeResourceName(id)
        } else {
          rawId
        }
        if (!resName.startsWith("android")) {
          resName = resName.substring(resName.indexOf(':') + 1)
        }
        "${attrValue[0]}$resName"
      }
      else -> attrValue
    }
  }
}

private fun AttributeSet.indexOfAttr(
  context: Context,
  matcher: (String) -> (Boolean)
): Int {
  for (i in 0 until attributeCount) {
    val nameResource = getAttributeNameResource(i)
    val literalName = context.resources.safeResourceName(nameResource)
    if (matcher(literalName)) {
      return i
    }
  }
  return -1
}
