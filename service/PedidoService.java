package service;
import dao.PedidoDAO;
import model.ItemPedido;
import model.Pedido;
import java.util.List;
import java.util.Scanner;

public class PedidoService {
  private final PedidoDAO pedidoDAO;
  private final Scanner sc;
  public PedidoService() {
    this.pedidoDAO = new PedidoDAO();
    this.sc = new Scanner(System.in);
  }

  public void crearPedido(Pedido pedido) {
    int pedidoId = pedidoDAO.insertar(pedido);
    pedido.setId(pedidoId);

    for (ItemPedido item : pedido.getItems()) {
      pedidoDAO.insertarItemPedido(pedidoId, item);
    }

    System.out.println("Pedido creado exitosamente. Número de pedido: " + pedidoId);
  }

  public void listarPedidos() {
    List<Pedido> pedidos = pedidoDAO.listarTodos();

    if (pedidos.isEmpty()) {
      System.out.println("No hay pedidos registrados.");
      return;
    }

    System.out.println("\n--- Lista de Pedidos ---");
    for (Pedido pedido : pedidos) {
      System.out.println(pedido);
    }
  }

  public void listarPedidosPendientes() {
    List<Pedido> pedidos = pedidoDAO.listarPorEstado("PENDIENTE");

    if (pedidos.isEmpty()) {
      System.out.println("No hay pedidos pendientes.");
      return;
    }

    System.out.println("\n--- Pedidos Pendientes ---");
    for (Pedido pedido : pedidos) {
      System.out.println(pedido.toStringDetallado());
      System.out.println("-----------------------------");
    }
  }

  public void listarPedidosEnPreparacion() {
    List<Pedido> pedidos = pedidoDAO.listarPorEstado("PREPARACION");

    if (pedidos.isEmpty()) {
      System.out.println("No hay pedidos en preparación.");
      return;
    }

    System.out.println("\n--- Pedidos en Preparación ---");
    for (Pedido pedido : pedidos) {
      System.out.println(pedido.toStringDetallado());
      System.out.println("-----------------------------");
    }
  }

  public void actualizarEstadoPedido() {
    listarPedidos();
    System.out.print("\nIngrese el ID del pedido a actualizar: ");
    int pedidoId = sc.nextInt();
    sc.nextLine();

    Pedido pedido = pedidoDAO.buscarPorId(pedidoId);
    if (pedido == null) {
      System.out.println("Pedido no encontrado.");
      return;
    }

    System.out.println("Estado actual: " + pedido.getEstado());
    System.out.println("Nuevo estado (PENDIENTE, PREPARACION, COMPLETADO, CANCELADO): ");
    String nuevoEstado = sc.nextLine().toUpperCase();

    pedido.setEstado(nuevoEstado);
    pedidoDAO.actualizar(pedido);
    System.out.println("Estado del pedido actualizado.");
  }
}
