package com.example.y.libb;

import java.util.List;

public abstract class Prototype {

    private String username;
    private String url;
    private List<String> repositorios;
    private List<String> updates;


    public String getUsername() {
        return username;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getRepositorios() {
        return repositorios;
    }
    public List<String> getUpdates() {
        return updates;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRepositorios(List<String> repositorios) {
        this.repositorios = repositorios;
    }
    public void setUpdate(List<String> updates) {
        this.updates = updates;
    }

    public abstract Object my_clone();
}
