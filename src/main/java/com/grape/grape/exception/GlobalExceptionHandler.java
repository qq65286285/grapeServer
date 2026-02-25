package com.grape.grape.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grape.grape.model.Resp;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 用于捕获并处理所有异常，将详细的错误信息返回给前端
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理SQL异常
     */
    @ExceptionHandler(SQLException.class)
    public Resp handleSQLException(SQLException e) {
        log.error("SQL异常: SQLState={}, ErrorCode={}, Message={}",
            e.getSQLState(), e.getErrorCode(), e.getMessage(), e);
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorType", "SQLException");
        errorData.put("sqlState", e.getSQLState());
        errorData.put("errorCode", e.getErrorCode());
        errorData.put("detailedMessage", e.getMessage());
        errorData.put("stackTrace", getStackTraceAsString(e));
        
        Resp resp = new Resp();
        resp.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        resp.setMessage("数据库操作失败");
        resp.setData(errorData);
        
        return resp;
    }

    /**
     * 处理SQL语法错误
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    public Resp handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error("SQL语法错误: {}", e.getMessage(), e);
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorType", "BadSqlGrammarException");
        errorData.put("detailedMessage", e.getMessage());
        errorData.put("stackTrace", getStackTraceAsString(e));
        
        Resp resp = new Resp();
        resp.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        resp.setMessage("SQL语法错误");
        resp.setData(errorData);
        
        return resp;
    }

    /**
     * 处理数据重复异常（唯一约束冲突）
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Resp handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("数据重复异常: {}", e.getMessage(), e);
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorType", "DuplicateKeyException");
        errorData.put("detailedMessage", e.getMessage());
        errorData.put("stackTrace", getStackTraceAsString(e));
        
        Resp resp = new Resp();
        resp.setCode(HttpStatus.CONFLICT.value());
        resp.setMessage("数据重复，违反唯一约束");
        resp.setData(errorData);
        
        return resp;
    }

    /**
     * 处理数据完整性异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Resp handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("数据完整性错误: {}", e.getMessage(), e);
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorType", "DataIntegrityViolationException");
        errorData.put("detailedMessage", e.getMessage());
        errorData.put("stackTrace", getStackTraceAsString(e));
        
        Resp resp = new Resp();
        resp.setCode(HttpStatus.BAD_REQUEST.value());
        resp.setMessage("数据完整性错误");
        resp.setData(errorData);
        
        return resp;
    }

    /**
     * 处理数据访问异常
     */
    @ExceptionHandler(DataAccessException.class)
    public Resp handleDataAccessException(DataAccessException e) {
        log.error("数据访问异常: {}", e.getMessage(), e);
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorType", "DataAccessException");
        errorData.put("detailedMessage", e.getMessage());
        errorData.put("stackTrace", getStackTraceAsString(e));
        
        Resp resp = new Resp();
        resp.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        resp.setMessage("数据访问异常");
        resp.setData(errorData);
        
        return resp;
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Resp handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数校验失败: {}", e.getMessage(), e);
        Map<String, Object> errorData = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        errorData.put("errorType", "MethodArgumentNotValidException");
        errorData.put("errors", errors);
        errorData.put("detailedMessage", e.getMessage());
        
        Resp resp = new Resp();
        resp.setCode(HttpStatus.BAD_REQUEST.value());
        resp.setMessage("参数校验失败");
        resp.setData(errorData);
        
        return resp;
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Resp handleBindException(BindException e) {
        log.error("参数绑定失败: {}", e.getMessage(), e);
        Map<String, Object> errorData = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        errorData.put("errorType", "BindException");
        errorData.put("errors", errors);
        errorData.put("detailedMessage", e.getMessage());
        
        Resp resp = new Resp();
        resp.setCode(HttpStatus.BAD_REQUEST.value());
        resp.setMessage("参数绑定失败");
        resp.setData(errorData);
        
        return resp;
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public Resp handleException(Exception e) {
        log.error("未处理的异常: 类型={}, 消息={}", e.getClass().getSimpleName(), e.getMessage(), e);
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorType", e.getClass().getSimpleName());
        errorData.put("detailedMessage", e.getMessage());
        errorData.put("stackTrace", getStackTraceAsString(e));
        
        Resp resp = new Resp();
        resp.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        resp.setMessage("服务器内部错误");
        resp.setData(errorData);
        
        return resp;
    }

    /**
     * 将异常堆栈信息转换为字符串
     */
    private String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
