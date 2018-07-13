import java.io.*;
import java.util.*;

public enum StatusCode {
	OK(200, "OK"),
	NOT_FOUND (404, "Not Found"),
  METHOD_NOT_ALLOWED (405, "Method Not Allowed"),
  CONFLICT (409, "Conflict"),
  INTERNAL_SERVER_ERROR (500, "Internal Server Error");

  private final int code;
  private final String description;

  StatusCode(int code, String description) {
  	this.code = code;
  	this.description = description;
  }

  // public String getError() {
  // 	return description;
  // }
}