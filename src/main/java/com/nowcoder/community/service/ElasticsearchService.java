package com.nowcoder.community.service;

import com.alibaba.fastjson.JSON;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.security.InvalidateTokenRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ElasticsearchService {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Autowired(required = false)
    private DiscussPostMapper discussPostMapper;

    /**
     * 添加一个对象到es中
     * @param post
     * @throws IOException
     */
    public void saveDiscussPost(DiscussPost post)throws IOException {
        IndexRequest request = new IndexRequest("discuss_post");
        request.id(""+post.getId());
        request.timeout(TimeValue.timeValueSeconds(1));
        request.source(JSON.toJSONString(post), XContentType.JSON);
        IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }

    /**
     * es 删除一个文档数据
     * @param id
     * @throws IOException
     */
    public void deleteDiscussPost(int id) throws IOException{
        DeleteRequest request = new DeleteRequest("discuss_post", "" + id);
        request.timeout("1s");
        DeleteResponse delete = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(delete);
    }

    /**
     *  查询 高亮 分页 显示
     * @param keyword
     * @param current
     * @param limit
     * @return
     * @throws IOException
     */
    public List<Map<String,Object>> searchDiscussPost(String keyword,int current,int limit)throws IOException{
        SearchRequest request = new SearchRequest("discuss_post");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //匹配所有 内容和标题
        MultiMatchQueryBuilder query = QueryBuilders.multiMatchQuery(keyword, "title", "content");
        //按查询到的类型进行倒序排序  1 是精华帖 先出现 0 是普通帖子在后面出现
        sourceBuilder.sort("type", SortOrder.DESC).sort("score",SortOrder.DESC).sort("createTime",SortOrder.DESC);

        //进行分页 设置分页大小  起始页  每页数据量
        sourceBuilder.from(current);
        sourceBuilder.size(limit);

        //设置搜索到的关键词 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置高亮的字段
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        //如果要多个字段高亮,这项要为false
        highlightBuilder.requireFieldMatch(false);
        //高亮设置  html开始便签
        highlightBuilder.preTags("<span style='color:red'>");
        //结束标签
        highlightBuilder.postTags("</span>");
         //将高亮设置 加入到构建搜索条件sourceBuilder中
        sourceBuilder.highlighter(highlightBuilder);
        sourceBuilder.query(query);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //将搜索条件 加到请求中 组合请求
        request.source(sourceBuilder);
        //响应请求  searchResponse是返回的结果
        SearchResponse searchResponse = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        // 解析结果
        List<Map<String, Object>> list = new ArrayList<>();
        if(searchResponse!=null){
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                //获取高亮字段
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                //当前标题
                HighlightField title = highlightFields.get("title" );
                //高亮的值
                HighlightField content = highlightFields.get("content" );
                //获取原来的结果
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //将原来的字段 换成高亮的字段
                if (title != null) {
                    Text[] fragments = title.fragments();
                    String newTitle="";
                    for(Text text:fragments){
                        newTitle+=text;
                    }
                    //高亮字段替换掉原来的字段
                    sourceAsMap.put("title",newTitle);
                }
                if (content != null) {
                    Text[] fragments = content.fragments();
                    String new_content = "";
                    for (Text fragment : fragments) {
                        new_content += fragment;
                    }
                    sourceAsMap.put("content", new_content);
                }
                list.add(sourceAsMap);
            }
        }
        return  list;
    }


    /**
     * 查出符合条件的行数
     * @param keyWord
     * @return
     * @throws IOException
     */
    public int searchCount(String keyWord) throws IOException {
        //查询数量的请求CountRequest
        CountRequest countRequest = new CountRequest("discuss_post");
        //完全匹配条件
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyWord);
        countRequest.query(multiMatchQueryBuilder);
        CountResponse response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
        return (int) response.getCount();
    }




}
