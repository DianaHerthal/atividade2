package br.edu.utfpr.td.tsi.model;

public class Publications {
    private String autor;
    private String titulo;
    private String ano;
    private String local;
    private String pages;
    private String url;
    private String abstractInfo;
    private String keyWords;

    public Publications(String autor, String titulo, String ano, String local, String pages, String url, String abstractInfo, String keyWords) {
        this.autor = autor;
        this.titulo = titulo;
        this.ano = ano;
        this.local = local;
        this.pages = pages;
        this.url = url;
        this.abstractInfo = abstractInfo;
        this.keyWords = keyWords;
    }

    public String getAutor() {
        return autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAno() {
        return ano;
    }

    public String getLocal() {
        return local;
    }

    public String getPages() {
        return pages;
    }

    public String getUrl() {
        return url;
    }

    public String getAbstractInfo() {
        return abstractInfo;
    }

    public String getKeyWords() {
        return keyWords;
    }
}
