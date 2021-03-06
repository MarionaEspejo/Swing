package aplicacioswing;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.jdatepicker.JDatePicker;
import org.mf.persistence.GestioProjectesException;
import org.mf.persistence.IGestioProjectes;
import org.milaifontanals.model.Projecte;
import org.milaifontanals.model.Rol;
import org.milaifontanals.model.Usuari;

public class AplicacioSwing {

    private JDialog subfinestra;
    private JButton boto, botoDesar, botoCancelar;
    private JPanel panellSud, panellNord, panellCentral;
    private JCheckBox veure;
    private JTextField snom, spassword, scognom1, scognom2;
    private String contrasenya;

    //Aquesta variable ens indicara quan es vol crear un usuari (1) o quan es vol editar (0)
    int modeCreacio = 0;

    private List<String> headersTProj = new ArrayList();

    JPanel panellLlistaUsuaris, panelde, panellBotons, panellLlistaProjectes, panellLlistaUsuarisAmbBotons, panellGestioUsuari, panellBotonsProjs;
    JLabel nom, cognom1, cognom2, dataNaix, login, passwd;
    JTextField text1, text2, text3, text4, text5, text6, text7;
    JButton botoAfegir, botoEliminar, botoGuardarCanvis;
    JList llistaUsuaris, llistaProjectes;
    GridBagConstraints c = new GridBagConstraints();
    JFrame f;
    int filaSeleccionada;
    int idUsuari;
    private JTable taulaProjectesAssignats;
    private DefaultTableModel tProjectesAssignats;

    private JTable taulaProjectesNoAssignats;
    private DefaultTableModel tProjectesNoAssignats;

    private JTable taulaUsuaris;
    private DefaultTableModel tUsuaris;

    JComboBox comboRol;
    
    JPanel panellProj = new JPanel();

    JButton btnAssignarProj = new JButton();
    JButton botoDesassignarProj = new JButton();

    GridBagConstraints constraints = new GridBagConstraints();

    IGestioProjectes cp = null;
    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat formatoUpd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public AplicacioSwing(String titol, IGestioProjectes interficie) {
        text1 = new JTextField();
        text2 = new JTextField();
        text3 = new JTextField();
        text4 = new JTextField();
        text5 = new JTextField();
        text6 = new JTextField();

        String[] petStrings = {"Programador", "Analista", "Client"};

        comboRol = new JComboBox(petStrings);
        comboRol.setSelectedIndex(1);

        cp = interficie;
        tProjectesAssignats = new DefaultTableModel();

        taulaProjectesAssignats = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // fem que no sigui editable
            }
        };
        afegirUsuari();

        taulaUsuaris.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) // si hi ha canvi de seleccio en el JTable
                {
                    modeCreacio = 0;
                    text1.setEditable(true);
                    text2.setEditable(true);
                    text3.setEditable(true);
                    text4.setEditable(true);
                    text5.setEditable(true);
                    text6.setEditable(true);
                    omplirFormulari();
                    filaSeleccionada = taulaUsuaris.getSelectedRow();
                    idUsuari = (int) taulaUsuaris.getValueAt(taulaUsuaris.getSelectedRow(), 0);
                    buidarTProjAss();
                    refreshTProjAssign();
                    buidarTProjNoAss();
                    refreshTProjNoAssign();
                }
            }
        });

        //Quan eliminem un usuari
        botoEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int resposta = JOptionPane.showConfirmDialog(null, "Segur que vols eliminar l'usuari?", "Eliminaci?? usuari", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (resposta == 0) {
                    idUsuari = (int) taulaUsuaris.getValueAt(taulaUsuaris.getSelectedRow(), 0);
                    try {
                        taulaUsuaris.removeAll();
                        cp.deleteUsuari(idUsuari);
                    } catch (GestioProjectesException ex) {
                        Logger.getLogger(AplicacioSwing.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        //Quan clickem boto usuari nou
        botoAfegir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modeCreacio = 1;
                text1.setEditable(true);
                text2.setEditable(true);
                text3.setEditable(true);
                text4.setEditable(true);
                text5.setEditable(true);
                text6.setEditable(true);

                text1.setText("");
                text2.setText("");
                text3.setText("");
                text4.setText("");
                text5.setText("");
                text6.setText("");
            }
        });

        //Quan clickem guardar, mirarem si esta en modeCreacio o edicio
        botoGuardarCanvis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //si aixo es cert, CREAREM un usuari, sino, UPDATAREM el usuari seleccionat
                if (modeCreacio == 1) {
                    Usuari usuNew = null;

                    try {
                        usuNew = new Usuari(cp.ultimID() + 1, text1.getText(),
                                text2.getText(),
                                text3.getText(),
                                formato.parse(text4.getText()),
                                text5.getText(),
                                convertirSHA256(text6.getText()));
                    } catch (ParseException | GestioProjectesException ex) {
                        Logger.getLogger(AplicacioSwing.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        cp.insertUsuari(usuNew);
                    } catch (GestioProjectesException ex) {
                        Logger.getLogger(AplicacioSwing.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    Usuari usuUpdate = null;
                    try {
                        //mode update
                        usuUpdate = new Usuari(idUsuari, text1.getText(),
                                text2.getText(),
                                text3.getText(),
                                formatoUpd.parse(text4.getText()),
                                text5.getText(),
                                convertirSHA256(text6.getText()));
                    } catch (ParseException ex) {
                        Logger.getLogger(AplicacioSwing.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        cp.updateUsuari(usuUpdate);
                    } catch (GestioProjectesException ex) {
                        Logger.getLogger(AplicacioSwing.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                buidarTUsu();
                refreshTUsu();
            }
        });

        //Quan clickem guardar, mirarem si esta en modeCreacio o edicio
        btnAssignarProj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (taulaProjectesNoAssignats.getSelectedRow() >= 0) {
                    try {
                        Usuari usu = cp.getUsuari(idUsuari);
                        int idProjecte = (int) taulaProjectesNoAssignats.getValueAt(taulaProjectesNoAssignats.getSelectedRow(), 0);
                        Projecte proj = cp.getProjecte(idProjecte);
                        int idRol = comboRol.getSelectedIndex();
                        Rol rol = cp.getRol(idRol);
                        cp.assignarProjecte(usu, proj, rol);
                        buidarTProjAss();
                        refreshTProjAssign();
                        buidarTProjNoAss();
                        refreshTProjNoAssign();
                    } catch (GestioProjectesException ex) {
                        Logger.getLogger(AplicacioSwing.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        botoDesassignarProj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (taulaProjectesAssignats.getSelectedRow() >= 0) {
                    try {
                        Usuari usu = cp.getUsuari(idUsuari);
                        int idProjecte = (int) taulaProjectesAssignats.getValueAt(taulaProjectesAssignats.getSelectedRow(), 0);
                        Projecte proj = cp.getProjecte(idProjecte);
                        int idRol = 1;
                        Rol rol = cp.getRolUsu(proj, usu);
                        cp.desassignarProjecte(usu, proj, rol);
                        buidarTProjAss();
                        refreshTProjAssign();
                        buidarTProjNoAss();
                        refreshTProjNoAssign();
                    } catch (GestioProjectesException ex) {
                        Logger.getLogger(AplicacioSwing.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        f.pack();
        f.setVisible(true);
        f.setResizable(false);
        f.setLocation(10, 10);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void omplirFormulari() {
        int fila = taulaUsuaris.getSelectedRow();
        Date dt = (Date) taulaUsuaris.getValueAt(fila, 4);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(dt);

        if (fila > -1) {
            text1.setText((String) taulaUsuaris.getValueAt(fila, 1));
            text2.setText((String) taulaUsuaris.getValueAt(fila, 2));
            text3.setText((String) taulaUsuaris.getValueAt(fila, 3));
            text4.setText((String) strDate);
            text5.setText((String) taulaUsuaris.getValueAt(fila, 5));
            text6.setText((String) taulaUsuaris.getValueAt(fila, 5));
        }
    }

    private void buidarTProjAss() {
        if (tProjectesAssignats.getRowCount() > 0) {
            for (int i = tProjectesAssignats.getRowCount() - 1; i >= 0; i--) {
                tProjectesAssignats.removeRow(i);
            }
        }
    }

    public static String convertirSHA256(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        byte[] hash = md.digest(password.getBytes());
        StringBuffer sb = new StringBuffer();

        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    private void buidarTProjNoAss() {
        if (tProjectesNoAssignats.getRowCount() > 0) {
            for (int i = tProjectesNoAssignats.getRowCount() - 1; i >= 0; i--) {
                tProjectesNoAssignats.removeRow(i);
            }
        }
    }

    private void buidarTUsu() {
        /*if (taulaUsuaris.getRowCount() > 0) {
            System.out.println(taulaUsuaris.getRowCount());
            for (int i = 0; i < taulaUsuaris.getRowCount(); i++) {
                System.out.println(i);
                taulaUsuaris.remove(i);
            }
        }*/
 /*DefaultTableModel dtm = (DefaultTableModel) taulaUsuaris.getModel();
        dtm.setRowCount(0);*/

    }

    public void afegirUsuari() {
        botoAfegir = new JButton();
        botoEliminar = new JButton();
        panellBotons = new JPanel();
        panellCentral = new JPanel();
        panellCentral.setLayout(new GridLayout(1, 3));
        panellCentral.setPreferredSize(new Dimension(1500, 750));

        f = new JFrame();
        f.setLayout(new GridBagLayout());

        crearLlistaUsuaris();
        crearFormulariUsuari();
        definirTaulaProjectesAssignats();
        definirTaulaProjectesNoAssignats();

        f.setVisible(true);
        f.setLocation(10, 10);
        f.setSize(1250, 800);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void crearLlistaUsuaris() {

        tUsuaris = new DefaultTableModel();

        Object data[][] = null;
        JPanel panellLlistaUsuaris = new JPanel();
        panellLlistaUsuaris.setLayout(new FlowLayout());
        DefaultListModel<String> l1 = new DefaultListModel<>();
        int i = 0;
        Font font = new Font("Helvetica Neue", Font.BOLD, 18);
        JLabel titolUsr = new JLabel("Llista d'usuaris");
        titolUsr.setFont(font);

        try {
            data = new Object[cp.getLlistaUsuaris().size()][7];
            for (Usuari usus : cp.getLlistaUsuaris()) {
                data[i][0] = usus.getID();
                data[i][1] = usus.getNom();
                data[i][2] = usus.getCognom1();
                if (usus.getCognom2() != null) {
                    data[i][3] = usus.getCognom2();
                } else {
                    data[i][3] = "-";
                }
                data[i][4] = usus.getDataNaix();
                data[i][5] = usus.getLogin();
                data[i][6] = usus.getPasswdHash();
                i++;
            }
        } catch (GestioProjectesException ex) {
            System.out.println("No s'ha pogut carregar la llista d'usuaris");
        }

        String column[] = {"ID", "Nom", "Cognom 1", "Cognom 2", "Data Naixement", "Login"};
        taulaUsuaris = new JTable(data, column);
        taulaUsuaris.setBounds(30, 40, 200, 300);
        JScrollPane sp = new JScrollPane(taulaUsuaris);

        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.gridx = 0; // El ??rea de texto empieza en la columna cero.
        constraints.gridy = 0; // El ??rea de texto empieza en la fila cero

        //f.getContentPane().add(titolUsr, constraints);
        panellLlistaUsuaris.add(titolUsr, constraints);

        constraints.gridx = 0; // El ??rea de texto empieza en la columna cero.
        constraints.gridy = 1; // El ??rea de texto empieza en la fila cero
        //f.getContentPane().add(sp, constraints);
        panellLlistaUsuaris.add(sp, constraints);

        botoAfegir.setText("Nou usuari");
        botoAfegir.setSize(new Dimension(4000, 200));
        botoEliminar.setText("Eliminar usuari");
        botoEliminar.setSize(new Dimension(300, 100));
        panellBotons.setLayout(new GridLayout());
        panellBotons.setPreferredSize(new Dimension(350, 35));
        panellBotons.add(botoAfegir, BorderLayout.NORTH);
        panellBotons.add(botoEliminar, BorderLayout.SOUTH);

        constraints.gridx = 1; // El ??rea de texto empieza en la columna cero.
        constraints.gridy = 3; // El ??rea de texto empieza en la fila cero
        //f.getContentPane().add(panellBotons, constraints);
        panellLlistaUsuaris.add(panellBotons, constraints);

        constraints.gridx = 0; // El ??rea de texto empieza en la columna cero.
        constraints.gridy = 0; // El ??rea de texto empieza en la fila cero

        panellCentral.add(panellLlistaUsuaris);
    }

    private void crearFormulariUsuari() {
        nom = new JLabel();
        cognom1 = new JLabel();
        cognom2 = new JLabel();
        dataNaix = new JLabel();
        login = new JLabel();
        passwd = new JLabel();
        panellGestioUsuari = new JPanel();
        text1 = new JTextField();
        text2 = new JTextField();
        text3 = new JTextField();
        text4 = new JTextField();
        text5 = new JTextField();
        text6 = new JTextField();
        text1.setEditable(false);
        text2.setEditable(false);
        text3.setEditable(false);
        text4.setEditable(false);
        text5.setEditable(false);
        text6.setEditable(false);
        JPanel panellBotonsUsr = new JPanel();
        botoGuardarCanvis = new JButton();

        Font font = new Font("Helvetica Neue", Font.BOLD, 18);
        JLabel titolUsr = new JLabel("Gesti?? d'usuaris");
        titolUsr.setFont(font);

        nom.setText("Nom");
        cognom1.setText("Cognom1");
        cognom2.setText("Cognom2");
        dataNaix.setText("Data naixement");
        login.setText("Login");
        passwd.setText("Password");

        JPanel panellAux = new JPanel();
        panellAux.setLayout(new GridLayout(3, 1));
        panellGestioUsuari.setLayout(new GridLayout(6, 2));
        panellGestioUsuari.add(nom);
        panellGestioUsuari.add(text1);
        panellGestioUsuari.add(cognom1);
        panellGestioUsuari.add(text2);
        panellGestioUsuari.add(cognom2);
        panellGestioUsuari.add(text3);
        panellGestioUsuari.add(dataNaix);
        panellGestioUsuari.add(text4);
        panellGestioUsuari.add(login);
        panellGestioUsuari.add(text5);
        panellGestioUsuari.add(passwd);
        panellGestioUsuari.add(text6);
        panellGestioUsuari.setPreferredSize(new Dimension(300, 400));

        botoGuardarCanvis.setPreferredSize(new Dimension(200, 35));
        botoGuardarCanvis.setText("Guardar usuari");
        panellBotonsUsr.add(botoGuardarCanvis);
        panellBotonsUsr.setPreferredSize(new Dimension(350, 350));

        panellAux.add(panellGestioUsuari);
        panellAux.add(panellBotonsUsr);
        panellAux.setSize(new Dimension(100, 100));

        panellCentral.add(panellAux);
        f.add(panellCentral);
    }

    private void definirTaulaProjectesAssignats() {
        panellBotonsProjs = new JPanel();
        Font font = new Font("Helvetica Neue", Font.BOLD, 18);
        JLabel titolUsr = new JLabel("Llista de projectes assignats");
        titolUsr.setFont(font);
        JPanel panellLlistaUsuaris = new JPanel();
        panellLlistaUsuaris.setLayout(new BoxLayout(panellLlistaUsuaris, BoxLayout.Y_AXIS));

        constraints.gridx = 0; // El ??rea de texto empieza en la columna cero.
        constraints.gridy = 0; // El ??rea de texto empieza en la fila cero
        panellLlistaUsuaris.add(titolUsr, constraints);

        taulaProjectesAssignats = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // fem que no sigui editable
            }
        };

        tProjectesAssignats = new DefaultTableModel();
        headersTProj.add("Id");
        headersTProj.add("Nom");
        headersTProj.add("Descripcio");

        for (int i = 0; i < headersTProj.size(); i++) {
            tProjectesAssignats.addColumn(headersTProj.get(i));
        }

        refreshTProjAssign();

        taulaProjectesAssignats.setModel(tProjectesAssignats);

        taulaProjectesAssignats.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(30);
        taulaProjectesAssignats.getColumnModel().getColumn(0).setMaxWidth(30);
        taulaProjectesAssignats.setSize(new Dimension(2000, 200));
        constraints.gridx = 0; // El ??rea de texto empieza en la columna cero.
        constraints.gridy = 1; // El ??rea de texto empieza en la fila cero
        //f.getContentPane().add(sp, constraints);

        btnAssignarProj.setText("Assignar projecte");
        btnAssignarProj.setSize(new Dimension(4000, 200));
        botoDesassignarProj.setText("Desassignar projecte");
        botoDesassignarProj.setSize(new Dimension(300, 100));
        panellBotonsProjs.setLayout(new GridLayout());
        panellBotonsProjs.setPreferredSize(new Dimension(350, 35));
        panellBotonsProjs.add(btnAssignarProj, BorderLayout.NORTH);
        panellBotonsProjs.add(botoDesassignarProj, BorderLayout.SOUTH);

        panellLlistaUsuaris.setSize(new Dimension(400, 400));
        panellLlistaUsuaris.add(taulaProjectesAssignats, constraints);
        panellLlistaUsuaris.add(panellBotonsProjs, constraints);
        panellProj.add(panellLlistaUsuaris, constraints);
        panellCentral.add(panellProj);

    }

    private void definirTaulaProjectesNoAssignats() {
        Font font = new Font("Helvetica Neue", Font.BOLD, 18);
        JLabel titolUsr = new JLabel("Llista de projectes NO assignats");
        titolUsr.setFont(font);
        JPanel panellLlistaUsuaris = new JPanel();
        panellLlistaUsuaris.setLayout(new BoxLayout(panellLlistaUsuaris, BoxLayout.Y_AXIS));

        constraints.gridx = 0; // El ??rea de texto empieza en la columna cero.
        constraints.gridy = 0; // El ??rea de texto empieza en la fila cero
        panellLlistaUsuaris.add(titolUsr, constraints);

        taulaProjectesNoAssignats = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tProjectesNoAssignats = new DefaultTableModel();
        headersTProj.add("Id");
        headersTProj.add("Nom");
        headersTProj.add("Descripcio");

        for (int i = 0; i < headersTProj.size(); i++) {
            tProjectesNoAssignats.addColumn(headersTProj.get(i));
        }

        refreshTProjNoAssign();

        taulaProjectesNoAssignats.setModel(tProjectesNoAssignats);

        taulaProjectesNoAssignats.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(30);
        taulaProjectesNoAssignats.getColumnModel().getColumn(0).setMaxWidth(30);
        taulaProjectesNoAssignats.setSize(new Dimension(2000, 40));

        constraints.gridx = 0; // El ??rea de texto empieza en la columna cero.
        constraints.gridy = 1; // El ??rea de texto empieza en la fila cero
        //f.getContentPane().add(sp, constraints);

        panellLlistaUsuaris.setSize(new Dimension(400, 400));
        panellLlistaUsuaris.add(taulaProjectesNoAssignats, constraints);
        panellProj.add(panellLlistaUsuaris, BorderLayout.SOUTH);
        panellProj.add(comboRol);
        panellCentral.add(panellProj);

    }

    private void refreshTProjAssign() {
        try {
            if (filaSeleccionada > -1 && idUsuari > 0) {
                List<Projecte> projectes = cp.getLlistaProjectes(cp.getUsuari(idUsuari));
                for (int i = 0; i < projectes.size(); i++) {
                    Object[] fila = new Object[3];
                    fila[0] = projectes.get(i).getID();
                    fila[1] = projectes.get(i).getNom();
                    fila[2] = projectes.get(i).getDescripcio();

                    tProjectesAssignats.addRow(fila);
                }
            }
        } catch (GestioProjectesException ex) {
            JOptionPane.showMessageDialog(null, "Error en omplir la taula de projectes assignats",
                    "Error gestio usuaris", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTProjNoAssign() {
        try {
            if (filaSeleccionada > -1 && idUsuari > 0) {
                List<Projecte> projectes = cp.getLlistaProjectesNoAssignats(cp.getUsuari(idUsuari));
                for (int i = 0; i < projectes.size(); i++) {
                    Object[] fila = new Object[3];
                    fila[0] = projectes.get(i).getID();
                    fila[1] = projectes.get(i).getNom();
                    fila[2] = projectes.get(i).getDescripcio();

                    tProjectesNoAssignats.addRow(fila);
                }
            }
        } catch (GestioProjectesException ex) {
            JOptionPane.showMessageDialog(null, "Error en omplir la taula de projectes assignats",
                    "Error gestio usuaris", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTUsu() {
        Object data[][] = null;
        int i = 0;
        try {
            data = new Object[cp.getLlistaUsuaris().size()][7];
            for (Usuari usus : cp.getLlistaUsuaris()) {
                data[i][0] = usus.getID();
                data[i][1] = usus.getNom();
                data[i][2] = usus.getCognom1();
                if (usus.getCognom2() != null) {
                    data[i][3] = usus.getCognom2();
                } else {
                    data[i][3] = "-";
                }
                data[i][4] = usus.getDataNaix();
                data[i][5] = usus.getLogin();
                data[i][6] = usus.getPasswdHash();
                i++;
            }
        } catch (GestioProjectesException ex) {
            System.out.println("No s'ha pogut carregar la llista d'usuaris");
        }
    }

    private void tancarCapaPersistencia() {
        try {
            cp.closeCapa();
        } catch (GestioProjectesException ex) {
            infoError(ex);
        }
    }

    private static void infoError(Throwable aux) {
        do {
            if (aux.getMessage() != null) {
                System.out.println("\t" + aux.getMessage());
            }
            aux = aux.getCause();
        } while (aux != null);

    }

}
