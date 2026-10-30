package com.mycompany.evaluadorcrediticio;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.util.function.Consumer;

/**
 * Ventana principal: carga y baja de clientes (personas y empresas).
 *
 * Esta clase solo lee los campos del formulario, se los pasa a
 * {@link Persona} / {@link Empresa} y muestra lo que esas clases devuelven.
 * Las operaciones de base de datos corren en segundo plano ({@link TareaDB})
 * para no congelar la ventana; la barra de progreso indica visualmente cuándo
 * hay una operación en curso.
 */
public class VentanaPrincipal extends JFrame {

    private static final Color VERDE = new Color(0x1B7F3B);
    private static final Color ROJO  = new Color(0xB00020);
    private static final NumberFormat PESOS =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-AR"));

    // ------------------------------------------------------------------ resultado combinado (operación + listado)
    private record RespPersona(Resultado res, List<Persona.Resumen> lista) {}
    private record RespEmpresa(Resultado res, List<Empresa.Resumen> lista) {}

    // ------------------------------------------------------------------ estado general
    private final ConexionBDD conexion;
    /** Auxiliares para traducir checkbox ↔ código de historial de cada tipo. */
    private final Persona  personaAux  = new Persona();
    private final Empresa  empresaAux  = new Empresa();
    private VentanaUsuario ventanaUsuario;

    // ------------------------------------------------------------------ formulario
    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"Persona", "Empresa"});

    /** Nombre único del cliente en el sistema (independiente del nombre real de la persona). */
    private final JTextField txtNombreCliente = new JTextField(20);

    /** Para Persona: nombre real. Para Empresa: se usa el campo de nombre del formulario
     *  solo si el SP de empresa lo requiere separado (actualmente no lo hace). */
    private final JLabel     lblNombre   = new JLabel("Nombre");
    private final JTextField txtNombre   = new JTextField(20);

    private final JLabel     lblApellido = new JLabel("Apellido");
    private final JTextField txtApellido = new JTextField(20);
    private final JLabel     lblDni      = new JLabel("DNI");
    private final JTextField txtDni      = new JTextField(20);

    private final JLabel     lblRubro    = new JLabel("Rubro");
    private final JTextField txtRubro    = new JTextField(20);
    private final JLabel     lblDirector = new JLabel("Director");
    private final JTextField txtDirector = new JTextField(20);

    private final JCheckBox  chkHistorial = new JCheckBox("¿Tiene buen historial?");
    private final JTextField txtIngresos  = new JTextField(20);
    private final JTextField txtGastos    = new JTextField(20);

    private final JButton btnRegistrar = new JButton("Registrar");
    private final JButton btnEliminar  = new JButton("Eliminar");

    // ------------------------------------------------------------------ barra de herramientas
    private final JButton btnGuardar   = new JButton("Guardar");
    private final JButton btnRefrescar = new JButton("Refrescar");
    private final JButton btnUsuarios  = new JButton("Abrir ventana de usuarios");

    /** Botones que se bloquean mientras hay una operación de BD en curso. */
    private final JButton[] botonesBD = {btnRegistrar, btnEliminar, btnGuardar, btnRefrescar};

    // ------------------------------------------------------------------ tabla y estado
    private final DefaultTableModel modeloPersonas = crearModelo(
            "Nombre cliente", "Nombre", "Apellido", "Ingresos", "Gastos", "Historial");
    private final DefaultTableModel modeloEmpresas = crearModelo(
            "Nombre cliente", "Rubro", "Director", "Ingresos", "Gastos", "Historial");

    private final JTable tabla = new JTable(modeloPersonas);

    private final JProgressBar barraProgreso = new JProgressBar();
    private final JLabel       lblEstado     = new JLabel(" ");

    /** Grupos de campos exclusivos de cada tipo (se muestran u ocultan al cambiar el combo). */
    private JComponent[] camposPersona;
    private JComponent[] camposEmpresa;

    // ==================================================================
    public VentanaPrincipal(ConexionBDD conexion) {
        super("Evaluador crediticio - Carga de clientes");
        this.conexion = conexion;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        construirInterfaz();
        conectarEventos();
        actualizarTipo();   // aplica campos visibles + carga inicial

        Usuario actual = Usuario.getUsuarioActual();
        if (actual != null) {
            setTitle("Evaluador crediticio - Carga de clientes (usuario: " + actual.nombreUsuario + ")");
        }
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    // ================================================================== construcción

    private static DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int f, int c) { return false; }
        };
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout());
        setContentPane(raiz);
        setJMenuBar(construirMenu());
        raiz.add(construirBarra(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setBorder(new EmptyBorder(14, 20, 6, 20));

        JLabel titulo = new JLabel("CARGA DE CLIENTES", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(24f));
        centro.add(titulo, BorderLayout.NORTH);

        JPanel centroForm = new JPanel(new GridBagLayout());
        centroForm.add(construirFormulario());
        centro.add(centroForm, BorderLayout.CENTER);
        centro.add(construirTabla(), BorderLayout.SOUTH);
        raiz.add(centro, BorderLayout.CENTER);

        // Barra de progreso + etiqueta de estado al pie
        barraProgreso.setIndeterminate(true);
        barraProgreso.setStringPainted(true);
        barraProgreso.setString("Cargando...");
        barraProgreso.setVisible(false);

        JPanel pie = new JPanel(new BorderLayout(0, 2));
        pie.setBorder(new EmptyBorder(0, 20, 8, 20));
        pie.add(barraProgreso, BorderLayout.NORTH);
        pie.add(lblEstado, BorderLayout.CENTER);
        raiz.add(pie, BorderLayout.SOUTH);
    }

    private JToolBar construirBarra() {
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);
        for (JButton b : new JButton[]{btnGuardar, btnRefrescar, btnUsuarios}) {
            b.setFocusable(false);
            barra.add(b);
        }
        return barra;
    }

    private JMenuBar construirMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu archivo = new JMenu("Archivo");
        JMenuItem cerrar = new JMenuItem("Cerrar sesión");
        cerrar.addActionListener(e -> cerrarSesion());
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> System.exit(0));
        archivo.add(cerrar);
        archivo.addSeparator();
        archivo.add(salir);

        JMenu admin = new JMenu("Administración");
        JMenuItem usuarios = new JMenuItem("Gestión de usuarios");
        usuarios.addActionListener(e -> abrirUsuarios());
        admin.add(usuarios);

        barra.add(archivo);
        barra.add(admin);
        return barra;
    }

    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        int f = 0;
        f = fila(p, f, new JLabel("Tipo de cliente"),    cmbTipo);
        f = fila(p, f, new JLabel("Nombre de cliente"),  txtNombreCliente);
        f = fila(p, f, lblNombre,                        txtNombre);
        f = fila(p, f, lblApellido,                      txtApellido);
        f = fila(p, f, lblDni,                           txtDni);
        f = fila(p, f, lblRubro,                         txtRubro);
        f = fila(p, f, lblDirector,                      txtDirector);
        f = fila(p, f, new JLabel("Historial"),          chkHistorial);
        f = fila(p, f, new JLabel("Ingresos"),           txtIngresos);
        f = fila(p, f, new JLabel("Gastos"),             txtGastos);

        txtNombreCliente.setToolTipText("Identificador único del cliente en el sistema");
        txtIngresos.setToolTipText("Use punto o coma decimal, sin separador de miles. Ej: 150000,50");
        txtGastos  .setToolTipText("Use punto o coma decimal, sin separador de miles. Ej: 80000");
        txtDni     .setToolTipText("7 u 8 dígitos, sin puntos ni espacios");

        camposPersona = new JComponent[]{lblNombre, txtNombre, lblApellido, txtApellido, lblDni, txtDni};
        camposEmpresa = new JComponent[]{lblRubro,  txtRubro,  lblDirector, txtDirector};

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        botones.add(btnRegistrar);
        botones.add(btnEliminar);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = f; c.gridx = 0; c.gridwidth = 2;
        c.insets = new Insets(18, 8, 4, 8);
        p.add(botones, c);
        return p;
    }

    private static int fila(JPanel p, int fila, JComponent etiqueta, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = fila;
        c.insets = new Insets(5, 8, 5, 8);
        c.gridx = 0; c.anchor = GridBagConstraints.LINE_END;
        p.add(etiqueta, c);
        c.gridx = 1; c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        p.add(campo, c);
        return fila + 1;
    }

    private JScrollPane construirTabla() {
        tabla.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Clientes registrados"));
        scroll.setPreferredSize(new Dimension(740, 170));
        return scroll;
    }

    private void conectarEventos() {
        cmbTipo      .addActionListener(e -> actualizarTipo());
        btnRegistrar .addActionListener(e -> registrar());
        btnGuardar   .addActionListener(e -> registrar());
        btnEliminar  .addActionListener(e -> eliminar());
        btnRefrescar .addActionListener(e -> refrescar());
        btnUsuarios  .addActionListener(e -> abrirUsuarios());
        getRootPane().setDefaultButton(btnRegistrar);
    }

    // ================================================================== lógica de tipo

    private boolean esPersona() {
        return "Persona".equals(cmbTipo.getSelectedItem());
    }

    /**
     * Se llama cuando cambia el combo: actualiza los campos visibles, el título
     * de la columna de nombre real/rubro, cambia el modelo de la tabla y recarga
     * el listado correspondiente de la base.
     */
    private void actualizarTipo() {
        boolean persona = esPersona();
        for (JComponent c : camposPersona) c.setVisible(persona);
        for (JComponent c : camposEmpresa) c.setVisible(!persona);
        lblNombre.setText(persona ? "Nombre" : "Nombre real de la empresa");
        tabla.setModel(persona ? modeloPersonas : modeloEmpresas);
        limpiarFormulario();
        cargarListado(true);
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    // ================================================================== acciones

    private void registrar() {
        Cliente cliente = armarParaAlta();
        if (cliente == null) return;

        if (esPersona()) {
            TareaDB.ejecutar(this, () -> {
                Resultado r = ((Persona) cliente).altaPersona();
                return new RespPersona(r, r.exito() ? Persona.listarPersonas(conexion) : null);
            }, resp -> {
                if (resp.res().exito()) {
                    estado(resp.res().mensaje() + ". " + cliente.evaluarHistorial(cliente.historial) + ".", false);
                    limpiarFormulario();
                    if (resp.lista() != null) llenarPersonas(resp.lista());
                } else {
                    advertir("No se pudo registrar", resp.res().mensaje());
                }
            }, null, barraProgreso, botonesBD);
        } else {
            TareaDB.ejecutar(this, () -> {
                Resultado r = ((Empresa) cliente).altaEmpresa();
                return new RespEmpresa(r, r.exito() ? Empresa.listarEmpresas(conexion) : null);
            }, resp -> {
                if (resp.res().exito()) {
                    estado(resp.res().mensaje() + ". " + cliente.evaluarHistorial(cliente.historial) + ".", false);
                    limpiarFormulario();
                    if (resp.lista() != null) llenarEmpresas(resp.lista());
                } else {
                    advertir("No se pudo registrar", resp.res().mensaje());
                }
            }, null, barraProgreso, botonesBD);
        }
    }

    private void eliminar() {
        Cliente cliente = armarParaBaja();
        if (cliente == null) return;

        boolean persona = cliente instanceof Persona;
        String quien = persona
                ? "a la persona con nombre de cliente \"" + cliente.nombreCliente
                  + "\" (DNI " + ((Persona) cliente).getDni() + ")"
                : "a la empresa con nombre de cliente \"" + cliente.nombreCliente + "\"";
        int r = JOptionPane.showConfirmDialog(this, "¿Eliminar " + quien + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;

        if (persona) {
            TareaDB.ejecutar(this, () -> {
                Resultado res = ((Persona) cliente).bajaPersona();
                return new RespPersona(res, res.exito() ? Persona.listarPersonas(conexion) : null);
            }, resp -> {
                if (resp.res().exito()) {
                    estado(resp.res().mensaje() + ".", false);
                    limpiarFormulario();
                    if (resp.lista() != null) llenarPersonas(resp.lista());
                } else {
                    advertir("No se pudo eliminar", resp.res().mensaje());
                }
            }, null, barraProgreso, botonesBD);
        } else {
            TareaDB.ejecutar(this, () -> {
                Resultado res = ((Empresa) cliente).bajaEmpresa();
                return new RespEmpresa(res, res.exito() ? Empresa.listarEmpresas(conexion) : null);
            }, resp -> {
                if (resp.res().exito()) {
                    estado(resp.res().mensaje() + ".", false);
                    limpiarFormulario();
                    if (resp.lista() != null) llenarEmpresas(resp.lista());
                } else {
                    advertir("No se pudo eliminar", resp.res().mensaje());
                }
            }, null, barraProgreso, botonesBD);
        }
    }

    private void refrescar() {
        limpiarFormulario();
        cargarListado(false);
    }

    private void cargarListado(boolean silencioso) {
        Consumer<String> onError = silencioso ? msg -> estado(msg, true) : null;
        if (esPersona()) {
            TareaDB.ejecutar(this, () -> Persona.listarPersonas(conexion), lista -> {
                llenarPersonas(lista);
                estado("Listado actualizado: " + lista.size() + " persona(s) registrada(s).", false);
            }, onError, barraProgreso, botonesBD);
        } else {
            TareaDB.ejecutar(this, () -> Empresa.listarEmpresas(conexion), lista -> {
                llenarEmpresas(lista);
                estado("Listado actualizado: " + lista.size() + " empresa(s) registrada(s).", false);
            }, onError, barraProgreso, botonesBD);
        }
    }

    private void abrirUsuarios() {
        if (ventanaUsuario == null || !ventanaUsuario.isDisplayable()) {
            ventanaUsuario = new VentanaUsuario();
        }
        ventanaUsuario.setVisible(true);
        ventanaUsuario.toFront();
    }

    private void cerrarSesion() {
        Usuario.cerrarSesion();
        if (ventanaUsuario != null) ventanaUsuario.dispose();
        dispose();
        new VentanaLogin(conexion).setVisible(true);
    }

    // ================================================================== formulario → clases lógicas

    /**
     * Lee los campos del formulario y construye la Persona o Empresa lista para
     * dar de alta. Devuelve null y muestra el error si los datos son inválidos.
     */
    private Cliente armarParaAlta() {
        try {
            // Montos: se parsean primero pero su error se informa después de
            // los campos de identidad, para que el primer mensaje sea el más relevante.
            String errMonto = null;
            double ingresos = 0, gastos = 0;
            try { ingresos = Cliente.parsearMonto(txtIngresos.getText(), "Ingresos"); }
            catch (IllegalArgumentException ex) { errMonto = ex.getMessage(); }
            try { gastos = Cliente.parsearMonto(txtGastos.getText(), "Gastos"); }
            catch (IllegalArgumentException ex) { if (errMonto == null) errMonto = ex.getMessage(); }

            boolean bueno = chkHistorial.isSelected();
            Cliente c;
            if (esPersona()) {
                c = new Persona(
                        txtNombreCliente.getText(),
                        txtNombre.getText(),
                        txtApellido.getText(),
                        txtDni.getText(),
                        ingresos, gastos,
                        personaAux.codigoHistorial(bueno),
                        conexion);
            } else {
                c = new Empresa(
                        txtNombreCliente.getText().trim(),
                        ingresos, gastos,
                        empresaAux.codigoHistorial(bueno),
                        txtRubro.getText(),
                        txtDirector.getText(),
                        conexion);
            }
            c.validarAlta();
            if (errMonto != null) throw new IllegalArgumentException(errMonto);
            return c;
        } catch (IllegalArgumentException ex) {
            advertir("Datos inválidos", ex.getMessage());
            return null;
        }
    }

    /** Lee solo los datos necesarios para la baja (no valida montos ni historial). */
    private Cliente armarParaBaja() {
        try {
            Cliente c = esPersona()
                    ? new Persona(txtNombreCliente.getText(), txtNombre.getText(),
                                  txtApellido.getText(), txtDni.getText(), 0, 0, "", conexion)
                    : new Empresa(txtNombreCliente.getText().trim(), 0, 0, "", "", "", conexion);
            c.validarBaja();
            return c;
        } catch (IllegalArgumentException ex) {
            advertir("Datos inválidos", ex.getMessage());
            return null;
        }
    }

    // ================================================================== clases lógicas → tabla

    private void llenarPersonas(List<Persona.Resumen> lista) {
        modeloPersonas.setRowCount(0);
        for (Persona.Resumen r : lista) {
            modeloPersonas.addRow(new Object[]{
                r.nombreCliente() != null ? r.nombreCliente() : "-",
                r.nombre(),
                r.apellido(),
                PESOS.format(r.ingresos()),
                PESOS.format(r.gastos()),
                historialPersona(r.historial())
            });
        }
    }

    private void llenarEmpresas(List<Empresa.Resumen> lista) {
        modeloEmpresas.setRowCount(0);
        for (Empresa.Resumen r : lista) {
            modeloEmpresas.addRow(new Object[]{
                r.nombreCliente(),
                r.rubro(),
                r.director(),
                PESOS.format(r.ingresos()),
                PESOS.format(r.gastos()),
                historialEmpresa(r.historial())
            });
        }
    }

    private String historialPersona(String codigo) {
        if (personaAux.esBuenHistorial(codigo)) return "Bueno";
        if (codigo != null && codigo.trim().equalsIgnoreCase(personaAux.codigoHistorial(false))) return "Malo";
        return codigo == null ? "-" : codigo;
    }

    private String historialEmpresa(String codigo) {
        if (codigo == null) return "-";
        return switch (codigo.trim().toUpperCase()) {
            case "A" -> "Bueno";
            case "B" -> "Regular";
            case "C" -> "Malo";
            default  -> codigo;
        };
    }

    private void limpiarFormulario() {
        txtNombreCliente.setText("");
        txtNombre   .setText("");
        txtApellido .setText("");
        txtDni      .setText("");
        txtRubro    .setText("");
        txtDirector .setText("");
        txtIngresos .setText("");
        txtGastos   .setText("");
        chkHistorial.setSelected(false);
        txtNombreCliente.requestFocusInWindow();
    }

    private void estado(String texto, boolean error) {
        lblEstado.setForeground(error ? ROJO : VERDE);
        lblEstado.setText(texto);
        lblEstado.setToolTipText(texto);
    }

    private void advertir(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.WARNING_MESSAGE);
    }
}
