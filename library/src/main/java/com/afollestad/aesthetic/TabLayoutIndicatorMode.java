package com.afollestad.aesthetic;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.afollestad.aesthetic.TabLayoutIndicatorMode.ACCENT;
import static com.afollestad.aesthetic.TabLayoutIndicatorMode.BLACK_WHITE_AUTO;
import static com.afollestad.aesthetic.TabLayoutIndicatorMode.PRIMARY;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef(value = {PRIMARY, ACCENT, BLACK_WHITE_AUTO})
public @interface TabLayoutIndicatorMode {
  int PRIMARY = 0;
  int ACCENT = 1;
  int BLACK_WHITE_AUTO = 2;
}
