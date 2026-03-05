package cc.leedaud.service;

import cc.leedaud.dto.SystemConfigDTO;
import cc.leedaud.entity.SystemConfig;

import java.util.List;

public interface SystemConfigService {
    /**
     * 鑾峰彇鎵€鏈夌郴缁熼厤缃?     * @return
     */
    List<SystemConfig> listAll();

    /**
     * 鏍规嵁閰嶇疆閿幏鍙栭厤缃?     * @param configKey
     * @return
     */
    SystemConfig getByKey(String configKey);

    /**
     * 鏍规嵁ID鑾峰彇閰嶇疆
     * @param id
     * @return
     */
    SystemConfig getById(Long id);

    /**
     * 娣诲姞绯荤粺閰嶇疆
     * @param systemConfigDTO
     */
    void addConfig(SystemConfigDTO systemConfigDTO);

    /**
     * 鏇存柊绯荤粺閰嶇疆
     * @param systemConfigDTO
     */
    void updateConfig(SystemConfigDTO systemConfigDTO);

    /**
     * 鎵归噺鍒犻櫎绯荤粺閰嶇疆
     * @param ids
     */
    void batchDelete(List<Long> ids);
}

