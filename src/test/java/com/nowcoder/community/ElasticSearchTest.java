package com.nowcoder.community;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.sun.org.apache.bcel.internal.generic.NEW;
import javafx.scene.control.IndexRange;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class ElasticSearchTest {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Autowired
    private DiscussPostMapper discussPostMapper;



    //创建索引  都是一个请求Request PUT
    @Test
    public void testCreatIndex() throws IOException {
        //创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("discuss_post");
        //客户端执行请求  IndicesClient，请求后获得相应  RequestOptions.DEFAULT 是客户端的默认请求参数 一般不改动
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    // 测试获取索引
    @Test
    void testExistIndex() throws IOException {
        //获取索引
        GetIndexRequest request = new GetIndexRequest("discuss_post");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println("是否存在-----"+exists);
    }
    // 测试删除索引
    @Test
    void testDelIndex() throws IOException {
        //获取索引
        DeleteIndexRequest request = new DeleteIndexRequest("discuss_post");
        //删除
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        //获取删除的状态
        System.out.println(delete.isAcknowledged());
    }

    // 测试添加文档
    @Test
    public void testAddDocument() throws IOException{
        //创建请求  链接索引的请求 将文本插入到那个索引中就获取那个索引
        IndexRequest request = new IndexRequest("fex_index");
        // 规则 PUT /yuan_index/_doc/1
        request.id("1");
        //设置过期事件1秒   request.timeout("1s")
        request.timeout(TimeValue.timeValueSeconds(1));
        //创建对象
        DiscussPost discussPost = new DiscussPost(1,2,"标题","内容",1,1,new Date(),2,2);
        // 将数据放入请求  把discussPost对象转变为json字符串
        IndexRequest source = request.source(JSON.toJSONString(discussPost), XContentType.JSON);
        //客户端发送请求  获取响应的结果
        IndexResponse index = client.index(request, RequestOptions.DEFAULT);
        System.out.println(index.status());
        // 命令返回状态 ：CREATED
        System.out.println(index.toString());
    }

    //获取文档 判断是否存在 GET /index/_doc/1
    @Test
    void testIsExists() throws IOException {
        GetRequest request = new GetRequest("fex_index", "1");
        // 获取返回_source的上下文，这里设为false，不返回
        request.fetchSourceContext(new FetchSourceContext(false));
        //设置排序的字段为null  不排序
        request.storedFields("_none_");

        boolean exists = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    // 获取文档的信息
    @Test
    public void testGetDocument() throws IOException{
        //获取索引链接
        GetRequest getRequest = new GetRequest("fex_index", "1");
        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
        //打印文档的内容 转换为Sting类型 这个是正常的内容
        System.out.println(response.getSourceAsString());
        //返回的是具体的文档内容
        System.out.println(response);
    }

    //更新文档的内容
    @Test
    public void testUpdateDocument() throws IOException{
        //获取索引链接
        UpdateRequest updateRequest = new UpdateRequest("fex_index", "1");
        //请求的超时时间
        updateRequest.timeout("1s");
        DiscussPost discussPost = new DiscussPost(1,2,"更新标题","更新内容",1,1,new Date(),2,2);
        //进行更新操作  转对象转换为json类型
        updateRequest.doc(JSON.toJSONString(discussPost),XContentType.JSON);
         //执行更新操作
        UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
        //返回更新后的状态
        System.out.println(response.status());
    }

    //删除文档记录
    @Test
    public void testDelDocument() throws IOException{
        DeleteRequest request = new DeleteRequest("discuss_post", "13");
        request.timeout("1s");

        DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(deleteResponse.status());
    }

    // 批量插入信息
    @Test
    public void testBulkDocument() throws IOException{
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        List<DiscussPost> discussPosts=new ArrayList<>();
        discussPosts.add(new DiscussPost(1,2,"更新标题01","更新内容",1,1,new Date(),2,2));
        discussPosts.add(new DiscussPost(1,2,"更新标题02","更新内容",1,1,new Date(),2,2));
        discussPosts.add(new DiscussPost(1,2,"更新标题03","更新内容",1,1,new Date(),2,2));
        //批处理请求
        for (int i = 0; i < discussPosts.size(); i++) {
            // 批量更新删除在这里修改对应的请求
            bulkRequest.add(
                    new IndexRequest("discuss_post")
                            .id(""+i+1)
                            .source(JSON.toJSONString(discussPosts.get(i)),XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.status());
        //判断是否执行失败判断是否失败
        System.out.println(bulkResponse.hasFailures());
    }

    // 查询
    // SearchRequest 搜索请求
    // SearchSourceBuilder 条件构造
    @Test
    public void testSearch() throws IOException {
        SearchRequest request = new SearchRequest("fex_index");
        // 构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

//      searchSourceBuilder.highlighter();  // 高亮

        // 查询条件，可以使用QueryBuilders来实现
        //  QueryBuilders.termQuery 精确搜索
        // QueryBuilders.matchAllQuery()    匹配所有
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("content", "更新内容");

        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        request.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse.getHits()));

        System.out.println("--------------------");
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Test
    public void testAddAll()throws IOException{
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        List<DiscussPost> discussPosts1 = discussPostMapper.selectDiscussPost(101, 0, 100,0);
        List<DiscussPost> discussPosts2 = discussPostMapper.selectDiscussPost(102, 0, 100,0);
        List<DiscussPost> discussPosts3 = discussPostMapper.selectDiscussPost(103, 0, 100,0);
        List<DiscussPost> discussPosts4 = discussPostMapper.selectDiscussPost(111, 0, 100,0);

        int l1 = discussPosts1.size();
        int l2 = discussPosts2.size();
        int l3 = discussPosts3.size();

        for (int i = 0; i < discussPosts4.size()/4; i++) {
            bulkRequest.add(
                    new IndexRequest("discuss_post")
                            .id(""+(l1+1+i))
                            .source(JSON.toJSONString(discussPosts4.get(i)),XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        //判断是否执行失败判断是否失败
        System.out.println(bulkResponse.hasFailures());
    }

    @Test
    public void run1(){
        List<DiscussPost> discussPosts4 = discussPostMapper.selectDiscussPost(103, 0, 100,0);
        System.out.println(discussPosts4.size());

    }


}






