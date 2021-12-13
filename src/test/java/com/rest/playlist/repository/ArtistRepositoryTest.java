package com.rest.playlist.repository;

import com.rest.playlist.model.Artist;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ArtistRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(ArtistRepositoryTest.class);

    @Autowired
    ArtistRepository artistRepository;
    private Artist savedArtist;

    @Before
    public void setupCreatePlaylist() {
        Artist artist = new Artist();
        artist.setName("Artist #1");
        artist.setPopularity(80L);

        savedArtist = artistRepository.save(artist);
        assertThat(savedArtist).isNotNull();
        assertThat(savedArtist).hasFieldOrPropertyWithValue("name", "Artist #1");
        assertThat(savedArtist).hasFieldOrPropertyWithValue("popularity", 80L);
    }

    @Test
    public void shouldFindAllPlaylists() {
        List<Artist> artists = artistRepository.findAll();
        assertThat(artists).isNotEmpty();
        assertThat(artists).hasSizeGreaterThanOrEqualTo(1);
        assertThat(artists).contains(artists.get(artists.size() - 1));
        assertThat(artists.get(artists.size() - 1).getId()).isNotNull();
    }

    @Test
    public void shouldFindPlaylistById() {
        Artist foundArtist = artistRepository.findById(savedArtist.getId()).orElse(null);
        assertThat(foundArtist).isNotNull();
        assertThat(foundArtist).isEqualTo(savedArtist);
        assertThat(foundArtist).hasFieldOrPropertyWithValue("name", savedArtist.getName());
        assertThat(foundArtist).hasFieldOrPropertyWithValue("popularity", savedArtist.getPopularity());
    }

    @Test
    public void shoulCreatePlaylist() {

        int sizeBeforeCreate = artistRepository.findAll().size();

        Artist artistToSave = new Artist();
        artistToSave.setName("Artist #Created");
        artistToSave.setPopularity(75L);

        Artist artist = artistRepository.save(artistToSave);

        int sizeAfterCreate = artistRepository.findAll().size();
        assertThat(sizeAfterCreate).isEqualTo(sizeBeforeCreate + 1);
        assertThat(artist).isNotNull();
        assertThat(artist).hasFieldOrPropertyWithValue("name", "Artist #Created");
        assertThat(artist).hasFieldOrPropertyWithValue("popularity", 75L);

    }

    @Test
    public void shouldUpdatePlaylist() {

        Artist foundArtist = artistRepository.getById(savedArtist.getId());
        assertThat(foundArtist).isNotNull();

        foundArtist.setName("Artist #Updated");
        foundArtist.setPopularity(50L);

        Artist updatedArtist = artistRepository.save(foundArtist);

        Artist checkArtist = artistRepository.getById(updatedArtist.getId());

        assertThat(checkArtist.getId()).isNotNull();
        assertThat(checkArtist.getId()).isEqualTo(updatedArtist.getId());
        assertThat(checkArtist.getName()).isEqualTo(updatedArtist.getName());
        assertThat(checkArtist.getPopularity()).isEqualTo(updatedArtist.getPopularity());
    }

    @Test
    public void shouldDeleteSonById() {
        int sizeBeforeDelete = artistRepository.findAll().size();

        Artist foundArtist = artistRepository.getById(savedArtist.getId());
        assertThat(foundArtist).isNotNull();

        artistRepository.deleteById(foundArtist.getId());

        int sizeAfterDelete = artistRepository.findAll().size();

        assertThat(sizeAfterDelete).isEqualTo(sizeBeforeDelete - 1);
    }
}
