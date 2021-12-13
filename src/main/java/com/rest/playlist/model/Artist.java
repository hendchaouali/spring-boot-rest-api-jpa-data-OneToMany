package com.rest.playlist.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "artist")
public class Artist extends AbstractAuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ARTIST_SEQ")
    @SequenceGenerator(name = "ARTIST_SEQ", sequenceName = "artist_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "name ne doit pas être null ou vide")
    @Size(min = 3, max = 50, message = "name doit être compris entre 3 et 50 caractères")
    private String name;

    @Column(name = "popularity")
    @NotNull(message = "popularity ne doit pas être nulle ou vide")
    @Min(value = 0, message = "popularity doit être entre 0 et 100")
    @Max(value = 100, message = "popularity doit être entre 0 et 100")
    private Long popularity;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "artist")
    @JsonIgnore
    private List<Song> songs = new ArrayList<>();
}
