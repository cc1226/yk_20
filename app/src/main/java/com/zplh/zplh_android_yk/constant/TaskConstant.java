package com.zplh.zplh_android_yk.constant;

/**
 * Created by yong hao zeng on 2018/4/16/016.
 */
public class TaskConstant {
    //0,1,15,16 17 18 19 20 21 22 23 24 25 26
    public static final int TASK_WX_FRIENDS_DS = 1; //朋友圈点赞
    public static final int TASK_WX_SUM_FRIENDS = 15;//统计好友数量
    public static final int TASK_WX_SAVE_SIGNATURE = 16;//修改个性签名
    public static final int TASK_WX_SEND_GZH = 17;//发送公众号名片
    public static final int TASK_WX_FRIENDS_GAME = 18;//朋友圈启动游戏
    public static final int TASK_WX_FRIENDS_SHOP = 19;//朋友圈进入京东购物
    public static final int TASK_WX_FRIENDS_VEDIO = 20;//朋友圈小视频发布
    public static final int TASK_WX_MASS_VEDIO = 21;//好友发视频
    public static final int TASK_WX_CROWD_VEDIO = 22;//微信群发视频
    public static final int TASK_WX_ADD_XT = 23;//嗅探加好友
    public static final int TASK_WX_ADD_CROWD = 24;//拉群任务
    public static final int TASK_WX_COUNT = 25;//统计好友和群成员
    public static final int TASK_WX_CONTACTS_ADD_BIG = 26;//通讯录加高权重号
    public static final int TASK_WX_ONE_MSG = 10;//好友逐个发图片
    public static final int TASK_WX_CROWD_MSG = 11;//微信群发消息
    public static final int TASK_WX_GO_XIAO_CHENG_XU = 59;//进入小程序
    public static final int TASK_WX_QUN_TU_WEN = 30;//微信群发图文
    public static final int TASK_WX_SHOU_FU_KUAN = 61;//收付款
    public static final int TASK_WX_TONG_JI_ALL = 25;//群成员 好友统计
    public static final int TASK_WX_COLLECT_FR = 54;//收藏朋友圈内容
    public static final int TASK_WX_READ_FRIEND_CIRCLE = 52;//阅读朋友圈
    public static final int TASK_WX_LOOK_FR_CIRCLE = 53;//查看好友朋友圈
    public static final int TASK_WX_SETTING = 27;//微信通用设置
    public static final int TASK_WX_PHONE_SET = 28;//微信手机设置
    public static final int TASK_WX_CROWD_TUWEN = 30;//微信群发图文
    public static final int TASK_WX_INIT = 32;//微信初始化
    public static final int TASK_WX_TIME_START = 33;//微信定时开关
    public static final int TASK_WX_CHECK_COLLECT_CONTENT = 55;//查看收藏内容
    public static final int TASK_WX_READ_TENCENT_NEWS = 56;//阅读腾讯新闻
    public static final int TASK_WX_TOP_STORIES = 57;//启用看一看
    public static final int TASK_WX_USER_SEARCH = 58;//启用搜一搜
    public static final int TASK_WX_CHECK_WALLET = 60;//    查看零钱明细
    public static final int TASK_WX_FIND_DEV = 37;//微信寻找设备
    public static final int Task_WX_ADD_FRIEND = 5;//通讯录加好友


    public static String getTaskNameForID(int taskId){
        switch (taskId){
            case TASK_WX_FRIENDS_DS:
                return "朋友圈点赞";
            case TASK_WX_SUM_FRIENDS:
                return "统计好友数量";
            case TASK_WX_SAVE_SIGNATURE:
                return "修改个性签名";
            case TASK_WX_SEND_GZH:
                return "发送公众号名片";
            case Task_WX_ADD_FRIEND:
                return "通讯录加好友";
            case TASK_WX_COUNT:
                return "统计好友和群成员";

        }
        return "未知任务";
    }

}
