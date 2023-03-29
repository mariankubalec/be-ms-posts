package com.testapp.bemsposts.services;

import com.testapp.bemsposts.models.Post;
import com.testapp.bemsposts.models.dtos.PostDTO;
import com.testapp.bemsposts.models.dtos.PostUpdateDTO;
import java.util.List;

public interface ExternalAPIService {
  void validateUserId(Integer userId);
  Post savePost(PostDTO inputPost);
  Post findPostById(Integer id);
  List<Post> findPostsByUserId(Integer userId);
  Post updatePostById(Integer id, PostUpdateDTO inputPost);
  void deletePostById(Integer id);
}
