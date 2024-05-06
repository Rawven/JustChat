package www.raven.jc.controller;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.config.MinioConfig;
import www.raven.jc.entity.bo.GetUrlBO;
import www.raven.jc.entity.vo.GetUrlVO;
import www.raven.jc.result.CommonResult;

/**
 * upload controller
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@RestController
@ResponseBody
public class FileController {
    @Autowired
    private MinioConfig minioConfig;

    @PostMapping("/getPreSignedUrl")
    public CommonResult<GetUrlVO> getPreSignedUrl(
        @RequestBody GetUrlBO bo) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String url = minioConfig.minioClient().getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
            .bucket(minioConfig.bucket)
            .object(bo.getMd5())
            .method(Method.PUT)
            .expiry(3600)
            .build());
        return CommonResult.operateSuccess("success", new GetUrlVO()
            .setUploadUrl(url)
            .setPreviewUrl(minioConfig.endpoint + '/' + minioConfig.bucket + '/' + bo.getMd5())
        );
    }
}
