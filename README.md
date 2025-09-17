# Gestión de Cafetería (Swing + SQLite)

Aplicación de escritorio para gestionar clientes, mozos, productos y pedidos de una cafetería. Incluye una GUI en Swing con vistas por rol (Mozo/Chef), persistencia en SQLite (JDBC) y flujo de actualización de estados con auto‐refresco entre instancias.

## Tabla de contenidos
- Visión general
- Arquitectura y diseño
- Esquema de base de datos y bootstrapping
- Flujo de pedidos por rol (Mozo vs Chef)
- Validaciones y reglas de negocio
- Auto‐refresco y concurrencia
- Estructura del proyecto
- Cómo ejecutar

## Visión general

La aplicación permite:
- Administrar Clientes y Mozos (listar, agregar, eliminar, buscar por nombre para Clientes).
- Administrar el catálogo de Productos (listado para Mozo, alta/baja para Chef).
- Crear y gestionar Pedidos con sus Items, llevando el total y el estado.
- Visualización de pedidos con dos vistas: “Todos” y “Completados”, y actualización del estado acorde al rol.

Moneda: Todos los importes se muestran en solsitos S/.

## Arquitectura y diseño

Patrón en capas con paquetes separados:
- model/: Entidades de dominio simples (POJOs): Cliente, Producto, Pedido, ItemPedido, Mozo.
- dao/: Acceso a datos con JDBC hacia SQLite. Cada DAO encapsula el SQL de su entidad.
- service/: Lógica de negocio y orquestación (p. ej., validaciones, reglas de unicidad, flujos de selección).
- ui/: Interfaz de usuario. Se ofrece GUI (Swing) en AppFrame y aún conviven menús de consola (opcional/legacy).
- BD/DatabaseConfig: Conexión y creación de tablas iniciales, con seeding básico si está vacío.

Decisiones de diseño:
- UI no accede a JDBC directo para entidades clave; usa services/DAOs. Se dejó mínimo SQL directo histórico, y se favorece continuar moviéndolo a Service/DAO.
- Tablas con DefaultTableModel no editables; todos los cambios se hacen mediante acciones y diálogos.
- Orden estable de pedidos por id ascendente para que al cambiar estado/fecha no se reordenen.

## Esquema de base de datos y bootstrapping

Tablas principales:
- clientes(id, nombre, dni UNIQUE NULL, telefono)
- mozos(id, nombre, turno)
- productos(id, nombre, descripcion, precio, categoria)
- pedidos(id, cliente_id FK, mozo_id FK, fecha, estado, total)
- items_pedido(id, pedido_id FK, producto_id FK, cantidad, precio)

Inicialización:
- Si no hay mozos/productos, se cargan registros de ejemplo.

## Flujo de pedidos por rol

Vistas de pedidos: “Todos” y “Completados”.

- Chef:
  - Cambia estados: PENDIENTE → PREPARACION → COMPLETADO (o CANCELADO).
  - Al marcar COMPLETADO, el pedido desaparece de “Todos” del Chef y pasa a “Completados”.
- Mozo:
  - En “Todos” ve PENDIENTE, PREPARACION y COMPLETADO (para entregarlo).
  - Solo puede cambiar estado a ENTREGADO o CANCELADO.
  - Al marcar ENTREGADO, el pedido sale de “Todos” del Mozo y pasa a “Completados”.
  - Si se marca CANCELADO en cualquier instancia (Chef o Mozo), va a “Completados”.

Cada cambio de estado actualiza la columna fecha, pero el orden de la tabla permanece estable (ORDER BY id ASC).

## Validaciones y reglas de negocio

- Clientes:
  - Nombre: obligatorio.
  - DNI: opcional; si se informa, debe ser numérico de 8 dígitos; sin duplicados.
  - Teléfono: opcional; si se informa, debe ser numérico de 9 dígitos; sin duplicados.
  - Al fallar la inserción (duplicados / validación), la UI muestra el motivo.
- Mozos:
  - Nombre y Turno se ingresan sin validación de alfabético (a pedido). Eliminación por selección.
- Productos (Chef):
  - Nombre alfanumérico y categoría con letras/espacios.
  - Precio > 0.

## Auto‐refresco y concurrencia

- Pedidos y Productos se refrescan automáticamente cada ~3s en la vista abierta.
- Esto permite que distintas instancias vean los cambios de estado o catálogo casi en tiempo real.
- Pedidos ordenados por id ascendente para evitar “saltos” al actualizar hora.

## Estructura del proyecto

- BD/DatabaseConfig.java — conexión SQLite y creación/migración/seeding inicial.
- model/*.java — POJOs: Cliente, Producto, Pedido, ItemPedido, Mozo.
- dao/*.java — DAOs: ClienteDAO, ProductoDAO, PedidoDAO, MozoDAO.
- service/*.java — Servicios: ClienteService, ProductoService, PedidoService.
- ui/AppFrame.java — Ventana principal (Swing) con pestañas por rol.
- ui/Menu*.java — Menús de consola (legacy).
- Cafeteria.java — Punto de entrada. Pide rol y lanza AppFrame.

## Cómo ejecutar

1) Compilar y ejecutar con Java 21 y con el driver JDBC de SQLite en el classpath.
2) Ejecutar `Cafeteria` (raíz). El sistema:
   - Inicializa la base de datos.
   - Seleccionar el rol (Mozo o Chef) y se abrirá la GUI.