package com.wallet.wallet_service.common.exception.advices;

import com.wallet.wallet_service.common.exception.*;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.wallet.wallet_service.common.dto.ApiErrorDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler
{
    List<String> lstErrors = new ArrayList<>();
    ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleInternalServerException(Exception e)
    {
        apiErrorDTO = ApiErrorDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .subErrors(lstErrors)
                .message(e.getMessage()).build();

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorDTO> handleUserAlreadyExistsException(UserAlreadyExistsException e)
    {
        apiErrorDTO = ApiErrorDTO.builder()
                .status(HttpStatus.CONFLICT)
                .subErrors(lstErrors)
                .message(e.getMessage()).build();

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorDTO> handleInvalidCredentialsException(InvalidCredentialsException e){
        apiErrorDTO = ApiErrorDTO.builder()
                    .subErrors(lstErrors)
                    .message(e.getMessage())
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
                    
        return new ResponseEntity<>(apiErrorDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        lstErrors =  e.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        apiErrorDTO = ApiErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Input Validation Failed")
                .subErrors(lstErrors).build();
        return new ResponseEntity<>(apiErrorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleUserNotFoundException(UserNotFoundException e)
    {
         apiErrorDTO = ApiErrorDTO.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getMessage()).build();

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiErrorDTO> handleInvalidPasswordException(InvalidPasswordException e)
    {
        apiErrorDTO = ApiErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage()).build();

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SamePasswordException.class)
    public ResponseEntity<ApiErrorDTO> handleSamePasswordException(SamePasswordException e)
    {
        apiErrorDTO = ApiErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage()).build();

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.BAD_REQUEST);
    }
}


