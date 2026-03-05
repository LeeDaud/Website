package cc.leedaud.mapper;

import cc.leedaud.annotation.AutoFill;
import cc.leedaud.dto.DailyViewCountDTO;
import cc.leedaud.dto.ViewPageQueryDTO;
import cc.leedaud.entity.Views;
import cc.leedaud.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ViewMapper {
    /**
     * 鎻掑叆涓€鏉℃祻瑙堣褰?     * @param view
     */
    void insert(Views view);

    /**
     * 鍒嗛〉鏌ヨ娴忚璁板綍
     * @param viewPageQueryDTO
     * @return
     */
    Page<Views> pageQuery(ViewPageQueryDTO viewPageQueryDTO);

    /**
     * 缁熻鎬绘祻瑙堥噺
     */
    @Select("select count(*) from views")
    Integer countTotal();

    /**
     * 缁熻浠婃棩娴忚閲?     */
    @Select("select count(*) from views where date(view_time) = curdate()")
    Integer countToday();

    /**
     * 缁熻鎸囧畾鏃ユ湡鑼冨洿鍐呮瘡鏃ユ祻瑙堥噺
     */
    List<DailyViewCountDTO> getDailyViewStats(LocalDate begin, LocalDate end);

    /**
     * 鎵归噺鍒犻櫎娴忚璁板綍
     * @param ids
     */
    void batchDelete(List<Long> ids);
}

