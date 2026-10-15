package com.api.blogapi.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TB_POST")
public class PostModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 70)
    private String autor;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Lob
    @Column(columnDefinition = "text", nullable = false)
    private String texto;

    @Column(length = 50)
    private String categoria;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComentarioModel> comentarios = new ArrayList<>();

    public PostModel() {
    }

    public PostModel(String autor, String titulo, String texto, String categoria) {
        this.autor = autor;
        this.titulo = titulo;
        this.texto = texto;
        this.categoria = categoria;
        this.data = LocalDate.now();
    }

    public void adicionarComentario(ComentarioModel comentario) {
        comentarios.add(comentario);
        comentario.setPost(this);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public List<ComentarioModel> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<ComentarioModel> comentarios) {
        this.comentarios = comentarios;
    }

    public void removerComentario(ComentarioModel comentario) {
        comentarios.remove(comentario);
        comentario.setPost(null);
    }
}
