package com.testapp.bemsposts.exceptions;

public class UserNotFoundException extends RuntimeException{
  public UserNotFoundException(String s) {
    super(s);
  }
}
