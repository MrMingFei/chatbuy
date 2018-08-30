CREATE TABLE donate_goods (
  goods_id bigint NOT NULL COMMENT '商品的id',
  user_id bigint NOT NULL COMMENT '捐赠人Id',
  delivery_type tinyint NOT NULL COMMENT '配送方式：1=自己送；2=上门取',
  pick_place varchar(128) DEFAULT NULL COMMENT '取货地点',
  pick_time_week tinyint(4) DEFAULT NULL COMMENT '取货时间：星期几',
  pick_time_day tinyint(4) DEFAULT NULL COMMENT '取货时间：上午或下午',
  donate_status tinyint NOT NULL COMMENT '处理状态：0=待审核;1=审核拒绝;2=待接收;3=接收拒绝;4=待投递;5=投递中;6=已送达',
  refuse_reason varchar(128) DEFAULT NULL COMMENT '拒收原因',
  refuse_time datetime DEFAULT NULL COMMENT '拒收时间',
  delivery_time datetime DEFAULT NULL COMMENT '送达时间',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (goods_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '捐赠商品表';