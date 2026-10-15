package com.api.blogapi.controller;

import com.api.blogapi.dto.request.ComentarioRequestDto;
import com.api.blogapi.dto.request.PostRequestDto;
import com.api.blogapi.dto.response.ComentarioResponseDto;
import com.api.blogapi.dto.response.PostResponseDto;
import com.api.blogapi.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@Tag(name = "Posts e comentários", description = "Operações do Blog API")
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    @Operation(summary = "Lista posts com paginação")
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "data") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String titulo) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        //return ResponseEntity.ok(postService.findAll(pageable));
        return ResponseEntity.ok(postService.findAll(pageable, titulo));
    }


    @GetMapping("/posts/{id}")
    @Operation(summary = "Lista posts com paginação por id")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    @PostMapping("/newpost")
    @Operation(summary = "Criar novo post")
    public ResponseEntity<PostResponseDto> createPost(@RequestBody @Valid PostRequestDto dto) {
        PostResponseDto created = postService.createPost(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/posts/{id}")
    @Operation(summary = "Editar um post")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable UUID id, @RequestBody @Valid PostRequestDto dto) {
        return ResponseEntity.ok(postService.updatePost(id, dto));
    }

    @DeleteMapping("/posts/{id}")
    @Operation(summary = "Excluir um post")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comentarios/{postId}")
    @Operation(summary = "Comentários do post")
    public ResponseEntity<ComentarioResponseDto> createComentario(@PathVariable UUID postId, @RequestBody @Valid ComentarioRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.addComentario(postId, dto));
    }

    @PutMapping("/comentarios/{comentarioId}")
    @Operation(summary = "Editar um comentário")
    public ResponseEntity<ComentarioResponseDto> updateComentario(@PathVariable UUID comentarioId, @RequestBody @Valid ComentarioRequestDto dto) {
        return ResponseEntity.ok(postService.updateComentario(comentarioId, dto));
    }

    @DeleteMapping("/comentarios/{comentarioId}")
    @Operation(summary = "Excluir um comentário")
    public ResponseEntity<Void> deleteComentario(@PathVariable UUID comentarioId) {
        postService.deleteComentario(comentarioId);
        return ResponseEntity.noContent().build();
    }
}
