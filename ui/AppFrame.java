package ui;

import dao.MozoDAO;
import dao.ProductoDAO;
import dao.PedidoDAO;
import model.*;
import service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AppFrame extends JFrame {
  public enum Role {
    MOZO, CHEF
  }

  private final ClienteService clienteService = new ClienteService();
  private final MozoDAO mozoDAO = new MozoDAO();
  private final ProductoDAO productoDAO = new ProductoDAO();
  private final PedidoDAO pedidoDAO = new PedidoDAO();

  public AppFrame(Role role) {
    super("Gestión de Cafetería");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1100, 720);
    setLocationRelativeTo(null);

    JTabbedPane tabs = new JTabbedPane();
  // Selección de pestañas según rol
  if (role == Role.MOZO) {
      tabs.addTab("Clientes", buildClientesPanel());
      tabs.addTab("Pedidos", buildPedidosPanel(true));
      tabs.addTab("Productos", buildProductosPanel(false));
    } else { // CHEF
      tabs.addTab("Mozos", buildMozosPanel());
      tabs.addTab("Pedidos", buildPedidosPanel(false));
      tabs.addTab("Productos", buildProductosPanel(true));
    }
    setContentPane(tabs);

    // Timer global para refrescos generales
    // El refresco específico de cada pestaña está dentro de cada panel
    new javax.swing.Timer(3000, e -> {
      // Hook para auto-refresh futuro; por ahora no hace nada pesado
    }).start();
  }

  private JPanel buildClientesPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    DefaultTableModel model = new DefaultTableModel(new Object[] { "ID", "Nombre", "DNI", "Teléfono" }, 0) {
      @Override
      public boolean isCellEditable(int r, int c) {
        return false;
      }
    };
    JTable table = new JTable(model);
  // columnas: hacer ID más angosta
  table.getColumnModel().getColumn(0).setMinWidth(40);
  table.getColumnModel().getColumn(0).setMaxWidth(60);
  table.getColumnModel().getColumn(0).setPreferredWidth(50);

  // Refresca tabla de clientes desde DB
  Runnable refresh = () -> {
      model.setRowCount(0);
      for (Cliente c : clienteService.listarTodosClientes()) {
        model.addRow(new Object[] { c.getId(), c.getNombre(), c.getDni(), c.getTelefono() });
      }
    };
    refresh.run();

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JTextField nombre = new JTextField(15);
    JTextField dni = new JTextField(10);
    JTextField tel = new JTextField(10);
    JButton add = new JButton("Agregar");
    JButton del = new JButton("Eliminar seleccionado");
  JButton buscar = new JButton("Buscar por nombre");
  JButton verTodos = new JButton("Todos");
    top.add(new JLabel("Nombre:"));
    top.add(nombre);
    top.add(new JLabel("DNI:"));
    top.add(dni);
    top.add(new JLabel("Tel:"));
    top.add(tel);
  top.add(add);
  top.add(del);
  top.add(buscar);
  top.add(verTodos);

    add.addActionListener(e -> {
      if (nombre.getText().isBlank() || dni.getText().isBlank()) {
        // Ahora DNI puede estar vacío; sólo el nombre es obligatorio
        if (nombre.getText().isBlank()) {
          JOptionPane.showMessageDialog(panel, "El nombre es obligatorio");
          return;
        }
      }
      String vNombre = nombre.getText().trim();
      // Validaciones: DNI 8 dígitos si no está vacío; Teléfono 9 dígitos si no está vacío
      String vDni = dni.getText().trim();
      if (!vDni.isEmpty() && !vDni.matches("\\d{8}")) {
        JOptionPane.showMessageDialog(panel, "El DNI debe tener 8 dígitos numéricos (o dejarlo vacío)");
        return;
      }
      String vTel = tel.getText().trim();
      if (!vTel.isEmpty() && !vTel.matches("\\d{9}")) {
        JOptionPane.showMessageDialog(panel, "El teléfono debe tener 9 dígitos numéricos (o dejarlo vacío)");
        return;
      }
  Cliente c = new Cliente(vNombre, vDni, vTel);
      boolean ok = clienteService.agregarCliente(c);
      if (!ok) {
          // Mostrar motivo específico desde el servicio (DNI/Teléfono duplicado, etc.)
          String msg = clienteService.getLastError();
          JOptionPane.showMessageDialog(panel, msg != null ? msg : "No se pudo agregar el cliente");
      }
      // limpiar campos
      nombre.setText("");
      dni.setText("");
      tel.setText("");
      refresh.run();
    });

    del.addActionListener(e -> {
      int row = table.getSelectedRow();
      if (row < 0)
        return;
      int id = (int) model.getValueAt(row, 0);
      if (clienteService.eliminarClientePorId(id))
        refresh.run();
    });

    buscar.addActionListener(e -> {
      String q = JOptionPane.showInputDialog(panel, "Nombre contiene:");
      if (q == null)
        return;
      model.setRowCount(0);
      for (Cliente c : clienteService.buscarClientesPorNombre(q)) {
        model.addRow(new Object[] { c.getId(), c.getNombre(), c.getDni(), c.getTelefono() });
      }
    });
    verTodos.addActionListener(e -> refresh.run());

    panel.add(top, BorderLayout.NORTH);
    panel.add(new JScrollPane(table), BorderLayout.CENTER);
    return panel;
  }

  private JPanel buildMozosPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    DefaultTableModel model = new DefaultTableModel(new Object[] { "ID", "Nombre", "Turno" }, 0) {
      @Override
      public boolean isCellEditable(int r, int c) {
        return false;
      }
    };
    JTable table = new JTable(model);
  // columnas: hacer ID más angosta
  table.getColumnModel().getColumn(0).setMinWidth(40);
  table.getColumnModel().getColumn(0).setMaxWidth(60);
  table.getColumnModel().getColumn(0).setPreferredWidth(50);

  // Refresca tabla de mozos desde DB
  Runnable refresh = () -> {
      model.setRowCount(0);
      for (Mozo m : mozoDAO.listarTodos()) {
        model.addRow(new Object[] { m.getId(), m.getNombre(), m.getTurno() });
      }
    };
    refresh.run();

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JTextField nombre = new JTextField(15);
    JTextField turno = new JTextField(10);
  JButton add = new JButton("Agregar");
  JButton del = new JButton("Eliminar seleccionado");
  JButton verTodos = new JButton("Todos");
    top.add(new JLabel("Nombre:"));
    top.add(nombre);
    top.add(new JLabel("Turno:"));
    top.add(turno);
  top.add(add);
  top.add(del);
  top.add(verTodos);

    add.addActionListener(e -> {
      Mozo mozo = new Mozo();
      mozo.setNombre(nombre.getText().trim());
      mozo.setTurno(turno.getText().trim());
      boolean ok = mozoDAO.insertar(mozo);
      if (!ok)
        JOptionPane.showMessageDialog(panel, "No se pudo agregar el mozo");
      // limpiar inputs
      nombre.setText("");
      turno.setText("");
      refresh.run();
    });

    del.addActionListener(e -> {
      int row = table.getSelectedRow();
      if (row < 0)
        return;
      int id = (int) model.getValueAt(row, 0);
      boolean ok = mozoDAO.eliminar(id);
      if (!ok)
        JOptionPane.showMessageDialog(panel, "No se pudo eliminar el mozo");
      refresh.run();
    });
    verTodos.addActionListener(e -> refresh.run());

    panel.add(top, BorderLayout.NORTH);
    panel.add(new JScrollPane(table), BorderLayout.CENTER);
    return panel;
  }

  private JPanel buildProductosPanel(boolean conAcciones) {
    JPanel panel = new JPanel(new BorderLayout());
    DefaultTableModel model = new DefaultTableModel(
        new Object[] { "ID", "Nombre", "Descripción", "Precio", "Categoría" }, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    JTable table = new JTable(model);
  // columnas: hacer ID más angosta
  table.getColumnModel().getColumn(0).setMinWidth(40);
  table.getColumnModel().getColumn(0).setMaxWidth(60);
  table.getColumnModel().getColumn(0).setPreferredWidth(50);
  // Refresca tabla de productos desde DB
  Runnable refresh = () -> {
      model.setRowCount(0);
      for (Producto p : productoDAO.listarTodos()) {
        model.addRow(
            new Object[] { p.getId(), p.getNombre(), p.getDescripcion(), "S/" + p.getPrecio(), p.getCategoria() });
      }
    };
    refresh.run();
  // Auto-refresh productos cada 3s
    new javax.swing.Timer(3000, e -> refresh.run()).start();

    if (conAcciones) {
      JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JTextField nombre = new JTextField(12);
      JTextField desc = new JTextField(15);
      JSpinner precio = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 1000.0, 0.1));
      JTextField categoria = new JTextField(12);
      JButton add = new JButton("Agregar");
      JButton del = new JButton("Eliminar seleccionado");
      top.add(new JLabel("Nombre:"));
      top.add(nombre);
      top.add(new JLabel("Descripción:"));
      top.add(desc);
      top.add(new JLabel("Precio:"));
      top.add(precio);
      top.add(new JLabel("Categoría:"));
      top.add(categoria);
      top.add(add);
      top.add(del);

      add.addActionListener(e -> {
        // Validaciones de producto: nombre alfanumérico y categoría sólo letras
        String vNombre = nombre.getText().trim();
        String vCategoria = categoria.getText().trim();
        if (!vNombre.matches("[A-Za-z0-9ÁÉÍÓÚáéíóúÑñ ]+")) {
          JOptionPane.showMessageDialog(panel, "El nombre del producto debe ser alfanumérico (letras/números y espacios)");
          return;
        }
        if (!vCategoria.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) {
          JOptionPane.showMessageDialog(panel, "La categoría sólo puede contener letras y espacios");
          return;
        }
        double vPrecio = ((Double) precio.getValue());
        if (vPrecio <= 0) {
          JOptionPane.showMessageDialog(panel, "El precio debe ser mayor a 0");
          return;
        }
        Producto p = new Producto(vNombre, desc.getText().trim(), vPrecio, vCategoria);
        boolean ok = productoDAO.insertar(p);
        if (!ok)
          JOptionPane.showMessageDialog(panel, "No se pudo agregar el producto");
        // limpiar inputs
        nombre.setText("");
        desc.setText("");
        precio.setValue(1.0);
        categoria.setText("");
        refresh.run();
      });

      del.addActionListener(e -> {
        int row = table.getSelectedRow();
        if (row < 0)
          return;
        int id = (int) model.getValueAt(row, 0);
        boolean ok = productoDAO.eliminar(id);
        if (!ok)
          JOptionPane.showMessageDialog(panel, "No se pudo eliminar el producto");
        refresh.run();
      });

      panel.add(top, BorderLayout.NORTH);
    }

    panel.add(new JScrollPane(table), BorderLayout.CENTER);
    return panel;
  }

  private JPanel buildPedidosPanel(boolean modoMozo) {
    JPanel panel = new JPanel(new BorderLayout());

    DefaultTableModel model = new DefaultTableModel(
        new Object[] { "ID", "Cliente", "Mozo", "Fecha", "Estado", "Total" }, 0) {
      @Override
      public boolean isCellEditable(int r, int c) {
        return false;
      }
    };
    JTable table = new JTable(model);
  // columnas: hacer ID más angosta
  table.getColumnModel().getColumn(0).setMinWidth(40);
  table.getColumnModel().getColumn(0).setMaxWidth(60);
  table.getColumnModel().getColumn(0).setPreferredWidth(50);

  final String[] vista = { "TODOS" }; // filtro actual (Todos vs Completados)
  // Refresca tabla de pedidos según filtro y rol
  Runnable refresh = () -> {
      model.setRowCount(0);
      List<Pedido> datos;
      switch (vista[0]) {
        case "COMPLETADOS":
          datos = pedidoDAO.listarCompletados();
          break;
        case "TODOS":
        default:
          if (modoMozo) {
            // Mozo ve PENDIENTE, PREPARACION, COMPLETADO en "Todos"
            datos = pedidoDAO.listarPorEstados("PENDIENTE", "PREPARACION", "COMPLETADO");
          } else {
            // Chef también ve PENDIENTE, PREPARACION, COMPLETADO en "Todos"
            datos = pedidoDAO.listarPorEstados("PENDIENTE", "PREPARACION", "COMPLETADO");
          }
      }
      for (Pedido p : datos) {
        model.addRow(new Object[] { p.getId(), p.getClienteNombre(), p.getMozoNombre(), p.getFecha(), p.getEstado(),
            "S/" + p.getTotal() });
      }
    };
    refresh.run();

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton nuevo = new JButton("Nuevo pedido");
    JButton todos = new JButton("Todos");
    JButton completados = new JButton("Completados");
    JButton actualizarEstado = new JButton("Actualizar estado");
    if (modoMozo) {
      top.add(nuevo);
    }
    top.add(todos);
    top.add(completados);
    top.add(actualizarEstado);

    nuevo.addActionListener(e -> {
      mostrarDialogoNuevoPedido(panel, refresh);
    });

    todos.addActionListener(e -> {
      vista[0] = "TODOS";
      refresh.run();
    });
    completados.addActionListener(e -> {
      vista[0] = "COMPLETADOS";
      refresh.run();
    });
    actualizarEstado.addActionListener(e -> {
      int row = table.getSelectedRow();
      if (row < 0)
        return;
      int id = (int) model.getValueAt(row, 0);
      String[] estados = modoMozo ? new String[] { "ENTREGADO", "CANCELADO" }
          : new String[] { "PENDIENTE", "PREPARACION", "COMPLETADO", "CANCELADO" };
      String estado = (String) JOptionPane.showInputDialog(panel, "Nuevo estado:", "Actualizar estado",
          JOptionPane.QUESTION_MESSAGE, null, estados, estados[0]);
      if (estado == null)
        return;
      Pedido p = pedidoDAO.buscarPorId(id);
      if (p == null)
        return;
      p.setEstado(estado.toUpperCase());
      pedidoDAO.actualizar(p);
      refresh.run();
    });

  // Auto-refresh periódico de la lista actual (cada 3s)
    new javax.swing.Timer(3000, e -> refresh.run()).start();

    panel.add(top, BorderLayout.NORTH);
    panel.add(new JScrollPane(table), BorderLayout.CENTER);
    return panel;
  }

  private void mostrarDialogoNuevoPedido(JComponent parent, Runnable onCreated) {
    JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(parent), "Nuevo Pedido",
        Dialog.ModalityType.APPLICATION_MODAL);
    dlg.setSize(700, 500);
    dlg.setLocationRelativeTo(parent);
    dlg.setLayout(new BorderLayout());

    // Top: selección de cliente y mozo
    JPanel selPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    List<Cliente> clientes = clienteService.listarTodosClientes();
    List<Mozo> mozos = mozoDAO.listarTodos();
    JComboBox<Cliente> cbCliente = new JComboBox<>(clientes.toArray(new Cliente[0]));
    JComboBox<Mozo> cbMozo = new JComboBox<>(mozos.toArray(new Mozo[0]));
    selPanel.add(new JLabel("Cliente:"));
    selPanel.add(cbCliente);
    selPanel.add(new JLabel("Mozo:"));
    selPanel.add(cbMozo);

    // Centro: selección de productos y items agregados
    JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));
    DefaultListModel<Producto> modeloProductos = new DefaultListModel<>();
    for (Producto p : productoDAO.listarTodos())
      modeloProductos.addElement(p);
    JList<Producto> listaProductos = new JList<>(modeloProductos);
    JPanel left = new JPanel(new BorderLayout());
    left.add(new JLabel("Productos"), BorderLayout.NORTH);
    left.add(new JScrollPane(listaProductos), BorderLayout.CENTER);

    JPanel right = new JPanel(new BorderLayout());
    DefaultListModel<String> modeloItems = new DefaultListModel<>();
    JList<String> listaItems = new JList<>(modeloItems);
    right.add(new JLabel("Items del pedido"), BorderLayout.NORTH);
    right.add(new JScrollPane(listaItems), BorderLayout.CENTER);

    JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JSpinner spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    JButton btnAgregarItem = new JButton("Agregar item");
    JLabel lblTotal = new JLabel("Total: S/0.0");
    addPanel.add(new JLabel("Cantidad:"));
    addPanel.add(spCantidad);
    addPanel.add(btnAgregarItem);
    addPanel.add(lblTotal);
    right.add(addPanel, BorderLayout.SOUTH);

    center.add(left);
    center.add(right);

    // Estado del pedido en construcción
    List<ItemPedido> items = new ArrayList<>();

    btnAgregarItem.addActionListener(e -> {
      Producto sel = listaProductos.getSelectedValue();
      if (sel == null)
        return;
      int cant = (int) spCantidad.getValue();
      ItemPedido it = new ItemPedido(sel.getId(), sel.getNombre(), cant, sel.getPrecio());
      items.add(it);
      modeloItems.addElement(cant + "x " + sel.getNombre() + " - S/" + (cant * sel.getPrecio()));
      double total = items.stream().mapToDouble(ItemPedido::getSubtotal).sum();
      lblTotal.setText("Total: S/" + total);
    });

    // Bottom: botones
    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton btnCrear = new JButton("Crear");
    JButton btnCancelar = new JButton("Cancelar");
    bottom.add(btnCancelar);
    bottom.add(btnCrear);

    btnCancelar.addActionListener(e -> dlg.dispose());
    btnCrear.addActionListener(e -> {
      if (cbCliente.getSelectedItem() == null || cbMozo.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(dlg, "Seleccione cliente y mozo");
        return;
      }
      if (items.isEmpty()) {
        JOptionPane.showMessageDialog(dlg, "Agregue al menos un producto");
        return;
      }
      Cliente c = (Cliente) cbCliente.getSelectedItem();
      Mozo m = (Mozo) cbMozo.getSelectedItem();
      Pedido pedido = new Pedido(c.getId(), c.getNombre(), m.getId(), m.getNombre());
      // Total se acumulará al agregar items
      for (ItemPedido it : items)
        pedido.agregarItem(it);
      int pedidoId = pedidoDAO.insertar(pedido);
      pedido.setId(pedidoId);
      for (ItemPedido it : items)
        pedidoDAO.insertarItemPedido(pedidoId, it);
      dlg.dispose();
      if (onCreated != null)
        onCreated.run();
      JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(parent), "Pedido creado: #" + pedidoId);
    });

    dlg.add(selPanel, BorderLayout.NORTH);
    dlg.add(center, BorderLayout.CENTER);
    dlg.add(bottom, BorderLayout.SOUTH);
    dlg.setVisible(true);
  }
}
