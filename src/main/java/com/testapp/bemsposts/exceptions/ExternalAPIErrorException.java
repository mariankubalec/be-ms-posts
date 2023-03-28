package com.testapp.bemsposts.exceptions;

public class ExternalAPIErrorException extends RuntimeException{
  public ExternalAPIErrorException(String s) {
    super(s);
  }
}
