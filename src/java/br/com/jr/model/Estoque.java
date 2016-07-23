package br.com.jr.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Estoque {

    private Map<String, Cerveja> cervejas = new HashMap<>();

    public Estoque() {
            Cerveja primeiraCerveja = new Cerveja("Stella Artois", "A cerveja belga", "Artois", Tipo.PILSEN);
        Cerveja segundaCerveja = new Cerveja("Erdinger Weissbier", "Cerveja de trigo alemã", "Erdinger Weissbräu", Tipo.WEIZEN);
        
        this.adicionarCerveja(primeiraCerveja);
        this.adicionarCerveja(segundaCerveja);
    }
   
    public Collection<Cerveja> listarCervejas() {
        return new ArrayList<>(this.cervejas.values());
    }

    public void adicionarCerveja(Cerveja cerveja) {
        this.cervejas.put(cerveja.getNome(), cerveja);
    }

    public Cerveja recuperarCervejaPeloNome(String nome) {
        return this.cervejas.get(nome);
    }

}
