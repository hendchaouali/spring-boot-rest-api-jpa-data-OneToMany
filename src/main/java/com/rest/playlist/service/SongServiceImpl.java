package com.rest.playlist.service;

import com.rest.playlist.enums.SongCategory;
import com.rest.playlist.web.exception.ResourceNotFoundException;
import com.rest.playlist.model.Artist;
import com.rest.playlist.model.Song;
import com.rest.playlist.repository.ArtistRepository;
import com.rest.playlist.repository.SongRepository;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SongServiceImpl implements ISongService {
    private static final Logger log = LoggerFactory.getLogger(SongServiceImpl.class);

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;

    public SongServiceImpl(SongRepository songRepository, ArtistRepository artistRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> getSongsByCategory(String category) {
        SongCategory searchedCategory = EnumUtils.getEnumIgnoreCase(SongCategory.class, category);
        if (searchedCategory == null) {
            throw new ResourceNotFoundException("Not found Category with value = " + category);
        }
        return songRepository.findSongsByCategory(searchedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> getSongsByArtistName(String name) {
        return songRepository.findSongsByArtist_Name(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Song getSongById(Long id) {

        return songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found song with id = " + id));
    }

    @Override
    public Song createSong(Song song) {
        Artist artist = artistRepository.findById(song.getArtist().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found artist with id = " + song.getArtist().getId()));
        song.setArtist(artist);
        return songRepository.save(song);
    }

    @Override
    public Song updateSong(Song song) {

        Artist artist = artistRepository.findById(song.getArtist().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found artist with id = " + song.getArtist().getId()));

        Song searchedSong = songRepository.findById(song.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found song with id = " + song.getId()));

        searchedSong.setTitle(song.getTitle());
        searchedSong.setDescription(song.getDescription());
        searchedSong.setCategory(song.getCategory());
        searchedSong.setDuration(song.getDuration());
        searchedSong.setArtist(artist);

        return songRepository.saveAndFlush(song);
    }

    @Override
    public void deleteSongById(Long id) {
        songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found song with id = " + id));

        songRepository.deleteById(id);
    }
}
