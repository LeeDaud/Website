package cc.leedaud.service.impl;

import cc.leedaud.constant.MessageConstant;
import cc.leedaud.dto.SystemConfigDTO;
import cc.leedaud.entity.SystemConfig;
import cc.leedaud.exception.BaseException;
import cc.leedaud.exception.SystemConfigException;
import cc.leedaud.mapper.SystemConfigMapper;
import cc.leedaud.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    /**
     * 鑾峰彇鎵€鏈夌郴缁熼厤缃?     * @return
     */
    public List<SystemConfig> listAll() {
        return systemConfigMapper.listAll();
    }

    /**
     * 鏍规嵁閰嶇疆閿幏鍙栭厤缃?     * @param configKey
     * @return
     */
    @Cacheable(value = "systemConfig", key = "#configKey")
    public SystemConfig getByKey(String configKey) {
        return systemConfigMapper.getByKey(configKey);
    }

    /**
     * 鏍规嵁ID鑾峰彇閰嶇疆
     * @param id
     * @return
     */
    public SystemConfig getById(Long id) {
        return systemConfigMapper.getById(id);
    }

    /**
     * 娣诲姞绯荤粺閰嶇疆
     * @param systemConfigDTO
     */
    @CacheEvict(value = "systemConfig", allEntries = true)
    public void addConfig(SystemConfigDTO systemConfigDTO) {
        // 妫€鏌ラ厤缃敭鏄惁宸插瓨鍦?        SystemConfig existingConfig = systemConfigMapper.getByKey(systemConfigDTO.getConfigKey());
        if (existingConfig != null) {
            throw new SystemConfigException(MessageConstant.ConfigKeyExists);
        }
        SystemConfig systemConfig = new SystemConfig();
        BeanUtils.copyProperties(systemConfigDTO, systemConfig);
        systemConfigMapper.insert(systemConfig);
    }

    /**
     * 鏇存柊绯荤粺閰嶇疆
     * @param systemConfigDTO
     */
    @CacheEvict(value = "systemConfig", allEntries = true)
    public void updateConfig(SystemConfigDTO systemConfigDTO) {
        SystemConfig systemConfig = new SystemConfig();
        BeanUtils.copyProperties(systemConfigDTO, systemConfig);
        systemConfigMapper.update(systemConfig);
    }

    /**
     * 鎵归噺鍒犻櫎绯荤粺閰嶇疆
     * @param ids
     */
    @CacheEvict(value = "systemConfig", allEntries = true)
    public void batchDelete(List<Long> ids) {
        systemConfigMapper.batchDelete(ids);
    }
}

