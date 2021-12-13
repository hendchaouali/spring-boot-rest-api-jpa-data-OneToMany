package com.rest.playlist.service.artist;

import com.rest.playlist.exception.ResourceNotFoundException;
import com.rest.playlist.model.Artist;
import com.rest.playlist.repository.ArtistRepository;
import com.rest.playlist.service.ArtistServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class ArtistServiceUnitTest {

    private final static Logger log = LoggerFactory.getLogger(ArtistServiceUnitTest.class);

    @MockBean
    private ArtistRepository artistRepository;

    private ArtistServiceImpl playlistService;

    private Artist myArtist;
    private List<Artist> artistList = new ArrayList<>();


    @Before
    public void setup() {
        playlistService = new ArtistServiceImpl(artistRepository);

        myArtist = new Artist();
        myArtist.setId(2000L);
        myArtist.setName("Artist #1");
        myArtist.setPopularity(80L);

    }

    @Test
    public void testGetAllArtists() {
        artistRepository.save(myArtist);
        when(artistRepository.findAll()).thenReturn(artistList);

        //test
        List<Artist> artists = playlistService.getAllArtists();
        assertEquals(artists, artistList);
        verify(artistRepository, times(1)).save(myArtist);
        verify(artistRepository, times(1)).findAll();
    }

    @Test
    public void testCreateArtist() {
        when(artistRepository.findById(2000L)).thenReturn(Optional.of(myArtist));
        when(artistRepository.save(any(Artist.class))).thenReturn(myArtist);
        playlistService.createArtist(myArtist);
        verify(artistRepository, times(1)).save(any(Artist.class));
    }

    @Test
    public void testUpdateArtist() {

        when(artistRepository.findById(myArtist.getId())).thenReturn(Optional.of(myArtist));

        myArtist.setName("Artist #1");
        myArtist.setPopularity(70L);

        given(artistRepository.saveAndFlush(myArtist)).willReturn(myArtist);

        Artist updatedArtist = playlistService.updateArtist(myArtist);

        assertThat(updatedArtist).isNotNull();
        assertThat(updatedArtist).isEqualTo(myArtist);
        assertThat(updatedArtist.getId()).isNotNull();
        assertThat(updatedArtist.getId()).isEqualTo(myArtist.getId());
        assertThat(updatedArtist.getName()).isEqualTo(myArtist.getName());
        assertThat(updatedArtist.getPopularity()).isEqualTo(myArtist.getPopularity());
    }


    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateArtistWithNonExistingId() {
        when(artistRepository.findById(myArtist.getId())).thenReturn(Optional.empty());
        playlistService.updateArtist(myArtist);

    }

    @Test
    public void testGetArtistsById() {
        // when
        when(artistRepository.findById(myArtist.getId())).thenReturn(Optional.ofNullable(myArtist));
        Artist foundArtist = playlistService.getArtistById(myArtist.getId());

        //test - then
        assertThat(foundArtist).isNotNull();
        assertThat(foundArtist).isEqualTo(myArtist);
        assertThat(foundArtist.getId()).isNotNull();
        assertThat(foundArtist.getId()).isEqualTo(myArtist.getId());
        assertThat(foundArtist.getId()).isEqualTo(myArtist.getId());
        assertThat(foundArtist.getName()).isEqualTo(myArtist.getName());
        assertThat(foundArtist.getPopularity()).isEqualTo(myArtist.getPopularity());

    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetArtistsWithNonExistingId() {

        // when
        when(artistRepository.findById(4000L)).thenReturn(Optional.empty());
        playlistService.getArtistById(4000L);
    }

    @Test
    public void testDeleteArtistById() {
        myArtist.setId(1000L);
        when(artistRepository.findById(myArtist.getId())).thenReturn(Optional.of(myArtist));
        playlistService.deleteArtistById(myArtist.getId());
        verify(artistRepository, times(1)).deleteById(myArtist.getId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteArtistWithNonExistingId() {
        when(artistRepository.findById(4000L)).thenReturn(Optional.empty());
        playlistService.deleteArtistById(4000L);
    }
}
