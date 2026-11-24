package org.connectantribera.model;

import java.util.List;

public class Centres{

    public record Centre() {}

    private String codi;
    private String nom;
    private String tipus_centre_codi;
    private String titular_codi;
    private String gestor_codi;
    private String adreca;
    private String municipi_codi;
    private int codi_postal;
    private double latitud;
    private double longitud;

    private String precisio_punt; // ???
    private String font_punt; // ???

    private String font_codi;
    private String adreca_font_codi;

    private List<Contactes> contactes;

    private Horaris horari;

    public String getCodi() { return codi; }
    public void setCodi(String codi) { this.codi = codi; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getTipus_centre_codi() { return tipus_centre_codi; }
    public void setTipus_centre_codi(String tipus_centre_codi) { this.tipus_centre_codi = tipus_centre_codi; }
    public String getTitular_codi() { return titular_codi; }
    public void setTitular_codi(String titular_codi) { this.titular_codi = titular_codi; }
    public String getGestor_codi() { return gestor_codi; }
    public void setGestor_codi(String gestor_codi) { this.gestor_codi = gestor_codi; }
    public String getAdreca() { return adreca; }
    public void setAdreca(String adreca) { this.adreca = adreca; }
    public String getMunicipi_codi() { return municipi_codi; }
    public void setMunicipi_codi(String municipi_codi) { this.municipi_codi = municipi_codi; }
    public int getCodi_postal() { return codi_postal; }
    public void setCodi_postal(int codi_postal) { this.codi_postal = codi_postal; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public String getPrecisio_punt() { return precisio_punt; }
    public void setPrecisio_punt(String precisio_punt) { this.precisio_punt = precisio_punt; }
    public String getFont_punt() { return font_punt; }
    public void setFont_punt(String font_punt) { this.font_punt = font_punt; }
    public String getFont_codi() { return font_codi; }
    public void setFont_codi(String font_codi) { this.font_codi = font_codi; }
    public String getAdreca_font_codi() { return adreca_font_codi; }
    public void setAdreca_font_codi(String adreca_font_codi) { this.adreca_font_codi = adreca_font_codi; }
    
    public List<Contactes> getContactes() { return contactes; }
    public void setContactes(List<Contactes> contactes) { this.contactes = contactes; }
    public Horaris getHorari() { return horari; }
    public void setHorari(Horaris horari) { this.horari = horari; }
    
}
