package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.PicInfoBean;
import com.campus.chatbuy.bean.PicRequestInfo;
import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.config.QStorageConfig;
import com.campus.chatbuy.id.generator.IdGeneratorFactory;
import com.campus.chatbuy.manager.IPicInfoManager;
import com.campus.chatbuy.model.PicInfo;
import com.campus.chatbuy.storage.QPictureUtil;
import com.campus.chatbuy.util.StringUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.guzz.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by jinku on 17-7-15.
 * <p/>
 * 图片上传：用户头像/商品图片
 */

@Controller
@RequestMapping(value = "/chatbuy/web/picture")
public class PicInfoAction {

    private static final Logger log = Logger.getLogger(PicInfoAction.class);

    @Autowired
    private IPicInfoManager picInfoManager;

    @RequestMapping(value = "/savePicInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean savePicInfo(PicRequestInfo picInfo) throws Exception {
        log.info("savePicInfo picInfo is [" + picInfo + "]");

        Assert.assertNotEmpty(picInfo.getSource_url(), "source_url is null");
        PicInfoBean picInfoBean = QPictureUtil.queryPicInfo(picInfo.getSource_url());
        // 保存图片记录
        picInfoBean.setPicId(picInfo.getPicId());
        picInfoManager.savePicInfo(picInfoBean);
        return ResultBean.success(picInfoBean);
    }

    @RequestMapping(value = "/picInfo", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean picInfo(Long picId) throws Exception {
        Assert.assertNotNull(picId, "picId is null");
        PicInfoBean picInfoBean = picInfoManager.queryPicInfo(picId);
        return ResultBean.success(picInfoBean);
    }

    /**
     * 用户头像上传
     *
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/uploadAvatar", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean uploadAvatar(MultipartFile file) throws Exception {
        PicInfoBean picInfo = QPictureUtil.uploadPic(file.getOriginalFilename(), QStorageConfig.avatarPath, file
                .getBytes());
        picInfoManager.savePicInfo(picInfo);
        PicInfoBean picInfoBean = new PicInfoBean();
        BeanUtils.copyProperties(picInfoBean, picInfo);
        return ResultBean.success(picInfoBean);
    }

    /**
     * 商品图片上传
     *
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/uploadGoodsPic", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean uploadGoodsPic(MultipartFile file) throws Exception {
        PicInfoBean picInfo = QPictureUtil.uploadPic(file.getOriginalFilename(), QStorageConfig.goodsPicPath, file
                .getBytes());
        picInfoManager.savePicInfo(picInfo);
        PicInfoBean picInfoBean = new PicInfoBean();
        BeanUtils.copyProperties(picInfoBean, picInfo);
        return ResultBean.success(picInfoBean);
    }
}
