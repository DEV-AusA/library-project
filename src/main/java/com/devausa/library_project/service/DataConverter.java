package com.devausa.library_project.service;

import com.devausa.library_project.model.Language;
import com.devausa.library_project.model.LanguageDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class DataConverter implements IDataConverter {
    private ObjectMapper objectMapper;

    public DataConverter() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Language.class, new LanguageDeserializer());
        objectMapper.registerModule(module);
    }

    @Override
    public <T> T fetchData(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            throw new RuntimeException("Empty JSON response");
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
    }
}
