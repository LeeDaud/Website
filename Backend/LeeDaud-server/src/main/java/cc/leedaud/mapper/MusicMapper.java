package cc.leedaud.mapper;

import cc.leedaud.annotation.AutoFill;
import cc.leedaud.dto.MusicPageQueryDTO;
import cc.leedaud.entity.Music;
import cc.leedaud.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MusicMapper {
    /**
     * 鎻掑叆闊充箰
     * @param music
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Music music);

    /**
     * 鍒嗛〉鏌ヨ闊充箰
     * @param musicPageQueryDTO
     * @return
     */
    Page<Music> pageQuery(MusicPageQueryDTO musicPageQueryDTO);

    /**
     * 鏇存柊闊充箰
     * @param music
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Music music);

    /**
     * 鍒犻櫎闊充箰
     * @param id
     */
    @Delete("delete from music where id = #{id}")
    void deleteById(Long id);

    /**
     * 鎵归噺鍒犻櫎闊充箰
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 鏍规嵁ID鏌ヨ闊充箰
     * @param id
     * @return
     */
    @Select("select * from music where id = #{id}")
    Music getById(Long id);

    /**
     * 鑾峰彇鎵€鏈夊彲瑙佺殑闊充箰
     * @return
     */
    @Select("select * from music where is_visible = 1 order by sort asc, id desc")
    List<Music> getAllVisibleMusic();
}

