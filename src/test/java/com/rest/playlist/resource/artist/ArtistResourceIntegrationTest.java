package com.rest.playlist.resource.artist;

import com.rest.playlist.exception.ServiceExceptionHandler;
import com.rest.playlist.model.Artist;
import com.rest.playlist.repository.ArtistRepository;
import com.rest.playlist.resource.ArtistResource;
import com.rest.playlist.service.IArtistService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.rest.playlist.TestUtils.asJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ArtistResourceIntegrationTest {
    private final static Logger log = LoggerFactory.getLogger(ArtistResourceIntegrationTest.class);

    private MockMvc mockMvc;

    @Autowired
    private ServiceExceptionHandler serviceExceptionHandler;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private IArtistService playlistService;

    private Artist myArtist;

    @Before
    public void setup() {

        ArtistResource artistResource = new ArtistResource(playlistService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(artistResource)
                .setControllerAdvice(serviceExceptionHandler)
                .build();

        myArtist = new Artist();
        myArtist.setName("Artist #1");
        myArtist.setPopularity(20L);

    }

    @Test
    public void testGetAllArtists() throws Exception {
        Artist savedArtist = artistRepository.saveAndFlush(myArtist);
        mockMvc.perform(get("/api/artists")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].name").value(hasItem(savedArtist.getName())));
    }

    @Test
    public void testGetNoContentArtists() throws Exception {
        artistRepository.deleteAll();
        mockMvc.perform(get("/api/artists")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetArtistById() throws Exception {
        Artist savedArtist = artistRepository.saveAndFlush(myArtist);
        mockMvc.perform(get("/api/artists/{id}", savedArtist.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedArtist.getId()))
                .andExpect(jsonPath("$.name").value(savedArtist.getName()))
                .andExpect(jsonPath("$.popularity").value(savedArtist.getPopularity()));
    }


    @Test
    public void testGetArtistByNonExistingId() throws Exception {
        mockMvc.perform(get("/api/artists/4000"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testCreateArtist() throws Exception {
        int sizeBefore = artistRepository.findAll().size();
        Artist savedArtist = artistRepository.saveAndFlush(myArtist);
        mockMvc.perform(post("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(savedArtist)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(savedArtist.getName()))
                .andExpect(jsonPath("$.popularity").value(savedArtist.getPopularity()));

        List<Artist> artists = artistRepository.findAll();

        assertThat(artists).hasSize(sizeBefore + 1);

        Artist lastArtist = artists.get(artists.size() - 1);

        assertThat(lastArtist.getName()).isEqualTo(savedArtist.getName());
        assertThat(lastArtist.getPopularity()).isEqualTo(savedArtist.getPopularity());
    }


    @Test
    public void testCreateArtistWithNameSizeLessThanThree() throws Exception {
        myArtist.setName("S");
        mockMvc.perform(post("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(myArtist)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("fieldErrors[0].message")
                        .value("Size: name doit être compris entre 3 et 50 caractères"));
    }

    @Test
    public void testCreateArtistWithPopularityGreaterThan100() throws Exception {
        myArtist.setPopularity(200L);
        mockMvc.perform(post("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(myArtist)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("fieldErrors[0].message")
                        .value("Max: popularity doit être entre 0 et 100"));
    }

    @Test
    public void testCreateArtistWithNameNull() throws Exception {
        myArtist.setName(null);
        mockMvc.perform(post("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(myArtist)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("fieldErrors[0].message")
                        .value("NotBlank: name ne doit pas être null ou vide"));
    }


    @Test
    public void testUpdateArtist() throws Exception {
        Artist savedArtist = artistRepository.saveAndFlush(myArtist);
        savedArtist.setName("Artist updated");
        mockMvc.perform(put("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(savedArtist)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateArtistWithNameSizeLessThanThree() throws Exception {
        Artist savedArtist = artistRepository.saveAndFlush(myArtist);
        savedArtist.setName("S");
        mockMvc.perform(post("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(savedArtist)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("fieldErrors[0].message")
                        .value("Size: name doit être compris entre 3 et 50 caractères"));
    }

    @Test
    public void testUpdateArtistWithPopularityGreaterThan100() throws Exception {
        Artist savedArtist = artistRepository.saveAndFlush(myArtist);
        savedArtist.setPopularity(200L);
        mockMvc.perform(post("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(savedArtist)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("fieldErrors[0].message")
                        .value("Max: popularity doit être entre 0 et 100"));
    }

    @Test
    public void testUpdateArtistWithNameNull() throws Exception {
        Artist savedArtist = artistRepository.saveAndFlush(myArtist);
        savedArtist.setName(null);
        mockMvc.perform(post("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(savedArtist)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("fieldErrors[0].message")
                        .value("NotBlank: name ne doit pas être null ou vide"));
    }


    @Test
    public void testDeleteArtistById() throws Exception {
        Artist savedArtist = artistRepository.saveAndFlush(myArtist);
        mockMvc.perform(delete("/api/artists/{id}", savedArtist.getId()))
                .andExpect(status().isOk());

    }

    @Test
    public void testDeleteNotFoundArtist() throws Exception {
        mockMvc.perform(delete("/api/artists/1000"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message").value("Not found artist with id = 1000"));
    }
}
