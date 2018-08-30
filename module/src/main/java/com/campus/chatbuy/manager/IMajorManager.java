package com.campus.chatbuy.manager;

import com.campus.chatbuy.model.DicMajor;
import com.campus.chatbuy.model.DicUniversity;

import java.util.List;

/**
 * Created by jinku on 2017/7/12.
 */
public interface IMajorManager {

    List<DicMajor> queryAllMajor();

    List<DicMajor> queryMajor(String fLetter);

    List<DicUniversity> queryAllUniversity();

    List<DicMajor> queryMajorByName(String majorName);

}
