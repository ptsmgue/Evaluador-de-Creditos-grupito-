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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana principal: carga y baja de clientes (personas y empresas).
 *
 * Esta clase solo lee los campos, se los pasa a {@link Persona} / {@link Empresa}
 * y muestra lo que esas clases devuelven. Las operaciones de base de datos
 * corren en segundo plano ({@link TareaDB}) para no congelar la ventana.
 */
public class VentanaPrincipal extends JFrame {

    private static final Color VERDE = new Color(0x1B7F3B);
    private static final Color ROJO = new Color(0xB00020);
    private static final NumberFormat PESOS = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-AR"));

    /** Lo que vuelve de segundo plano: el resultado de la operación y, si corresponde, el listado actualizado. */
    private record Respuesta(Resultado resultado, List<Persona.Resumen> personas) {
    }

    private final ConexionBDD conexion;
    /** Solo se usan para traducir el checkbox de historial al código de cada tipo (y los códigos al mostrarlos). */
    private final Persona personaAuxiliar = new Persona();
    private final Empresa empresaAuxiliar = new Empresa();
    private VentanaUsuario ventanaUsuario;

    // Formulario
    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[] { "Persona", "Empresa" });
    private final JLabel lblNombre = new JLabel("Nombre");
    private final JTextField txtNombre = new JTextField(20);
    private final JLabel lblApellido = new JLabel("Apellido");
    private final JTextField txtApellido = new JTextField(20);
    private final JLabel lblDni = new JLabel("DNI");
    private final JTextField txtDni = new JTextField(20);
    private final JLabel lblRubro = new JLabel("Rubro");
    private final JTextField txtRubro = new JTextField(20);
    private final JLabel lblDirector = new JLabel("Director");
    private final JTextField txtDirector = new JTextField(20);
    private final JCheckBox chkBuenHistorial = new JCheckBox("¿Tiene buen historial?");
    private final JTextField txtIngresos = new JTextField(20);
    private final JTextField txtGastos = new JTextField(20);
    private final JButton btnRegistrar = new JButton("Registrar");
    private final JButton btnEliminar = new JButton("Eliminar");

    // Barra de herramientas
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnRefrescar = new JButton("Refrescar");
    private final JButton btnUsuarios = new JButton("Abrir ventana de usuarios");

    // Listado y estado
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[] { "Nombre", "Apellido", "Ingresos", "Gastos", "Historial" }, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JLabel lblEstado = new JLabel(" ");

    private JComponent[] camposPersona;
    private JComponent[] camposEmpresa;

    public VentanaPrincipal(ConexionBDD conexion) {
        super("Evaluador crediticio - Carga de clientes");
        this.conexion = conexion;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        construirInterfaz();
        conectarEventos();
        actualizarTipo();

        Usuario actual = Usuario.getUsuarioActual();
        if (actual != null) {
            setTitle("Evaluador crediticio - Carga de clientes (usuario: " + actual.nombreUsuario + ")");
        }
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);

        cargarPersonas(true);
    }

    // ================================================================== construcción de la interfaz

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout());
        setContentPane(raiz);
        setJMenuBar(construirMenu());
        raiz.add(construirBarraHerramientas(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setBorder(new EmptyBorder(14, 20, 6, 20));

        JLabel titulo = new JLabel("CARGA DE CLIENTES", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(24f));
        centro.add(titulo, BorderLayout.NORTH);

        JPanel contenedorForm = new JPanel(new GridBagLayout()); // centra el formulario
        contenedorForm.add(construirFormulario());
        centro.add(contenedorForm, BorderLayout.CENTER);
        centro.add(construirTabla(), BorderLayout.SOUTH);
        raiz.add(centro, BorderLayout.CENTER);

        lblEstado.setBorder(new EmptyBorder(6, 20, 8, 20));
        raiz.add(lblEstado, BorderLayout.SOUTH);
    }

    private JToolBar construirBarraHerramientas() {
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);
        for (JButton b : new JButton[] { btnGuardar, btnRefrescar, btnUsuarios }) {
            b.setFocusable(false);
            barra.add(b);
        }
        return barra;
    }

    private JMenuBar construirMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu archivo = new JMenu("Archivo");
        JMenuItem cerrarSesion = new JMenuItem("Cerrar sesión");
        cerrarSesion.addActionListener(e -> cerrarSesion());
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> System.exit(0));
        archivo.add(cerrarSesion);
        archivo.addSeparator();
        archivo.add(salir);

        JMenu administracion = new JMenu("Administración");
        JMenuItem gestionUsuarios = new JMenuItem("Gestión de usuarios");
        gestionUsuarios.addActionListener(e -> abrirUsuarios());
        administracion.add(gestionUsuarios);

        barra.add(archivo);
        barra.add(administracion);
        return barra;
    }

    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        int fila = 0;
        fila = agregarFila(p, fila, new JLabel("Tipo de cliente"), cmbTipo);
        fila = agregarFila(p, fila, lblNombre, txtNombre);
        fila = agregarFila(p, fila, lblApellido, txtApellido);
        fila = agregarFila(p, fila, lblDni, txtDni);
        fila = agregarFila(p, fila, lblRubro, txtRubro);
        fila = agregarFila(p, fila, lblDirector, txtDirector);
        fila = agregarFila(p, fila, new JLabel("Historial"), chkBuenHistorial);
        fila = agregarFila(p, fila, new JLabel("Ingresos"), txtIngresos);
        fila = agregarFila(p, fila, new JLabel("Gastos"), txtGastos);

        txtIngresos.setToolTipText("Monto con punto o coma decimal, sin separador de miles. Ej: 150000,50");
        txtGastos.setToolTipText("Monto con punto o coma decimal, sin separador de miles. Ej: 80000");
        txtDni.setToolTipText("7 u 8 dígitos, sin puntos");

        camposPersona = new JComponent[] { lblApellido, txtApellido, lblDni, txtDni };
        camposEmpresa = new JComponent[] { lblRubro, txtRubro, lblDirector, txtDirector };

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        botones.add(btnRegistrar);
        botones.add(btnEliminar);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = fila;
        c.gridx = 0;
        c.gridwidth = 2;
        c.insets = new Insets(18, 8, 4, 8);
        p.add(botones, c);
        return p;
    }

    /** Agrega "etiqueta | campo" en una fila del formulario. */
    private static int agregarFila(JPanel p, int fila, JComponent etiqueta, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = fila;
        c.insets = new Insets(5, 8, 5, 8);
        c.gridx = 0;
        c.anchor = GridBagConstraints.LINE_END;
        p.add(etiqueta, c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        p.add(campo, c);
        return fila + 1;
    }

    private JScrollPane construirTabla() {
        JTable tabla = new JTable(modeloTabla);
        tabla.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Personas registradas"));
        scroll.setPreferredSize(new Dimension(700, 170));
        return scroll;
    }

    private void conectarEventos() {
        cmbTipo.addActionListener(e -> actualizarTipo());
        btnRegistrar.addActionListener(e -> registrar());
        btnGuardar.addActionListener(e -> registrar());
        btnEliminar.addActionListener(e -> eliminar());
        btnRefrescar.addActionListener(e -> refrescar());
        btnUsuarios.addActionListener(e -> abrirUsuarios());
        getRootPane().setDefaultButton(btnRegistrar); // Enter = Registrar
    }

    // ================================================================== acciones

    private boolean esPersona() {
        return "Persona".equals(cmbTipo.getSelectedItem());
    }

    /** Muestra los campos que corresponden al tipo de cliente elegido. */
    private void actualizarTipo() {
        boolean persona = esPersona();
        lblNombre.setText(persona ? "Nombre" : "Nombre de la empresa");
        for (JComponent c : camposPersona) {
            c.setVisible(persona);
        }
        for (JComponent c : camposEmpresa) {
            c.setVisible(!persona);
        }
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    /** Registrar / Guardar: da de alta la persona o empresa cargada en el formulario. */
    private void registrar() {
        Cliente cliente = crearParaAlta();
        if (cliente == null) {
            return;
        }
        boolean persona = cliente instanceof Persona;
        TareaDB.ejecutar(this, () -> {
            Resultado r = persona ? ((Persona) cliente).altaPersona() : ((Empresa) cliente).altaEmpresa();
            return new Respuesta(r, persona && r.exito() ? intentarListar() : null);
        }, resp -> {
            if (resp.resultado().exito()) {
                estado(resp.resultado().mensaje() + ". " + cliente.evaluarHistorial(cliente.historial) + ".", false);
                limpiarFormulario();
            } else {
                advertir("No se pudo registrar", resp.resultado().mensaje());
            }
            mostrarListado(resp.personas());
        }, null, botonesDB());
    }

    /** Eliminar: da de baja la persona (nombre + apellido + DNI) o la empresa (nombre) indicada. */
    private void eliminar() {
        Cliente cliente = crearParaBaja();
        if (cliente == null) {
            return;
        }
        boolean persona = cliente instanceof Persona;
        String quien = persona ? "a la persona " + cliente.nombreCliente + " (DNI " + ((Persona) cliente).getDni() + ")"
                : "a la empresa " + cliente.nombreCliente;
        int r = JOptionPane.showConfirmDialog(this, "¿Eliminar " + quien + "?", "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        TareaDB.ejecutar(this, () -> {
            Resultado res = persona ? ((Persona) cliente).bajaPersona() : ((Empresa) cliente).bajaEmpresa();
            return new Respuesta(res, persona && res.exito() ? intentarListar() : null);
        }, resp -> {
            if (resp.resultado().exito()) {
                estado(resp.resultado().mensaje() + ".", false);
                limpiarFormulario();
            } else {
                advertir("No se pudo eliminar", resp.resultado().mensaje());
            }
            mostrarListado(resp.personas());
        }, null, botonesDB());
    }

    /** Refrescar: limpia el formulario y vuelve a pedir el listado a la base. */
    private void refrescar() {
        limpiarFormulario();
        cargarPersonas(false);
    }

    private void cargarPersonas(boolean silencioso) {
        TareaDB.ejecutar(this, () -> Persona.listarPersonas(conexion), lista -> {
            cargarTabla(lista);
            estado("Listado actualizado: " + lista.size() + " persona(s) registrada(s).", false);
        }, silencioso ? msg -> estado(msg, true) : null, botonesDB());
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
        if (ventanaUsuario != null) {
            ventanaUsuario.dispose();
        }
        dispose();
        new VentanaLogin(conexion).setVisible(true);
    }

    // ================================================================== del formulario a las clases lógicas

    /** Arma la Persona/Empresa del formulario y la valida. Si algo está mal avisa y devuelve null. */
    private Cliente crearParaAlta() {
        try {
            // Los montos se leen primero pero su error se informa último, para que si faltan
            // datos de identidad el mensaje sea el del primer campo vacío del formulario.
            String errorMonto = null;
            double ingresos = 0;
            double gastos = 0;
            try {
                ingresos = Cliente.parsearMonto(txtIngresos.getText(), "Ingresos");
            } catch (IllegalArgumentException ex) {
                errorMonto = ex.getMessage();
            }
            try {
                gastos = Cliente.parsearMonto(txtGastos.getText(), "Gastos");
            } catch (IllegalArgumentException ex) {
                if (errorMonto == null) {
                    errorMonto = ex.getMessage();
                }
            }

            boolean bueno = chkBuenHistorial.isSelected();
            Cliente cliente;
            if (esPersona()) {
                cliente = new Persona(txtNombre.getText(), txtApellido.getText(), txtDni.getText(),
                        ingresos, gastos, personaAuxiliar.codigoHistorial(bueno), conexion);
            } else {
                cliente = new Empresa(txtNombre.getText().trim(), ingresos, gastos, empresaAuxiliar.codigoHistorial(bueno),
                        txtRubro.getText(), txtDirector.getText(), conexion);
            }
            cliente.validarAlta();
            if (errorMonto != null) {
                throw new IllegalArgumentException(errorMonto);
            }
            return cliente;
        } catch (IllegalArgumentException ex) {
            advertir("Datos inválidos", ex.getMessage());
            return null;
        }
    }

    private Cliente crearParaBaja() {
        try {
            Cliente cliente = esPersona()
                    ? new Persona(txtNombre.getText(), txtApellido.getText(), txtDni.getText(), 0, 0, "", conexion)
                    : new Empresa(txtNombre.getText().trim(), 0, 0, "", "", "", conexion);
            cliente.validarBaja();
            return cliente;
        } catch (IllegalArgumentException ex) {
            advertir("Datos inválidos", ex.getMessage());
            return null;
        }
    }

    // ================================================================== de las clases lógicas a la pantalla

    /** Listado para refrescar la tabla tras un cambio. Si falla, no se pisa el resultado de la operación. */
    private List<Persona.Resumen> intentarListar() {
        try {
            return Persona.listarPersonas(conexion);
        } catch (Exception e) {
            return null;
        }
    }

    private void mostrarListado(List<Persona.Resumen> personas) {
        if (personas != null) {
            cargarTabla(personas);
        }
    }

    private void cargarTabla(List<Persona.Resumen> lista) {
        modeloTabla.setRowCount(0);
        for (Persona.Resumen r : lista) {
            modeloTabla.addRow(new Object[] {
                r.nombre(), r.apellido(), PESOS.format(r.ingresos()), PESOS.format(r.gastos()),
                descripcionHistorial(r.historial()) });
        }
    }

    private String descripcionHistorial(String codigo) {
        if (personaAuxiliar.esBuenHistorial(codigo)) {
            return "Bueno";
        }
        if (codigo != null && codigo.trim().equalsIgnoreCase(personaAuxiliar.codigoHistorial(false))) {
            return "Malo";
        }
        return codigo == null ? "-" : codigo; // código que no es de este sistema: se muestra tal cual
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtDni.setText("");
        txtRubro.setText("");
        txtDirector.setText("");
        txtIngresos.setText("");
        txtGastos.setText("");
        chkBuenHistorial.setSelected(false);
        txtNombre.requestFocusInWindow();
    }

    private JComponent[] botonesDB() {
        return new JComponent[] { btnRegistrar, btnEliminar, btnGuardar, btnRefrescar };
    }

    private void estado(String texto, boolean error) {
        lblEstado.setForeground(error ? ROJO : VERDE);
        lblEstado.setText(texto);
        lblEstado.setToolTipText(texto); // por si el mensaje es más ancho que la ventana
    }

    private void advertir(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.WARNING_MESSAGE);
    }
}
