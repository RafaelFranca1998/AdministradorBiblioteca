package release.saosalvador.com.administradorbiblioteca.model;

import java.util.Date;

/**
 * Classe livro
 */
public class Livro {
    private String idLivro;
    private String nome;
    private String editora;
    private String edicao;
    private String ano;
    private String autor;
    private String categoria;
    private String area;
    private String linkDownload;
    private String imgDownload;
    private boolean isFavorite;
    private Date dataAdicionado;
    private Date dataVisitado;

    public Livro() {
        isFavorite = false;
    }

    public String getLinkDownload() {
        return linkDownload;
    }

    public void setLinkDownload(String linkDownload) {
        this.linkDownload = linkDownload;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdLivro() {
        return idLivro;
    }

    public void setIdLivro(String idLivro) {
        this.idLivro = idLivro;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public String getEdicao() {
        return edicao;
    }

    public void setEdicao(String edicao) {
        this.edicao = edicao;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getImgDownload() {
        return imgDownload;
    }

    public void setImgDownload(String imgDownload) {
        this.imgDownload = imgDownload;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public Date getDataAdicionado() {
        return dataAdicionado;
    }

    public void setDataAdicionado(Date dataAdicionado) {
        this.dataAdicionado = dataAdicionado;
    }

    public Date getDataVisitado() {
        return dataVisitado;
    }

    public void setDataVisitado(Date dataVisitado) {
        this.dataVisitado = dataVisitado;
    }
}
