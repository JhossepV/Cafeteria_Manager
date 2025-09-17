package dao;

import model.Producto;
import BD.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

  public List<Producto> listarTodos() {
  String sql = "SELECT id, nombre, descripcion, precio, categoria FROM productos ORDER BY id ASC";
    List<Producto> productos = new ArrayList<>();

    try (Connection conn = DatabaseConfig.connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        Producto producto = new Producto();
        producto.setId(rs.getInt("id"));
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setPrecio(rs.getDouble("precio"));
        producto.setCategoria(rs.getString("categoria"));
        productos.add(producto);
      }
    } catch (SQLException e) {
      System.out.println("Error obteniendo productos: " + e.getMessage());
    }

    return productos;
  }

  public Producto buscarPorId(int id) {
    String sql = "SELECT id, nombre, descripcion, precio, categoria FROM productos WHERE id = ?";
    Producto producto = null;

    try (Connection conn = DatabaseConfig.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        producto = new Producto();
        producto.setId(rs.getInt("id"));
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setPrecio(rs.getDouble("precio"));
        producto.setCategoria(rs.getString("categoria"));
      }
    } catch (SQLException e) {
      System.out.println("Error buscando producto: " + e.getMessage());
    }

    return producto;
  }

  public List<Producto> buscarPorCategoria(String categoria) {
    String sql = "SELECT id, nombre, descripcion, precio, categoria FROM productos WHERE categoria = ?";
    List<Producto> productos = new ArrayList<>();

    try (Connection conn = DatabaseConfig.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, categoria);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        Producto producto = new Producto();
        producto.setId(rs.getInt("id"));
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setPrecio(rs.getDouble("precio"));
        producto.setCategoria(rs.getString("categoria"));
        productos.add(producto);
      }
    } catch (SQLException e) {
      System.out.println("Error buscando productos por categoría: " + e.getMessage());
    }

    return productos;
  }

  public boolean insertar(Producto producto) {
    String sql = "INSERT INTO productos(nombre, descripcion, precio, categoria) VALUES(?, ?, ?, ?)";
    try (Connection conn = DatabaseConfig.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, producto.getNombre());
      ps.setString(2, producto.getDescripcion());
      ps.setDouble(3, producto.getPrecio());
      ps.setString(4, producto.getCategoria());
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      System.out.println("Error insertando producto: " + e.getMessage());
      return false;
    }
  }

  public boolean eliminar(int id) {
    String sql = "DELETE FROM productos WHERE id = ?";
    try (Connection conn = DatabaseConfig.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, id);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      System.out.println("Error eliminando producto: " + e.getMessage());
      return false;
    }
  }
}
