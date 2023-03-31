package com.testapp.bemsposts.controllers;

import com.testapp.bemsposts.exceptions.ExternalAPIErrorException;
import com.testapp.bemsposts.exceptions.NullInputException;
import com.testapp.bemsposts.exceptions.PostNotFoundException;
import com.testapp.bemsposts.exceptions.UserNotFoundException;
import com.testapp.bemsposts.models.ErrorMessageDTO;
import com.testapp.bemsposts.models.Post;
import com.testapp.bemsposts.models.PostDTO;
import com.testapp.bemsposts.models.PostUpdateDTO;
import com.testapp.bemsposts.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Posts Microservice", description = "Posts Microservice with external API support")
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

  @Operation(
      summary = "Add a Post",
      description = "Adds a Post object. The response is added Post object with id, title," +
          " body and userId.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(
          implementation = Post.class), mediaType = "application/json")}),
      @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(
          implementation = ErrorMessageDTO.class), mediaType = "application/json")}),
      @ApiResponse(responseCode = "503", content = {@Content(schema = @Schema(
          implementation = ErrorMessageDTO.class), mediaType = "application/json")})})
  @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Post> addPost(@RequestBody PostDTO inputPost) {
    Post post = postService.addPost(inputPost);
    return ResponseEntity.status(HttpStatus.CREATED).body(post);
  }

  //  >>> FUNCTIONAL REQUIREMENT <<<
  //    - Zobrazenie príspevku na základe id
  //    - ak sa príspevok nenájde v systéme, je potrebné ho dohľadať pomocou externej API a uložiť

  @Operation(
      summary = "Retrieve a Post by Id",
      description = "Gets a Post object by specifying its id. The response is Post object with" +
          " id, title, body and userId.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(
          implementation = Post.class), mediaType = "application/json")}),
      @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(
          implementation = ErrorMessageDTO.class), mediaType = "application/json")}),
      @ApiResponse(responseCode = "503", content = {@Content(schema = @Schema(
          implementation = ErrorMessageDTO.class), mediaType = "application/json")})})
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
  //    - Zobrazenie príspevkov na základe userId

  @Operation(
      summary = "Retrieve a Posts by UserId",
      description = "Gets a Post objects by specifying UserId as a request parameter. " +
          "The response is the List of Post objects with id, title, body and userId.")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          content = {
              @Content(
                  array = @ArraySchema(schema = @Schema(implementation = Post.class)),
                  mediaType = "application/json")
          }
      ),
      @ApiResponse(
          responseCode = "503",
          content = {
              @Content(schema = @Schema(implementation = ErrorMessageDTO.class),
                  mediaType = "application/json")
          }
      )
  })
  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<List<Post>> findPostsByUserId(
      @RequestParam(value = "userId") Integer userId) {
    List<Post> posts = postService.findPostsByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(posts);
  }

  //  >>> FUNCTIONAL REQUIREMENT <<<
  //    - Odstránenie príspevku
  @Operation(
      summary = "Delete a Post by Id", description = "Deletes a Post object by specifying its id.")
  @ApiResponses({
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "503", content = {@Content(schema = @Schema(
          implementation = ErrorMessageDTO.class), mediaType = "application/json")})})
  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Post> deletePostById(@PathVariable(value = "id") Integer id) {
    postService.deletePostById(id);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  //  >>> FUNCTIONAL REQUIREMENT <<<
  //    - Upravenie príspevku - možnosť meniť title a body
  @Operation(
      summary = "Update a Post by Id",
      description = "Updates a Post object by specifying its id. The response is Post object with" +
          " id, title, body and userId.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(
          implementation = Post.class), mediaType = "application/json")}),
      @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(
          implementation = ErrorMessageDTO.class), mediaType = "application/json")}),
      @ApiResponse(responseCode = "503", content = {@Content(schema = @Schema(
          implementation = ErrorMessageDTO.class), mediaType = "application/json")})})
  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Post> putPostById(@PathVariable(value = "id") Integer id,
                                          @RequestBody PostUpdateDTO inputPost) {
    Post post = postService.updatePostById(id, inputPost);
    return ResponseEntity.status(HttpStatus.OK).body(post);
  }

  @ExceptionHandler(PostNotFoundException.class)
  public ResponseEntity<ErrorMessageDTO> handlePostNotFoundException(PostNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorMessageDTO(exception.getMessage()));
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorMessageDTO> handleUserNotFoundException(UserNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorMessageDTO(exception.getMessage()));
  }

  @ExceptionHandler(ExternalAPIErrorException.class)
  public ResponseEntity<ErrorMessageDTO> handleExternalAPIErrorException(ExternalAPIErrorException exception) {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(new ErrorMessageDTO(exception.getMessage()));
  }

  @ExceptionHandler(NullInputException.class)
  public ResponseEntity<ErrorMessageDTO> handleNullInputException(NullInputException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorMessageDTO(exception.getMessage()));
  }

}
