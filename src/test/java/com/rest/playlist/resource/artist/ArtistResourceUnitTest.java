package com.rest.playlist.resource.artist;

import com.rest.playlist.exception.ResourceNotFoundException;
import com.rest.playlist.model.Artist;
import com.rest.playlist.resource.ArtistResource;
import com.rest.playlist.service.IArtistService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static com.rest.playlist.TestUtils.asJsonString;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ArtistResource.class)
public class ArtistResourceUnitTest {

    private static final Logger log = LoggerFactory.getLogger(ArtistResourceUnitTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IArtistService playlistService;

    private Artist myArtist;
    private List<Artist> artistList = new ArrayList<>();


    @Before
    public void setup() {
        myArtist = new Artist();
        myArtist.setId(1000L);
        myArtist.setName("Artist");
        myArtist.setPopularity(34L);

    }

    @Test
    public void testGetAllArtists() throws Exception {
        artistList.add(myArtist);
        when(playlistService.getAllArtists()).thenReturn(artistList);

        mockMvc.perform(get("/api/artists")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].name").value(artistList.get(0).getName()));
        verify(playlistService).getAllArtists();
        verify(playlistService,times(1)).getAllArtists();
    }

   @Test
    public void testGetNoContentArtists() throws Exception {
        when(playlistService.getAllArtists()).thenReturn(artistList);

        mockMvc.perform(get("/api/artists")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }


   @Test
    public void testGetArtistById() throws Exception {
        when(playlistService.getArtistById(myArtist.getId())).thenReturn(myArtist);

        mockMvc.perform(get("/api/artists/" + myArtist.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(myArtist.getName()))
                .andExpect(jsonPath("$.popularity").value(myArtist.getPopularity()));
    }


    @Test
    public void testGetArtistByNonExistingId() throws Exception {
        doThrow(new ResourceNotFoundException("Not found Artist with id = 1000")).when(playlistService).getArtistById(1000L);
        mockMvc.perform(get("/api/artists/1000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message").value("Not found Artist with id = 1000"));
    }


    @Test
    public void testCreateArtist() throws Exception {
            when(playlistService.createArtist(any(Artist.class))).thenReturn(myArtist);
        mockMvc.perform(post("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(myArtist)))
                .andExpect(status().isCreated());
        verify(playlistService,times(1)).createArtist(any());
    }

    @Test
    public void testCreateArtistWithNameSizeLessThanThree() throws Exception {
        myArtist.setName("S");
        doThrow(new ResourceNotFoundException("Size: titre doit être compris entre 3 et 50 caractères"))
                .when(playlistService).createArtist(myArtist);
        mockMvc.perform(post("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(myArtist)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("fieldErrors[0].message")
                        .value("Size: name doit être compris entre 3 et 50 caractères"));
    }

    @Test
    public void testCreateArtistWithPopularitySizeLessThanThree() throws Exception {
        myArtist.setPopularity(200L);
        doThrow(new ResourceNotFoundException("Max: popularity doit être entre 0 et 100"))
                .when(playlistService).createArtist(myArtist);
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
        doThrow(new ResourceNotFoundException("NotBlank: name ne doit pas être null ou vide"))
                .when(playlistService).createArtist(myArtist);
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
        when(playlistService.updateArtist(myArtist)).thenReturn(myArtist);
        mockMvc.perform(put("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(myArtist)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateArtistWithNameSizeLessThanThree() throws Exception {
        myArtist.setName("S");
        doThrow(new ResourceNotFoundException("Size: name doit être compris entre 3 et 50 caractères"))
                .when(playlistService).updateArtist(myArtist);
        mockMvc.perform(post("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(myArtist)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("fieldErrors[0].message")
                        .value("Size: name doit être compris entre 3 et 50 caractères"));
    }

    @Test
    public void testUpdateArtistWithPopularityGreaterThan100() throws Exception {
        myArtist.setPopularity(200L);
        doThrow(new ResourceNotFoundException("Max: popularity doit être entre 0 et 100"))
                .when(playlistService).updateArtist(myArtist);
        mockMvc.perform(post("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(myArtist)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("fieldErrors[0].message")
                        .value("Max: popularity doit être entre 0 et 100"));
    }

    @Test
    public void testUpdateArtistWithNameNull() throws Exception {
        myArtist.setName(null);
        doThrow(new ResourceNotFoundException("NotBlank: titre ne doit pas être null ou vide"))
                .when(playlistService).updateArtist(myArtist);
        mockMvc.perform(post("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(myArtist)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("fieldErrors[0].message")
                        .value("NotBlank: name ne doit pas être null ou vide"));
    }

    @Test
    public void testDeleteArtistById() throws Exception {
        doNothing().when(playlistService).deleteArtistById(myArtist.getId());
        mockMvc.perform(delete("/api/artists/" + myArtist.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteNotFoundArtist() throws Exception {
        doThrow(new ResourceNotFoundException("Not found Artist with id = 1000")).when(playlistService).deleteArtistById(1000L);
        mockMvc.perform(delete("/api/artists/1000"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message").value("Not found Artist with id = 1000"));
    }
}
