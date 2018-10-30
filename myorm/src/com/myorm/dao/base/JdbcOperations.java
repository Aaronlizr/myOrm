package com.myorm.dao.base;

import java.io.Serializable;
import java.util.List;

/*
* 指定一个基本的JDBC操作接口，由JdbcTemolate实现
* */
public interface JdbcOperations {

    /*
    * 根据ID删除对象
    * @param entityClass 表示要操作的类型
    * @param id 表示要删除的id
    * */
    void remove(Class<?> entityClass,Serializable id);
    /*
    * 根据Id查询数据
    *  @param entityClass 表示要操作的类型
    * @param id 表示要查询的id
    * */
    public <T> T get(Class<T> entityClass , Serializable id);
    /*
     * 查询所有数据
     *  @param entityClass 表示要操作的类型
     *  @param 返回装载对象的集合
     * */
    public <T> List<T> getAll(Class<T> entityClass);
    /*
     * 保存数据
     *  @param entity 要保存的对象数据
     *  @param 自动插入的主键
     * */
    public <T> Serializable save(T entity);
    /*
    * 修改对象
    * @param entity - 要修改的数据实体对象，该数据从数据库查询出来的，必须存在id
    * */
    <T> void updata(T entity);
    /*
    * 个性化查询
    * @param entityClass 表示要操作得类型
    * @param queryString Sql语句
    * @param values 个性化Sql的参数，对应queryString中的"？"
    * @return 查询返回的对象集合，具体的类型由泛型决定
    * */
    <T> List<T> query(Class<T> entityClass,String queryString,Object... values);

}
