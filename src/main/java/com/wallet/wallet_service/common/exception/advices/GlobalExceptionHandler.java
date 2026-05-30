package com.wallet.wallet_service.common.exception.advices;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.wallet.wallet_service.common.dto.ApiErrorDTO;
import com.wallet.wallet_service.common.exception.InsufficientFundsException;
import com.wallet.wallet_service.common.exception.InvalidCredentialsException;
import com.wallet.wallet_service.common.exception.InvalidPasswordException;
import com.wallet.wallet_service.common.exception.InvalidTransactionException;
import com.wallet.wallet_service.common.exception.SamePasswordException;
import com.wallet.wallet_service.common.exception.TransactionNotFoundException;
import com.wallet.wallet_service.common.exception.UserAlreadyExistsException;
import com.wallet.wallet_service.common.exception.UserNotFoundException;
import com.wallet.wallet_service.common.exception.WalletAlreadyExistsException;
import com.wallet.wallet_service.common.exception.WalletNotFoundException;


@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleInternalServerException(Exception e)
    {
        List<String> lstErrors = new ArrayList<>();
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO = ApiErrorDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .subErrors(lstErrors)
                .message(e.getMessage()).build();

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorDTO> handleUserAlreadyExistsException(UserAlreadyExistsException e)
    {
        List<String> lstErrors = new ArrayList<>();
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO = ApiErrorDTO.builder()
                .status(HttpStatus.CONFLICT)
                .subErrors(lstErrors)
                .message(e.getMessage()).build();

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorDTO> handleInvalidCredentialsException(InvalidCredentialsException e){
        List<String> lstErrors = new ArrayList<>();
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
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
        List<String> lstErrors = new ArrayList<>();
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
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
         ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
         apiErrorDTO = ApiErrorDTO.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getMessage()).build();

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiErrorDTO> handleInvalidPasswordException(InvalidPasswordException e)
    {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO = ApiErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage()).build();

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SamePasswordException.class)
    public ResponseEntity<ApiErrorDTO> handleSamePasswordException(SamePasswordException e)
    {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO = ApiErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage()).build();

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WalletAlreadyExistsException.class)
    public ResponseEntity<ApiErrorDTO> handleWalletAlreadyExistsException(WalletAlreadyExistsException e){
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO = ApiErrorDTO.builder()
                      .status(HttpStatus.CONFLICT)
                      .message(e.getMessage()).build();
        
        return new ResponseEntity<>(apiErrorDTO, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleWalletNotFoundException(WalletNotFoundException e){
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO = ApiErrorDTO.builder()
                     .status(HttpStatus.NOT_FOUND)
                     .message(e.getMessage()).build();
        
        return new ResponseEntity<>(apiErrorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ApiErrorDTO> handleInsufficientFundsException(InsufficientFundsException e){
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO = ApiErrorDTO.builder()
                      .status(HttpStatus.BAD_REQUEST)
                      .message(e.getMessage()).build();
        return new ResponseEntity<>(apiErrorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleTransactionNotFoundException(TransactionNotFoundException e){
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO = ApiErrorDTO.builder()
                      .status(HttpStatus.NOT_FOUND)
                      .message(e.getMessage()).build();
        return new ResponseEntity<>(apiErrorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ApiErrorDTO> handleInvalidTransactionException(InvalidTransactionException e){
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO = ApiErrorDTO.builder()
                      .status(HttpStatus.BAD_REQUEST)
                      .message(e.getMessage()).build();
        return new ResponseEntity<>(apiErrorDTO, HttpStatus.BAD_REQUEST);
    }
}


