package ui;

import dao.MozoDAO;
import model.*;
import service.ClienteService;
import service.ProductoService;
import service.PedidoService;
import java.util.List;
import java.util.Scanner;

public class MenuMozo {
  private final ClienteService clienteService;
  private final ProductoService productoService;
  private final PedidoService pedidoService;
  private final MozoDAO mozoDAO;
  private final Scanner sc;

  public MenuMozo() {
    this.clienteService = new ClienteService();
    this.productoService = new ProductoService();
    this.pedidoService = new PedidoService();
    this.mozoDAO = new MozoDAO();
    this.sc = new Scanner(System.in);
  }

  public void mostrarMenu() {
    int opcionSeleccion;
    do {
      System.out.println("\n=== MENÚ MOZO ===");
      System.out.println("1. Clientes");
      System.out.println("2. Crear Pedido");
      System.out.println("3. Ver Pedidos");
      System.out.println("4. Volver al Menú Principal");
      System.out.print("Seleccione una opción: ");
      opcionSeleccion = sc.nextInt();
      sc.nextLine();

      switch (opcionSeleccion) {
        case 1 -> menuClientes();
        case 2 -> crearPedido();
        case 3 -> pedidoService.listarPedidos();
        case 4 -> System.out.println("Volviendo al menú principal...");
        default -> System.out.println("Opción inválida.");
      }
    } while (opcionSeleccion != 4);
  }

  private void crearPedido() {
    System.out.println("\n--- Crear Nuevo Pedido ---");

    Cliente cliente;
    System.out.println("¿Cómo desea seleccionar el cliente?");
    System.out.println("1. Buscar por nombre");
    System.out.println("2. Seleccionar de la lista");
    System.out.print("Opción: ");
    int opcionSeleccionCliente = sc.nextInt();
    sc.nextLine();

    if (opcionSeleccionCliente == 1) {
      cliente = clienteService.buscarClientePorNombre();
    } else {
      cliente = clienteService.seleccionarCliente();
    }

    if (cliente == null) {
      return;
    }

    System.out.println("\n--- Seleccionar Mozo ---");
    List<Mozo> mozos = mozoDAO.listarTodos();
    for (int i = 0; i < mozos.size(); i++) {
      System.out.println((i + 1) + ". " + mozos.get(i));
    }

    System.out.print("Seleccione un mozo: ");
    int opcionSeleccionMozo = sc.nextInt();
    sc.nextLine();

    if (opcionSeleccionMozo < 1 || opcionSeleccionMozo > mozos.size()) {
      System.out.println("Opción inválida.");
      return;
    }

    Mozo mozo = mozos.get(opcionSeleccionMozo - 1);

    Pedido pedido = new Pedido(cliente.getId(), cliente.getNombre(), mozo.getId(), mozo.getNombre());

    int opcionContinuarAgregandoProducto = -1;
    do {
      System.out.println("\n¿Cómo desea seleccionar productos?");
      System.out.println("1. Ver todos los productos");
      System.out.println("2. Ver por categoría");
      System.out.print("Opción: ");
      int opcionTipoBusquedaProducto = sc.nextInt();
      sc.nextLine();

      Producto productoSeleccionado = null;
      if (opcionTipoBusquedaProducto == 1) {
        productoSeleccionado = productoService.seleccionarProducto();
      } else if (opcionTipoBusquedaProducto == 2) {
        productoSeleccionado = productoService.seleccionarProductoPorCategoria();
      } else {
        System.out.println("Opción inválida.");
        continue;
      }

      if (productoSeleccionado == null) {
        if (pedido.getItems().isEmpty()) {
          System.out.println("No se seleccionó ningún producto. Cancelando creación de pedido.");
          return;
        } else {
          break;
        }
      }

      System.out.print("Cantidad: ");
      int cantidadSolicitada = sc.nextInt();
      sc.nextLine();

      ItemPedido itemPedido = new ItemPedido(productoSeleccionado.getId(), productoSeleccionado.getNombre(),
          cantidadSolicitada, productoSeleccionado.getPrecio());
      pedido.agregarItem(itemPedido);

      System.out.print("¿Desea agregar otro producto? (1: Sí, 0: No): ");
      opcionContinuarAgregandoProducto = sc.nextInt();
      sc.nextLine();
    } while (opcionContinuarAgregandoProducto != 0);

    if (pedido.getItems().isEmpty()) {
      System.out.println("No hay productos en el pedido. Cancelando creación de pedido.");
      return;
    }

    // Vista previa sin número de pedido (aún no persistido)
    System.out.println("\nVista previa del pedido (sin número):");
    System.out.println("Cliente: " + pedido.getClienteNombre());
    System.out.println("Mozo: " + pedido.getMozoNombre());
    System.out.println("Items:");
    for (ItemPedido it : pedido.getItems()) {
      System.out.println("  - " + it.toString());
    }
    System.out.println("Total: S/" + pedido.getTotal());
    System.out.print("¿Confirmar pedido? (1: Sí, 0: No): ");
    int confirmar = sc.nextInt();
    sc.nextLine();

    if (confirmar == 1) {
      pedidoService.crearPedido(pedido);
      // Mostrar resumen final ya con número de pedido asignado
      System.out.println("\nResumen del pedido creado:");
      System.out.println(pedido.toStringDetallado());
    } else {
      System.out.println("Pedido cancelado.");
    }
  }

  private void menuClientes() {
    int op;
    do {
      System.out.println("\n--- Gestión de Clientes ---");
      System.out.println("1. Listar clientes");
      System.out.println("2. Agregar cliente");
      System.out.println("3. Eliminar cliente");
      System.out.println("4. Volver");
      System.out.print("Opción: ");
      op = sc.nextInt();
      sc.nextLine();

      switch (op) {
        case 1 -> listarClientes();
        case 2 -> clienteService.registrarCliente();
        case 3 -> eliminarCliente();
        case 4 -> {
        }
        default -> System.out.println("Opción inválida.");
      }
    } while (op != 4);
  }

  private void eliminarCliente() {
    service.ClienteService cs = new service.ClienteService();
    var clientes = cs.listarTodosClientes();
    if (clientes.isEmpty()) {
      System.out.println("No hay clientes para eliminar.");
      return;
    }
    for (int i = 0; i < clientes.size(); i++) {
      System.out.println((i + 1) + ". " + clientes.get(i));
    }
    System.out.print("Seleccione el número del cliente a eliminar (0 para cancelar): ");
    int idx = sc.nextInt();
    sc.nextLine();
    if (idx <= 0 || idx > clientes.size()) {
      System.out.println("Operación cancelada o inválida.");
      return;
    }
    int clienteId = clientes.get(idx - 1).getId();
    boolean ok = cs.eliminarClientePorId(clienteId);
    System.out.println(ok ? "Cliente eliminado." : "No se pudo eliminar el cliente.");
  }

  private void listarClientes() {
    service.ClienteService cs = new service.ClienteService();
    var clientes = cs.listarTodosClientes();
    if (clientes.isEmpty()) {
      System.out.println("No hay clientes registrados.");
      return;
    }
    System.out.println("\n--- Clientes Registrados ---");
    for (int i = 0; i < clientes.size(); i++) {
      System.out.println((i + 1) + ". " + clientes.get(i));
    }
  }
}
