package service;
import dao.ProductoDAO;
import model.Producto;
import java.util.List;
import java.util.Scanner;

public class ProductoService {
  private final ProductoDAO productoDAO;
  private final Scanner sc;

  public ProductoService() {
    this.productoDAO = new ProductoDAO();
    this.sc = new Scanner(System.in);
  }

  public Producto seleccionarProducto() {
    List<Producto> listaProductos = productoDAO.listarTodos();

    System.out.println("\n--- Seleccionar Producto ---");
    for (int i = 0; i < listaProductos.size(); i++) {
      System.out.println((i + 1) + ". " + listaProductos.get(i));
    }

    System.out.print("Seleccione un producto (0 para cancelar): ");
    int opcionSeleccionProducto = sc.nextInt();
    sc.nextLine();

    if (opcionSeleccionProducto == 0) {
      return null;
    }

    if (opcionSeleccionProducto < 1 || opcionSeleccionProducto > listaProductos.size()) {
      System.out.println("Opción inválida.");
      return null;
    }

    return listaProductos.get(opcionSeleccionProducto - 1);
  }

  // método de selección por categoría consolidado en seleccionarProductoPorCategoria()

  public Producto seleccionarProductoPorCategoria() {
    System.out.println("\n--- Productos por Categoría ---");
    System.out.println("1. Bebidas Calientes");
    System.out.println("2. Bebidas Frías");
    System.out.println("3. Comida");
    System.out.println("4. Postres");
    System.out.print("Seleccione una categoría: ");

    int opcionCategoriaSeleccion = sc.nextInt();
    sc.nextLine();

    String categoria;
    switch (opcionCategoriaSeleccion) {
      case 1:
        categoria = "Bebidas Calientes";
        break;
      case 2:
        categoria = "Bebidas Frías";
        break;
      case 3:
        categoria = "Comida";
        break;
      case 4:
        categoria = "Postres";
        break;
      default:
        System.out.println("Opción inválida.");
        return null;
    }

    List<Producto> productosFiltrados = productoDAO.buscarPorCategoria(categoria);
    if (productosFiltrados.isEmpty()) {
      System.out.println("No hay productos en la categoría seleccionada.");
      return null;
    }

    System.out.println("\n--- " + categoria + " ---");
    for (int i = 0; i < productosFiltrados.size(); i++) {
      System.out.println((i + 1) + ". " + productosFiltrados.get(i));
    }

    System.out.print("Seleccione un producto (0 para cancelar): ");
    int opcionSeleccionProducto = sc.nextInt();
    sc.nextLine();

    if (opcionSeleccionProducto == 0) {
      return null;
    }

    if (opcionSeleccionProducto < 1 || opcionSeleccionProducto > productosFiltrados.size()) {
      System.out.println("Opción inválida.");
      return null;
    }

    return productosFiltrados.get(opcionSeleccionProducto - 1);
  }
}
