package com.devausa.library_project.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookData(
        @JsonAlias("id")
        Long id,

        @JsonAlias("title")
        String title,
        @JsonAlias("authors")
        List<AuthorData> authors,

        @JsonAlias("download_count")
        Double downloadCount,

        @JsonAlias("languages")
        List<Language> languages) {

}

