package www.raven.jc.result;

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
    boolean isSuccess;
    private Integer code;
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

    public static CommonResult<Void> operateFailure(String message) {
        return new CommonResult<>(ResultCode.FAIL_CODE, false, message);
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

    public static <T> CommonResult<T> operateFailure(String message, T data) {
        return new CommonResult<>(
                ResultCode.FAIL_CODE,
                false,
                message,
                data
        );
    }

    public static <T> CommonResult<T> operateFailure(Integer code, String message) {
        return new CommonResult<>(
                code,
                false,
                message
        );
    }

    public static <T> CommonResult<T> operateFailure(Integer code, String message, T data) {
        return new CommonResult<>(
                code,
                false,
                message,
                data
        );
    }
}
