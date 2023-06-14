package cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import io.github.doocs.im.ImClient;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.FreudApp;
import org.ecnusmartboys.infrastructure.config.CosConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;

@Slf4j
@SpringBootTest(classes = FreudApp.class)
@ActiveProfiles({"local", "test"})
@Transactional
public class CosTest {

    @Resource
    CosConfig cosConfig;

    COSClient cosClient;

    @BeforeEach
    public void init() {
        cosClient = cosConfig.cosClient();
    }

    @Test
    public void testSendImage() {
        String cosBucketDomain = "https://freud-1311238733.cos.ap-shanghai.myqcloud.com/";
        String uploadPath = "images/";
        String fileName = "1.png";
        String completePath = cosBucketDomain + uploadPath + fileName;

        File imageFile = new File("src/main/resources/static/1.png");


        // 创建上传请求
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.cosBucket(), uploadPath + fileName, imageFile);

        // 执行上传
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);

        // 处理上传结果
        if (putObjectResult != null) {
            // 上传成功
            System.out.println("Image uploaded successfully. Access URL: " + completePath);
        } else {
            // 上传失败
            System.out.println("Failed to upload image.");
        }

    }

}
