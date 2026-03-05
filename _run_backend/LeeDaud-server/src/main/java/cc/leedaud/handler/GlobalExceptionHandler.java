package cc.leedaud.handler;

import cc.leedaud.constant.MessageConstant;
import cc.leedaud.exception.BaseException;
import cc.leedaud.exception.BlockedException;
import cc.leedaud.exception.GuestReadOnlyException;
import cc.leedaud.exception.TokenException;
import cc.leedaud.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 鍏ㄥ眬寮傚父澶勭悊鍣紝澶勭悊椤圭洰涓姏鍑虹殑涓氬姟寮傚父
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 鎹曡幏涓氬姟寮傚父
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("涓氬姟寮傚父锛歿}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result exceptionHandler(TokenException ex){
        log.error("浠ょ墝寮傚父锛歿}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result exceptionHandler(BlockedException ex){
        log.error("灏佺寮傚父锛歿}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result exceptionHandler(GuestReadOnlyException ex){
        log.error("娓稿鍙寮傚父锛歿}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 鎹曡幏鍙傛暟鏍￠獙寮傚父锛園Valid鏍￠獙澶辫触锛?     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(MethodArgumentNotValidException ex){
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.error("鍙傛暟鏍￠獙寮傚父锛歿}", errorMsg);
        return Result.error(errorMsg);
    }

    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        if(message.contains("Duplicate entry")){
            String [] split = message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.ALREADY_EXIST;
            return Result.error(msg);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

    /**
     * 璇锋眰鏂规硶涓嶆敮鎸佸紓甯?     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result exceptionHandler(HttpRequestMethodNotSupportedException ex){
        log.error("璇锋眰鏂规硶涓嶆敮鎸侊細{}", ex.getMessage());
        return Result.error("涓嶆敮鎸佺殑璇锋眰鏂规硶锛? + ex.getMethod());
    }

    /**
     * 缂哄皯璇锋眰鍙傛暟寮傚父
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(MissingServletRequestParameterException ex){
        log.error("缂哄皯璇锋眰鍙傛暟锛歿}", ex.getMessage());
        return Result.error("缂哄皯蹇呰鍙傛暟锛? + ex.getParameterName());
    }

    /**
     * 鏂囦欢涓婁紶澶у皬瓒呴檺寮傚父
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(MaxUploadSizeExceededException ex){
        log.error("鏂囦欢涓婁紶澶у皬瓒呴檺锛歿}", ex.getMessage());
        return Result.error("涓婁紶鏂囦欢澶у皬瓒呰繃闄愬埗");
    }

    /**
     * 鍏滃簳寮傚父澶勭悊
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result exceptionHandler(Exception ex){
        log.error("鏈煡寮傚父锛?, ex);
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

}

