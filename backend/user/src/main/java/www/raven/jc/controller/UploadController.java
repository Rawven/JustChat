package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import www.raven.jc.client.IpfsClient;
import www.raven.jc.result.CommonResult;

import javax.servlet.http.HttpServletRequest;

/**
 * upload controller
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@RestController
@ResponseBody
public class UploadController {
    @Autowired
    private IpfsClient ipfsClient;
    @PostMapping("/upload")
    public CommonResult<String> upload(@RequestParam("file") MultipartFile profile) {
        String upload = ipfsClient.upload(profile);
        return CommonResult.operateSuccess("上传成功", upload);
    }
}