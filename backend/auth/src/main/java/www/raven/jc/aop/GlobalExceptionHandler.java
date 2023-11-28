package www.raven.jc.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import www.raven.jc.result.CommonResult;

/**
 * @author 刘家辉
 * @date 2023/10/29
 */
@ControllerAdvice
@Component
@Slf4j
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(NullPointerException.class)
    public CommonResult<Void> handlerNullPointerException(NullPointerException ex) {
        log.error("空指针异常", ex);
        return CommonResult.operateFail("后端逻辑有误 请通知管理员");
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    public CommonResult<Void> handlerIllegalArgumentException(IllegalArgumentException ex) {
        log.error("用户信息或数据库异常", ex);
        return CommonResult.operateFail(ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public CommonResult<Void> handlerRuntimeException(RuntimeException ex) {
        log.error("运行时异常", ex);
        return CommonResult.operateFail("运行异常 请通知管理员");
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public CommonResult<Void> handlerException(Exception ex) {
        log.error("未知异常", ex);
        return CommonResult.operateFail("系统繁忙 请稍后再试");
    }
}