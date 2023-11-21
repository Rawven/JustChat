package Raven.example.controller;

import Raven.example.client.IpfsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * file upload controller
 *
 * @author 刘家辉
 * @date 2023/11/21
 */
@RestController
@ResponseBody
@RequestMapping("/ipfs")
public class FileUploadController {
    @Autowired
    private IpfsClient client;

    @GetMapping("/upload")
    public String upload(MultipartFile file){
        return client.upload(file);
    }
}
