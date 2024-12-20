package com.accesadades.jdbc.util;


public class Paginator {
  private final int PAGE_SIZE;
  private final int MAX_PAGE;
  private final QuickIO io;
  private Object[] items;
  private int page = 0;
  
  public Paginator(Object[] files, int pageSize, QuickIO io) {
    this.items = files; 
    this.PAGE_SIZE = pageSize;
    this.MAX_PAGE = (files.length/PAGE_SIZE) + (files.length%PAGE_SIZE > 0 ? 1 : 0)  ;
    this.io = io;
  }

  public void start() throws Exception {
    page = 0;
    while (true) {

      if (items.length == 0) {
        Color.RED.println("No hi ha cap entrada a les dades");
        return;
      }
      
      showPage(items);
      System.out.println();
      System.out.println("O use '<' | '>' para moverse entre páginas");
      System.out.println("Elija el indice de la pagina  que quiere utilizar"); 
  
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
}
