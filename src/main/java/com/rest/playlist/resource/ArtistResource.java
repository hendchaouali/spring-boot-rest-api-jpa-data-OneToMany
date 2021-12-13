package com.rest.playlist.resource;

import com.rest.playlist.model.Artist;
import com.rest.playlist.service.IArtistService;
import com.rest.playlist.service.ArtistServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistResource {

    final private IArtistService IArtistService;
    private static final Logger log = LoggerFactory.getLogger(ArtistServiceImpl.class);

    public ArtistResource(IArtistService IArtistService) {
        this.IArtistService = IArtistService;
    }

    @GetMapping
    public ResponseEntity<List<Artist>> getAllArtists() {

        List<Artist> artists = IArtistService.getAllArtists();

        if (artists.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(artists, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable Long id) {
        Artist artist = IArtistService.getArtistById(id);
        return new ResponseEntity<>(artist, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Artist> createArtist(@Valid @RequestBody Artist artist) {
        Artist addedArtist = IArtistService.createArtist(artist);
        return new ResponseEntity<>(addedArtist, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity updateArtist(@Valid @RequestBody Artist artist) {
        Artist updatedArtist = IArtistService.updateArtist(artist);
        return new ResponseEntity<>(updatedArtist, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteArtistById(@PathVariable Long id) {
        IArtistService.deleteArtistById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
