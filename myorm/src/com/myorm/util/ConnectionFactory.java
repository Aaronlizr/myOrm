package com.myorm.util;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/*
* 连接工厂
* 
* */
public class ConnectionFactory {

    //静态的数据源
    private static DataSource ds;

    //获取数据源实例
    static{
        InputStream is = null;
         try{
             //读取classpath下面的db.properties文件
             is =ConnectionFactory.class.getResourceAsStream("/db.properties");
             //创建一个Properties对象
             Properties pro = new Properties();
             //读取流中的配置信息
             pro.load(is);
             System.out.println(pro);
             //创建一个apache的数据源
             ds = BasicDataSourceFactory.createDataSource(pro);
            }catch (Exception e){
                     System.out.println("************获取数据源失败*************");
                     e.printStackTrace();
            }finally {
                 try{
                      is.close();
                    }catch (Exception e){
                          e.printStackTrace();
                      }
            }

    }

    public static Connection getConnection(){
         try{
                 return ds.getConnection();
                 }catch (Exception e){
                     e.printStackTrace();
                     return null;
                 }
    }

    public static void close(Connection con , PreparedStatement pstm , ResultSet rs){
         try{
                if (rs != null) rs.close();
                if (pstm != null) pstm.close();
                if(rs != null) rs.close();

                 }catch (Exception e){
                     e.printStackTrace();
                 }
    }
}
