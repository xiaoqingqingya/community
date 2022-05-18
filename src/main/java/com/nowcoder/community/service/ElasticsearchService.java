package com.nowcoder.community.service;


import com.nowcoder.community.dao.DiscussPostMapper;


import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;


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

/*        // 1. 查询索引的所有数据
        SearchRequest request = new SearchRequest();
        request.indices("discusspost");
        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        HighlightBuilder highlightBuilder = new HighlightBuilder();

        SearchSourceBuilder builder  = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(current * limit).size(limit);
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

        for (SearchHit hit : hits) {


            DiscussPost post = new DiscussPost();

            String id = hit.getSourceAsMap().get("id").toString();
            //post.setId(Integer.valueOf(id));

            post = discussPostMapper.selectDiscussPostById(Integer.valueOf(id));

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
        }*/
        //需要查询的字段
        BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("title",keyword))
                .should(QueryBuilders.matchQuery("content",keyword));

        //构建高亮查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, limit))//.from(current * limit).size(limit);
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
        Map<String, Object> map = new HashMap<>();

        //map.put("size", searchHits.size());
        System.out.println(searchHits.size());
        //map.put("list",list);
        //return map;
        return list;

    }
}
