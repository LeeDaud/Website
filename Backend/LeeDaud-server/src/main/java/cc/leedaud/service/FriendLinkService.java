package cc.leedaud.service;

import cc.leedaud.dto.FriendLinkDTO;
import cc.leedaud.entity.FriendLinks;
import cc.leedaud.vo.FriendLinkVO;

import java.util.List;

public interface FriendLinkService {
    /**
     * 绠＄悊绔幏鍙栨墍鏈夊弸鎯呴摼鎺?     * @return
     */
    List<FriendLinks> getAllFriendLink();

    /**
     * 绠＄悊绔坊鍔犲弸鎯呴摼鎺?     * @param friendLinkDTO
     */
    void addFriendLink(FriendLinkDTO friendLinkDTO);

    /**
     * 鎵归噺鍒犻櫎鍙嬫儏閾炬帴
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 绠＄悊绔慨鏀瑰弸鎯呴摼鎺?     * @param friendLinkDTO
     */
    void updateFriendLink(FriendLinkDTO friendLinkDTO);

    /**
     * 鍗氬绔幏鍙栧彲瑙佸弸鎯呴摼鎺?     * @return
     */
    List<FriendLinkVO> getVisibleFriendLink();
}

