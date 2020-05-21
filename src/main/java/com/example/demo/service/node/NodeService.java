package com.example.demo.service.node;


import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

public interface NodeService {
//    Connection  sshConnect();
//    void getSysMemInfo();
//    float getSysCpuInfo();
    void Connect();
    Map<String, Object>  getFromRedis();
    void  getInfo()throws Exception;
    boolean testConnect(String ip,String userName ,String password ,int port);

}






