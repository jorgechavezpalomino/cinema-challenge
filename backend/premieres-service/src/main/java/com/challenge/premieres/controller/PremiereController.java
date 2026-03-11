package com.challenge.premieres.controller;

import com.challenge.premieres.model.Premiere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/premieres")
@CrossOrigin(origins = "*")
public class PremiereController {

    private static final Logger log = LoggerFactory.getLogger(PremiereController.class);

    @GetMapping
    public List<Premiere> getPremieres() {
        log.info("Returning premieres catalog");
        return List.of(
                new Premiere(1L, "Mision Estelar", "Accion espacial con funciones extendidas y preventa VIP.", "https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=1200&q=80"),
                new Premiere(2L, "Noche en Lima", "Thriller urbano con estreno exclusivo de medianoche.", "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1200&q=80"),
                new Premiere(3L, "Pixel Hearts", "Comedia romantica con maraton y combos especiales.", "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&w=1200&q=80")
        );
    }
}
