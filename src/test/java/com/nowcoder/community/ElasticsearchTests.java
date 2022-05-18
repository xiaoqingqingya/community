package com.nowcoder.community;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.dao.DiscussPostMapper;
//import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;

/*import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;*/
//import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
//import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
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
import org.elasticsearch.index.query.BoolQueryBuilder;
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
import java.util.Map;

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

  @Autowired
    private DiscussPostRepository discussRepository;


// @Autowired
//    private ElasticsearchTemplate elasticTemplate;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;


   @Test
    public void testInsert() {
        discussRepository.save(discussMapper.selectDiscussPostById(283));
        discussRepository.save(discussMapper.selectDiscussPostById(284));
        discussRepository.save(discussMapper.selectDiscussPostById(285));
    }

/* @Test
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
    }*/

/*
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

        Page<DiscussPost> page = discussRepository.search(searchQuery);//search(searchQuery);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }*/

    /*@Test
    public void testSearchByTemplate() throws IOException {


        //需要查询的字段
        BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("title","互联网寒冬"))
                .should(QueryBuilders.matchQuery("content","互联网寒冬"));

        //构建高亮查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title")
                        ,new HighlightBuilder.Field("content"))
                .withHighlightBuilder(new HighlightBuilder().preTags("<span style='color:red'>").postTags("</span>"))
                .build();
        //查询
        //SearchHits<DiscussPost> search = elasticsearchTemplate.search(searchQuery, DiscussPost.class);
        org.springframework.data.elasticsearch.core.SearchHits<DiscussPost> search = elasticsearchTemplate.search(searchQuery, DiscussPost.class);

        //得到查询返回的内容
        List<org.springframework.data.elasticsearch.core.SearchHit<DiscussPost>> searchHits = search.getSearchHits();




        List<DiscussPost> list = new ArrayList<>();
        //遍历返回的内容进行处理
        for(org.springframework.data.elasticsearch.core.SearchHit<DiscussPost> searchHit:searchHits){
            //高亮的内容
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            //将高亮的内容填充到content中
            searchHit.getContent().setTitle(highlightFields.get("name")==null ? searchHit.getContent().getTitle():highlightFields.get("name").get(0));
            searchHit.getContent().setContent(highlightFields.get("info")==null ? searchHit.getContent().getContent():highlightFields.get("info").get(0));
            //放到实体类中
            list.add(searchHit.getContent());
        }
        for (DiscussPost post : list) {
            System.out.println(post);
        }

    }*/

}
