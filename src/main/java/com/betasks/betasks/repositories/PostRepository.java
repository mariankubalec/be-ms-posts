package com.betasks.betasks.repositories;

import com.betasks.betasks.models.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
  List<Post> findAllByUserId(Integer userId);
}
