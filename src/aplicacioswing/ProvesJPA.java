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

        AplicacioSwing as = new AplicacioSwing("Gestió d'usuaris", cp);
        
    }

    private static void crearCapaPersistencia(String nomFitxer) {

        Properties props = new Properties();
        try {
            props.load(new FileReader(nomFitxer));
        } catch (FileNotFoundException ex) {
            infoError(ex);
            return;
        } catch (IOException ex) {
            infoError(ex);
            return;
        }
        String nomCapa = props.getProperty("nomCapa");
        if (nomCapa == null || nomCapa.equals("")) {
            System.out.println("Nom capa no trobada");
            return;
        }
        
        cp = null;

        try {
            cp = SingletonGP.getGestorProjectes(nomCapa);
        } catch (Exception ex) {
            infoError(ex);
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
