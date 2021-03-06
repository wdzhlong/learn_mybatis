package com.mybatis;

import com.mybatis.entity.Batch;
import com.mybatis.entity.Role;
import com.mybatis.entity.Student;
import com.mybatis.mapper.BatchMapper;
import com.mybatis.mapper.RoleMapper;
import com.mybatis.mapper.StudentMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: zhenghailong
 * @date: 2019/9/29 10:43
 * @modified By:
 * @description:
 */
public class MainMybatis {

    public SqlSessionFactory sqlSessionFactory(){
        // mybatis主配置文件
        String resource = "mybatis-config.xml";
        // 加载到数据流
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 通赤SqlSessionFactoryBuilder构建SqlSessionFactory
        return new SqlSessionFactoryBuilder().build(inputStream);
    }

    /**
     * SqlSession用途：
     * (1)获取映射器，让映射器通过命名空间和方法名称找到对应的SQL，发送给数据库执行后返回结果
     * (2)直接通过命名信息去执行SQL返回结果，在SqlSession层我们可以通过update,insert,select,delete等方法，
     * 带上sql的id来操作xml中配置好的sql,从而完成我们的工作;与此同时它也支持事务，通过commit,rollback方法
     * 提交或者回滚事务
     * @return
     * @throws IOException
     */
    public SqlSession sqlSession() {
        return sqlSessionFactory().openSession();
    }

    @Test
    public void getRole() {
        try (SqlSession sqlSession = sqlSession()){
            RoleMapper mapper = sqlSession.getMapper(RoleMapper.class);
            Role role = mapper.getRole(4L);
            System.out.println(role.toString());
        }
    }

    @Test
    public void getRole2() {
        try(SqlSession sqlSession = sqlSession()) {
            Role role = sqlSession.selectOne("com.mybatis.mapper.RoleMapper.getRole", 1L);
            System.out.println(role.toString());
        }
    }

    @Test
    public void insertRole() {
        try (SqlSession sqlSession = sqlSession()){
            RoleMapper mapper = sqlSession.getMapper(RoleMapper.class);
            Role role = new Role(null,"b","c");
            mapper.insertRole(role);
            sqlSession.commit();
            System.out.println(role.toString());
        }
    }

    @Test
    public void insertRoleCustom() {
        try (SqlSession sqlSession = sqlSession()){
            RoleMapper mapper = sqlSession.getMapper(RoleMapper.class);
            Role role = new Role(null,"b","c");
            mapper.insertRoleCustom(role);
            sqlSession.commit();
            System.out.println(role.toString());
        }
    }

    @Test
    public void resultIsMap(){
        try (SqlSession sqlSession = sqlSession()){
            RoleMapper mapper = sqlSession.getMapper(RoleMapper.class);
            Map<String, Role> map = mapper.getMRoleReturnMap("a");
            System.out.println(map.toString());
        }
    }

    @Test
    public void findStudent(){
        try (SqlSession sqlSession = sqlSession()){
            StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
            Student student = mapper.getStudent(1L);
            System.out.println(student.toString());
        }
    }

    @Test
    public void batchInsert(){
        try (SqlSession sqlSession = sqlSession()){
            BatchMapper mapper = sqlSession.getMapper(BatchMapper.class);
            List<Batch> list = new ArrayList<>();
            Batch batch1 = new Batch(null,"b1",1);
            Batch batch2 = new Batch(null,"b2",2);
            Batch batch3 = new Batch(null,"b3",3);
            list.add(batch1);
            list.add(batch2);
            list.add(batch3);
            int i = mapper.batchInsert(list);
            sqlSession.commit();
            System.out.println(i);
        }
    }

    @Test
    public void batchUpdate(){
        try (SqlSession sqlSession = sqlSession()){
            BatchMapper mapper = sqlSession.getMapper(BatchMapper.class);
            List<Batch> list = new ArrayList<>();
            Batch batch1 = new Batch(4L,"b1b",11);
            Batch batch2 = new Batch(5L,"b2b",21);
            Batch batch3 = new Batch(6L,"b3b",31);
            list.add(batch1);
            list.add(batch2);
            list.add(batch3);
            int i = mapper.batchUpdate(list);
            sqlSession.commit();
            System.out.println(i);
        }
    }

    @Test
    public void batchDelete(){
        try (SqlSession sqlSession = sqlSession()){
            BatchMapper mapper = sqlSession.getMapper(BatchMapper.class);
            List<Long> list = new ArrayList<>();
            list.add(4L);
            list.add(5L);
            list.add(6L);
            int i = mapper.batchDelete(list);
            sqlSession.commit();
            System.out.println(i);
        }
    }
}
