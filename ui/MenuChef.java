package ui;

import dao.MozoDAO;
import model.Mozo;
import service.PedidoService;
import java.util.List;
import java.util.Scanner;

public class MenuChef {
  private final PedidoService pedidoService;
  private final MozoDAO mozoDAO;
  private final Scanner sc;

  public MenuChef() {
    this.pedidoService = new PedidoService();
    this.mozoDAO = new MozoDAO();
    this.sc = new Scanner(System.in);
  }

  public void mostrarMenu() {
    int opcionSeleccion;
    do {
      System.out.println("\n=== MENÚ CHEF ===");
      System.out.println("1. Ver Pedidos Pendientes");
      System.out.println("2. Ver Pedidos en Preparación");
      System.out.println("3. Actualizar Estado de Pedido");
      System.out.println("4. Mozos");
      System.out.println("5. Volver al Menú Principal");
      System.out.print("Seleccione una opción: ");
      opcionSeleccion = sc.nextInt();
      sc.nextLine();

      switch (opcionSeleccion) {
        case 1 -> pedidoService.listarPedidosPendientes();
        case 2 -> pedidoService.listarPedidosEnPreparacion();
        case 3 -> pedidoService.actualizarEstadoPedido();
        case 4 -> menuMozos();
        case 5 -> System.out.println("Volviendo al menú principal...");
        default -> System.out.println("Opción inválida.");
      }
    } while (opcionSeleccion != 5);
  }

  private void menuMozos() {
    int op;
    do {
      System.out.println("\n--- Gestión de Mozos ---");
      System.out.println("1. Listar mozos");
      System.out.println("2. Agregar mozo");
      System.out.println("3. Eliminar mozo");
      System.out.println("4. Volver");
      System.out.print("Opción: ");
      op = sc.nextInt();
      sc.nextLine();

      switch (op) {
        case 1 -> listarMozos();
        case 2 -> agregarMozo();
        case 3 -> eliminarMozo();
        case 4 -> {
        }
        default -> System.out.println("Opción inválida.");
      }
    } while (op != 4);
  }

  private void listarMozos() {
    List<Mozo> mozos = mozoDAO.listarTodos();
    if (mozos.isEmpty()) {
      System.out.println("No hay mozos registrados.");
      return;
    }
    for (int i = 0; i < mozos.size(); i++) {
      System.out.println((i + 1) + ". " + mozos.get(i));
    }
  }

  private void agregarMozo() {
    System.out.print("Nombre: ");
    String nombre = sc.nextLine();
    System.out.print("Turno: ");
    String turno = sc.nextLine();
    boolean ok = mozoDAO.insertar(new Mozo(nombre, turno));
    System.out.println(ok ? "Mozo agregado." : "No se pudo agregar al mozo.");
  }

  private void eliminarMozo() {
    listarMozos();
    System.out.print("Seleccione el número del mozo a eliminar (0 para cancelar): ");
    int idx = sc.nextInt();
    sc.nextLine();
    if (idx <= 0)
      return;
    List<Mozo> mozos = mozoDAO.listarTodos();
    if (idx > mozos.size()) {
      System.out.println("Opción inválida.");
      return;
    }
    int mozoId = mozos.get(idx - 1).getId();
    boolean ok = mozoDAO.eliminar(mozoId);
    System.out.println(ok ? "Mozo eliminado." : "No se pudo eliminar al mozo.");
  }
}
