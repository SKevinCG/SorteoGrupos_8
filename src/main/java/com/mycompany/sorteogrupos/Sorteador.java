package com.mycompany.sorteogrupos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sorteador {

    private final int total;
    private final List<String> disponibles = new ArrayList<>();
    private final List<String> sacados = new ArrayList<>();
    private final Random random = new Random();

    public Sorteador(int total) {
        this.total = total;
        reiniciar();
    }

    public String sacarSiguiente() {
        if (disponibles.isEmpty()) {
            return null;
        }
        int posicion = random.nextInt(disponibles.size());
        String grupo = disponibles.remove(posicion);
        sacados.add(grupo);
        return grupo;
    }

    public boolean hayDisponibles() {
        return !disponibles.isEmpty();
    }

    public int getRestantes() {
        return disponibles.size();
    }

    public List<String> getSacados() {
        return new ArrayList<>(sacados);
    }

    public void reiniciar() {
        disponibles.clear();
        sacados.clear();
        for (int i = 1; i <= total; i++) {
            disponibles.add("Grupo " + i);
        }
    }
}