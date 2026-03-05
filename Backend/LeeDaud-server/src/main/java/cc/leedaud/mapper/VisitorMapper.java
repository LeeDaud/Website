package cc.leedaud.mapper;

import cc.leedaud.annotation.AutoFill;
import cc.leedaud.dto.DailyVisitorCountDTO;
import cc.leedaud.dto.ProvinceCountDTO;
import cc.leedaud.dto.VisitorPageQueryDTO;
import cc.leedaud.entity.Visitors;
import cc.leedaud.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface VisitorMapper {
    /**
     * 鏍规嵁璁垮鎸囩汗鏌ヨ璁垮淇℃伅
     * @param fingerprint
     * @return
     */
    @Select("select * from visitors where fingerprint = #{fingerprint}")
    Visitors findVisitorByFingerprint(String fingerprint);

    /**
     * 鏍规嵁id鏌ヨ璁垮淇℃伅
     * @param id
     * @return
     */
    @Select("select * from visitors where id = #{id}")
    Visitors findById(Long id);

    /**
     * 鎻掑叆璁垮淇℃伅
     * @param visitor
     */
    @AutoFill(value = OperationType.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertVisitor(Visitors visitor);

    /**
     * 鏍规嵁id鏇存柊璁垮淇℃伅
     * @param visitor
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateById(Visitors visitor);

    /**
     * 鍒嗛〉鏌ヨ
     * @param visitorPageQueryDTO
     * @return
     */
    Page<Visitors> pageQuery(VisitorPageQueryDTO visitorPageQueryDTO);

    /**
     * 鎵归噺灏佺璁垮
     * @param ids
     */
    void batchBlock(List<Long> ids);

    /**
     * 鎵归噺瑙ｅ皝璁垮
     * @param ids
     */
    void batchUnblock(List<Long> ids);

    /**
     * 缁熻鎬昏瀹㈡暟
     */
    @Select("select count(*) from visitors")
    Integer countTotal();

    /**
     * 缁熻浠婃棩鏂板璁垮鏁?     */
    @Select("select count(*) from visitors where date(create_time) = curdate()")
    Integer countToday();

    /**
     * 缁熻鎸囧畾鏃ユ湡鑼冨洿鍐呮瘡鏃ユ柊澧炶瀹㈡暟
     */
    List<DailyVisitorCountDTO> getDailyNewVisitorStats(LocalDate begin, LocalDate end);

    /**
     * 缁熻璁垮鐪佷唤鍒嗗竷
     */
    List<ProvinceCountDTO> getProvinceDistribution();
}

