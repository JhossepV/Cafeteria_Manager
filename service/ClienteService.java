package service;

import dao.ClienteDAO;
import model.Cliente;

import java.util.List;
import java.util.Scanner;

public class ClienteService {
  private final ClienteDAO clienteDAO;
  private final Scanner sc;
  // Mensaje de error de la última operación (para mostrar en UI)
  private String lastError;

  public ClienteService() {
    this.clienteDAO = new ClienteDAO();
    this.sc = new Scanner(System.in);
  }

  public String getLastError() { return lastError; }

  public void registrarCliente() {
    System.out.println("\n--- Registrar Nuevo Cliente ---");
    System.out.print("Nombre: ");
    String nombre = sc.nextLine();
    System.out.print("DNI: ");
    String dni = sc.nextLine();
    System.out.print("Teléfono: ");
    String telefono = sc.nextLine();

    Cliente cliente = new Cliente(nombre, dni, telefono);
    clienteDAO.insertar(cliente);
    System.out.println("Cliente registrado exitosamente.");
  }

  public Cliente seleccionarCliente() {
    List<Cliente> clientes = clienteDAO.listarTodos();

    if (clientes.isEmpty()) {
      System.out.println("No hay clientes registrados. Por favor registre un cliente primero.");
      return null;
    }

    System.out.println("\n--- Seleccionar Cliente ---");
    for (int i = 0; i < clientes.size(); i++) {
      System.out.println((i + 1) + ". " + clientes.get(i));
    }

    System.out.print("Seleccione un cliente (0 para cancelar): ");
    int opcion = sc.nextInt();
    sc.nextLine();

    if (opcion == 0) {
      return null;
    }

    if (opcion < 1 || opcion > clientes.size()) {
      System.out.println("Opción inválida.");
      return null;
    }

    return clientes.get(opcion - 1);
  }

  public Cliente buscarClientePorNombre() {
    System.out.print("Ingrese el nombre del cliente (o parte): ");
    String nombre = sc.nextLine();

    List<Cliente> resultados = clienteDAO.buscarPorNombre(nombre);
    if (resultados.isEmpty()) {
      System.out.println("No se encontraron clientes con ese nombre.");
      return null;
    }

    if (resultados.size() == 1) {
      return resultados.get(0);
    }

    System.out.println("\n--- Resultados de Búsqueda ---");
    for (int i = 0; i < resultados.size(); i++) {
      System.out.println((i + 1) + ". " + resultados.get(i));
    }

    System.out.print("Seleccione un cliente (0 para cancelar): ");
    int opcion = sc.nextInt();
    sc.nextLine();

    if (opcion == 0) {
      return null;
    }
    if (opcion < 1 || opcion > resultados.size()) {
      System.out.println("Opción inválida.");
      return null;
    }
    return resultados.get(opcion - 1);
  }

  public List<Cliente> buscarClientesPorNombre(String nombreLike) {
    return clienteDAO.buscarPorNombre(nombreLike);
  }

  public boolean agregarCliente(Cliente c) {
    // Comprobaciones previas: no duplicar DNI ni teléfono cuando no estén vacíos
    try (var conn = BD.DatabaseConfig.connect()) {
      // DNI duplicado
      if (c.getDni() != null && !c.getDni().isBlank()) {
        try (var psChk = conn.prepareStatement("SELECT id FROM clientes WHERE dni = ?")) {
          psChk.setString(1, c.getDni());
          try (var rs = psChk.executeQuery()) {
            if (rs.next()) {
              lastError = "DNI ya registrado";
              return false;
            }
          }
        }
      }
      // Teléfono duplicado
      if (c.getTelefono() != null && !c.getTelefono().isBlank()) {
        try (var psChk = conn.prepareStatement("SELECT id FROM clientes WHERE telefono = ?")) {
          psChk.setString(1, c.getTelefono());
          try (var rs = psChk.executeQuery()) {
            if (rs.next()) {
              lastError = "Teléfono ya registrado";
              return false;
            }
          }
        }
      }

      try (var ps = conn.prepareStatement("INSERT INTO clientes(nombre, dni, telefono) VALUES(?, ?, ?)")) {
      ps.setString(1, c.getNombre());
        if (c.getDni() == null || c.getDni().isBlank()) ps.setNull(2, java.sql.Types.VARCHAR); else ps.setString(2, c.getDni());
        if (c.getTelefono() == null || c.getTelefono().isBlank()) ps.setNull(3, java.sql.Types.VARCHAR); else ps.setString(3, c.getTelefono());
        boolean ok = ps.executeUpdate() > 0;
        lastError = ok ? null : "No se pudo agregar el cliente";
        return ok;
      }
    } catch (Exception e) {
      if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
        lastError = "DNI o Teléfono ya registrado";
      } else {
        lastError = "Error agregando cliente: " + e.getMessage();
      }
      return false;
    }
  }

  // Nuevos helpers para UI: listar y eliminar
  public List<Cliente> listarTodosClientes() {
    return clienteDAO.listarTodos();
  }

  public boolean eliminarClientePorId(int clienteId) {
    try (var conn = BD.DatabaseConfig.connect(); var ps = conn.prepareStatement("DELETE FROM clientes WHERE id = ?")) {
      ps.setInt(1, clienteId);
      return ps.executeUpdate() > 0;
    } catch (Exception e) {
      System.out.println("Error eliminando cliente: " + e.getMessage());
      return false;
    }
  }
}
