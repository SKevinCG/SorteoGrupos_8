package com.mycompany.sorteogrupos;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sonido {

    private static final Map<String, Clip> clips = new HashMap<>();

    public static void precargar(String... rutas) {
        for (String ruta : rutas) {
            try {
                URL url = Sonido.class.getResource(ruta);
                AudioInputStream audio = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audio);
                clips.put(ruta, clip);
            } catch (Exception e) {
                System.err.println("No se pudo cargar: " + ruta);
            }
        }
    }

    public static void reproducir(String ruta) {
        Clip clip = clips.get(ruta);
        if (clip == null) {
            return;
        }
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public static void reproducirDesde(String ruta, int saltarMs) {
        Clip clip = clips.get(ruta);
        if (clip == null) {
            return;
        }
        clip.stop();
        clip.setMicrosecondPosition(saltarMs * 1000L);
        clip.start();
    }

    public static void detener(String ruta) {
        Clip clip = clips.get(ruta);
        if (clip != null) {
            clip.stop();
        }
    }
}