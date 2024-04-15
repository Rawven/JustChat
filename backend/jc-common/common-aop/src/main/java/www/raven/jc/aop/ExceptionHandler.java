package www.raven.jc.aop;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import www.raven.jc.result.CommonResult;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 刘家辉
 * @date 2023/10/29
 */
@ControllerAdvice
@Component
@Slf4j
public class ExceptionHandler {

    /**
     * bind exception handler
     * 处理 form data方式调用接口校验失败抛出的异常
     *
     * @param e e
     * @return {@link CommonResult}<{@link Void}>
     */
    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(BindException.class)
    public CommonResult<Void> bindExceptionHandler(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return CommonResult.operateFailure("请求参数异常：" + String.join(",", collect));
    }

    /**
     * method argument not valid exception handler
     * <2> 处理 json 请求体调用接口校验失败抛出的异常
     *
     * @param e e
     * @return {@link CommonResult}<{@link Void}>
     */
    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<Void> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return CommonResult.operateFailure("请求参数异常：" + String.join(",", collect));
    }

    /**
     * constraint violation exception handler
     * <3> 处理单个参数校验失败抛出的异常
     *
     * @param e e
     * @return {@link CommonResult}<{@link Void}>
     */
    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    public CommonResult<Void> constraintViolationExceptionHandler(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        return CommonResult.operateFailure("请求参数异常：" + String.join(",", collect));
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(NullPointerException.class)
    public CommonResult<Void> handlerNullPointerException(NullPointerException ex) {
        log.error("空指针异常", ex);
        return CommonResult.operateFailure(ex.getMessage());
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public CommonResult<Void> handlerIllegalArgumentException(IllegalArgumentException ex) {
        return CommonResult.operateFailure(ex.getLocalizedMessage());
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(RuntimeException.class)
    public CommonResult<Void> handlerRuntimeException(RuntimeException ex) {
        log.error("运行时异常", ex);
        return CommonResult.operateFailure(ex.getMessage());
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public CommonResult<Void> handlerException(Exception ex) {
        log.error("未知异常", ex);
        return CommonResult.operateFailure(ex.getLocalizedMessage());
    }
}