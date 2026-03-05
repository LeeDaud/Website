package cc.leedaud.controller.admin;

import cc.leedaud.dto.ViewPageQueryDTO;
import cc.leedaud.result.PageResult;
import cc.leedaud.result.Result;
import cc.leedaud.service.ViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绠＄悊绔祻瑙堣褰曟帴鍙? */
@Slf4j
@RestController("adminViewController")
@RequestMapping("/admin/view")
public class ViewController {

    @Autowired
    private ViewService viewService;

    /**
     * 鑾峰彇娴忚璁板綍鍒楄〃
     * @param viewPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> getViewList(ViewPageQueryDTO viewPageQueryDTO) {
        log.info("鑾峰彇娴忚璁板綍鍒楄〃,{}", viewPageQueryDTO);
        PageResult pageResult = viewService.pageQuery(viewPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 鎵归噺鍒犻櫎娴忚璁板綍
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result batchDelete(@RequestParam List<Long> ids) {
        log.info("鎵归噺鍒犻櫎娴忚璁板綍,{}", ids);
        viewService.batchDelete(ids);
        return Result.success();
    }
}

