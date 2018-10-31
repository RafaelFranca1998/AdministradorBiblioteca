package release.saosalvador.com.administradorbiblioteca.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Livro {
    @PrimaryKey
    @NonNull private String idLivro;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "editora")
    private String editora;

    @ColumnInfo(name = "edicao")
    private String edicao;

    @ColumnInfo(name = "ano")
    private String ano;

    @ColumnInfo(name = "autor")
    private String autor;

    @ColumnInfo(name = "categoria")
    private String categoria;

    @ColumnInfo(name = "area")
    private String area;

    @ColumnInfo(name = "linkDownload")
    private String linkDownload;

    @ColumnInfo(name = "imgDownload")
    private String imgDownload;


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
}
