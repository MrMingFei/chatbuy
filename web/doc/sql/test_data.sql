INSERT INTO `dic_major`(`majorName`, `validity`, `categoryName`, `updateTime`, `createTime`) VALUES ('计算机专业', '1', '理学门类', null, now());
INSERT INTO `dic_major`(`majorName`, `validity`, `categoryName`, `updateTime`, `createTime`) VALUES ('自动化', '1', '理学门类', null, now());
INSERT INTO `dic_major`(`majorName`, `validity`, `categoryName`, `updateTime`, `createTime`) VALUES ('英语', '1', '教学类', null, now());

INSERT INTO `dic_university`(`universityName`, `validity`, `updateTime`, `createTime`) VALUES ('东北大学秦皇岛校区', '1', null, now());
INSERT INTO `dic_university`(`universityName`, `validity`, `updateTime`, `createTime`) VALUES ('东北大学', '1', null, now());
INSERT INTO `dic_university`(`universityName`, `validity`, `updateTime`, `createTime`) VALUES ('北京大学', '1', null, now());

INSERT INTO `dic_goods_category`(`categoryName`, `validity`, `updateTime`, `createTime`) VALUES ('文具', '1', null, now());
INSERT INTO `dic_goods_category`(`categoryName`, `validity`, `updateTime`, `createTime`) VALUES ('电脑配件', '1', null, now());
INSERT INTO `dic_goods_category`(`categoryName`, `validity`, `updateTime`, `createTime`) VALUES ('书籍', '1', null, now());

INSERT INTO `dic_goods_tag`(`tagName`, `validity`, `updateTime`, `createTime`) VALUES ('5折送', '1', null, now());
INSERT INTO `dic_goods_tag`(`tagName`, `validity`, `updateTime`, `createTime`) VALUES ('聊嗨免费送', '1', null, now());
INSERT INTO `dic_goods_tag`(`tagName`, `validity`, `updateTime`, `createTime`) VALUES ('精品', '0', null, now());
INSERT INTO `dic_goods_tag`(`tagName`, `validity`, `updateTime`, `createTime`) VALUES ('女神', '1', null, now());


INSERT INTO `user_info`(`userId`, `mobile`, `userName`, `avatar`, `sex`, `birthDate`, `universityId`, `majorId`, `entranceYear`, `updateTime`, `createTime`)
VALUES ('11111', '18713506432', 'lijk', 'http://www.baidu.com', 1, '1992-05-25', 1, 1, 2010, null, now());

INSERT INTO `user_info`(`userId`, `mobile`, `userName`, `avatar`, `sex`, `birthDate`, `universityId`, `majorId`, `entranceYear`, `updateTime`, `createTime`)
VALUES ('11112', '18713506435', 'niumk', 'http://www.google.com', 1, '1990-09-10', 1, 2, 2012, null, now());

INSERT INTO `user_info`(`userId`, `mobile`, `userName`, `avatar`, `sex`, `birthDate`, `universityId`, `majorId`, `entranceYear`, `updateTime`, `createTime`)
VALUES ('22222', '18713506430', 'dashen', 'http://www.facebook.com', 1, '1993-01-25', 3, 3, 2011, null, now());


INSERT INTO `goods_info`(`goodsId`, `userId`, `goodsName`, `goodsDesc`, `categoryId`, `originPrice`, `source`, `price`, `payWay`, `status`, `updateTime`, `createTime`)
VALUES ('55555', '11111', '程序员是怎样炼成的', '程序员是怎样炼成ddfdfdfdfdfdf', 3, '3000', 1, 500, 1, 1, null, now());

INSERT INTO `goods_info`(`goodsId`, `userId`, `goodsName`, `goodsDesc`, `categoryId`, `originPrice`, `source`, `price`, `payWay`, `status`, `updateTime`, `createTime`)
VALUES ('55556', '11112', '英语4/6级', '英语4/6级fdfdfdfdf', 3, '4000', 1, 600, 1, 1, null, now());

INSERT INTO `goods_info`(`goodsId`, `userId`, `goodsName`, `goodsDesc`, `categoryId`, `originPrice`, `source`, `price`, `payWay`, `status`, `updateTime`, `createTime`)
VALUES ('55557', '11111', '键盘', '程序员专用祖传机械键盘', 2, '30000', 1, 5000, 1, 1, null, now());

INSERT INTO `goods_info`(`goodsId`, `userId`, `goodsName`, `goodsDesc`, `categoryId`, `originPrice`, `source`, `price`, `payWay`, `status`, `updateTime`, `createTime`)
VALUES ('55558', '22222', '铅笔', '妹子使用过的铅笔', 1, '1000', 1, 200, 1, 3, null, now());



INSERT INTO `con_goods_tag`(`goodsId`, `tagId`, `updateTime`, `createTime`) VALUES ('55555', 1, null, now());
INSERT INTO `con_goods_tag`(`goodsId`, `tagId`, `updateTime`, `createTime`) VALUES ('55555', 2, null, now());

INSERT INTO `con_goods_tag`(`goodsId`, `tagId`, `updateTime`, `createTime`) VALUES ('55556', 3, null, now());
INSERT INTO `con_goods_tag`(`goodsId`, `tagId`, `updateTime`, `createTime`) VALUES ('55556', 4, null, now());

INSERT INTO `con_goods_tag`(`goodsId`, `tagId`, `updateTime`, `createTime`) VALUES ('55557', 1, null, now());
INSERT INTO `con_goods_tag`(`goodsId`, `tagId`, `updateTime`, `createTime`) VALUES ('55557', 2, null, now());
INSERT INTO `con_goods_tag`(`goodsId`, `tagId`, `updateTime`, `createTime`) VALUES ('55557', 3, null, now());
INSERT INTO `con_goods_tag`(`goodsId`, `tagId`, `updateTime`, `createTime`) VALUES ('55557', 4, null, now());

INSERT INTO `con_goods_tag`(`goodsId`, `tagId`, `updateTime`, `createTime`) VALUES ('55558', 1, null, now());
INSERT INTO `con_goods_tag`(`goodsId`, `tagId`, `updateTime`, `createTime`) VALUES ('55558', 2, null, now());
INSERT INTO `con_goods_tag`(`goodsId`, `tagId`, `updateTime`, `createTime`) VALUES ('55558', 3, null, now());


SELECT
    goods_info.goodsId, goods_info.goodsName, goods_info.categoryId, user_info.universityId, user_info.majorId, con_goods_tag.tagId, user_info.majorId
FROM
    goods_info
        LEFT JOIN
    user_info ON goods_info.userId = user_info.userId
        LEFT JOIN
    con_goods_tag ON goods_info.goodsId = con_goods_tag.goodsId
WHERE
    con_goods_tag.tagId in (1,2,3)
	AND user_info.universityId = 1
Group By goods_info.goodsId;
