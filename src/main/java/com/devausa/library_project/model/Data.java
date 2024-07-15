package com.devausa.library_project.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Data(
        @JsonAlias("count")
        Double total,

        @JsonAlias("results")
        List<BookData> results
) {
}

