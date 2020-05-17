package com.example.demo.service.node;
import java.io.*;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.alibaba.fastjson.JSONObject;
import com.sun.corba.se.impl.orb.DataCollectorBase;
import org.hibernate.dialect.Database;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.lang.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

import ch.ethz.ssh2.StreamGobbler;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.apache.log4j.Logger;


@Service
public class NodeServiceImpl  implements NodeService  {

    @Value("${docker.ip}")
    private String nodeIp;

    @Value("${node.user}")
    private String username;

    @Value("${node.password}")
    private String password;

    private int CPU_IDLE=0;
    int processStatus =0;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;




    private int MEM_INFO=0;


    private Thread thread;

//ssh 连接集群
    public Connection sshConnect(){
        Connection conn = null;
        boolean isAuthenticated = false;
        try {
            conn = new Connection(nodeIp); // hostname 你要远程登录的主机IP地址,如10.0.2.1
            conn.connect();
            isAuthenticated = conn.authenticateWithPassword(username, password);
            if (isAuthenticated){
//                Session session = conn.openSession();
//                this.getSysCpuInfo(conn);
                return conn;

            }
        }catch (Exception e){
            System.out.println("连接失败");
        }
        return null;


    }


//获取节点的内存信息
    @Scheduled(cron = "0/5 * * * * ?")    //定时执行 每5s执行一次
    public void getSysMemInfo()  {
        Connection connection = this.sshConnect();

        Map<String, Object> map = new HashMap<String, Object>();
        InputStream is = null;
        BufferedReader brStat = null;
        StringTokenizer tokenStat=  null ;
        String line = "";
        int i=0,j=0,cpuidle=0;
        long totalMem = 0, freeMem = 0;
        float memUsage = 0.0f;
/**
 * 对于执行linux shell.
 *
 */
        Session sess = null;
        try {
            sess=connection.openSession();
        }catch (Exception e){
            System.out.println("连接错误");
        }
        if (sess!=null){
        try{
            sess.execCommand("cat /proc/meminfo ");
            is = new StreamGobbler(sess.getStdout());
            brStat = new BufferedReader(new InputStreamReader(is));

            while((line = brStat.readLine()) != null){
                String[] memInfo = line.split("\\s+");
                if(memInfo[0].startsWith("MemTotal")){
                    totalMem = Long.parseLong(memInfo[1]);
                }
                if(memInfo[0].startsWith("MemFree")){
                    freeMem = Long.parseLong(memInfo[1]);
                }
                memUsage = 1- (float)freeMem/(float)totalMem;
                map.put("memUsage",memUsage);
                map.put("totalMem",totalMem);
                map.put("freeMem",freeMem);
            }
//            System.out.println("total:");
//            System.out.println(totalMem/1024);
//            System.out.println("Free:");
//            System.out.println(freeMem/1024);
//            System.out.println("内存使用率："+memUsage);
        }catch(Exception e){
            System.out.println("测试失败");
        }
        }

        Date dNow = new Date( );
        SimpleDateFormat ft =
                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        JSONObject jsonpObject = new JSONObject();
        jsonpObject.put("data",map);
        jsonpObject.put("date",ft.format(dNow));

        stringRedisTemplate.opsForList().rightPush("redis_data", jsonpObject.toJSONString());

        Long stringValueLength = stringRedisTemplate.opsForList().size("redis_data");
        System.out.println("长度:"+stringValueLength);
        if (stringValueLength !=null && stringValueLength > 20){
            stringRedisTemplate.opsForList().leftPop("redis_data");
        }
        Long stringValueLength1 = stringRedisTemplate.opsForList().size("redis_data");
        System.out.println("长度1:"+stringValueLength1);

//        List<String> list = template.opsForList().range("redis_data", 0, -1);
//        System.out.println(list);
//        return map;
    }

    public List<String> getFromRedis(){

        return stringRedisTemplate.opsForList().range("redis_data",0,-1);
    }



    public  Map<String, Object> getSysCpuInfo(Connection connection){
        Session sess = null;
        try {
             sess=connection.openSession();
        }catch (Exception e){
            System.out.println("连接错误");
        }
        if (sess!=null){
        InputStream is = null;
        InputStream is1 = null;
        BufferedReader brStat = null, brStat1=null;
        String line = "",line1="";
        long idleCpuTime = 0, totalCpuTime = 0; //分别为系统启动后空闲的CPU时间和总的CPU时间
        float cpuUsage = 0;
        try {
            sess.execCommand("cat /proc/stat");
            is = new StreamGobbler(sess.getStdout());
            brStat = new BufferedReader(new InputStreamReader(is));
            while ((line = brStat.readLine()) != null) {
                if (line.startsWith("cpu")) {
                    line = line.trim();
                    String[] temp = line.split("\\s+");
                    idleCpuTime = Long.parseLong(temp[4]);
                    for (String s : temp) {
                        if (!s.equals("cpu")) {
                            totalCpuTime += Long.parseLong(s);
                        }
                    }
//                    System.out.println("IdleCpuTime: " + idleCpuTime + ", " + "TotalCpuTime" + totalCpuTime);

                    break;
                }

//                System.out.println(line);
            }
            brStat.close();
            is.close();
            sess.close();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
//                System.out.println("CpuUsage休眠时发生InterruptedException. " + e.getMessage());
//                System.out.println(sw.toString());
            }


            //第二次采集CPU时间

            Session sess1 = null;
            try {
                sess1=connection.openSession();
            }catch (Exception e){
                System.out.println("连接错误");
            }
            if(sess1!=null) {

                long endTime = System.currentTimeMillis();
                System.out.println("第二次");
                try {
                sess1.execCommand("cat /proc/stat");
                is1 = new StreamGobbler(sess1.getStdout());
                brStat1 = new BufferedReader(new InputStreamReader(is1));

                long idleCpuTime2 = 0, totalCpuTime2 = 0;    //分别为系统启动后空闲的CPU时间和总的CPU时间
                while ((line1 = brStat1.readLine()) != null) {
                    if (line1.startsWith("cpu")) {
                        line1 = line1.trim();

                        String[] temp = line1.split("\\s+");
                        idleCpuTime2 = Long.parseLong(temp[4]);
                        for (String s : temp) {
                            if (!s.equals("cpu")) {
                                totalCpuTime2 += Long.parseLong(s);
                            }
                        }
//                        System.out.println("IdleCpuTime: " + idleCpuTime2 + ", " + "TotalCpuTime" + totalCpuTime2);
                        break;
                    }

                }
                    if (idleCpuTime != 0 && totalCpuTime != 0 && idleCpuTime2 != 0 && totalCpuTime2 != 0) {
                        cpuUsage = 1 - (float) (idleCpuTime2 - idleCpuTime) / (float) (totalCpuTime2 - totalCpuTime);
//                        System.out.println("本节点CPU使用率为: " + cpuUsage);
                    }
                    brStat1.close();
                    is1.close();
                    sess1.close();
                }
                catch (Exception e){
                    System.out.println("第二次获取失败");
                }

            }

        }
        catch (Exception e ){
            System.out.println("error");
        }
        }
        return null;
    }


}
