package com.devausa.library_project.service;

public interface IDataConverter {
    <T> T fetchData(String json, Class<T> clase);
}
