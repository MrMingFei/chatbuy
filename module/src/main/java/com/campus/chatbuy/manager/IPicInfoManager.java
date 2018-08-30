package com.campus.chatbuy.manager;

import com.campus.chatbuy.bean.PicInfoBean;
import com.campus.chatbuy.bean.PicRequestInfo;
import com.campus.chatbuy.model.PicInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jinku on 2017/7/19.
 */
public interface IPicInfoManager {
    Map<Long, PicInfoBean> queryPicInfo(Set<Long> picIdSet) throws Exception;

    void savePicInfo(PicInfoBean picInfo);

    PicInfoBean queryPicInfo(long picId) throws Exception;
}
