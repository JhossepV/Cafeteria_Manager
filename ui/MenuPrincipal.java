package ui;

import java.util.Scanner;

public class MenuPrincipal {
  private final Scanner sc;

  public MenuPrincipal() {
    this.sc = new Scanner(System.in);
  }

  public void mostrarMenu() {
    int opcionSeleccion;
    do {
      System.out.println("\n=== SISTEMA DE GESTIÓN DE CAFETERÍA ===");
      System.out.println("1. Menú Mozo");
      System.out.println("2. Menú Chef");
      System.out.println("3. Salir");
      System.out.print("Seleccione una opción: ");
      opcionSeleccion = sc.nextInt();
      sc.nextLine();

      switch (opcionSeleccion) {
        case 1 -> {
          MenuMozo menuMozo = new MenuMozo();
          menuMozo.mostrarMenu();
        }
        case 2 -> {
          MenuChef menuChef = new MenuChef();
          menuChef.mostrarMenu();
        }
        case 3 -> System.out.println("Saliendo del sistema...");
        default -> System.out.println("Opción inválida.");
      }
    } while (opcionSeleccion != 3);
  }
}
