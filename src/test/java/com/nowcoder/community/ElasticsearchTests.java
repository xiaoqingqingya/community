package com.nowcoder.community;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.dao.DiscussPostMapper;
//import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;

/*import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;*/
//import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
//import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;

import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
/*import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;*/

/*import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;*/
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author coolsen
 * @version 1.0.0
 * @ClassName ElasticsearchTests.java
 * @Description Elasticsearch Tests
 * @createTime 2020/5/19 14:17
 */


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {

    @Autowired
    private DiscussPostMapper discussMapper;

    @Autowired
    private RestHighLevelClient esClient;

//  @Autowired
//    private DiscussPostRepository discussRepository;


 /*@Autowired
    private ElasticsearchTemplate elasticTemplate;*/


/*    @Test
    public void testInsert() {
        discussRepository.save(discussMapper.selectDiscussPostById(283));
        discussRepository.save(discussMapper.selectDiscussPostById(284));
        discussRepository.save(discussMapper.selectDiscussPostById(285));
    }

 @Test
    public void testInsertList() {
        discussRepository.saveAll(discussMapper.selectDiscussPosts(101, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(102, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(103, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(111, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(112, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(131, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(132, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(133, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(134, 0, 100));
    }


    @Test
    public void testUpdate() {
        DiscussPost post = discussMapper.selectDiscussPostById(231);
        post.setContent("我是新人,使劲灌水.");
        discussRepository.save(post);
    }

    @Test
    public void testDelete() {
        // discussRepository.deleteById(231);
        discussRepository.deleteAll();
    }*/

/* @Test
    public void testSearchByRepository() {
     NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
             .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
             .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
             .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
             .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
             .withPageable(PageRequest.of(0, 10))
             .withHighlightFields(
                     new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                     new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
             ).build();

     // elasticTemplate.queryForPage(searchQuery, class, SearchResultMapper)
        // 底层获取得到了高亮显示的值, 但是没有返回.

        Page<DiscussPost> page = discussRepository.search(searchQuery);
        discussRepository.searchSimilar(searchQuery)
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }*/

    @Test
    public void testSearchByTemplate() throws IOException {
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
                .query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
        .from(0).size(10);
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



        request.source(builder);
        List<DiscussPost> list = new ArrayList<>();
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();// getHits

        long totalHits = hits.getTotalHits().value;
        for ( SearchHit hit : hits ) {
            System.out.println(hit.getSourceAsString());
        }


        for (SearchHit hit : hits) {


            DiscussPost post = new DiscussPost();

            String id = hit.getSourceAsMap().get("id").toString();
            post.setId(Integer.valueOf(id));

            String userId = hit.getSourceAsMap().get("userId").toString();
            post.setUserId(Integer.valueOf(userId));

            String title = hit.getSourceAsMap().get("title").toString();
            post.setTitle(title);

            String content = hit.getSourceAsMap().get("content").toString();
            post.setContent(content);

            String status = hit.getSourceAsMap().get("status").toString();
            post.setStatus(Integer.valueOf(status));

            String createTime = hit.getSourceAsMap().get("createTime").toString();
            post.setCreateTime(new Date(Long.valueOf(createTime)));

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



        System.out.println(totalHits);

        System.out.println(response.getTook());

        System.out.println(list.size());
        for (DiscussPost discussPost : list) {
            System.out.println(discussPost);
        }
    }

}
