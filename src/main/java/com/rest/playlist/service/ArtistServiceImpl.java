package com.rest.playlist.service;

import com.rest.playlist.web.exception.ResourceNotFoundException;
import com.rest.playlist.model.Artist;
import com.rest.playlist.repository.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ArtistServiceImpl implements IArtistService {
    private static final Logger log = LoggerFactory.getLogger(ArtistServiceImpl.class);

    private final ArtistRepository artistRepository;

    public ArtistServiceImpl(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public Artist getArtistById(Long id) {

        return artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found artist with id = " + id));
    }

    @Override
    public Artist createArtist(Artist artist) {
        return artistRepository.save(artist);
    }

    @Override
    public Artist updateArtist(Artist artist) {

        Artist searchedArtist = artistRepository.findById(artist.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found artist with id = " + artist.getId()));

        searchedArtist.setName(artist.getName());
        searchedArtist.setPopularity(artist.getPopularity());

        return artistRepository.saveAndFlush(artist);
    }

    @Override
    public void deleteArtistById(Long id) {
        artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found artist with id = " + id));

        artistRepository.deleteById(id);
    }
}
