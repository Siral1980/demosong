package it.its.demo.repositories;

import it.its.demo.models.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
    
    boolean existsByTitleAndArtistAndDurationAndPublishingYearAndGenreAllIgnoreCase(
            String title, String artist, int duration, int publishingYear, String genre);
    // Ricerca testuale parziale (CONTAINS) e insensibile alle maiuscole (IGNORECASE)
    List<Song> findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCaseOrGenreContainingIgnoreCase(
            String title, String artist, String genre
    );

    // Ricerca numerica esatta (Nota: i numeri non supportano il "Containing")
    List<Song> findByDurationOrPublishingYear(int duration, int publishingYear);

}