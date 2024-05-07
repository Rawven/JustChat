package www.raven.jc.controller;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import www.raven.jc.entity.bo.ChunkMergeBO;
import www.raven.jc.entity.bo.ChunkUploadBO;
import www.raven.jc.entity.bo.GetUrlBO;
import www.raven.jc.entity.bo.UploadBO;
import www.raven.jc.entity.vo.ChunkVO;
import www.raven.jc.entity.vo.GetUrlVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.FileService;

@RestController
@ResponseBody
@CrossOrigin
public class FileController {
    @Resource
    private FileService fileService;

    /**
     * 服务端直传
     *
     * @param file file
     * @param md5  md5
     * @return {@link CommonResult }<{@link Void }>
     */
    @PostMapping("/upload/{md5}")
    public CommonResult<Void> upload(
        @RequestParam("file") @NotNull MultipartFile file,
        @PathVariable("md5") @NotNull String md5) throws IOException {
        fileService.upload(UploadBO.builder().file(file).md5(md5).build());
        return CommonResult.operateSuccess("上传文件成功");
    }

    /**
     * 向客户端提供预签名链接以实现客户端直传
     *
     * @param getUrlBO get url bo
     * @return {@link CommonResult }<{@link GetUrlVO }>
     */
    @PostMapping("/getPresignedUrl")
    public CommonResult<GetUrlVO> getPresignedUrl(
        @RequestBody GetUrlBO getUrlBO) {
        return CommonResult.operateSuccess("获取预签名url成功", fileService.getPresignedUrl(getUrlBO));
    }

    /**
     * 返回客户端分片上传所需的预签名链接
     *
     * @param chunkUploadBO chunk upload bo
     * @return {@link CommonResult }<{@link ChunkVO }>
     */
    @PostMapping("/getPresignedUrlByChunk")
    public CommonResult<ChunkVO> getPresignedUrlByChunk(
        @RequestBody ChunkUploadBO chunkUploadBO) {
        return CommonResult.operateSuccess("请求分片上传文件成功", fileService.getPresignedUrlForChunk(chunkUploadBO));
    }

    /**
     * 客户端上传完分片后，服务端合并分片
     *
     * @param chunkMergeBO chunk merge bo
     * @return {@link CommonResult }<{@link Void }>
     */
    @PostMapping("/chunkMerge")
    public CommonResult<Void> chunkMerge(
        @RequestBody ChunkMergeBO chunkMergeBO) {
        fileService.chunkCompose(chunkMergeBO);
        return CommonResult.operateSuccess("合并文件成功");
    }

    @GetMapping("/{objectName}")
    public CommonResult<String> getUrl(
        @PathVariable("objectName") @NotNull String objectName) {
        return CommonResult.operateSuccess("获取url成功", fileService.getUrl(objectName));
    }
}
