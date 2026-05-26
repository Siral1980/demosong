package it.its.demo.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String artist;
    private String genre;
    private int duration;
    private int publishingYear;
    //private byte[] audioFile;
    //private byte[] coverImage;
    private boolean active;
}
