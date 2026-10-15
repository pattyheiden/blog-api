package com.api.blogapi;


import com.api.blogapi.dto.request.PostRequestDto;
import com.api.blogapi.dto.response.PostResponseDto;
import com.api.blogapi.exception.ResourceNotFoundException;
import com.api.blogapi.mapper.ComentarioMapper;
import com.api.blogapi.mapper.PostMapper;
import com.api.blogapi.model.ComentarioModel;
import com.api.blogapi.model.PostModel;
import com.api.blogapi.repository.ComentarioRepository;
import com.api.blogapi.repository.PostRepository;
import com.api.blogapi.service.MessageService;
import com.api.blogapi.service.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private ComentarioRepository comentarioRepository;
    @Mock
    private ComentarioMapper comentarioMapper;
    @Mock
    private MessageService messageService;

    @InjectMocks
    private PostServiceImpl service;

    @Test
    void deveFiltrarPostsPorTituloEMapearComMethodReference() {
        PostModel post = new PostModel("Ana", "Spring Boot", "Texto", "Tecnologia");
        PostResponseDto response = new PostResponseDto(
                UUID.randomUUID(), "Ana", LocalDate.now(), "Spring Boot", "Texto", "Tecnologia", List.of());
        var pageable = PageRequest.of(0, 5);

        when(postRepository.findByTituloContainingIgnoreCase("spring", pageable))
                .thenReturn(new PageImpl<>(List.of(post), pageable, 1));
        when(postMapper.toDto(post)).thenReturn(response);

        var resultado = service.findAll(pageable, "spring");

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Spring Boot", resultado.getContent().get(0).titulo());
        verify(postRepository).findByTituloContainingIgnoreCase("spring", pageable);
        verify(postMapper).toDto(post);
    }

    @Test
    void deveLancarExcecaoPersonalizadaQuandoPostNaoExiste() {
        UUID id = UUID.randomUUID();
        when(postRepository.findById(id)).thenReturn(Optional.empty());
        when(messageService.getMessage("post.nao.encontrado", id))
                .thenReturn("Post não encontrado com o ID " + id + ".");

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> service.findById(id));

        assertEquals("Post não encontrado com o ID " + id + ".", exception.getMessage());
    }

    @Test
    void deveEditarPostExistente() {
        UUID id = UUID.randomUUID();
        PostModel post = new PostModel("Autor", "Título antigo", "Texto antigo", "Geral");
        PostRequestDto request = new PostRequestDto("Novo autor", "Novo título", "Novo texto", "Java");
        PostResponseDto response = new PostResponseDto(
                id, "Novo autor", LocalDate.now(), "Novo título", "Novo texto", "Java", List.of());

        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        doAnswer(invocation -> {
            PostRequestDto dto = invocation.getArgument(0);
            PostModel entity = invocation.getArgument(1);
            entity.setAutor(dto.autor());
            entity.setTitulo(dto.titulo());
            entity.setTexto(dto.texto());
            entity.setCategoria(dto.categoria());
            return null;
        }).when(postMapper).updateEntity(request, post);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(response);

        PostResponseDto resultado = service.updatePost(id, request);

        assertEquals("Novo título", resultado.titulo());
        verify(postMapper).updateEntity(request, post);
        verify(postRepository).save(post);
    }

    @Test
    void deveExcluirComentarioExistente() {
        UUID comentarioId = UUID.randomUUID();
        ComentarioModel comentario = new ComentarioModel();
        when(comentarioRepository.findById(comentarioId)).thenReturn(Optional.of(comentario));

        service.deleteComentario(comentarioId);

        verify(comentarioRepository).delete(comentario);
    }
}