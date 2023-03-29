package com.testapp.bemsposts.services;

import com.testapp.bemsposts.exceptions.ExternalAPIErrorException;
import com.testapp.bemsposts.exceptions.PostNotFoundException;
import com.testapp.bemsposts.exceptions.UserNotFoundException;
import com.testapp.bemsposts.models.Post;
import com.testapp.bemsposts.models.dtos.PostDTO;
import com.testapp.bemsposts.models.dtos.PostUpdateDTO;
import com.testapp.bemsposts.models.dtos.UserDTO;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ExternalAPIServiceImpl implements ExternalAPIService {

  private final WebClient webClient;

  @Autowired
  public ExternalAPIServiceImpl(WebClient webClient) {
    this.webClient = webClient;
  }

  @Override
  public void validateUserId(Integer userId) {
    webClient.get()
        .uri(uriBuilder -> uriBuilder.pathSegment("users", userId.toString()).build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError,
            response -> Mono.error(new UserNotFoundException("User Not Found!")))
        .bodyToMono(UserDTO.class)
        .onErrorMap(Predicate.not(UserNotFoundException.class::isInstance),
            throwable -> new ExternalAPIErrorException("External API Error"))
        .block();
  }

  @Override
  public Post savePost(PostDTO inputPost) {
    return webClient.post()
        .uri(uriBuilder -> uriBuilder.pathSegment("posts").build())
        .bodyValue(inputPost)
        .retrieve()
        .bodyToMono(Post.class)
        .onErrorMap(throwable -> new ExternalAPIErrorException("External API Error"))
        .block();
  }

  @Override
  public Post findPostById(Integer id) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.pathSegment("posts", id.toString()).build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError,
            response -> Mono.error(new PostNotFoundException("Post Not Found")))
        .bodyToMono(Post.class)
        .onErrorMap(Predicate.not(PostNotFoundException.class::isInstance),
            throwable -> new ExternalAPIErrorException("External API Error"))
        .block();
  }

  @Override
  public List<Post> findPostsByUserId(Integer userId) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .pathSegment("posts").queryParam("userId", userId).build())
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<List<Post>>() {
        })
        .onErrorMap(throwable -> new ExternalAPIErrorException("External API Error"))
        .block();
  }

  @Override
  public Post updatePostById(Integer id, PostUpdateDTO inputPost) {
    Post post = findPostById(id);
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
        .onStatus(HttpStatusCode::is4xxClientError,
            response -> Mono.error(new PostNotFoundException("Post Not Found")))
        .bodyToMono(Post.class)
        .onErrorMap(Predicate.not(PostNotFoundException.class::isInstance),
            throwable -> new ExternalAPIErrorException("External API Error"))
        .block();
  }

  @Override
  public void deletePostById(Integer id) {
    webClient.delete()
        .uri(uriBuilder -> uriBuilder.pathSegment("posts", id.toString()).build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError,
            response -> Mono.error(new PostNotFoundException("Post Not Found")))
        .bodyToMono(Post.class)
        .onErrorMap(Predicate.not(PostNotFoundException.class::isInstance),
            throwable -> new ExternalAPIErrorException("External API Error"))
        .block();
  }
}
