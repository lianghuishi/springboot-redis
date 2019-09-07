package com.example.springbootredis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootRedisApplication.class)
public class SpringbootRedisApplicationTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /*** 添加一个字符串 */
    @Test
    public void testSet() {
        this.redisTemplate.opsForValue().set("key", "******");
    }

    /*** 获取一个字符串 */
    @Test
    public void testGet() {
        String value = (String) this.redisTemplate.opsForValue().get("key");
        System.out.println(value);
    }

    /**
     * 添加 Users 对象  （jdk这种方式存储会比json格式存要大五倍）
     */
    @Test
    public void testSetUesrs() {
        Users users = new Users();
        users.setAge(20);
        users.setName("张三丰");
        users.setId(1); //重新设置序列化器
        this.redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        this.redisTemplate.opsForValue().set("users", users);
    }

    /*** 取 Users 对象 */
    @Test
    public void testGetUsers() {
        //重新设置序列化器
        this.redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        Users users = (Users) this.redisTemplate.opsForValue().get("users");
        System.out.println(users);
    }

    /**
     * 基于 JSON 格式存 Users 对象
     */
    @Test
    public void testSetUsersUseJSON() {
        Users users = new Users();
        users.setAge(20);
        users.setName("李四丰");
        users.setId(1);
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Users.class));
        this.redisTemplate.opsForValue().set("users_json", users);
    }

    /*** 基于 JSON 格式取 Users 对象 */
    @Test
    public void testGetUseJSON() {
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Users.class));
        Users users = (Users) this.redisTemplate.opsForValue().get("users_json");
        System.out.println(users);
    }


    /** 保存和读取Set **/
    @Test
    public void testGetAddSet() {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add("set1","22");
        set.add("set1","33");
        set.add("set1","44");
        Set<Object> resultSet =redisTemplate.opsForSet().members("set1");
        System.out.println("resultSet:"+resultSet);
    }

    /** 保存 读取 Hash **/
    @Test
    public void testGetAddHash() {
        Map<String,String> map=new HashMap<String,String>();
        map.put("key1","value1");
        map.put("key2","value2");
        map.put("key3","value3");
        map.put("key4","value4");
        map.put("key5","value5");
        redisTemplate.opsForHash().putAll("map1",map);
        Map<Object, Object> resultMap= redisTemplate.opsForHash().entries("map1");
        List<Object> reslutMapList=redisTemplate.opsForHash().values("map1");
        Set<Object> resultMapSet=redisTemplate.opsForHash().keys("map1");
        String value=(String)redisTemplate.opsForHash().get("map1","key1");
        System.out.println("value:"+value);
        System.out.println("resultMapSet:"+resultMapSet);
        System.out.println("resultMap:"+resultMap);
        System.out.println("resulreslutMapListtMap:"+reslutMapList);
    }

    /** 保存 读取 Hash **/
    @Test
    public void testGetAddList() {
        List<String> list1=new ArrayList<String>();
        list1.add("a1");
        list1.add("a2");
        list1.add("a3");

        List<String> list2=new ArrayList<String>();
        list2.add("b1");
        list2.add("b2");
        list2.add("b3");
        redisTemplate.opsForList().leftPush("listkey1",list1);
        redisTemplate.opsForList().rightPush("listkey2",list2);
        List<String> resultList1=(List<String>)redisTemplate.opsForList().leftPop("listkey1");
        List<String> resultList2=(List<String>)redisTemplate.opsForList().rightPop("listkey2");
        System.out.println("resultList1:"+resultList1);
        System.out.println("resultList2:"+resultList2);
    }

}
