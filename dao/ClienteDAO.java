package dao;

import model.Cliente;
import BD.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

  public void insertar(Cliente cliente) {
    String sql = "INSERT INTO clientes(nombre, dni, telefono) VALUES(?, ?, ?)";

    try (Connection conn = DatabaseConfig.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, cliente.getNombre());
      pstmt.setString(2, cliente.getDni());
      pstmt.setString(3, cliente.getTelefono());
      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Error insertando cliente: " + e.getMessage());
    }
  }

  public List<Cliente> listarTodos() {
    String sql = "SELECT id, nombre, dni, telefono FROM clientes";
    List<Cliente> clientes = new ArrayList<>();

    try (Connection conn = DatabaseConfig.connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setDni(rs.getString("dni"));
        cliente.setTelefono(rs.getString("telefono"));
        clientes.add(cliente);
      }
    } catch (SQLException e) {
      System.out.println("Error obteniendo clientes: " + e.getMessage());
    }

    return clientes;
  }

  public Cliente buscarPorId(int id) {
    String sql = "SELECT id, nombre, dni, telefono FROM clientes WHERE id = ?";
    Cliente cliente = null;

    try (Connection conn = DatabaseConfig.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setDni(rs.getString("dni"));
        cliente.setTelefono(rs.getString("telefono"));
      }
    } catch (SQLException e) {
      System.out.println("Error buscando cliente: " + e.getMessage());
    }

    return cliente;
  }

  public Cliente buscarPorDni(String dni) {
    String sql = "SELECT id, nombre, dni, telefono FROM clientes WHERE dni = ?";
    Cliente cliente = null;

    try (Connection conn = DatabaseConfig.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, dni);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setDni(rs.getString("dni"));
        cliente.setTelefono(rs.getString("telefono"));
      }
    } catch (SQLException e) {
      System.out.println("Error buscando cliente por DNI: " + e.getMessage());
    }

    return cliente;
  }

  public List<Cliente> buscarPorNombre(String nombreLike) {
    String sql = "SELECT id, nombre, dni, telefono FROM clientes WHERE nombre LIKE ?";
    List<Cliente> clientes = new ArrayList<>();

    try (Connection conn = DatabaseConfig.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, "%" + nombreLike + "%");
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNombre(rs.getString("nombre"));
        c.setDni(rs.getString("dni"));
        c.setTelefono(rs.getString("telefono"));
        clientes.add(c);
      }
    } catch (SQLException e) {
      System.out.println("Error buscando clientes por nombre: " + e.getMessage());
    }

    return clientes;
  }
}
