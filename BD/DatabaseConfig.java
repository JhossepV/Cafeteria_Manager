package BD;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseConfig {
  private static final String URL = "jdbc:sqlite:cafeteria.db";

  public static Connection connect() {
    Connection conn = null;
    try {
      conn = DriverManager.getConnection(URL);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return conn;
  }

  public static void initializeDatabase() {
    String[] createTables = {
    "CREATE TABLE IF NOT EXISTS clientes (" +
      "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
      "nombre TEXT NOT NULL, " +
      "dni TEXT UNIQUE, " +
      "telefono TEXT)",

        "CREATE TABLE IF NOT EXISTS mozos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT NOT NULL, " +
            "turno TEXT NOT NULL)",

        "CREATE TABLE IF NOT EXISTS productos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT NOT NULL, " +
            "descripcion TEXT, " +
            "precio REAL NOT NULL, " +
            "categoria TEXT NOT NULL)",

    "CREATE TABLE IF NOT EXISTS pedidos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cliente_id INTEGER NOT NULL, " +
            "mozo_id INTEGER NOT NULL, " +
            "fecha TEXT NOT NULL, " +
            "estado TEXT NOT NULL, " +
      "total REAL NOT NULL, " +
            "FOREIGN KEY (cliente_id) REFERENCES clientes (id), " +
            "FOREIGN KEY (mozo_id) REFERENCES mozos (id))",

        "CREATE TABLE IF NOT EXISTS items_pedido (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "pedido_id INTEGER NOT NULL, " +
            "producto_id INTEGER NOT NULL, " +
            "cantidad INTEGER NOT NULL, " +
            "precio REAL NOT NULL, " +
            "FOREIGN KEY (pedido_id) REFERENCES pedidos (id), " +
            "FOREIGN KEY (producto_id) REFERENCES productos (id))"
    };

    try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
      for (String sql : createTables) {
        stmt.execute(sql);
      }

      // Migración: asegurar columna DNI en 'clientes'
      try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(clientes)")) {
        boolean tieneDni = false;
        while (rs.next()) {
          String col = rs.getString("name");
          if ("dni".equalsIgnoreCase(col)) {
            tieneDni = true;
            break;
          }
        }
        if (!tieneDni) {
          // Agregar columna dni; no se puede agregar NOT NULL en ALTER con SQLite sin default
          stmt.execute("ALTER TABLE clientes ADD COLUMN dni TEXT");
          // Índice único para mantener unicidad de DNI
          stmt.execute("CREATE UNIQUE INDEX IF NOT EXISTS idx_clientes_dni ON clientes(dni)");
        }
      }

      try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM mozos")) {
        if (rs.next() && rs.getInt("cnt") == 0) {
          stmt.execute("INSERT INTO mozos (nombre, turno) VALUES " +
              "('Juan Pérez', 'Mañana'), " +
              "('María García', 'Tarde'), " +
              "('Carlos López', 'Noche')");
        }
      }

      try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM productos")) {
        if (rs.next() && rs.getInt("cnt") == 0) {
          stmt.execute("INSERT INTO productos (nombre, descripcion, precio, categoria) VALUES " +
              "('Café Americano', 'Café negro estándar', 2.50, 'Bebidas Calientes'), " +
              "('Café Latte', 'Café con leche vaporizada', 3.50, 'Bebidas Calientes'), " +
              "('Capuchino', 'Café con leche y espuma', 3.75, 'Bebidas Calientes'), " +
              "('Té Verde', 'Té verde natural', 2.00, 'Bebidas Calientes'), " +
              "('Refresco', 'Refresco de cola o naranja', 2.25, 'Bebidas Frías'), " +
              "('Jugo Natural', 'Jugo de naranja o manzana', 3.00, 'Bebidas Frías'), " +
              "('Sandwich Club', 'Sandwich de pollo, tocino y lechuga', 5.50, 'Comida'), " +
              "('Ensalada César', 'Ensalada con pollo y aderezo césar', 6.00, 'Comida'), " +
              "('Bagel', 'Bagel con queso crema', 3.25, 'Comida'), " +
              "('Donut', 'Donut glaseado o con chocolate', 2.00, 'Postres'), " +
              "('Tarta de Queso', 'Porción de tarta de queso', 4.50, 'Postres'), " +
              "('Galletas', 'Galletas caseras', 1.75, 'Postres')");
        }
      }

    } catch (SQLException e) {
      System.out.println("Error inicializando la base de datos: " + e.getMessage());
    }
  }
}