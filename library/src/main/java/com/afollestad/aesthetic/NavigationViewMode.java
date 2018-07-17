package com.afollestad.aesthetic;

/** @author Aidan Follestad (afollestad) */
public enum NavigationViewMode {
  SELECTED_PRIMARY(0),
  SELECTED_ACCENT(1);

  private final int value;

  NavigationViewMode(int value) {
    this.value = value;
  }

  public int toInt() {
    return value;
  }

  public static NavigationViewMode fromInt(int value) {
    switch (value) {
      case 0:
        return SELECTED_PRIMARY;
      default:
        return SELECTED_ACCENT;
    }
  }
}
