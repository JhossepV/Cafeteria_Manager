package dao;

import model.Pedido;
import model.ItemPedido;
import BD.DatabaseConfig;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

  public int insertar(Pedido pedido) {
    String sql = "INSERT INTO pedidos(cliente_id, mozo_id, fecha, estado, total) VALUES(?, ?, ?, ?, ?)";
    int generatedId = -1;

    try (Connection conn = DatabaseConfig.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      pstmt.setInt(1, pedido.getClienteId());
      pstmt.setInt(2, pedido.getMozoId());
      pstmt.setString(3, pedido.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
      pstmt.setString(4, pedido.getEstado());
  pstmt.setDouble(5, pedido.getTotal());

      int affectedRows = pstmt.executeUpdate();
      if (affectedRows > 0) {
        try (ResultSet rs = pstmt.getGeneratedKeys()) {
          if (rs.next()) {
            generatedId = rs.getInt(1);
          }
        }
      }
    } catch (SQLException e) {
      System.out.println("Error insertando pedido: " + e.getMessage());
    }

    return generatedId;
  }

  public void actualizar(Pedido pedido) {
    String sql = "UPDATE pedidos SET estado = ?, fecha = ? WHERE id = ?";

    try (Connection conn = DatabaseConfig.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      pstmt.setString(1, pedido.getEstado());
      pstmt.setString(2, now);
      pstmt.setInt(3, pedido.getId());
      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Error actualizando pedido: " + e.getMessage());
    }
  }

  public List<Pedido> listarTodos() {
  String sql = "SELECT p.id, p.cliente_id, c.nombre as cliente_nombre, p.mozo_id, m.nombre as mozo_nombre, " +
    "p.fecha, p.estado, p.total " +
        "FROM pedidos p " +
        "JOIN clientes c ON p.cliente_id = c.id " +
        "JOIN mozos m ON p.mozo_id = m.id " +
  "ORDER BY p.id ASC";
    List<Pedido> pedidos = new ArrayList<>();

    try (Connection conn = DatabaseConfig.connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getInt("id"));
        pedido.setClienteId(rs.getInt("cliente_id"));
        pedido.setClienteNombre(rs.getString("cliente_nombre"));
        pedido.setMozoId(rs.getInt("mozo_id"));
        pedido.setMozoNombre(rs.getString("mozo_nombre"));
        pedido.setFecha(LocalDateTime.parse(rs.getString("fecha"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        pedido.setEstado(rs.getString("estado"));
        pedido.setTotal(rs.getDouble("total"));
  // favorito eliminado

        pedido.setItems(obtenerItemsPedido(pedido.getId()));

        pedidos.add(pedido);
      }
    } catch (SQLException e) {
      System.out.println("Error obteniendo pedidos: " + e.getMessage());
    }

    return pedidos;
  }

  public Pedido buscarPorId(int id) {
  String sql = "SELECT p.id, p.cliente_id, c.nombre as cliente_nombre, p.mozo_id, m.nombre as mozo_nombre, " +
    "p.fecha, p.estado, p.total " +
        "FROM pedidos p " +
        "JOIN clientes c ON p.cliente_id = c.id " +
        "JOIN mozos m ON p.mozo_id = m.id " +
        "WHERE p.id = ?";
    Pedido pedido = null;

    try (Connection conn = DatabaseConfig.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        pedido = new Pedido();
        pedido.setId(rs.getInt("id"));
        pedido.setClienteId(rs.getInt("cliente_id"));
        pedido.setClienteNombre(rs.getString("cliente_nombre"));
        pedido.setMozoId(rs.getInt("mozo_id"));
        pedido.setMozoNombre(rs.getString("mozo_nombre"));
        pedido.setFecha(LocalDateTime.parse(rs.getString("fecha"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        pedido.setEstado(rs.getString("estado"));
        pedido.setTotal(rs.getDouble("total"));
  // favorito eliminado

        pedido.setItems(obtenerItemsPedido(pedido.getId()));
      }
    } catch (SQLException e) {
      System.out.println("Error buscando pedido: " + e.getMessage());
    }

    return pedido;
  }

  public List<Pedido> listarPorEstado(String estado) {
  String sql = "SELECT p.id, p.cliente_id, c.nombre as cliente_nombre, p.mozo_id, m.nombre as mozo_nombre, " +
    "p.fecha, p.estado, p.total " +
        "FROM pedidos p " +
        "JOIN clientes c ON p.cliente_id = c.id " +
        "JOIN mozos m ON p.mozo_id = m.id " +
  "WHERE p.estado = ? " +
  "ORDER BY p.id ASC";
    List<Pedido> pedidos = new ArrayList<>();

    try (Connection conn = DatabaseConfig.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, estado);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getInt("id"));
        pedido.setClienteId(rs.getInt("cliente_id"));
        pedido.setClienteNombre(rs.getString("cliente_nombre"));
        pedido.setMozoId(rs.getInt("mozo_id"));
        pedido.setMozoNombre(rs.getString("mozo_nombre"));
        pedido.setFecha(LocalDateTime.parse(rs.getString("fecha"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        pedido.setEstado(rs.getString("estado"));
        pedido.setTotal(rs.getDouble("total"));
  // favorito eliminado

        pedido.setItems(obtenerItemsPedido(pedido.getId()));

        pedidos.add(pedido);
      }
    } catch (SQLException e) {
      System.out.println("Error obteniendo pedidos por estado: " + e.getMessage());
    }

    return pedidos;
  }

  public List<Pedido> listarPorEstados(String... estados) {
    if (estados == null || estados.length == 0) return new ArrayList<>();
    StringBuilder in = new StringBuilder();
    for (int i = 0; i < estados.length; i++) {
      if (i > 0) in.append(",");
      in.append("?");
    }
    String sql = "SELECT p.id, p.cliente_id, c.nombre as cliente_nombre, p.mozo_id, m.nombre as mozo_nombre, " +
      "p.fecha, p.estado, p.total " +
      "FROM pedidos p " +
      "JOIN clientes c ON p.cliente_id = c.id " +
      "JOIN mozos m ON p.mozo_id = m.id " +
      "WHERE p.estado IN (" + in + ") " +
  "ORDER BY p.id ASC";
    List<Pedido> pedidos = new ArrayList<>();

    try (Connection conn = DatabaseConfig.connect();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
      for (int i = 0; i < estados.length; i++) {
        pstmt.setString(i + 1, estados[i]);
      }
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getInt("id"));
        pedido.setClienteId(rs.getInt("cliente_id"));
        pedido.setClienteNombre(rs.getString("cliente_nombre"));
        pedido.setMozoId(rs.getInt("mozo_id"));
        pedido.setMozoNombre(rs.getString("mozo_nombre"));
        pedido.setFecha(LocalDateTime.parse(rs.getString("fecha"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        pedido.setEstado(rs.getString("estado"));
        pedido.setTotal(rs.getDouble("total"));
        pedido.setItems(obtenerItemsPedido(pedido.getId()));
        pedidos.add(pedido);
      }
    } catch (SQLException e) {
      System.out.println("Error obteniendo pedidos por estados: " + e.getMessage());
    }

    return pedidos;
  }

  public List<Pedido> listarActivos() {
    String sql = "SELECT p.id, p.cliente_id, c.nombre as cliente_nombre, p.mozo_id, m.nombre as mozo_nombre, " +
      "p.fecha, p.estado, p.total " +
      "FROM pedidos p " +
      "JOIN clientes c ON p.cliente_id = c.id " +
      "JOIN mozos m ON p.mozo_id = m.id " +
  "WHERE p.estado IN ('PENDIENTE','PREPARACION','COMPLETADO') " +
  "ORDER BY p.id ASC";
    List<Pedido> pedidos = new ArrayList<>();

    try (Connection conn = DatabaseConfig.connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getInt("id"));
        pedido.setClienteId(rs.getInt("cliente_id"));
        pedido.setClienteNombre(rs.getString("cliente_nombre"));
        pedido.setMozoId(rs.getInt("mozo_id"));
        pedido.setMozoNombre(rs.getString("mozo_nombre"));
        pedido.setFecha(LocalDateTime.parse(rs.getString("fecha"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        pedido.setEstado(rs.getString("estado"));
        pedido.setTotal(rs.getDouble("total"));
        pedido.setItems(obtenerItemsPedido(pedido.getId()));
        pedidos.add(pedido);
      }
    } catch (SQLException e) {
      System.out.println("Error obteniendo pedidos activos: " + e.getMessage());
    }

    return pedidos;
  }

  public List<Pedido> listarCompletados() {
    String sql = "SELECT p.id, p.cliente_id, c.nombre as cliente_nombre, p.mozo_id, m.nombre as mozo_nombre, " +
      "p.fecha, p.estado, p.total " +
      "FROM pedidos p " +
      "JOIN clientes c ON p.cliente_id = c.id " +
      "JOIN mozos m ON p.mozo_id = m.id " +
  "WHERE p.estado IN ('ENTREGADO','CANCELADO') " +
  "ORDER BY p.fecha ASC";
    List<Pedido> pedidos = new ArrayList<>();

    try (Connection conn = DatabaseConfig.connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getInt("id"));
        pedido.setClienteId(rs.getInt("cliente_id"));
        pedido.setClienteNombre(rs.getString("cliente_nombre"));
        pedido.setMozoId(rs.getInt("mozo_id"));
        pedido.setMozoNombre(rs.getString("mozo_nombre"));
        pedido.setFecha(LocalDateTime.parse(rs.getString("fecha"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        pedido.setEstado(rs.getString("estado"));
        pedido.setTotal(rs.getDouble("total"));
        pedido.setItems(obtenerItemsPedido(pedido.getId()));
        pedidos.add(pedido);
      }
    } catch (SQLException e) {
      System.out.println("Error obteniendo pedidos completados: " + e.getMessage());
    }

    return pedidos;
  }

  // listarFavoritos eliminado

  private List<ItemPedido> obtenerItemsPedido(int pedidoId) {
    String sql = "SELECT ip.id, ip.producto_id, p.nombre as producto_nombre, ip.cantidad, ip.precio " +
        "FROM items_pedido ip " +
        "JOIN productos p ON ip.producto_id = p.id " +
        "WHERE ip.pedido_id = ?";
    List<ItemPedido> items = new ArrayList<>();

    try (Connection conn = DatabaseConfig.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, pedidoId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        ItemPedido item = new ItemPedido();
        item.setId(rs.getInt("id"));
        item.setProductoId(rs.getInt("producto_id"));
        item.setProductoNombre(rs.getString("producto_nombre"));
        item.setCantidad(rs.getInt("cantidad"));
        item.setPrecio(rs.getDouble("precio"));
        items.add(item);
      }
    } catch (SQLException e) {
      System.out.println("Error obteniendo items del pedido: " + e.getMessage());
    }

    return items;
  }

  public void insertarItemPedido(int pedidoId, ItemPedido item) {
    String sql = "INSERT INTO items_pedido(pedido_id, producto_id, cantidad, precio) VALUES(?, ?, ?, ?)";

    try (Connection conn = DatabaseConfig.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, pedidoId);
      pstmt.setInt(2, item.getProductoId());
      pstmt.setInt(3, item.getCantidad());
      pstmt.setDouble(4, item.getPrecio());
      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Error insertando item de pedido: " + e.getMessage());
    }
  }
}
