package com.afollestad.aesthetic;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.afollestad.aesthetic.TabLayoutBgMode.ACCENT;
import static com.afollestad.aesthetic.TabLayoutBgMode.PRIMARY;
import static com.afollestad.aesthetic.TabLayoutBgMode.WINDOW_BG;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef(value = {PRIMARY, ACCENT, WINDOW_BG})
public @interface TabLayoutBgMode {
  int PRIMARY = 0;
  int ACCENT = 1;
  int WINDOW_BG = 2;
}
