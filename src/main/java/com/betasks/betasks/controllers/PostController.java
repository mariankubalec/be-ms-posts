package com.betasks.betasks.controllers;

import com.betasks.betasks.exceptions.PostNotFoundException;
import com.betasks.betasks.exceptions.UserNotFoundException;
import com.betasks.betasks.models.ErrorMessageDTO;
import com.betasks.betasks.models.Post;
import com.betasks.betasks.models.PostDTO;
import com.betasks.betasks.models.PostUpdateDTO;
import com.betasks.betasks.services.PostService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/posts")
public class PostController {
  private final PostService postService;

  @Autowired
  public PostController(PostService postService) {
    this.postService = postService;
  }

  //  >>> FUNCTIONAL REQUIREMENT <<<
  //    - Pridanie príspevku - potrebné validovať userID pomocou externej API

  @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Post> addPost(@RequestBody PostDTO inputPost) {
    Post post = postService.addPost(inputPost);
    return ResponseEntity.status(HttpStatus.CREATED).body(post);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity handleUserNotFoundException(UserNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorMessageDTO(exception.getMessage()));
  }

  //  >>> FUNCTIONAL REQUIREMENT <<<
  //    - Zobrazenie príspevku na základe id
  //    - ak sa príspevok nenájde v systéme, je potrebné ho dohľadať pomocou externej API a uložiť

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Post> findPostById(@PathVariable(value = "id") Integer id) {
    Post post = postService.findById(id);
    return ResponseEntity.status(HttpStatus.OK).body(post);
  }

  // ### HTML view ###
  @GetMapping(value = "/{id}", produces = MediaType.TEXT_HTML_VALUE)
  public String getIndex(Model model, @PathVariable(value = "id") Integer id) {
    model.addAttribute("post", postService.findById(id));
    return "postview";
  }

  //  >>> FUNCTIONAL REQUIREMENT <<<
  //    - Zobrazenie príspevku na základe userId

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<List<Post>> findPostsByUserId(
      @RequestParam(value = "userId", required = true) Integer userId) {
    List<Post> posts = postService.findPostsByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(posts);
  }

  //  >>> FUNCTIONAL REQUIREMENT <<<
  //    - Odstránenie príspevku

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Post> deletePostById(@PathVariable(value = "id") Integer id) {
    postService.deletePostById(id);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  //  >>> FUNCTIONAL REQUIREMENT <<<
  //    - Upravenie príspevku - možnosť meniť title a body

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Post> putPostById(@PathVariable(value = "id") Integer id,
                                          @RequestBody PostUpdateDTO inputPost) {
    Post post = postService.updatePostById(id, inputPost);
    return ResponseEntity.status(HttpStatus.OK).body(post);
  }

  @ExceptionHandler(PostNotFoundException.class)
  public ResponseEntity handlePostNotFoundException(PostNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorMessageDTO(exception.getMessage()));
  }
}
