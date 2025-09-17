package dao;

import model.Mozo;
import BD.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MozoDAO {

  public List<Mozo> listarTodos() {
    String sql = "SELECT id, nombre, turno FROM mozos";
    List<Mozo> mozos = new ArrayList<>();

    try (Connection conn = DatabaseConfig.connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        Mozo mozo = new Mozo();
        mozo.setId(rs.getInt("id"));
        mozo.setNombre(rs.getString("nombre"));
        mozo.setTurno(rs.getString("turno"));
        mozos.add(mozo);
      }
    } catch (SQLException e) {
      System.out.println("Error obteniendo mozos: " + e.getMessage());
    }

    return mozos;
  }

  public Mozo buscarPorId(int id) {
    String sql = "SELECT id, nombre, turno FROM mozos WHERE id = ?";
    Mozo mozo = null;

    try (Connection conn = DatabaseConfig.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        mozo = new Mozo();
        mozo.setId(rs.getInt("id"));
        mozo.setNombre(rs.getString("nombre"));
        mozo.setTurno(rs.getString("turno"));
      }
    } catch (SQLException e) {
      System.out.println("Error buscando mozo: " + e.getMessage());
    }

    return mozo;
  }

  public boolean insertar(Mozo mozo) {
    String sql = "INSERT INTO mozos(nombre, turno) VALUES(?, ?)";
    try (Connection conn = DatabaseConfig.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, mozo.getNombre());
      ps.setString(2, mozo.getTurno());
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      System.out.println("Error insertando mozo: " + e.getMessage());
      return false;
    }
  }

  public boolean eliminar(int id) {
    String sql = "DELETE FROM mozos WHERE id = ?";
    try (Connection conn = DatabaseConfig.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, id);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      System.out.println("Error eliminando mozo: " + e.getMessage());
      return false;
    }
  }
}
