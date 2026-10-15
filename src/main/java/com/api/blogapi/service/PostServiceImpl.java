package com.api.blogapi.service;

import com.api.blogapi.dto.request.ComentarioRequestDto;
import com.api.blogapi.dto.request.PostRequestDto;
import com.api.blogapi.dto.response.ComentarioResponseDto;
import com.api.blogapi.dto.response.PostResponseDto;
import com.api.blogapi.exception.ResourceNotFoundException;
import com.api.blogapi.mapper.ComentarioMapper;
import com.api.blogapi.mapper.PostMapper;
import com.api.blogapi.model.ComentarioModel;
import com.api.blogapi.model.PostModel;
import com.api.blogapi.repository.ComentarioRepository;
import com.api.blogapi.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ComentarioRepository comentarioRepository;
    private final ComentarioMapper comentarioMapper;
    private final MessageService messageService;

    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper, ComentarioRepository comentarioRepository, ComentarioMapper comentarioMapper, MessageService messageService) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.comentarioRepository = comentarioRepository;
        this.comentarioMapper = comentarioMapper;
        this.messageService = messageService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> findAll(Pageable pageable) {
        Page<PostModel> posts = postRepository.findAll(pageable);

        return posts.map(post -> postMapper.toDto(post));
    }

    @Override
    public Page<PostResponseDto> findAll(Pageable pageable, String titulo) {
        Page<PostModel> posts = (titulo == null || titulo.isBlank())
                ? postRepository.findAll(pageable)
                : postRepository.findByTituloContainingIgnoreCase(titulo, pageable);

        return posts.map(postMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponseDto findById(UUID id) {
        PostModel post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messageService.getMessage("post.nao.encontrado", id)));
        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public PostResponseDto createPost(PostRequestDto dto) {
        PostModel post = postMapper.toEntity(dto);
        PostModel saved = postRepository.save(post);
        return postMapper.toDto(saved);
    }

    @Override
    @Transactional
    public PostResponseDto updatePost(UUID id, PostRequestDto dto) {
        PostModel post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messageService.getMessage("post.nao.encontrado", id)));
        postMapper.updateEntity(dto, post);
        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    @Transactional
    public void deletePost(UUID id) {
        PostModel post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messageService.getMessage("post.nao.encontrado", id)));
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public ComentarioResponseDto addComentario(UUID postId, ComentarioRequestDto dto) {
        Optional<PostModel> optionalPost = postRepository.findById(postId);
        PostModel post = optionalPost.get();
        ComentarioModel comentario = new ComentarioModel(dto.comentario(), post);
        post.adicionarComentario(comentario);
        ComentarioModel saved = comentarioRepository.save(comentario);
        return comentarioMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ComentarioResponseDto updateComentario(UUID comentarioId, ComentarioRequestDto dto) {
        ComentarioModel comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new ResourceNotFoundException(messageService.getMessage("comentario.nao.encontrado", comentarioId)));
        comentarioMapper.updateEntity(dto, comentario);
        return comentarioMapper.toDto(comentarioRepository.save(comentario));
    }

    @Override
    @Transactional
    public void deleteComentario(UUID comentarioId) {
        ComentarioModel comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new ResourceNotFoundException(messageService.getMessage("comentario.nao.encontrado", comentarioId)));
        comentarioRepository.delete(comentario);
    }

}
