package com.afollestad.aesthetic;

/** @author Aidan Follestad (afollestad) */
public enum BottomNavBgMode {
  BLACK_WHITE_AUTO(0),
  PRIMARY(1),
  PRIMARY_DARK(2),
  ACCENT(3);

  private final int value;

  BottomNavBgMode(int value) {
    this.value = value;
  }

  public int toInt() {
    return value;
  }

  public static BottomNavBgMode fromInt(int value) {
    switch (value) {
      case 0:
        return BLACK_WHITE_AUTO;
      case 1:
        return PRIMARY;
      case 2:
        return PRIMARY_DARK;
      default:
        return ACCENT;
    }
  }
}
