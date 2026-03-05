package cc.leedaud.service;

import cc.leedaud.dto.PersonalInfoDTO;
import cc.leedaud.entity.PersonalInfo;
import cc.leedaud.vo.PersonalInfoVO;

public interface PersonalInfoService {
    /**
     * 鑾峰彇涓汉淇℃伅
     * @return
     */
    PersonalInfo getAllPersonalInfo();

    /**
     * 鏇存柊涓汉淇℃伅
     * @param personalInfoDTO
     */
    void updatePersonalInfo(PersonalInfoDTO personalInfoDTO);

    /**
     * 鍏朵粬绔幏鍙栦釜浜轰俊鎭?     * @return
     */
    PersonalInfoVO getPersonalInfo();
}

