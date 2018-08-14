package cn.xiaolongonly.hkvsdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.standards.libhikvision.bean.AccountInfo;
import com.standards.libhikvision.bean.HkCameraInfo;
import com.standards.libhikvision.bean.MonitorInfo;
import com.standards.libhikvision.bean.ResourceNode;
import com.standards.libhikvision.ui.IndexActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * 此模块仅针对8700视频设备
 * @author CRAWLER
 */
public class MainActivity extends AppCompatActivity {
    private MonitorInfo monitorInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startVideoActivity();
        finish();
    }


    /**
     * 播放需要登录所以需要帐号信息。
     * 获取摄像头和底下节点 是因为海康在后端接口中和APP接口中获取到的节点id是不一致的
     * 如果只获取某个节点，将获取不到底下的摄像头信息，这边进行的处理是获取节点和底下的摄像头信息，来获得摄像头id
     */
    private void startVideoActivity() {
        MonitorInfo monitorInfo = new MonitorInfo();
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.userName = "ur user name";
        accountInfo.url = "ur host address";
        accountInfo.password = "ur password";
        monitorInfo.setAccountInfo(accountInfo);
        ResourceNode resourceNode = new ResourceNode();
        resourceNode.node_type = 2;
        resourceNode.id = "85d7e662e91c4b6dafdf47979a38250d";
        resourceNode.name = "福建省厦门市XXXXX站点";
        List<HkCameraInfo> cameraInfos = new ArrayList<>();
        cameraInfos.add(new HkCameraInfo("85d7e662e91c4b6dafdf47979a38250d", "测试1"));
        cameraInfos.add(new HkCameraInfo("60555b1f4e1c4b1eb96ae014857d5478", "测试2"));
        resourceNode.cameras = cameraInfos;
        monitorInfo.setResourceNode(resourceNode);
        Intent intent = new Intent(this, IndexActivity.class);
        intent.putExtra("monitorInfo", monitorInfo);
        startActivity(intent);
    }
}
