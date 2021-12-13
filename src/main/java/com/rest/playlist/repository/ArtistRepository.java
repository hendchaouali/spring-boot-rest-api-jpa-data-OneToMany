package com.rest.playlist.repository;

import com.rest.playlist.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaAuditing
public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
