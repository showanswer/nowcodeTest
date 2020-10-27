package com.nowcoder.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "discusspost",shards = 6  ,replicas = 3,type = "_doc")
public class DiscussPost {
    @Id
    private int id;
    @Field(type = FieldType.Integer)
    private int userId;
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String title;
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String content;
    //0-普通; 1-置顶;
    @Field(type = FieldType.Integer)
    private int type;
    //如果为  0-正常; 1-精华; 2-拉黑;
    @Field(type = FieldType.Integer)
    private int status;
    @Field(type = FieldType.Date)
    private Date createTime;
    //回帖数量
    @Field(type = FieldType.Integer)
    private int commentCount;
    @Field(type = FieldType.Double)
    private double score;
}
