package com.betasks.betasks.services;

import com.betasks.betasks.models.Post;
import com.betasks.betasks.models.PostDTO;
import com.betasks.betasks.models.PostUpdateDTO;
import java.util.List;


public interface PostService {

  Post addPost(PostDTO inputPost);

  Post findById(Integer id);

  List<Post> findPostsByUserId(Integer userId);

  void deletePostById(Integer id);

  Post updatePostById(Integer id, PostUpdateDTO inputPost);

}
