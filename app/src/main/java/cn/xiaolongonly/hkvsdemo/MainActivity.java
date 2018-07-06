package cn.xiaolongonly.hkvsdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.standards.libhikvision.bean.AccountInfo;
import com.standards.libhikvision.bean.MonitorInfo;
import com.standards.libhikvision.bean.ResourceNode;
import com.standards.libhikvision.ui.IndexActivity;



/**
 * @author CRAWLER
 */
public class MainActivity extends AppCompatActivity {
    private MonitorInfo monitorInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        monitorInfo = new MonitorInfo();
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.userName = "ur user name";
        accountInfo.url = "ur host address";
        accountInfo.password = "ur password";
        monitorInfo.setAccountInfo(accountInfo);
        ResourceNode resourceNode = new ResourceNode();
        resourceNode.node_type = 2;
        resourceNode.id = "1";
        resourceNode.name = "福建省厦门市XXXXX站点";
        monitorInfo.setResourceNode(resourceNode);
        Intent intent = new Intent(this, IndexActivity.class);
        intent.putExtra("monitorInfo", monitorInfo);
        startActivity(intent);
        finish();
    }
}
