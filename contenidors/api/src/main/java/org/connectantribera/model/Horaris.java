package org.connectantribera.model;

import java.util.List;

public class Horaris {

    private String tipus;
    private String descripcio;
    private String zona_horaria;
    private String vigent_des_de;
    private String vigent_fins_a;
    private List<String> franges;
    private String font_codi;
    
    public String getTipus() { return tipus; }
    public void setTipus(String tipus) { this.tipus = tipus; }
    public String getDescripcio() { return descripcio; }
    public void setDescripcio(String descripcio) { this.descripcio = descripcio; }
    public String getZona_horaria() { return zona_horaria; }
    public void setZona_horaria(String zona_horaria) { this.zona_horaria = zona_horaria; }
    public String getVigent_des_de() { return vigent_des_de; }
    public void setVigent_des_de(String vigent_des_de) { this.vigent_des_de = vigent_des_de; }
    public String getVigent_fins_a() { return vigent_fins_a; }
    public void setVigent_fins_a(String vigent_fins_a) { this.vigent_fins_a = vigent_fins_a; }
    public List<String> getFranges() { return franges; }
    public void setFranges(List<String> franges) { this.franges = franges; }
    public String getFont_codi() { return font_codi; }
    public void setFont_codi(String font_codi) { this.font_codi = font_codi; }
    
}
