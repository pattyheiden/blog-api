package com.api.blogapi.repository;

import com.api.blogapi.model.ComentarioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ComentarioRepository extends JpaRepository<ComentarioModel, UUID> {



}
