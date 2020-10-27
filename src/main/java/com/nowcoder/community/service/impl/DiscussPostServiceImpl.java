package com.nowcoder.community.service.impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // Caffeine核心接口: Cache,子接口: LoadingCache, AsyncLoadingCache

    // 帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;
    // 帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init(){
        // 初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        //缓存数据的来源--从数据库中查数据
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误!");
                        }
                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误!");
                        }
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);
                        // 二级缓存: Redis -> mysql   这里还还可以加个二级缓存  本地缓存没有在二级缓存中查看  二级缓存中还没有再查找数据库
                        logger.debug("load post list from DB.");
                        return discussPostMapper.selectDiscussPost(0, offset, limit, 1);
                    }
                });
        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        logger.debug("load post rows from DB.");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }



    @Override
    public List<DiscussPost> selectDiscussPost(int userId, int offset, int limit,int orderMode) {
        //当帖子按热度排名，查看首页信息时，直接从缓存中查询
        if (userId == 0 && orderMode == 1) {
            return postListCache.get(offset + ":" + limit);
        }
        //从数据库中查询
        logger.debug("load post list from DB.");
        return discussPostMapper.selectDiscussPost(userId,offset,limit,orderMode);
    }

    @Override
    public int selectDiscussPostRows(int userId) {
        //首页查询帖子总数的时候  走缓存查询
        if (userId == 0) {
            return postRowsCache.get(userId);
        }
        //数据库查询
        logger.debug("load post rows from DB.");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    //保存 插入的帖子
    @Override
    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        // 转义HTML标记  HtmlUtils 是springmvc里的工具 ：是html的便签转译为文本内容 不进行渲染
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    //查询帖子数据
    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    @Override
    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id,score);
    }
}
