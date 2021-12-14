package com.rest.playlist.service.artist;

import com.rest.playlist.web.exception.ResourceNotFoundException;
import com.rest.playlist.model.Artist;
import com.rest.playlist.repository.ArtistRepository;
import com.rest.playlist.service.ArtistServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
public class ArtistServiceIntegrationTest {

    private final static Logger log = LoggerFactory.getLogger(ArtistServiceIntegrationTest.class);

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistServiceImpl playlistService;

    private Artist defaultArtist;

    @Before
    public void setup() {
        Artist myArtist = new Artist();

        myArtist.setName("Artist");
        myArtist.setPopularity(86L);
        defaultArtist = artistRepository.saveAndFlush(myArtist);

    }

    @Test
    public void testGetAllArtists() {
        List<Artist> artists = playlistService.getAllArtists();
        assertThat(artists).isNotNull().isNotEmpty();
    }

    @Test
    public void testGetArtistById() {
        Artist artist = playlistService.getArtistById(defaultArtist.getId());
        assertThat(artist).isNotNull();
        assertThat(artist.getId()).isNotNull();
        assertThat(artist.getId()).isEqualTo(defaultArtist.getId());
        assertThat(artist.getName()).isEqualTo(defaultArtist.getName());
        assertThat(artist.getPopularity()).isEqualTo(defaultArtist.getPopularity());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetArtistWithNonExistingId() {
        playlistService.getArtistById(4000L);
    }

    @Test
    public void testCreateArtist() {
        Artist savedArtist = playlistService.createArtist(defaultArtist);
        assertThat(savedArtist).isNotNull();
        assertThat(savedArtist.getId()).isNotNull();
        assertThat(savedArtist.getName()).isEqualTo(defaultArtist.getName());
        assertThat(savedArtist.getPopularity()).isEqualTo(defaultArtist.getPopularity());
    }

    @Test
    public void testUpdateArtist() {
        defaultArtist.setName("Updated Artist");
        defaultArtist.setPopularity(40L);

        Artist updatedArtist = playlistService.updateArtist(defaultArtist);
        assertThat(updatedArtist).isNotNull();
        assertThat(updatedArtist.getId()).isNotNull();
        assertThat(updatedArtist.getName()).isEqualTo("Updated Artist");
        assertThat(updatedArtist.getPopularity()).isEqualTo(40L);

    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateArtistWithNonExistingId() {
        defaultArtist.setId(4000L);
        playlistService.updateArtist(defaultArtist);

    }
    @Test
    public void testDeleteArtistById() {
        playlistService.deleteArtistById(defaultArtist.getId());
        Optional<Artist> deletedArtist = artistRepository.findById(defaultArtist.getId());
        assertThat(deletedArtist.isPresent()).isFalse();

    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteArtistWithNonExistingId() {
        playlistService.deleteArtistById(4000L);

    }

}
