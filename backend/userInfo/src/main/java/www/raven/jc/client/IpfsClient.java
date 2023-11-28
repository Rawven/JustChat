package www.raven.jc.client;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import www.raven.jc.exception.IpfsException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;


/**
 * ipfs client
 *
 * @author 刘家辉
 * @date 2023/11/21
 */
@RefreshScope
public class IpfsClient {

    private static IPFS ipfsClient;

    public IpfsClient(String api) {
        ipfsClient = new IPFS(api);
    }
    /**
     * 上传文件到ipfs
     *
     * @param file 文件
     * @return 返回cid
     */
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IpfsException("文件为空");
        }
        String fileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(fileName)) {
            throw new IpfsException("文件名为空");
        }
        // 添加标识
        int index = fileName.lastIndexOf('.');
        File realFile = addIdentification(file, fileName.substring(0, index), fileName.substring(index));

        String cid = upload(realFile);
        if (StringUtils.isEmpty(cid)) {
            throw new IpfsException("cid为空,上传失败");
        }
        return cid;
    }

    /**
     * add identification
     * 添加唯一标识
     *
     * @param prefix 文件名缀
     * @param suffix 文件后缀
     * @param file   原文件
     * @return 文件
     */
    private File addIdentification(MultipartFile file, String prefix, String suffix) {
        File f;
        try {
            f = File.createTempFile(prefix + System.currentTimeMillis(), suffix);
            file.transferTo(f);
        } catch (IOException e) {
            throw new IpfsException("文件添加唯一标识失败");
        }
        return f;
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return cid
     */
    private String upload(File file) {
        NamedStreamable.FileWrapper fileBytes = new NamedStreamable.FileWrapper(file);
        MerkleNode addResult;
        try {
            addResult = ipfsClient.add(fileBytes).get(0);
        } catch (IOException e) {
            throw new IpfsException("文件上传失败");
        }
        return addResult.hash.toString();
    }

    /**
     * 移除文件
     *
     * @param cid 文件cid
     */
    public void remove(String cid) {
        try {
            ipfsClient.pin.rm(Multihash.fromBase58(cid));
        } catch (IOException e) {
            throw new IpfsException("文件删除失败");
        }
    }
}
