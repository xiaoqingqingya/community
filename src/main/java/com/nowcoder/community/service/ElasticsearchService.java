package com.nowcoder.community.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.nowcoder.community.dao.DiscussPostMapper;

/*import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;*/
//import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import javafx.scene.canvas.GraphicsContext;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
/*import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;*/
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

/**
 * @author coolsen
 * @version 1.0.0
 * @ClassName ElasticsearchService.java
 * @Description Elasticsearch Service
 * @createTime 2020/5/19 16:19
 */

@Service
public class ElasticsearchService {


    @Autowired
    private RestHighLevelClient esClient;//restHighLevelClient

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private DiscussPostMapper discussPostMapper;


    public void saveDiscussPost(DiscussPost post) throws IOException {



        discussRepository.save(post);

      /*  IndexRequest request = new IndexRequest();
        request.index("dicusspost");

        // 向ES插入数据，必须将数据转换位JSON格式
        ObjectMapper mapper = new ObjectMapper();
        String dsJson = mapper.writeValueAsString(post);
        request.source(dsJson, XContentType.JSON);

        IndexResponse response = esClient.index(request, RequestOptions.DEFAULT);*/
    }

    public void deleteDiscussPost(int id) throws IOException {
        discussRepository.deleteById(id);
        /*DeleteRequest request = new DeleteRequest();
        request.index("discusspost").id(Integer.toString(id));

        DeleteResponse response = esClient.delete(request, RequestOptions.DEFAULT);*/
    }

    /**
     * @Description: es 搜索功能
     * @param keyword

     * @return: org.springframework.data.domain.Page<com.nowcoder.community.entity.DiscussPost>
     * @Date 2020/5/19
     **/
    public long searchDiscussPosts(String keyword) throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices("discusspost");


        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        HighlightBuilder highlightBuilder = new HighlightBuilder();

        SearchSourceBuilder builder  = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC));


        highlightBuilder.field(new HighlightBuilder.Field("title"));
        highlightBuilder.field(new HighlightBuilder.Field("content"));

        // 设置高亮样式
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");

        searchSourceBuilder.highlighter(highlightBuilder);


        request.source(builder);
        List<DiscussPost> list = new ArrayList<>();
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();

        return hits.getTotalHits().value;
    }
    public List<DiscussPost> searchDiscussPost(String keyword, int current, int limit) throws IOException {

        // 1. 查询索引的所有数据
        SearchRequest request = new SearchRequest();
        request.indices("discusspost");
//        request.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
//        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
//        request.source(new SearchSourceBuilder()
//                .query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
//                .sort("type", SortOrder.DESC)
//                .sort("score", SortOrder.DESC)
//                .sort("createTime", SortOrder.DESC));
//
//        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        HighlightBuilder highlightBuilder = new HighlightBuilder();

        SearchSourceBuilder builder  = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(current * limit).size(limit);
//                .highlighter(
//                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>"),
//                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>"));
                        //.highlighter(new HighlightBuilder().preTags("<font color='red'>").postTags("</font>").field("title").field("content"));
        highlightBuilder.field(new HighlightBuilder.Field("title"));
        highlightBuilder.field(new HighlightBuilder.Field("content"));

        // 设置高亮样式
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");

        searchSourceBuilder.highlighter(highlightBuilder);

        //意思是从第几条开始查找
        //searchSourceBuilder.from(current * limit);
        //查几条
        //searchSourceBuilder.size(limit);

        request.source(builder);
        List<DiscussPost> list = new ArrayList<>();
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println("333");
        System.out.println(keyword);
        for ( SearchHit hit : hits ) {
            System.out.println(hit.getSourceAsMap());
        }

        for (SearchHit hit : hits) {


            DiscussPost post = new DiscussPost();

            String id = hit.getSourceAsMap().get("id").toString();
            //post.setId(Integer.valueOf(id));

            post = discussPostMapper.selectDiscussPostById(Integer.valueOf(id));

           /* String userId = hit.getSourceAsMap().get("userId").toString();
            post.setUserId(Integer.valueOf(userId));

            String title = hit.getSourceAsMap().get("title").toString();
            post.setTitle(title);

            String content = hit.getSourceAsMap().get("content").toString();
            post.setContent(content);

            String status = hit.getSourceAsMap().get("status").toString();
            post.setStatus(Integer.valueOf(status));*/

            /*String createTime = hit.getSourceAsMap().get("createTime").toString();
            post.setCreateTime(new Date(Long.valueOf(createTime)));
*/
            String commentCount = hit.getSourceAsMap().get("commentCount").toString();
            post.setCommentCount(Integer.valueOf(commentCount));

            // 处理高亮显示的结果
            HighlightField titleField = hit.getHighlightFields().get("title");
            if (titleField != null) {
                post.setTitle(titleField.getFragments()[0].toString());


            }

            HighlightField contentField = hit.getHighlightFields().get("content");
            if (contentField != null) {
                post.setContent(contentField.getFragments()[0].toString());
            }

            list.add(post);
        }
       // PageImpl(List<T> content, Pageable pageable, long total)
        //Page<DiscussPost> pageResult=(Page<DiscussPost>) list;
        for (DiscussPost discussPost : list) {
            System.out.println(discussPost);
        }
        return list;

    }
}
