package it.its.demo.services;

import it.its.demo.exceptions.ResourceNotFoundException;
import it.its.demo.models.Song;
import it.its.demo.repositories.SongRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@Service
public class SongService {

@Autowired
private SongRepository songRepository;

public Song addSong(Song song, String requestId){
    log.info("SongService - started at " + LocalDateTime.now() + "[RequestID: {}", requestId);

    try {
        boolean isFound = songRepository.existsByTitleAndArtistAndDurationAndPublishingYearAndGenreAllIgnoreCase(
                song.getTitle(),
                song.getArtist(),
                song.getDuration(),
                song.getPublishingYear(),
                song.getGenre()
        );

        if (isFound) {
            throw new IllegalArgumentException("Error: song " + song.getTitle().toUpperCase() + " already exists");
        }
        song.setActive(true);
        log.info("SongService - finished at " + LocalDateTime.now() + "[RequestID: {}", requestId);

        return songRepository.save(song);
    }   catch (IllegalArgumentException e){
            log.error("Duplication try blocked: {}, [RequestId]: {}", e.getMessage(), requestId);
            throw e;
        }

}





public List<Song> findAllSongs(){
    return songRepository.findAll();
}


    public Song findSongById(int id){
        // Cerca la canzone. Se non esiste, lancia subito l'eccezione
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song with ID " + id + " not found"));
        return song;
    }

    public String toogleSong(int id) {
        // Cerca la canzone. Se non esiste, lancia subito l'eccezione
        Song song = findSongById(id);

        // Se arriva qui, la canzone esiste sicuramente
        song.setActive(!song.isActive());
        songRepository.save(song);

        String status = song.isActive() ? "active" : "not active";
        return "Song: " + song.getTitle() + " Artist: " + song.getArtist() + " is now " + status;
    }

    public Song updateSong(int id,
                           String title,
                           String artist,
                           String genre,
                           int duration,
                           int publishingYear) {
        // Stessa logica: fail-fast se la canzone non esiste
        Song song = findSongById(id);

        // Controlli per i campi di tipo String
        if (title != null && !title.isBlank()) {
            song.setTitle(title);
        }
        if (artist != null && !artist.isBlank()) {
            song.setArtist(artist);
        }
        if (genre != null && !genre.isBlank()) {
            song.setGenre(genre);
        }

        // Controlli per i campi di tipo int
        if (duration > 0) {
            song.setDuration(duration);
        }
        if (publishingYear > 0) {
            song.setPublishingYear(publishingYear);
        }

        return songRepository.save(song);
    }

public List<Song> findAllSongsByFilter(String query) {
        if (query == null || query.isBlank()) {
            return songRepository.findAll();
        }

        String sanitizedQuery = query.trim();

        // Usiamo un Set per unire i risultati eliminando automaticamente i duplicati
        Set<Song> combinedResults = new LinkedHashSet<>();

        // 1. Eseguiamo SEMPRE la ricerca testuale (che accetta qualsiasi stringa)
        List<Song> textResults = songRepository.findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCaseOrGenreContainingIgnoreCase(
                sanitizedQuery, sanitizedQuery, sanitizedQuery
        );
        combinedResults.addAll(textResults);

        // 2. SE la stringa è convertibile in numero, lanciamo ANCHE la ricerca numerica
        try {
            int numericValue = Integer.parseInt(sanitizedQuery);
            List<Song> numericResults = songRepository.findByDurationOrPublishingYear(numericValue, numericValue);

            // Uniamo i risultati numerici a quelli testuali
            combinedResults.addAll(numericResults);
        } catch (NumberFormatException e) {
            // Se non è un numero, non facciamo nulla: combinedResults contiene già i risultati del testo
        }

        // Convertiamo il Set in List per restituirlo al Controller
        return new ArrayList<>(combinedResults);
    }

}




