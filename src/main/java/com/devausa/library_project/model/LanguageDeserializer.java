package com.devausa.library_project.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Arrays;

public class LanguageDeserializer extends JsonDeserializer<Language> {

    @Override
    public Language deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        String key = jsonParser.getText().toUpperCase().trim(); // Convert the value to uppercase and trim spaces

        return Arrays.stream(Language.values())
                .filter(language -> language.getLanguageCode().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No enum constant com.alura.Literalura.model.Language." + key));
    }

}
