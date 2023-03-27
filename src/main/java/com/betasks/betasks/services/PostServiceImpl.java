package com.betasks.betasks.services;

import com.betasks.betasks.exceptions.PostNotFoundException;
import com.betasks.betasks.exceptions.UserNotFoundException;
import com.betasks.betasks.models.Post;
import com.betasks.betasks.models.PostDTO;
import com.betasks.betasks.models.PostUpdateDTO;
import com.betasks.betasks.models.UserDTO;
import com.betasks.betasks.repositories.PostRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PostServiceImpl implements PostService {
  private final PostRepository postRepository;
  private final WebClient webClient;

  @Autowired
  public PostServiceImpl(PostRepository postRepository, WebClient webClient) {
    this.postRepository = postRepository;
    this.webClient = webClient;
  }

  // *** Services for new Post ***
  @Override
  public Post addPost(PostDTO inputPost) {
    validateUserID(inputPost.getUserId());
    Post post = savePostToExternal(inputPost);
    savePost(post);
    return post;
  }


  private void validateUserID(Integer userId) {
    webClient.get()
        .uri(uriBuilder -> uriBuilder.pathSegment("users", userId.toString()).build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError,
            response -> Mono.error(new UserNotFoundException("Wrong User ID")))
        .bodyToMono(UserDTO.class)
        .block();
  }

  private void savePost(Post post) {
    postRepository.save(post);
  }

  private Post savePostToExternal(PostDTO inputPost) {
    return webClient.post()
        .uri(uriBuilder -> uriBuilder.pathSegment("posts").build())
        .bodyValue(inputPost)
        .retrieve()
        .bodyToMono(Post.class)
        .block();
  }

  // *** Services for find Post by Id ***
  @Override
  public Post findById(Integer id) {
    Optional<Post> myPost = postRepository.findById(id);
    if (myPost.isPresent()) {
      return myPost.get();
    } else {
      Post externalPost = findExternalPostById(id);
      savePost(externalPost);
      return externalPost;
    }
  }

  private Post findExternalPostById(Integer id) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.pathSegment("posts", id.toString()).build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError,
            response -> Mono.error(new PostNotFoundException("Post Not Found")))
        .bodyToMono(Post.class)
        .block();
  }

  // *** Services for find Posts by User Id ***
  @Override
  public List<Post> findPostsByUserId(Integer userId) {
    List<Post> internalPosts = postRepository.findAllByUserId(userId);
    List<Post> externalPosts = findExternalPostsByUserId(userId);
    return mergePosts(internalPosts, externalPosts);
  }

  private List<Post> findExternalPostsByUserId(Integer userId) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .pathSegment("posts").queryParam("userId", userId).build())
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<List<Post>>() {
        })
        .block();
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

  // *** Services for Post Update ***
  @Override
  public Post updatePostById(Integer id, PostUpdateDTO inputPost) {
    Post post = updateExternalPostById(id, inputPost);
    if (post != null) {
      savePost(post);
    }
    return post;
  }

  private Post updateExternalPostById(Integer id, PostUpdateDTO inputPost) {
    Post post = findExternalPostById(id);
    if (inputPost.getTitle() != null) {
      post.setTitle(inputPost.getTitle());
    }
    if (inputPost.getBody() != null) {
      post.setBody(inputPost.getBody());
    }
    return webClient.put()
        .uri(uriBuilder -> uriBuilder.pathSegment("posts", id.toString()).build())
        .bodyValue(post)
        .retrieve()
        .bodyToMono(Post.class)
        .block();
  }

  // *** Services for Post Delete ***
  @Override
  public void deletePostById(Integer id) {
    deleteExternalPostById(id);
    postRepository.deleteById(id);
  }

  private void deleteExternalPostById(Integer id) {
    webClient.delete()
        .uri(uriBuilder -> uriBuilder.pathSegment("posts", id.toString()).build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError,
            response -> Mono.error(new PostNotFoundException("Post Not Found")))
        .bodyToMono(Post.class)
        .block();
  }
}
