/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.internal

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes

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
    val attrName = res.getResourceName(attrId)
    val attrIndex = attrs.indexOfAttr(context) { it == attrName }
    if (attrIndex == -1) {
      return ""
    }

    val attrValue = attrs.getAttributeValue(attrIndex)
    return when {
      attrValue.startsWith('@') || attrValue.startsWith('?') -> {
        val id = attrValue.substring(1)
            .toInt()
        if (id == 0) {
          return ""
        }
        var resName = res.getResourceName(id)
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
    val literalName = if (nameResource != 0) context.resources.getResourceName(nameResource) else ""
    if (matcher(literalName)) {
      return i
    }
  }
  return -1
}

//private fun scanForRClass(
//  safeContext: Context,
//  subclass: String
//): Class<*> {
//  val pkg = safeContext.packageName
//  val cls = Class.forName("$pkg.R")
//  return cls.declaredClasses.singleOrNull { it.simpleName == subclass }
//      ?: throw IllegalArgumentException("Didn't find class $pkg.R.$subclass")
//}
//
//private fun scanForFieldValue(
//  classToSearch: Class<*>,
//  target: Int
//): String? {
//  for (field in classToSearch.declaredFields) {
//    try {
//      val fieldValue = field.get(null) as Int
//      if (fieldValue == target) {
//        return field.name
//      }
//    } catch (_: Throwable) {
//    }
//  }
//  return null
//}
