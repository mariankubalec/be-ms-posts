package com.testapp.bemsposts.services;

import com.testapp.bemsposts.models.Post;
import com.testapp.bemsposts.models.PostDTO;
import com.testapp.bemsposts.models.PostUpdateDTO;
import com.testapp.bemsposts.repositories.PostRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// ==========================
// *** Main Post Services ***
// ==========================
@Service
public class PostServiceImpl implements PostService {
  private final PostRepository postRepository;
  private final ExternalAPIService externalAPIService;

  @Autowired
  public PostServiceImpl(
      PostRepository postRepository,
      ExternalAPIService externalAPIService
  ) {
    this.postRepository = postRepository;
    this.externalAPIService = externalAPIService;
  }

  // -----------------------------
  // *** Services for new Post ***
  // -----------------------------
  @Override
  public Post addPost(PostDTO inputPost) {
    externalAPIService.validateUserId(inputPost.getUserId());
    Post post = externalAPIService.savePost(inputPost);
    savePost(post);
    return post;
  }

  private void savePost(Post post) {
    postRepository.save(post);
  }


  // ------------------------------------
  // *** Services for find Post by Id ***
  // ------------------------------------
  @Override
  public Post findById(Integer id) {
    Optional<Post> myPost = postRepository.findById(id);
    if (myPost.isPresent()) {
      return myPost.get();
    } else {
      Post externalPost = externalAPIService.findPostById(id);
      savePost(externalPost);
      return externalPost;
    }
  }

  // ------------------------------------------
  // *** Services for find Posts by User Id ***
  // ------------------------------------------

  @Override
  public List<Post> findPostsByUserId(Integer userId) {
    List<Post> internalPosts = postRepository.findAllByUserId(userId);
    List<Post> externalPosts = externalAPIService.findPostsByUserId(userId);
    return mergePosts(internalPosts, externalPosts);
  }
  private List<Post> mergePosts(List<Post> internalPosts, List<Post> externalPosts) {
    List<Integer> internalPostIds = new ArrayList<>();
    for (Post element : internalPosts) {
      internalPostIds.add(element.getId());
    }
    for (Post element : externalPosts) {
      if (!internalPostIds.contains(element.getId())) {
        internalPosts.add(element);
      }
    }
    return internalPosts;
  }

  // --------------------------------
  // *** Services for Post Update ***
  // --------------------------------
  @Override
  public Post updatePostById(Integer id, PostUpdateDTO inputPost) {
    Post post = externalAPIService.updatePostById(id, inputPost);
    if (post != null) {
      savePost(post);
    }
    return post;
  }

  // --------------------------------
  // *** Services for Post Delete ***
  // --------------------------------
  @Override
  public void deletePostById(Integer id) {
    externalAPIService.deletePostById(id);
    postRepository.deleteById(id);
  }
}
