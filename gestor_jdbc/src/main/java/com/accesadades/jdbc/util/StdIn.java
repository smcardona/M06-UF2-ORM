package com.accesadades.jdbc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StdIn {
  private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

  /**
   * Llegeix una línia de l'entrada estàndard i la retorna en forma de
   * String
   *
   * Converteix IOException en RuntimeException per evitar que sigui
   * obligatòri gestionar-la i així simplificar l'accés al llenguatge a
   * estudiants nous. Enginy casolà ;)
   */
  public static String readLine() {
      try {
          String line = reader.readLine();
          if (line == null) {
              throw new RuntimeException("Se ha cancelado la entrada forzosamente ...");
          }
          return line;
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
  }
}