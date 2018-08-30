CREATE TABLE dic_major (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键Id自增',
  majorName varchar(32) NOT NULL COMMENT '专业名称',
  validity tinyint DEFAULT 1 COMMENT '有效性（0=无效，1=有效）',
  fLetter char(1) NOT NULL COMMENT '首字母',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='专业字典表';


CREATE TABLE dic_university (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键Id自增',
  universityName varchar(32) NOT NULL COMMENT '大学名称',
  validity tinyint DEFAULT 1 COMMENT '有效性（0=无效，1=有效）',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='大学字典表';


CREATE TABLE user_info (
  userId bigint NOT NULL COMMENT '用户id主键',
  mobile char(11) NOT NULL COMMENT '手机号',
  userName varchar(16) NOT NULL COMMENT '用户名称',
  avatarPicId bigint DEFAULT NULL COMMENT '用户头像Id',
  sex tinyint NOT NULL COMMENT '性别（1=男，2=女）',
  universityId int(11) NOT NULL COMMENT '就读大学id',
  majorId int NOT NULL COMMENT '所学专业id',
  universityName varchar(32) NOT NULL COMMENT '大学名称',
  majorName varchar(32) NOT NULL COMMENT '专业名称',
  entranceYear char(4) NOT NULL COMMENT '入学年份',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (userId),
  KEY idx_majorId (majorId),
  UNIQUE KEY idx_mobile (mobile)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户信息表';


CREATE TABLE user_security (
  userId bigint NOT NULL COMMENT '用户id主键',
  mobile char(11) NOT NULL COMMENT '手机号',
  password varchar(64) NOT NULL COMMENT '加密后密码',
  roleType tinyint NOT NULL COMMENT '用户类型:1=普通用户;2=管理员',
  status tinyint NOT NULL COMMENT '账号状态信息(0=未激活，1=正常，2=锁定)',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`userId`),
  UNIQUE KEY idx_mobile (mobile)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户安全信息表';

CREATE TABLE user_sign (
  userId varchar(32) NOT NULL COMMENT '用户id主键',
  userSign text DEFAULT NULL COMMENT '用户帐号签名',
  signTime datetime NOT NULL COMMENT '签名时间',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户帐号签名表';

CREATE TABLE user_mobile_history (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键Id自增',
  userId bigint(20) NOT NULL COMMENT '用户id',
  mobile char(11) NOT NULL COMMENT '用户旧的电话号码',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户电话号码历史表';


CREATE TABLE dic_goods_category (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键Id自增',
  categoryName varchar(32) NOT NULL COMMENT '类型名称',
  parentId int DEFAULT 0 COMMENT '父类Id',
  validity tinyint DEFAULT 1 COMMENT '有效性（0=无效，1=有效）',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '商品分类字典表';


CREATE TABLE dic_goods_tag (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键Id自增',
  tagName varchar(10) NOT NULL COMMENT '标签名称',
  validity tinyint DEFAULT 1 COMMENT '有效性（0=无效，1=有效）',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '商品标签字典表';


CREATE TABLE goods_info (
  goodsId bigint(20) NOT NULL COMMENT '商品id主键',
  userId bigint(20) NOT NULL COMMENT '用户Id',
  goodsName varchar(64) NOT NULL COMMENT '商品名称',
  goodsDesc text DEFAULT NULL COMMENT '商品描述',
  categoryId bigint(20) DEFAULT NULL COMMENT '商品分类Id',
  originPrice bigint(20) NOT NULL COMMENT '原价',
  source varchar(16) DEFAULT NULL COMMENT '来源',
  price bigint(20) NOT NULL COMMENT '现价',
  payWay tinyint NOT NULL DEFAULT '1' COMMENT '支付方式：1=线下支付',
  status tinyint NOT NULL COMMENT '商品状态信息(0=未发布，1=已发布，2=已下架，3=已出售)',
  readNum bigint(20) DEFAULT 0 COMMENT '阅读量',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (goodsId) ,
  KEY idx_userId (userId) ,
  KEY idx_categoryId (categoryId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '商品信息表';


CREATE TABLE con_goods_tag (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键Id自增',
  goodsId bigint NOT NULL COMMENT '商品Id',
  tagId int NOT NULL COMMENT '标签Id',
  validity tinyint NOT NULL COMMENT '有效性（0=无效，1=有效）',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id) ,
  KEY idx_goodsId (goodsId) ,
  KEY idx_tagId (tagId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '商品标签关联表';


CREATE TABLE goods_pic (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键Id自增',
  goodsId bigint NOT NULL COMMENT '商品Id',
  picId bigint NOT NULL COMMENT '图片Id',
  validity tinyint NOT NULL COMMENT '有效性（0=无效，1=有效）',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id) ,
  KEY idx_goodsId (goodsId) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '商品图片表';


CREATE TABLE pic_info (
  picId bigint NOT NULL COMMENT '图片主键Id',
  originUrl varchar(128) NOT NULL COMMENT '原图地址',
  bigUrl varchar(128) NOT NULL COMMENT '大图图地址',
  smallUrl varchar(128) NOT NULL COMMENT '小图地址',
  width int NOT NULL COMMENT '原图宽',
  height int NOT NULL COMMENT '原图高',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (picId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '图片信息表';

CREATE TABLE goods_complain (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  initiatorId bigint NOT NULL COMMENT '投诉发起人Id',
  targetId bigint NOT NULL COMMENT '被投诉人Id',
  goodsId bigint NOT NULL COMMENT '相关商品id',
  reasonType tinyint NOT NULL COMMENT '投诉类型:0=其他;1=非在校学生;2=未来参与交易;3=未按规定时间交易;4=不按谈好价格交易',
  reasonDesc VARCHAR(256) DEFAULT NULL COMMENT '投诉原因',
  type tinyint NOT NULL COMMENT '投诉类型，买家投诉卖家1，卖家投诉买家2',
  status tinyint NOT NULL COMMENT '0：未处理，1：已处理，2：无效投诉',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '商品投诉信息表';

CREATE TABLE user_session (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  sessionId bigint NOT NULL COMMENT '会话Id',
  userId bigint NOT NULL COMMENT '买方用户Id',
  otherUserId bigint NOT NULL COMMENT '卖方用户Id',
  goodsId bigint NOT NULL COMMENT '商品Id',
  userType tinyint NOT NULL COMMENT '用户类型:1=买方;2=卖方',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_user_goods (userId, goodsId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户会话信息表';

CREATE TABLE trade_info (
  tradeId bigint NOT NULL COMMENT '流水号',
  goodsId bigint NOT NULL COMMENT '交易商品的id',
  tradePrice bigint NOT NULL COMMENT '交易价格',
  buyerId bigint NOT NULL COMMENT '购买者Id',
  sellerId bigint NOT NULL COMMENT '销售者Id',
  tradeTime varchar(32) NOT NULL COMMENT '交易时间',
  tradeAddress varchar(200) NOT NULL COMMENT '交易地址',
  tradeState tinyint NOT NULL COMMENT '交易状态',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (tradeId),
  KEY idx_goods_buyer (goodsId, buyerId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '商品交易表';

CREATE TABLE trade_record (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  userId bigint NOT NULL COMMENT '用户Id',
  otherUserId bigint NOT NULL COMMENT '对方Id',
  tradeType tinyint NOT NULL COMMENT '交易类型:1=购买;2=售卖',
  tradeId bigint NOT NULL COMMENT '流水号',
  goodsId bigint NOT NULL COMMENT '交易商品的id',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_user_goods (userId, goodsId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户交易记录表';

CREATE TABLE message (
  messageId bigint NOT NULL COMMENT '主键Id',
  sessionId bigint NOT NULL COMMENT '会话Id',
  fromUserId bigint NOT NULL COMMENT '发送者Id',
  toUserId bigint NOT NULL COMMENT '接收者Id',
  msgType tinyint NOT NULL COMMENT '消息类型',
  businessType tinyint NOT NULL COMMENT '业务类型',
  msgContent text DEFAULT NULL COMMENT '消息内容',
  createTime datetime NOT NULL COMMENT '创建时间',
  updateTime datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (messageId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '消息表';

CREATE TABLE black_list (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  user_id bigint NOT NULL COMMENT '用户Id',
  black_user_id bigint NOT NULL COMMENT '黑名单用户Id',
  validity tinyint NOT NULL COMMENT '有效性（0=无效，1=有效）',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_user_id (user_id, black_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '黑名单表';