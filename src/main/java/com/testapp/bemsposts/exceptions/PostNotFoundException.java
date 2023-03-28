package com.testapp.bemsposts.exceptions;

public class PostNotFoundException extends RuntimeException{
  public PostNotFoundException(String s) {
    super(s);
  }
}
