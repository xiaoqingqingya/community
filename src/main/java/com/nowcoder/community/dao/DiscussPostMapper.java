package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    /* 查到的事多个帖子，所以是集合，这个userId是为了开发我的主页的时候才需要，但是首页的时候就不传，
   得到的是0，那么对应的就是一个动态的sql*/
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);
    List<DiscussPost> selectAllDiscussPosts();//这个是我自己定义的，在后面的es测试里面用到了

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    // 这个userId和上面是一样的功能
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);
}
