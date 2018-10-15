package chriswow333.fileapi.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalHandler {
	
	private static final Logger globalExceptionLogger = LogManager.getLogger(GlobalHandler.class);

	@ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public final ResponseEntity<Map<String,String>> handleAllExceptions(Exception ex, WebRequest request) {
		ex.printStackTrace();
		globalExceptionLogger.error("",ex);
		Map<String, String> map = new HashMap<>();
		map.put("message", "Under maintenance, please try later.");
		return new ResponseEntity<Map<String,String>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(value = {NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
	public final ResponseEntity<Map<String,String>> handleResourceNotFoundExceptions(Exception ex, WebRequest request) {
		ex.printStackTrace();
		Map<String, String> map = new HashMap<>();
		map.put("message", "resource not found.");
		return new ResponseEntity<Map<String,String>>(map, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public final ResponseEntity<Map<String,String>> handleMethodNotAllowedExceptions(Exception ex, WebRequest request) {
		ex.printStackTrace();
		Map<String, String> map = new HashMap<>();
		map.put("message", "method not allowed.");
		return new ResponseEntity<Map<String,String>>(map, HttpStatus.METHOD_NOT_ALLOWED);
	}

}
