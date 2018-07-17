package com.afollestad.aesthetic;

/** @author Aidan Follestad (afollestad) */
public enum TabLayoutBgMode {
  PRIMARY(0),
  ACCENT(1);

  private final int value;

  TabLayoutBgMode(int value) {
    this.value = value;
  }

  public int toInt() {
    return value;
  }

  public static TabLayoutBgMode fromInt(int value) {
    switch (value) {
      case 0:
        return PRIMARY;
      default:
        return ACCENT;
    }
  }
}
