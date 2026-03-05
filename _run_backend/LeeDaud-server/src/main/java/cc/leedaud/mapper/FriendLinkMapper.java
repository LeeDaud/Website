package cc.leedaud.mapper;

import cc.leedaud.annotation.AutoFill;
import cc.leedaud.entity.FriendLinks;
import cc.leedaud.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface FriendLinkMapper {
    /**
     * 鑾峰彇鎵€鏈夊弸鎯呴摼鎺?     * @return
     */
    @Select("select * from friend_links order by sort asc, id asc")
    List<FriendLinks> getAllFriendLink();

    /**
     * 娣诲姞鍙嬫儏閾炬帴
     * @param friendLink
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(FriendLinks friendLink);

    /**
     * 鍒犻櫎鍙嬫儏閾炬帴
     * @param id
     */
    @Delete("delete from friend_links where id = #{id}")
    void delete(Long id);

    /**
     * 鎵归噺鍒犻櫎鍙嬫儏閾炬帴
     * @param ids
     */
    void batchDelete(List<Long> ids);

    @AutoFill(value = OperationType.UPDATE)
    void update(FriendLinks friendLink);

    /**
     * 鑾峰彇鍙鍙嬫儏閾炬帴
     * @return
     */
    @Select("select * from friend_links where is_visible = 1 order by sort asc, id asc")
    List<FriendLinks> getVisibleFriendLink();
}

