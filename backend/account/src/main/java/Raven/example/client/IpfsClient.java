package Raven.example.client;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

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
@Configuration
public class IpfsClient {
    @Value("${ipfs.server-addr")
    private String serverAddr;

    @Value("${ipfs.gateway")
    private String gateway;

    private static IPFS ipfsClient;

    @PostConstruct
    public void init(){
        ipfsClient = new IPFS(serverAddr);
    }

    /**
     * 上传文件到ipfs
     *
     * @param file 文件
     * @return 返回cid
     * @throws IOException 文件传输异常
     */
    public String upload(MultipartFile file){
        if(file.isEmpty()){
            throw new RuntimeException("文件为空");
        }
        String fileName = file.getOriginalFilename();
        if(StringUtils.isEmpty(fileName)){
            throw new RuntimeException("文件名为空");
        }
        // 添加标识
        int index = fileName.lastIndexOf('.');
        File realFile = addIdentification(file, fileName.substring(0, index), fileName.substring(index));

        String cid = upload(realFile);
        if(StringUtils.isEmpty(cid)){
            throw new RuntimeException("cid为空,上传失败");
        }
        return cid;
    }

    /**
     * 获取http访问地址
     * @param cid 文件cid
     * @return 返回uri
     */
    public String getUri(String cid){
        return gateway + "/ipfs/" + cid;
    }

    /**
     * 添加唯一标识
     *
     * @param prefix 文件名缀
     * @param suffix 文件后缀
     * @param file 原文件
     * @return 文件
     * @throws IOException 文件传输异常
     */
    private File addIdentification(MultipartFile file, String prefix, String suffix){
        File f = null;
        try {
            f = File.createTempFile(prefix + System.currentTimeMillis(),suffix);
            file.transferTo(f);
        } catch (IOException e) {
            throw new RuntimeException("文件添加唯一标识失败");
        }
        return f;
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return cid
     * @throws IOException 文件传输异常
     */
    private String upload(File file){
        NamedStreamable.FileWrapper fileBytes = new NamedStreamable.FileWrapper(file);
        MerkleNode addResult = null;
        try {
            addResult = ipfsClient.add(fileBytes).get(0);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败");
        }
        return addResult.hash.toString();
    }

    /**
     * 移除文件
     * @param cid 文件cid
     */
    public void remove(String cid){
        try {
            ipfsClient.pin.rm(Multihash.fromBase58(cid));
        } catch (IOException e) {
            throw new RuntimeException("文件移除失败");
        }
    }
}
