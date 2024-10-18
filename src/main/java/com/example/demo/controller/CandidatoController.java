package com.example.demo.controller;

import com.example.demo.entity.Candidato;
import com.example.demo.service.CandidatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/candidatos")
@CrossOrigin(origins = "http://localhost:5173")
public class CandidatoController {

    @Autowired
    private CandidatoService candidatoService;

    @PostMapping
    public ResponseEntity<String> crearCandidato(@RequestParam("nombre") String nombre,
                                                 @RequestParam("apellido") String apellido,
                                                 @RequestParam("curso") String curso,
                                                 @RequestParam("partido") String partido,
                                                 @RequestParam("file") MultipartFile file) {
        // Verificación de que el archivo no esté vacío
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo no puede estar vacío.");
        }
        try {
            Candidato nuevoCandidato = new Candidato(); // Usar constructor por defecto
            nuevoCandidato.setNombre(nombre);
            nuevoCandidato.setApellido(apellido);
            nuevoCandidato.setCurso(curso);
            nuevoCandidato.setPartido(partido);
            // Agrega el candidato y guarda la foto
            candidatoService.agregarCandidato(nuevoCandidato, file);
            return ResponseEntity.ok("Candidato creado correctamente con foto: " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el candidato: " + e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarCandidato(@PathVariable("id") Long id,
                                                      @RequestParam("nombre") String nombre,
                                                      @RequestParam("apellido") String apellido,
                                                      @RequestParam("curso") String curso,
                                                      @RequestParam("partido") String partido,
                                                      @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            Candidato candidatoActualizado = new Candidato();
            candidatoActualizado.setNombre(nombre);
            candidatoActualizado.setApellido(apellido);
            candidatoActualizado.setCurso(curso);
            candidatoActualizado.setPartido(partido);

            candidatoService.editarCandidato(id, candidatoActualizado, file);

            String mensaje = "Candidato editado correctamente";
            if (file != null && !file.isEmpty()) {
                mensaje += " con foto: " + file.getOriginalFilename();
            }

            return ResponseEntity.ok(mensaje);

        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el candidato: " + e.getMessage());
        }
    }


    @GetMapping
    public List<Candidato> obtenerCandidato() {
        return candidatoService.historyCandidate();
    }
    @GetMapping("/total-votos")
    public ResponseEntity<Integer> obtenerTotalVotos() {
        int totalVotos = candidatoService.obtenerTotalVotos();
        return ResponseEntity.ok(totalVotos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCandidato(@PathVariable Long id) {
        try {
            candidatoService.eliminarCandidato(id);
            return ResponseEntity.ok("Candidato eliminado correctamente");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar el candidato: " + e.getMessage());
        }
    }


    @PostMapping("/votar/{id}")
    public Candidato votarCandidato(@PathVariable Long id) {
        return candidatoService.votarPorCandidato(id);
    }
}
