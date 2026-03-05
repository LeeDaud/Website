package cc.leedaud.service.impl;


import cc.leedaud.dto.PersonalInfoDTO;
import cc.leedaud.entity.PersonalInfo;
import cc.leedaud.mapper.PersonalInfoMapper;
import cc.leedaud.service.PersonalInfoService;
import cc.leedaud.vo.PersonalInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PersonalInfoServiceImpl implements PersonalInfoService {

    @Autowired
    private PersonalInfoMapper personalInfoMapper;

    /**
     * 绠＄悊绔幏鍙栨墍鏈変釜浜轰俊鎭?     * @return
     */
    @Cacheable(value = "personalInfo", key = "'all'")
    public PersonalInfo getAllPersonalInfo() {
        PersonalInfo personalInfo = personalInfoMapper.getPersonalInfo();
        return personalInfo;
    }

    /**
     * 绠＄悊绔洿鏂颁釜浜轰俊鎭?     * @param personalInfo
     */
    @CacheEvict(value = "personalInfo", allEntries = true)
    public void updatePersonalInfo(PersonalInfoDTO personalInfoDTO) {
        PersonalInfo personalInfo = new PersonalInfo();
        BeanUtils.copyProperties(personalInfoDTO, personalInfo);
        // 鏇存柊涓汉淇℃伅
        personalInfoMapper.updateById(personalInfo);
    }

    /**
     * 鍏朵粬绔幏鍙栦釜浜轰俊鎭?     * @return
     */
    @Cacheable(value = "personalInfo", key = "'vo'")
    public PersonalInfoVO getPersonalInfo() {
        PersonalInfo personalInfo = personalInfoMapper.getPersonalInfo();
        PersonalInfoVO personalInfoVO = PersonalInfoVO.builder()
                .id(personalInfo.getId())
                .nickname(personalInfo.getNickname())
                .tag(personalInfo.getTag())
                .description(personalInfo.getDescription())
                .avatar(personalInfo.getAvatar())
                .website(personalInfo.getWebsite())
                .email(personalInfo.getEmail())
                .github(personalInfo.getGithub())
                .location(personalInfo.getLocation())
                .build();
        return personalInfoVO;
    }
}

