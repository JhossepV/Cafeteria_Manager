package ui;

public class Main {
  public static void main(String[] args) {
    BD.DatabaseConfig.initializeDatabase();

    MenuPrincipal menuPrincipal = new MenuPrincipal();
    menuPrincipal.mostrarMenu();
  }
}
