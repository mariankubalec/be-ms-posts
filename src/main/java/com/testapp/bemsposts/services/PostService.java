package com.testapp.bemsposts.services;

import com.testapp.bemsposts.models.Post;
import com.testapp.bemsposts.models.PostDTO;
import com.testapp.bemsposts.models.PostUpdateDTO;
import java.util.List;


public interface PostService {

  Post addPost(PostDTO inputPost);

  Post findById(Integer id);

  List<Post> findPostsByUserId(Integer userId);

  void deletePostById(Integer id);

  Post updatePostById(Integer id, PostUpdateDTO inputPost);

}
