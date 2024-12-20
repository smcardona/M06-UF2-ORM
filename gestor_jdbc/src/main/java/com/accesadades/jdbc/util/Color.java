package com.accesadades.jdbc.util;

/**
 * Funcionalidad para hacer prints a color 
 */
public enum Color {
  RED("\u001B[31m"),
  GRE("\u001B[32m"),
  YEL("\u001B[33m"),
  BLU("\u001B[34m"),
  MAG("\u001B[35m"),
  CYA("\u001B[36m"),
  WHI("\u001B[37m");

  private final String colorCode;
  public static String resetCode = "\u001B[0m";

  // Constructor del enum
  private Color(String colorCode) {
    this.colorCode = colorCode;
  }

  public String getColorCode() { return this.colorCode; }

  // Métodos de impresión
  public void print(Object toPrint) {
    System.out.print(this.colorCode + toPrint + resetCode);
  }

  public void println(Object toPrint) {
    System.out.println(this.colorCode + toPrint + resetCode);
  }

  public void printf(String format, Object... args) {
    String formattedText = String.format(format, args);
    System.out.print(this.colorCode + formattedText + resetCode);
  }

  // Método para devolver una cadena coloreada
  public String apply(String text) {
    return this.colorCode + text + resetCode;
  }

  public String format(String format, Object... args) {
    return String.format(format, args);
  }

  // SHORTCUTS
  public static String RED (String txt) { return RED.apply(txt); }
  public static String GRE (String txt) { return GRE.apply(txt); }
  public static String YEL (String txt) { return YEL.apply(txt); }
}
