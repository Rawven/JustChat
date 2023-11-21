package Raven.example.result;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * common result
 *
 * @author 刘家辉
 * @date 2023/11/21
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CommonResult<T> {
    private Integer code;

    boolean isSuccess;

    private String message;

    private T data;

    private CommonResult(Integer code, boolean isSuccess, String message) {
        this.code = code;
        this.isSuccess = isSuccess;
        this.message = message;
    }

    private CommonResult(Integer code, boolean isSuccess, String message, T data) {
        this.code = code;
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }

    public static CommonResult<Void> operateFailWithMessage(String message) {
        return new CommonResult<>(ResultCode.FAIL_CODE, false, message);
    }

    public static CommonResult<Void> operateFailDueToToken(Integer code, String message) {
        return new CommonResult<>(code, false, message);
    }

    public static CommonResult<Void> operateSuccess(String message) {
        return new CommonResult<>(ResultCode.SUCCESS_CODE, true, message);
    }

    public static <T> CommonResult<T> operateSuccess(String message, T data) {
        return new CommonResult<>(
                ResultCode.SUCCESS_CODE,
                true,
                message,
                data
        );
    }
}