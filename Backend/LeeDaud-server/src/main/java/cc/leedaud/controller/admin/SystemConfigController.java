package cc.leedaud.controller.admin;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.dto.SystemConfigDTO;
import cc.leedaud.entity.SystemConfig;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.result.Result;
import cc.leedaud.service.SystemConfigService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绠＄悊绔郴缁熼厤缃帴鍙? */
@Slf4j
@RestController("adminSystemConfigController")
@RequestMapping("/admin/systemConfig")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 鑾峰彇鎵€鏈夌郴缁熼厤缃?     * @return
     */
    @GetMapping
    public Result<List<SystemConfig>> listAll() {
        List<SystemConfig> configList = systemConfigService.listAll();
        return Result.success(configList);
    }

    /**
     * 鏍规嵁閰嶇疆閿幏鍙栭厤缃?     * @param configKey
     * @return
     */
    @GetMapping("/key/{configKey}")
    public Result<SystemConfig> getByKey(@PathVariable String configKey) {
        log.info("鏍规嵁閰嶇疆閿幏鍙栭厤缃?{}", configKey);
        SystemConfig systemConfig = systemConfigService.getByKey(configKey);
        return Result.success(systemConfig);
    }

    /**
     * 鏍规嵁ID鑾峰彇閰嶇疆
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SystemConfig> getById(@PathVariable Long id) {
        log.info("鏍规嵁ID鑾峰彇閰嶇疆,{}", id);
        SystemConfig systemConfig = systemConfigService.getById(id);
        return Result.success(systemConfig);
    }

    /**
     * 娣诲姞绯荤粺閰嶇疆
     * @param systemConfigDTO
     * @return
     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "systemConfig")
    public Result addConfig(@Valid @RequestBody SystemConfigDTO systemConfigDTO) {
        log.info("娣诲姞绯荤粺閰嶇疆,{}", systemConfigDTO);
        systemConfigService.addConfig(systemConfigDTO);
        return Result.success();
    }

    /**
     * 鏇存柊绯荤粺閰嶇疆
     * @param systemConfigDTO
     * @return
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "systemConfig", targetId = "#systemConfigDTO.id")
    public Result updateConfig(@Valid @RequestBody SystemConfigDTO systemConfigDTO) {
        log.info("鏇存柊绯荤粺閰嶇疆,{}", systemConfigDTO);
        systemConfigService.updateConfig(systemConfigDTO);
        return Result.success();
    }

    /**
     * 鎵归噺鍒犻櫎绯荤粺閰嶇疆
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "systemConfig", targetId = "#ids")
    public Result deleteConfig(@RequestParam List<Long> ids) {
        log.info("鎵归噺鍒犻櫎绯荤粺閰嶇疆,{}", ids);
        systemConfigService.batchDelete(ids);
        return Result.success();
    }
}

