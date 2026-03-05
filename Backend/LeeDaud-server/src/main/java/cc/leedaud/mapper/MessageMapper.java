package cc.leedaud.mapper;

import cc.leedaud.dto.MessagePageQueryDTO;
import cc.leedaud.entity.Messages;
import cc.leedaud.vo.MessageVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 鐣欒█Mapper
 */
@Mapper
public interface MessageMapper {

    /**
     * 鏂板鐣欒█
     * @param messages
     */
    void save(Messages messages);

    /**
     * 鍒嗛〉鏉′欢鏌ヨ鐣欒█
     * @param messagePageQueryDTO
     * @return
     */
    List<Messages> pageQuery(MessagePageQueryDTO messagePageQueryDTO);

    /**
     * 鎵归噺瀹℃牳閫氳繃鐣欒█
     * @param ids
     */
    void batchApprove(List<Long> ids);

    /**
     * 鎵归噺鍒犻櫎鐣欒█
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 鏍规嵁ID鏌ヨ鐣欒█
     * @param id
     * @return
     */
    Messages getById(Long id);

    // ===== 鍗氬绔柟娉?=====

    /**
     * 鑾峰彇鐣欒█鍒楄〃锛堝凡瀹℃牳 + 鎸囧畾璁垮鐨勬湭瀹℃牳鐣欒█锛?     */
    List<MessageVO> getApprovedList(@Param("visitorId") Long visitorId);

    /**
     * 鏇存柊鐣欒█鍐呭锛堣瀹㈢紪杈戯級
     */
    void updateContent(Messages messages);

    /**
     * 鍒犻櫎鍗曟潯鐣欒█
     */
    @Delete("delete from messages where id = #{id}")
    void deleteById(Long id);

    /**
     * 缁熻鎬荤暀瑷€鏁?     */
    @Select("select count(*) from messages")
    Integer countTotal();

    /**
     * 缁熻寰呭鏍哥暀瑷€鏁?     */
    @Select("select count(*) from messages where is_approved = 0")
    Integer countPending();

    /**
     * 鏍规嵁鏍圭暀瑷€ID鍒犻櫎鎵€鏈夊瓙鐣欒█
     */
    @Delete("delete from messages where root_id = #{rootId}")
    void deleteByRootId(Long rootId);

    /**
     * 缁熻鏌愭牴鐣欒█涓嬬殑瀛愮暀瑷€鏁?     */
    @Select("select count(*) from messages where root_id = #{rootId}")
    Integer countByRootId(Long rootId);
}


