/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicacioswing;


public class usu_prov {

    public String getNom() {
        return nom;
    }

    public String getCognom() {
        return cognom;
    }

    public usu_prov(String nom, String cognom) {
        this.nom = nom;
        this.cognom = cognom;
    }
    private String nom;
    private String cognom;
}
