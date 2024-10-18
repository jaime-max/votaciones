package com.example.demo.service;

import com.example.demo.entity.Candidato;
import com.example.demo.repository.CandidatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class CandidatoService {

    @Autowired
    private CandidatoRepository candidatoRepository;

    private final String uploadDir = "uploads/";

    public List<Candidato> historyCandidate() {
        return candidatoRepository.findAll();
    }

    public Candidato agregarCandidato(Candidato candidato, MultipartFile file) throws IOException {
        // Guarda la foto y obtiene el nombre del archivo
        String fileName = savePhoto(file);
        // Guarda el nombre del archivo en el objeto Candidato
        candidato.setFoto(fileName); // Asegúrate de que tengas un campo 'foto' en tu entidad Candidato
        return candidatoRepository.save(candidato);
    }
    public Candidato editarCandidato(Long id, Candidato candidatoEdit, MultipartFile file) throws IOException {
        Candidato candidateExistence = candidatoRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("Candidato no encontrado"));
        candidateExistence.setNombre(candidatoEdit.getNombre());
        candidateExistence.setApellido(candidatoEdit.getApellido());
        candidateExistence.setCurso(candidatoEdit.getCurso());
        candidateExistence.setPartido(candidatoEdit.getPartido());

        if (file != null && !file.isEmpty()) {
            String fileName = savePhoto(file);
            candidateExistence.setFoto(fileName);
        }
        return candidatoRepository.save(candidateExistence);
    }
    public int obtenerTotalVotos() {
        List<Candidato> candidatos = candidatoRepository.findAll();
        return candidatos.stream().mapToInt(Candidato::getVotos).sum();
    }


    public void eliminarCandidato(Long id){
        candidatoRepository.deleteById(id);
    }

    public Candidato votarPorCandidato(Long id) {
        Candidato candidato = candidatoRepository.findById(id).orElseThrow();
        candidato.setVotos(candidato.getVotos()+1);
        return candidatoRepository.save(candidato);
    }
    public String savePhoto(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;// Devuelve el nombre del archivo o su ruta para almacenarla en la base de datos
    }
}
