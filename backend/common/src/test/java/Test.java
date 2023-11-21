import cn.hutool.core.io.file.FileReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;


@RunWith(org.springframework.test.context.junit4.SpringRunner.class)
@Slf4j
public class Test {
    @org.junit.Test
    public void test(){
        log.info(new FileReader("common/src/main/resources/key.txt").readString());
    }
}
