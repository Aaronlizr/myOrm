package com.myorm.util;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class testPro {

    private  static DataSource ds ;
    static {

    }

    public static void main(String[] args){
        InputStream is = null;
        try{
            is = testPro.class.getResourceAsStream("/db.properties");
            Properties pro = new Properties();
            pro.load(is);
            //BasicDataSourceFactory.createDataSource(pro);
            System.out.println(pro);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
