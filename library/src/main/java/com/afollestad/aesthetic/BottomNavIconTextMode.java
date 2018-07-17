package com.afollestad.aesthetic;

/** @author Aidan Follestad (afollestad) */
public enum BottomNavIconTextMode {
  SELECTED_PRIMARY(0),
  SELECTED_ACCENT(1),
  BLACK_WHITE_AUTO(2);

  private final int value;

  BottomNavIconTextMode(int value) {
    this.value = value;
  }

  public int toInt() {
    return value;
  }

  public static BottomNavIconTextMode fromInt(int value) {
    switch (value) {
      case 0:
        return SELECTED_PRIMARY;
      case 1:
        return SELECTED_ACCENT;
      default:
        return BLACK_WHITE_AUTO;
    }
  }
}
