/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nico
 */
@Entity
@Table(name = "Users")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u")
    , @NamedQuery(name = "Users.findByCi", query = "SELECT u FROM Users u WHERE u.ci = :ci")
    , @NamedQuery(name = "Users.findByPassword", query = "SELECT u FROM Users u WHERE u.password = :password")
    , @NamedQuery(name = "Users.findByNombre", query = "SELECT u FROM Users u WHERE u.nombre = :nombre")
    , @NamedQuery(name = "Users.findByApellido", query = "SELECT u FROM Users u WHERE u.apellido = :apellido")
    , @NamedQuery(name = "Users.findByRol", query = "SELECT u FROM Users u WHERE u.rol = :rol")
    , @NamedQuery(name = "Users.findByPrimeraContra", query = "SELECT u FROM Users u WHERE u.primeraContra = :primeraContra")
    , @NamedQuery(name = "Users.findByPublicKey", query = "SELECT u FROM Users u WHERE u.publicKey = :publicKey")
    , @NamedQuery(name = "Users.findByTieneClaves", query = "SELECT u FROM Users u WHERE u.tieneClaves = :tieneClaves")})
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ci")
    private String ci;
    @Basic(optional = false)
    @Column(name = "password")
    private String password;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "apellido")
    private String apellido;
    @Basic(optional = false)
    @Column(name = "rol")
    private int rol;
    @Basic(optional = false)
    @Column(name = "primeraContra")
    private boolean primeraContra;
    @Column(name = "PublicKey")
    private String publicKey;
    @Basic(optional = false)
    @Column(name = "tieneClaves")
    private boolean tieneClaves;

    public Users() {
    }

    public Users(String ci) {
        this.ci = ci;
    }

    public Users(String ci, String password, String nombre, String apellido, int rol, boolean primeraContra, boolean tieneClaves) {
        this.ci = ci;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.primeraContra = primeraContra;
        this.tieneClaves = tieneClaves;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public boolean getPrimeraContra() {
        return primeraContra;
    }

    public void setPrimeraContra(boolean primeraContra) {
        this.primeraContra = primeraContra;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public boolean getTieneClaves() {
        return tieneClaves;
    }

    public void setTieneClaves(boolean tieneClaves) {
        this.tieneClaves = tieneClaves;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ci != null ? ci.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        if ((this.ci == null && other.ci != null) || (this.ci != null && !this.ci.equals(other.ci))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Models.Users[ ci=" + ci + " ]";
    }
    
    public void seCambioContra(){
        if(this.getPrimeraContra()==false){
            this.setPrimeraContra(true);
        }
    }
    
}
