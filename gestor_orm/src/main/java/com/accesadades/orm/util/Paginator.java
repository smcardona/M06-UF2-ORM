package com.accesadades.orm.util;

import com.accesadades.orm.util.exceptions.CancelCommandException;
import com.accesadades.orm.util.exceptions.ExitException;

public class Paginator<T> {
  private final int PAGE_SIZE;
  private int MAX_PAGE;
  private final QuickIO io;
  private T[] items;
  private int page = 0;
  private T pick = null;
  
  public Paginator(T[] items, int pageSize, QuickIO io) {
    this.items = items; 
    this.PAGE_SIZE = pageSize;
    this.MAX_PAGE = calcMaxPage();
    this.io = io;
  }

  private int calcMaxPage () { return (items.length/PAGE_SIZE) + (items.length%PAGE_SIZE > 0 ? 1 : 0); }

  // Exige escoger un item de los de la lista, los indices son de los items y no de las paginas
  public void chose() throws ExitException, CancelCommandException {
    page = 0;
    pick = null;
    MAX_PAGE = calcMaxPage();
    
    while (true) {

      if (items.length == 0) {
        Color.RED.println("No hi ha cap entrada a les dades");
        return;
      }
      
      showPage(items);
      System.out.println();
      System.out.println("   '<' | '>' per moure entre pàgines");
      System.out.println("Tria el index del element que vulguis fer servir"); 
  
      String input = io.getInputWithPrompt("(idx, <, >, sortir): ");

      switch (input) {
        case ">":
          page = (page+1) % (MAX_PAGE);
          break;

        case "<":
          page = (page-1+MAX_PAGE) % (MAX_PAGE);
          break;
      
        case "sortir": 
          throw new CancelCommandException("Tria d'element cancelada");

        default:

        if (UtilString.isInteger(input)) {
          int pick = Integer.parseInt(input);
          if (pick >= 0 && pick < items.length) {
            this.pick = items[pick];
            return;
          }
          else Color.RED.println("Index fora del rang de elements");
        }
        else Color.RED.println("Entrada invàlida ...");
      }
    }
  }

  public int showPage(Object[] items) {
    int i;
    for (i = 0; i < PAGE_SIZE; i++){
      int realIndex = i + PAGE_SIZE * page;
      if (realIndex >= items.length) break; 
      System.out.println(
        String.format(" %d : %s ", realIndex, items[realIndex].toString())
      );
    }
    System.out.printf("%nPàgina %d de %d%n", page+1, MAX_PAGE);
    return i -1; // Ultimo indice imprimido, -1 si no pudo imprimir ninguno puesto que 0 indica al indice 0
  }

  public T getChoice() {
    return pick;
  }

  // Solo muestra los items por paginas, permite navegacion facil entre paginas
  public void show () throws ExitException, CancelCommandException {
    page = 0;
    pick = null;
    MAX_PAGE = calcMaxPage();
    
    while (true) {

      if (items.length == 0) {
        Color.RED.println("No hi ha cap entrada a les dades");
        return;
      }
      
      showPage(items);
      System.out.println();
      System.out.println("   '<' | '>' per moure entre pàgines");
      System.out.println("Tria el index del element que vulguis fer servir"); 
  
      String input = io.getInputWithPrompt("(idx, <, >, sortir): ");

      switch (input) {
        case ">":
          page = (page+1) % (MAX_PAGE);
          break;

        case "<":
          page = (page-1+MAX_PAGE) % (MAX_PAGE);
          break;
      
        case "sortir": 
          System.out.println("Sortint de les mostres");
          return;

        default:

        if (UtilString.isInteger(input)) {
          int pick = Integer.parseInt(input);
          if (pick >= 0 && pick <= MAX_PAGE) page = pick-1;
          else Color.RED.println("Index fora del rang de pàgines");
        }
        else Color.RED.println("Entrada invàlida ...");
      }
    }
  }
}
