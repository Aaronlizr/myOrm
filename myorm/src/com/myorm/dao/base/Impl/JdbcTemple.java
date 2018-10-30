package com.myorm.dao.base.Impl;

import com.myorm.annotation.Column;
import com.myorm.annotation.Table;
import com.myorm.dao.base.JdbcOperations;
import com.myorm.util.ConnectionFactory;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemple implements JdbcOperations {

    /*
     * 获取表名的方法
     * @param entityClass
     * @param 映射的表名
     * */
    protected String getTableName(Class<?> entityClass) {
        //反射检查类上面是否有@Table注解
        Table t = entityClass.getAnnotation(Table.class);
        if (t != null) {
            return t.name();
        } else {
            return entityClass.getSimpleName();
        }
    }

    /*
     * 获取表名的方法
     * @param Field
     * @param 映射的列名
     * */
    protected String getColumnName(Field field) {
        //反射检查类上面是否有@Column注解
        Column column = field.getAnnotation(Column.class);
        if (column != null) {
            return column.nane();
        } else {
            return field.getName();
        }
    }

    /*
     *
     * */
    @Override
    public void remove(Class<?> entityClass, Serializable id) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            //获取连接
            con = ConnectionFactory.getConnection();
            /***************自动生成SQl*********************/

            String tableName = this.getTableName(entityClass);

            StringBuffer sql = new StringBuffer();
            sql.append("delete from ").append(tableName).append(" where id = ?");
            System.out.println(sql);

            pstm = con.prepareStatement(sql.toString());
            //设置参数
            pstm.setObject(1, id);
            //执行
            pstm.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(con, pstm, null);
        }
    }

    /*
     * 所有表的根据id查询语句只有table名不一样，select * from table where id = ?
     * @param 类型
     * @param id
     * */
    @Override
    public <T> T get(Class<T> entityClass, Serializable id) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = ConnectionFactory.getConnection();
            /***************自动生成SQl*********************/
            //获得表名
            String tableName = this.getTableName(entityClass);
            StringBuffer sql = new StringBuffer();
            sql.append("select * from ").append(tableName).append(" where id = ? ");
            System.out.println(sql);
            //获得Statement
            pstm = con.prepareStatement(sql.toString());
            //设置参数
            pstm.setObject(1, id);
            rs = pstm.executeQuery();
            //处理结果集
            if (rs.next()) {
                //通过反射创建对象 ====== User user = new User();
                Object obj = entityClass.newInstance();
                /*******************将rs每一列保存到对象对应的属性当中***********************/
                //获得所有字段
                Field[] fields = entityClass.getDeclaredFields();

                for (Field field : fields) {
                    //字段名 @Column(name="name") private String name
                    String columnName = this.getColumnName(field);
                    //字段值 rs.getInteger(id)
                    Object columnValue = rs.getObject(columnName);
                    //通过字段名查找属性描述器
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), entityClass);
                    //调用set方法
                    pd.getWriteMethod().invoke(obj, columnValue);
                }
                /*******************将rs每一列保存到对象对应的属性当中***********************/
                return (T) obj;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            ConnectionFactory.close(con, pstm, rs);
        }

    }

    @Override
    public <T> List<T> getAll(Class<T> entityClass) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = ConnectionFactory.getConnection();
            /***************自动生成SQl*********************/
            String tableName = this.getTableName(entityClass);
            StringBuffer sql = new StringBuffer();
            sql.append(" select * from ").append(tableName);
            pstm = con.prepareStatement(sql.toString());
            rs = pstm.executeQuery();
            //处理结果集
            List<T> list = new ArrayList<>();
            while (rs.next()) {

                Object obj = entityClass.newInstance();
                Field[] fields = entityClass.getDeclaredFields();
                for (Field field : fields) {
                    String columnNane = this.getColumnName(field);
                    Object columnValue = rs.getObject(columnNane);
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), entityClass);
                    pd.getWriteMethod().invoke(obj, columnValue);
                }
                list.add((T) obj);
            }
            return list;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("chuc");
            return null;
        } finally {
            ConnectionFactory.close(con, pstm, rs);
        }
    }

    /*
     * insert into table(列,列....) values(?,?....)
     * */
    @Override
    public <T> Serializable save(T entity) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = ConnectionFactory.getConnection();
            /******************自动生成SQL***************************/
            //获得反射类型
            Class<?> entityClass = entity.getClass();
            //获得表名
            String tableName = this.getTableName(entityClass);
            StringBuffer sql = new StringBuffer();
            sql.append(" insert into ").append(tableName).append(" ( ");
            //获得所有字段
            Field[] fields = entityClass.getDeclaredFields();
            //遍历
            for (Field field : fields) {
                if (!field.getName().equals("id")) {
                    String columnName = this.getColumnName(field);
                    sql.append(columnName).append(",");
                }
            }
            //去掉最后一个逗号
            sql.deleteCharAt(sql.length() - 1);
            sql.append(" ) values ( ");

            //遍历
            for (Field field : fields) {
                if (!field.getName().equals("id")) {
                    sql.append("?,");
                }
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(" ) ");
            System.out.println(sql);

            pstm = con.prepareStatement(sql.toString(), new String[]{"id"});
            //设置参数
            int parameterIndex = 1;
            for (Field field : fields) {
                if (!field.getName().equals("id")) {
                    //找到对应的属性描述器
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), entityClass);
                    Object value = pd.getReadMethod().invoke(entity);
                    //设置参数
                    pstm.setObject(parameterIndex++, value);
                }
            }
            //执行，返回影响行数
            int result = pstm.executeUpdate();
            //大于0表示执行成功
            if (result > 0) {
                //获得生成的主键的结果集
                rs = pstm.getGeneratedKeys();
                //结果集向下移动
                rs.next();
                //取第一个值
                Object key = rs.getObject(1);
                //返回
                return (Serializable) key;
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            ConnectionFactory.close(con, pstm, rs);
        }

    }

    /*
     * updata tableName set
     * */
    @Override
    public <T> void updata(T entity) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = ConnectionFactory.getConnection();
            //获得Class
            Class<?> entityClass = entity.getClass();
            //获得表名
            String tableName = this.getTableName(entityClass);
            //sql
            StringBuffer sql = new StringBuffer();
            sql.append(" update ").append(tableName).append(" set ");
            //获得所有字段
            Field[] fields = entityClass.getDeclaredFields();
            for (Field field : fields) {
                if (!field.getName().equals("id")) {
                    //列名
                    String ColumnName = this.getColumnName(field);
                    sql.append(ColumnName).append(" = ?,");
                }
            }
            //去掉最后一个逗号
            sql.deleteCharAt(sql.length() - 1);
            sql.append(" where id = ? ");
            System.out.println(sql);

            //获得Statment
            pstm = con.prepareStatement(sql.toString());
            //设置参数
            int parameterIndex = 1;
            for (Field field : fields) {
                if (!field.getName().equals("id")) {
                    //获得想要设置得参数
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), entityClass);
                    //获得get方法
                    Method getMethod = pd.getReadMethod();
                    //设置值
                    pstm.setObject(parameterIndex++, getMethod.invoke(entity));
                }
            }
            PropertyDescriptor pd = new PropertyDescriptor("id", entityClass);
            Method method = pd.getReadMethod();
            pstm.setObject(parameterIndex++, method.invoke(entity));
            pstm.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            ConnectionFactory.close(con, pstm, rs);
        }
    }


    /*
    *
    * */
    @Override
    public <T> List<T> query(Class<T> entityClass, String queryString, Object... values) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = ConnectionFactory.getConnection();
            //获得Staement
            pstm = con.prepareStatement(queryString);
            //判断参数数组
            if (values != null && values.length > 0) {
                //遍历每一个参数
                int index = 1;
                for (Object value : values) {
                    //设置参数
                    pstm.setObject(index++, value);
                }
            }
            //执行
            rs = pstm.executeQuery();
            List<T> list = new ArrayList<>();
            //遍历
            while (rs.next()) {
                //创建对象
                Object obj = entityClass.newInstance();
                //获得所有字段
                Field[] fields = entityClass.getDeclaredFields();
                //遍历
                for (Field field : fields) {
                    //列名
                    String columnName = this.getColumnName(field);
                    //列值
                    Object columnValue = rs.getObject(columnName);
                    //调用Set方法
                    PropertyDescriptor pro = new PropertyDescriptor(field.getName(), entityClass);
                    Method setMethod = pro.getWriteMethod();
                    setMethod.invoke(obj);
                }
                list.add((T) obj);
            }
            return list;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(con, pstm, rs);
        }
        return null;
    }
}