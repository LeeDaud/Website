package cc.leedaud.service.impl;

import cc.leedaud.dto.FriendLinkDTO;
import cc.leedaud.entity.FriendLinks;
import cc.leedaud.mapper.FriendLinkMapper;
import cc.leedaud.service.FriendLinkService;
import cc.leedaud.vo.FriendLinkVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class FriendLinkServiceImpl implements FriendLinkService {

    @Autowired
    private FriendLinkMapper friendLinkMapper;

    /**
     * 鑾峰彇鎵€鏈夊弸閾?     * @return
     */
    @Cacheable(value = "friendLinks", key = "'all'")
    public List<FriendLinks> getAllFriendLink() {
        List<FriendLinks> friendLinkList = friendLinkMapper.getAllFriendLink();
        if(friendLinkList != null && friendLinkList.size() > 0){
            return friendLinkList;
        }
        return Collections.emptyList();
    }

    /**
     * 娣诲姞鍙嬮摼
     * @param friendLink
     */
    @CacheEvict(value = "friendLinks", allEntries = true)
    public void addFriendLink(FriendLinkDTO friendLinkDTO) {
        FriendLinks friendLink = new FriendLinks();
        BeanUtils.copyProperties(friendLinkDTO, friendLink);
        friendLinkMapper.insert(friendLink);
    }

    /**
     * 鎵归噺鍒犻櫎鍙嬮摼
     * @param ids
     */
    @CacheEvict(value = "friendLinks", allEntries = true)
    public void batchDelete(List<Long> ids) {
        friendLinkMapper.batchDelete(ids);
    }

    /**
     * 淇敼鍙嬮摼
     * @param friendLink
     */
    @CacheEvict(value = "friendLinks", allEntries = true)
    public void updateFriendLink(FriendLinkDTO friendLinkDTO) {
        FriendLinks friendLink = new FriendLinks();
        BeanUtils.copyProperties(friendLinkDTO, friendLink);
        friendLinkMapper.update(friendLink);
    }

    /**
     * 鍗氬绔幏鍙栧彲瑙佺殑鍙嬮摼
     * @return
     */
    @Cacheable(value = "friendLinks", key = "'visible'")
    public List<FriendLinkVO> getVisibleFriendLink() {
        List<FriendLinks> friendLinkList = friendLinkMapper.getVisibleFriendLink();
        if(friendLinkList != null && friendLinkList.size() > 0){
            List<FriendLinkVO> friendLinkVOList = friendLinkList.stream().map(friendLink -> FriendLinkVO.builder()
                    .id(friendLink.getId())
                    .name(friendLink.getName())
                    .url(friendLink.getUrl())
                    .avatarUrl(friendLink.getAvatarUrl())
                    .description(friendLink.getDescription())
                    .sort(friendLink.getSort())
                    .build()).toList();
            return friendLinkVOList;
        }
        return Collections.emptyList();
    }
}

