package br.edu.utfpr.td.tsi.model;

public class Publications {
    private String autor;
    private String titulo;
    private String ano;
    private String local;
    private String pages;
    private String url;

    public Publications(String autor, String titulo, String ano, String local, String pages, String url) {
        this.autor = autor;
        this.titulo = titulo;
        this.ano = ano;
        this.local = local;
        this.pages = pages;
        this.url = url;
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
}
