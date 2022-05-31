/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicacioswing;

import java.awt.List;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.mf.persistence.GestioProjectesException;
import org.mf.persistence.IGestioProjectes;
import org.mf.persistence.SingletonGP;
import org.milaifontanals.model.Usuari;

/**
 *
 * @author miona
 */
public class ProvesJPA {

    /**
     * @param args the command line arguments
     */
    static IGestioProjectes cp;

    public static void main(String[] args) throws GestioProjectesException {

        String nomFitxer = null;
        if (args.length == 0) {
            nomFitxer = "infoCapa.properties";
        } else {
            nomFitxer = args[0];
        }

        crearCapaPersistencia(nomFitxer);

        /*for (Usuari usu: cp.getLlistaUsuaris()) {
            System.out.println(usu.toString());
        }*/
        
        AplicacioSwing gestio = new AplicacioSwing("Gestió d'usuaris", cp);
        
    }

    private static void crearCapaPersistencia(String nomFitxer) {

        Properties props = new Properties();
        try {
            props.load(new FileReader(nomFitxer));
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "No es troba fitxer de propietats " + nomFitxer,
                    "Error Capa de Persistencia", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error en carregar fitxer de propietats " + nomFitxer,
                    "Error Capa de Persistencia", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String nomCapa = props.getProperty("nomCapa");
        if (nomCapa == null || nomCapa.equals("")) {
            JOptionPane.showMessageDialog(null, "Fitxer de propietats " + nomFitxer + " no conté propietat nomCapa",
                    "Error Capa de Persistencia", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        cp = null;

        try {
            cp = SingletonGP.getGestorProjectes(nomCapa);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error en crear capa de persistencia",
                    "Error Capa de Persistencia", JOptionPane.ERROR_MESSAGE);
            return;
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
