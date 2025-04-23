package com.example.guide.repository;

import com.example.guide.model.Path;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PathRepository extends JpaRepository<Path,Long> {
}
