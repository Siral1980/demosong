package it.its.demo.controllers;

import it.its.demo.models.Song;
import it.its.demo.services.SongService;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/api/songs") // Endpoint base al plurale, come da buona pratica REST
public class SongController {

    @Autowired
    private SongService songService;

    // 1. Endpoint per creare una nuova canzone (POST)
    @PostMapping("/addSong")
    public ResponseEntity<Song> createSong(@RequestBody Song song) {
        String requestId = UUID.randomUUID().toString();
        log.info("SongController - started at " + LocalDateTime.now() + "[RequestID: {}", requestId);
        Song createdSong = songService.addSong(song, requestId);
        return ResponseEntity.ok(createdSong);
    }

    @GetMapping("/findAllSongs")
    public ResponseEntity<List<Song>> findAllSongs() {
        List<Song> songs = songService.findAllSongs();
        return ResponseEntity.ok(songs);
    }
/* 
    @PostMapping("/uploadSong")
    public ResponseEntity<Song> uploadSong(@RequestParam String title,
                                           @RequestParam String artist,
                                           @RequestParam String genre,
                                           @RequestParam int duration,
                                           @RequestParam int publishingYear,
                                           @RequestParam MultipartFile audioFile,
                                           @RequestParam MultipartFile coverImage)
                                            throws IOException {
        Song song = new Song();
        song.setTitle(title);
        song.setArtist(artist);
        song.setGenre(genre);
        song.setDuration(duration);
        song.setPublishingYear(publishingYear);
        song.setAudioFile(audioFile.getBytes());
        song.setCoverImage(coverImage.getBytes());
        Song createdSong = songService.addSong(song);
        return ResponseEntity.ok(createdSong);
    }*/

    @GetMapping("/findSongById/{id}")
    public ResponseEntity<Song> findSongById(@PathVariable int id) {
        Song song = songService.findSongById(id);
        return ResponseEntity.ok(song);
    }

    @GetMapping("/filterSearch")
    public ResponseEntity<List<Song>> findAllSongsByFilter(@RequestParam(value = "filter", required = false, defaultValue = "")
                                                                String filter){
        List<Song> songs = songService.findAllSongsByFilter(filter);
        return ResponseEntity.ok(songs);

    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<String> toggleSong(@PathVariable int id) {
        // Nessun try-catch: se il service lancia l'eccezione, ci pensa il tuo GlobalExceptionHandler!
        String message = songService.toogleSong(id);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Song> updateSong(
            @PathVariable int id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) Integer publishingYear
    ) {
        int finalDuration = (duration != null) ? duration : 0;
        int finalYear = (publishingYear != null) ? publishingYear : 0;

        // Anche qui il codice scende a una sola riga d'azione
        Song updatedSong = songService.updateSong(id, title, artist, genre, finalDuration, finalYear);
        return ResponseEntity.ok(updatedSong);
    }
}
