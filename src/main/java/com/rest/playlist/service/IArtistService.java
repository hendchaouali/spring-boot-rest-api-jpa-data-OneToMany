package com.rest.playlist.service;

import com.rest.playlist.model.Artist;

import java.util.List;

public interface IArtistService {

    List<Artist> getAllArtists();

    Artist getArtistById(Long id);

    Artist createArtist(Artist artist);

    Artist updateArtist(Artist artist);

    void deleteArtistById(Long id);
}
