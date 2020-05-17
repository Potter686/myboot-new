package com.example.demo.service.node;


import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

public interface NodeService {
    Connection  sshConnect();
    void getSysMemInfo();
    Map<String, Object> getSysCpuInfo(Connection connection);

    List<String> getFromRedis();

}






