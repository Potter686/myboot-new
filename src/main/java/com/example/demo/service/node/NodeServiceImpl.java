package com.example.demo.service.node;
import java.io.*;

import com.alibaba.fastjson.JSONObject;

import com.example.demo.entity.NodeInfo;
import com.example.demo.repository.NodeInfoReposotory;
import com.example.demo.service.docker.DockerService;
import com.sun.scenario.effect.impl.sw.java.JSWBlend_SRC_OUTPeer;
import org.omg.CORBA.ShortSeqHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.*;

import java.text.SimpleDateFormat;
import java.util.*;



import com.jcraft.jsch.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;


import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.apache.log4j.Logger;


@Service
public class NodeServiceImpl  implements NodeService {

    //    @Value("${docker.}")
//    private String nodeIp;
//
//    @Value("${node.user}")
//    private String username;
//
//    @Value("${node.password}")
//    private String password;
//
//    private int CPU_IDLE=0;
//    int processStatus =0;
//
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private NodeInfoReposotory nodeInfoReposotory;

    @Autowired
    private DockerService dockerService;

    private List<Session> sessions = new ArrayList<>();


    private int MEM_INFO = 0;



    public void Connect()  {
        List<NodeInfo> nodeInfos = nodeInfoReposotory.findAll();
        for (NodeInfo nodeInfo : nodeInfos) {
            System.out.println(nodeInfo);
            JSch jSch = new JSch();
            String userName = nodeInfo.getUserName();
            String password = nodeInfo.getPassword();
            String nodeIp = nodeInfo.getIp();
            try {
                Session session = jSch.getSession(userName, nodeIp);
                session.setPassword(password);
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();
                sessions.add(session);
            }catch (Exception e){
                System.out.println("连接错误");
            }

        }


    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void getInfo() throws Exception {
        if (sessions.size()<1){
            this.Connect();
        }

        String cmd = "cat /proc/meminfo |grep 'MemTotal\\|MemFree'|awk '{print $2}'; vmstat|awk 'NR==3''{print $13, $14, $16, $15}';date +'%Y-%m-%d %T%n'";

//        String cmd = "cat /proc/meminfo |grep 'MemTotal\\|MemFree'|awk '{print $2}'";
//        String cmd="vmstat|awk 'NR==3''{print $13, $14, $16, $15}'";
        String line = "";
        for (Session session : sessions) {
            Map<String, Object> map = new HashMap<>();
            List<String> results = new ArrayList<>();
            String hosts = session.getHost();
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    channel.getInputStream()));
            channel.setCommand(cmd);
            channel.connect();
            while ((line = in.readLine()) != null) {
                results.add(line);
            }
            double memTotal = Double.parseDouble(results.get(0));
            double memFree = Double.parseDouble(results.get(1));
            double memUsed = (memTotal-memFree)/memTotal;
            map.put("memTotal",memTotal/1024/1024);
            map.put("memFree",memFree/1024/1024);
            map.put("memUsed",memUsed);
            String[] cpuInfos = results.get(2).toString().split(" ");
            map.put("cpuUser",Double.parseDouble(cpuInfos[0])/100);
            map.put("cpuSys",Double.parseDouble(cpuInfos[1])/100);
            map.put("cpuWait",Double.parseDouble(cpuInfos[2])/100);
            map.put("cpuIdle",Double.parseDouble(cpuInfos[3])/100);
            map.put("date",results.get(3));

            JSONObject jsonpObject = new JSONObject();
            jsonpObject.put("data",map);

            stringRedisTemplate.opsForList().rightPush(hosts, jsonpObject.toJSONString());
            Long stringValueLength = stringRedisTemplate.opsForList().size(hosts);
            if (stringValueLength !=null && stringValueLength > 20){
                stringRedisTemplate.opsForList().leftPop(hosts);
            }
            Long stringValueLength1 = stringRedisTemplate.opsForList().size(hosts);
            map.clear();
        }
    }

        public Map<String, Object> getFromRedis(){
            Map<String, Object> map = new HashMap<>();
        List<NodeInfo> nodeInfos = nodeInfoReposotory.findAll();
        for (NodeInfo nodeInfo:nodeInfos){
            String nodeIp = nodeInfo.getIp();
            map.put(nodeInfo.getIp().toString(),stringRedisTemplate.opsForList().range(nodeIp,0,-1));
        }

        return map;

    }

    public boolean testConnect(String ip,String userName ,String password ,int port) {
        System.out.println(ip);
        System.out.println(userName);
        System.out.println(password);
        boolean status = dockerService.isHostConnectable(ip,port);
        System.out.println(status);
        if (!status){
            System.out.println("无法连接");
            return false;
        }
        try {
            JSch jSch = new JSch();
            Session session = jSch.getSession(userName, ip);
            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(80000);
            session.connect();
            session.disconnect();
            System.out.println("添加成功");
            return true;

        }catch (Exception e){
            System.out.println("错误");
        }
        return false;

    }



//        try {
//             Session session = jSch.getSession(userName,ip, 22);
////            session.setConfig(
////                    "PreferredAuthentications",
////                    "publickey,keyboard-interactive,password");
////            session.setConfig("PreferredAuthentications","password");
//            session.setConfig("StrictHostKeyChecking","no");
//            session.setPassword(password);
////            session.connect(100000);   // making a connection with timeout.
//            if(session.isConnected()){
//                System.out.println("session connect success");
//            }else{
//                System.out.println("session connect fail");
//            }
//        } catch (JSchException e) {
//            e.printStackTrace();
//        }
//        return  false;
//    }




}

//ssh 连接集群
//    public void  sshConnect(String newNodeIp ,String name ,String pass){
////        Connection conn = null;
////        boolean isAuthenticated = false;
////        try {
////            conn = new Connection(newNodeIp); // hostname 你要远程登录的主机IP地址,如10.0.2.1
////            conn.connect();
////            isAuthenticated = conn.authenticateWithPassword(name, pass);
////            if (isAuthenticated){
//////                Session session = conn.openSession();
//////                this.getSysCpuInfo(conn);
////                return conn;
////
////            }
////        }catch (Exception e){
////            System.out.println("连接失败");
////        }
////        return null;
//
//        try{
//            JSch jsch=new JSch();
//
//            //jsch.setKnownHosts("/home/foo/.ssh/known_hosts");
//
//            String host=null;
//            Session session=jsch.getSession(name, newNodeIp, 22);
//
//            String passwd = JOptionPane.showInputDialog("123456");
//            session.setPassword(passwd);
//
//            session.connect(30000);   // making a connection with timeout.
//
//        }
//        catch(Exception e){
//            System.out.println(e);
//        }
//    }
//}

//    public Connection sshConnect(){
//        Connection conn = null;
//        boolean isAuthenticated = false;
//        try {
//            conn = new Connection("192.168.142.130"); // hostname 你要远程登录的主机IP地址,如10.0.2.1
//            conn.connect();
//            isAuthenticated = conn.authenticateWithPassword("root", "123456");
//            if (isAuthenticated){
////                Session session = conn.openSession();
////                this.getSysCpuInfo(conn);
//                return conn;
//
//            }
//        }catch (Exception e){
//            System.out.println("连接失败");
//        }
//        return null;
//
//
//    }

//
////获取节点的内存信息
//    @Scheduled(cron = "0/5 * * * * ?")    //定时执行 每5s执行一次
//    public void getSysMemInfo()  {
//        Connection connection = this.sshConnect();
//
//        Map<String, Object> map = new HashMap<String, Object>();
//        InputStream is = null;
//        BufferedReader brStat = null;
//        StringTokenizer tokenStat=  null ;
//        String line = "";
//        int i=0,j=0,cpuidle=0;
//        long totalMem = 0, freeMem = 0;
//        float memUsage = 0.0f;
///**
// * 对于执行linux shell.
// *
// */
//        Session sess = null;
//        try {
//            sess=connection.openSession();
//        }catch (Exception e){
//            System.out.println("连接错误");
//        }
//        if (sess!=null){
//        try{
//            sess.execCommand("cat /proc/meminfo ");
//            is = new StreamGobbler(sess.getStdout());
//            brStat = new BufferedReader(new InputStreamReader(is));
//
//            while((line = brStat.readLine()) != null){
//                String[] memInfo = line.split("\\s+");
//                if(memInfo[0].startsWith("MemTotal")){
//                    totalMem = Long.parseLong(memInfo[1]);
//                }
//                if(memInfo[0].startsWith("MemFree")){
//                    freeMem = Long.parseLong(memInfo[1]);
//                }
//
////                float cpuUsage = this.getSysCpuInfo();
////                System.out.println("cpuUsage:"+cpuUsage);
//
//                memUsage = 1- (float)freeMem/(float)totalMem;
//                map.put("memUsage",memUsage);
//                map.put("totalMem",totalMem);
//                map.put("freeMem",(freeMem/1024.0/1024.0));
////                map.put("cpuUsage",cpuUsage);
//            }
//
//        }catch(Exception e){
//            System.out.println("测试失败");
//        }
//        }
//
//        Date dNow = new Date( );
//        SimpleDateFormat ft =
//                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        JSONObject jsonpObject = new JSONObject();
//        jsonpObject.put("data",map);
//        jsonpObject.put("date",ft.format(dNow));
//
//        stringRedisTemplate.opsForList().rightPush("redis_data", jsonpObject.toJSONString());
//
//        Long stringValueLength = stringRedisTemplate.opsForList().size("redis_data");
//        System.out.println("长度:"+stringValueLength);
//        if (stringValueLength !=null && stringValueLength > 20){
//            stringRedisTemplate.opsForList().leftPop("redis_data");
//        }
//        Long stringValueLength1 = stringRedisTemplate.opsForList().size("redis_data");
//        System.out.println("长度1:"+stringValueLength1);
//
////        List<String> list = template.opsForList().range("redis_data", 0, -1);
////        System.out.println(list);
////        return map;
//    }
//
//    public List<String> getFromRedis(){
//            return stringRedisTemplate.opsForList().range("redis_data",0,-1);
//        }




//
//
//    public  float getSysCpuInfo(){
//        Connection connection =this.sshConnect();
//        Session sess = null;
//        Map<String, Object> map = new HashMap<String, Object>();
//        try {
//             sess=connection.openSession();
//        }catch (Exception e){
//            System.out.println("连接错误");
//        }
//        if (sess!=null){
//        InputStream is = null;
//        InputStream is1 = null;
//        BufferedReader brStat = null, brStat1=null;
//        String line = "",line1="";
//        long idleCpuTime = 0, totalCpuTime = 0; //分别为系统启动后空闲的CPU时间和总的CPU时间
//        float cpuUsage = 0;
//        try {
//            sess.execCommand("cat /proc/stat");
//            is = new StreamGobbler(sess.getStdout());
//            brStat = new BufferedReader(new InputStreamReader(is));
//            while ((line = brStat.readLine()) != null) {
//                if (line.startsWith("cpu")) {
//                    line = line.trim();
//                    String[] temp = line.split("\\s+");
//                    idleCpuTime = Long.parseLong(temp[4]);
//                    for (String s : temp) {
//                        if (!s.equals("cpu")) {
//                            totalCpuTime += Long.parseLong(s);
//                        }
//                    }
////                    System.out.println("IdleCpuTime: " + idleCpuTime + ", " + "TotalCpuTime" + totalCpuTime);
//
//                    break;
//                }
//
////                System.out.println(line);
//            }
//            brStat.close();
//            is.close();
//            sess.close();
//
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                StringWriter sw = new StringWriter();
//                e.printStackTrace(new PrintWriter(sw));
////                System.out.println("CpuUsage休眠时发生InterruptedException. " + e.getMessage());
////                System.out.println(sw.toString());
//            }
//
//
//            //第二次采集CPU时间
//
//            Session sess1 = null;
//            try {
//                sess1=connection.openSession();
//            }catch (Exception e){
//                System.out.println("连接错误");
//            }
//            if(sess1!=null) {
//
//                long endTime = System.currentTimeMillis();
//                System.out.println("第二次");
//                try {
//                sess1.execCommand("cat /proc/stat");
//                is1 = new StreamGobbler(sess1.getStdout());
//                brStat1 = new BufferedReader(new InputStreamReader(is1));
//
//                long idleCpuTime2 = 0, totalCpuTime2 = 0;    //分别为系统启动后空闲的CPU时间和总的CPU时间
//                while ((line1 = brStat1.readLine()) != null) {
//                    if (line1.startsWith("cpu")) {
//                        line1 = line1.trim();
//
//                        String[] temp = line1.split("\\s+");
//                        idleCpuTime2 = Long.parseLong(temp[4]);
//                        for (String s : temp) {
//                            if (!s.equals("cpu")) {
//                                totalCpuTime2 += Long.parseLong(s);
//                            }
//                        }
////                        System.out.println("IdleCpuTime: " + idleCpuTime2 + ", " + "TotalCpuTime" + totalCpuTime2);
//                        break;
//                    }
//
//                }
//                    if (idleCpuTime != 0 && totalCpuTime != 0 && idleCpuTime2 != 0 && totalCpuTime2 != 0) {
//                        cpuUsage = 1 - (float) (idleCpuTime2 - idleCpuTime) / (float) (totalCpuTime2 - totalCpuTime);
////                        System.out.println("本节点CPU使用率为: " + cpuUsage);
//
//                    }
//                    brStat1.close();
//                    is1.close();
//                    sess1.close();
//                    return cpuUsage;
//
//                }
//                catch (Exception e){
//                    System.out.println("第二次获取失败");
//                }
//
//
//            }
//
//        }
//        catch (Exception e ){
//            System.out.println("error");
//        }
//        }
//    return 0;
//    }
//
//    public void addNode(String newNodeIp ,String name ,String pass){
//        Connection connection = sshConnect(newNodeIp,name,pass);
//        if(connection==null){
//            System.out.println("提示ssh信息错误");
//        }
//        else {
//
//        }
//
//    }
//
//
//}
