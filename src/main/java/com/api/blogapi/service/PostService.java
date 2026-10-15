package com.api.blogapi.service;

import com.api.blogapi.dto.request.ComentarioRequestDto;
import com.api.blogapi.dto.request.PostRequestDto;
import com.api.blogapi.dto.response.ComentarioResponseDto;
import com.api.blogapi.dto.response.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PostService {

    Page<PostResponseDto> findAll(Pageable pageable);

    Page<PostResponseDto> findAll(Pageable pageable, String titulo);

    PostResponseDto findById(UUID id);

    PostResponseDto createPost(PostRequestDto dto);

    PostResponseDto updatePost(UUID id, PostRequestDto dto);

    void deletePost(UUID id);

    ComentarioResponseDto addComentario (UUID postId, ComentarioRequestDto dto);

    ComentarioResponseDto updateComentario(UUID comentarioId, ComentarioRequestDto dto);

    void deleteComentario(UUID comentarioId);
}
