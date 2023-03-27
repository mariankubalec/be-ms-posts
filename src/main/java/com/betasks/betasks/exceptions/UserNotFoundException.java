package com.betasks.betasks.exceptions;

public class UserNotFoundException extends RuntimeException{
  public UserNotFoundException(String s) {
    super(s);
  }
}
