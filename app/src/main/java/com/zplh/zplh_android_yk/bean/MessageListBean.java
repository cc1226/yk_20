package com.zplh.zplh_android_yk.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lichun on 2017/6/27.
 * Description:
 */

public class MessageListBean implements Serializable {

    /**
     * content : {"data":[{"log_id":"1","param":{"dz_num":""},"range":[],"task_id":"1"},{"log_id":"2","param":{"dz_num":"2"},"range":[],"task_id":"1"},{"log_id":"3","param":{"dz_num":"2"},"range":[],"task_id":"1"},{"log_id":"4","param":{"dz_num":"2"},"range":[],"task_id":"1"}]}
     */

    private ContentBean content;

    public ContentBean getContent() {
        return content;
    }


    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean implements Serializable {
        private List<DataBean> data;

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean implements Serializable {
            @Override
            public String toString() {
                return "DataBean{" +
                        "param=" + param +
                        ", task_id=" + task_id +
                        ", todo_time='" + todo_time + '\'' +
                        ", range=" + range +
                        '}';
            }

            /**
             * param : {"dz_num":"5"}
             * range : ["1376","1359","1361","1378","1379","1377","1377","1360"]
             * task_id : 1
             * todo_time :
             */
            private ParamBean param;
            private int task_id;
            private String todo_time;
            private List<String> range;
            private String log_id;

            public boolean isListTask() {
                return isListTask;
            }

            public void setListTask(boolean listTask) {
                isListTask = listTask;
            }

            private boolean isListTask;//是否是一组任务，如果是一组任务设置间隔时间

            public String getLog_id() {
                return log_id;
            }

            public void setLog_id(String log_id) {
                this.log_id = log_id;
            }

            public ParamBean getParam() {
                return param;
            }

            public void setParam(ParamBean param) {
                this.param = param;
            }

            public int getTask_id() {
                return task_id;
            }

            public void setTask_id(int task_id) {
                this.task_id = task_id;
            }

            public String getTodo_time() {
                return todo_time;
            }

            public void setTodo_time(String todo_time) {
                this.todo_time = todo_time;
            }

            public List<String> getRange() {
                return range;
            }

            public void setRange(List<String> range) {
                this.range = range;
            }

            public static class ParamBean implements Serializable {
                public String ali_get_num_s;
                public String ali_get_num_e;

                public String getAli_get_num_s() {
                    return ali_get_num_s;
                }

                public void setAli_get_num_s(String ali_get_num_s) {
                    this.ali_get_num_s = ali_get_num_s;
                }

                public String getAli_get_num_e() {
                    return ali_get_num_e;
                }

                public void setAli_get_num_e(String ali_get_num_e) {
                    this.ali_get_num_e = ali_get_num_e;
                }


                public String table;

                public String getTable() {
                    return table;
                }

                public void setTable(String table) {
                    this.table = table;
                }

                private String del_content ; // 删除朋友圈指定内容

                public String getDel_content() {
                    return del_content;
                }

                public void setDel_content(String del_content) {
                    this.del_content = del_content;
                }


                public String gender;

                public String getGender() {
                    return gender;
                }

                public void setGender(String gender) {
                    this.gender = gender;
                }

                public String is_gender;

                public String getIs_gender() {
                    return is_gender;
                }

                public void setIs_gender(String is_gender) {
                    this.is_gender = is_gender;
                }

                private String ss_comment;

                public String getSs_comment() {
                    return ss_comment;
                }

                public void setSs_comment(String ss_comment) {
                    this.ss_comment = ss_comment;
                }

                private String count ;

                public String getCount() {
                    return count;
                }

                public void setCount(String count) {
                    this.count = count;
                }

                public String[] is_accType;

                public String[] getIs_accType() {
                    return is_accType;
                }

                public void setIs_accType(String[] is_accType) {
                    this.is_accType = is_accType;
                }
                private String fanType ;     //粉丝类型
                private String fanId ;          //  粉丝ID
                private String addGroupNum ;  //拉群次数
                private String groupType ; //群类型
                private String preGrpName ; // 群名前缀
                private String personNum ; // 群人数

                public String getFanType() {
                    return fanType;
                }

                public void setFanType(String fanType) {
                    this.fanType = fanType;
                }

                public String getFanId() {
                    return fanId;
                }

                public void setFanId(String fanId) {
                    this.fanId = fanId;
                }

                public String getAddGroupNum() {
                    return addGroupNum;
                }

                public void setAddGroupNum(String addGroupNum) {
                    this.addGroupNum = addGroupNum;
                }

                public String getGroupType() {
                    return groupType;
                }

                public void setGroupType(String groupType) {
                    this.groupType = groupType;
                }

                public String getPreGrpName() {
                    return preGrpName;
                }

                public void setPreGrpName(String preGrpName) {
                    this.preGrpName = preGrpName;
                }

                public String getPersonNum() {
                    return personNum;
                }

                public void setPersonNum(String personNum) {
                    this.personNum = personNum;
                }

                private String account ;

                public String getAccount() {
                    return account;
                }

                public void setAccount(String account) {
                    this.account = account;
                }

                private String[] is_newAdd;

                public String[] getIs_newAdd() {
                    return is_newAdd;
                }

                public void setIs_newAdd(String[] is_newAdd) {
                    this.is_newAdd = is_newAdd;
                }

                /**
                 * dz_num : 5
                 */
                private String phone_add_num;//一次任务每部手机最多请求加好友次数

                private String wx_add_num;//一次任务每个微信最多请求加好友次数

                private String dz_num;//朋友圈点赞

                private String materia_id;//图片素材ID//也可以成为视频素材ID

                private String is_remind;	// 设置朋友圈发布查看权限

                private String is_protect;//是否设置为朋友圈视频私密

                private String one_add_num;	/*13*///一个微信号每次任务最多请求加好友次数(通讯录加好友)

                private String day_add_num;	/*10*///一个微信号每天最多请求加好友次数:(通讯录加好友)

                private String day_add_num_e;



                public String getDay_add_num_e() {
                    return day_add_num_e;
                }

                public void setDay_add_num_e(String day_add_num_e) {
                    this.day_add_num_e = day_add_num_e;
                }

                public String getDay_add_num_s() {
                    return day_add_num_s;
                }

                public void setDay_add_num_s(String day_add_num_s) {
                    this.day_add_num_s = day_add_num_s;
                }

                private String day_add_num_s;

                private String is_verify;	/*1*///添加认证信息(通讯录加好友)

                private String sniffing_type;	/*1*///嗅探加好友 好友来源方式

                private String materia_pic;//微信群发图片

                private String materia_vedio;//微信群发视频

                private String is_mass;//好友发消息

                private String materia_msg;//好友发文字内容

                private String designated;//指定好友发消息

                public String getDesignated() {
                    return designated;
                }

                public void setDesignated(String designated) {
                    this.designated = designated;
                }

                private List<String> materia_phone ;//后台发送过来的手机号码 并且进行接收

                private String interval_time;//执行任务间隔时间

                private String download;//APK下载地址

                private String ali_add_num;//每次添加好友的次数

                private String task_time_s;//每一个任务间隔时间 开始点

                private String random_time_s;//任务启动随机时间 开始

                private String remark_interval_time_e;//修改备注结束时间

                private String remark_interval_time_s;//修改备注间隔时间

                private String agree_interval_time_s;//自动通过好友开始时间

                private String agree_interval_time_e;//自动通过好友结束时间

                private String task_time_e;//每一个任务结束点

                private String random_time_e;//任务启动时间 结束

                private String msg_interval_time_e;//单次发消息间隔时间 结束

                private String msg_interval_time_s;//单次发消息间隔时间 开始

                private String add_interval_time_s;//单词加好友间隔时间 开始

                private String add_interval_time_e;//单次加好友结束间隔时间

                private String dz_num_e;//点赞次数结束

                private String dz_num_s;//点赞次数开始

                private String wx_add_num_e;//微信搜索加好友 结束

                private String wx_add_num_s;//微信搜索加好友 开始

                private String dz_interval_e;//点赞间隔

                private String dz_interval_s;//点赞间隔

                private String maxPullNum;  //支付宝拉群最大值

                private String pullNum;     //支付宝拉群最大数

                private int blockType;//初始化类型

                private String start_time_s;//支付宝加好友随机起始时间

                private String start_time_e;//支付宝加好友随机结束时间

                private String send_text;

                public String getSend_text() {
                    return send_text;
                }

                public void setSend_text(String send_text) {
                    this.send_text = send_text;
                }

                public String getStart_time_s() {
                    return start_time_s;
                }

                public void setStart_time_s(String start_time_s) {
                    this.start_time_s = start_time_s;
                }

                public String getStart_time_e() {
                    return start_time_e;
                }

                public void setStart_time_e(String start_time_e) {
                    this.start_time_e = start_time_e;
                }

                public int getBlockType() {
                    return blockType;
                }

                public void setBlockType(int blockType) {
                    this.blockType = blockType;
                }

                public String getMaxPullNum() {
                    return maxPullNum;
                }

                public void setMaxPullNum(String maxPullNum) {
                    this.maxPullNum = maxPullNum;
                }

                public String getPullNum() {
                    return pullNum;
                }

                public void setPullNum(String pullNum) {
                    this.pullNum = pullNum;
                }

                public String getVideo_time_e() {
                    return video_time_e;
                }

                public void setVideo_time_e(String video_time_e) {
                    this.video_time_e = video_time_e;
                }

                public String getVideo_time_s() {
                    return video_time_s;
                }

                public void setVideo_time_s(String video_time_s) {
                    this.video_time_s = video_time_s;
                }

                public String getVoice_time_e() {
                    return voice_time_e;
                }

                public void setVoice_time_e(String voice_time_e) {
                    this.voice_time_e = voice_time_e;
                }

                public String getVoice_time_s() {
                    return voice_time_s;
                }

                public void setVoice_time_s(String voice_time_s) {
                    this.voice_time_s = voice_time_s;
                }

                private String video_time_e;//视频间隔

                private String video_time_s;//视频间隔

                private String voice_time_e;//语音间隔

                private String voice_time_s;//语音间隔

                private String crowd_ad_time_e;//群发单间隔

                private String crowd_ad_time_s;

                private String record_time_e;//语音录制时长

                private String record_time_s;

                private String article_num_e;//公众号文章浏览篇数

                private String article_num_s;

                private String publicName;//关注公众号

                private String read_time_e;//公众号停留时间

                private String read_time_s;

                private String is_statistic;//支付宝发单是否统计

                private String publicType;//关注公众号方式

                private String typeName;

                public String getSlip_time_e() {
                    return slip_time_e;
                }

                public void setSlip_time_e(String slip_time_e) {
                    this.slip_time_e = slip_time_e;
                }

                public String getSlip_time_s() {
                    return slip_time_s;
                }

                public void setSlip_time_s(String slip_time_s) {
                    this.slip_time_s = slip_time_s;
                }

                private String slip_time_e;//浏览公众号滑动次数

                private String slip_time_s;//浏览公众号滑动次数

                public String getPublicType() {
                    return publicType;
                }

                public void setPublicType(String publicType) {
                    this.publicType = publicType;
                }

                public String getTypeName() {
                    return typeName;
                }

                public void setTypeName(String typeName) {
                    this.typeName = typeName;
                }

                public String getIs_statistic() {
                    return is_statistic;
                }

                public void setIs_statistic(String is_statistic) {
                    this.is_statistic = is_statistic;
                }

                public String getArticle_num_e() {
                    return article_num_e;
                }

                public void setArticle_num_e(String article_num_e) {
                    this.article_num_e = article_num_e;
                }

                public String getArticle_num_s() {
                    return article_num_s;
                }

                public void setArticle_num_s(String article_num_s) {
                    this.article_num_s = article_num_s;
                }

                public String getPublicName() {
                    return publicName;
                }

                public void setPublicName(String publicName) {
                    this.publicName = publicName;
                }

                public String getRead_time_e() {
                    return read_time_e;
                }

                public void setRead_time_e(String read_time_e) {
                    this.read_time_e = read_time_e;
                }

                public String getRead_time_s() {
                    return read_time_s;
                }

                public void setRead_time_s(String read_time_s) {
                    this.read_time_s = read_time_s;
                }

                public String getCrowd_ad_time_e() {
                    return crowd_ad_time_e;
                }

                public void setCrowd_ad_time_e(String crowd_ad_time_e) {
                    this.crowd_ad_time_e = crowd_ad_time_e;
                }

                public String getCrowd_ad_time_s() {
                    return crowd_ad_time_s;
                }

                public void setCrowd_ad_time_s(String crowd_ad_time_s) {
                    this.crowd_ad_time_s = crowd_ad_time_s;
                }

                public String getRecord_time_e() {
                    return record_time_e;
                }

                public void setRecord_time_e(String record_time_e) {
                    this.record_time_e = record_time_e;
                }

                public String getRecord_time_s() {
                    return record_time_s;
                }

                public void setRecord_time_s(String record_time_s) {
                    this.record_time_s = record_time_s;
                }

                private String materia_url;

                private int boot_time_h;//开机小时

                private int off_time_m;//关机分钟

                private int off_time_h;//关机小时

                private int boot_time_m;//开机分钟

                private String send_type;//分享链接  朋友圈或好友状态

                private String crowd;//支付宝发单群

                private String more ;// 支付宝发单群类型

                public String getMore() {
                    return more;
                }

                public void setMore(String more) {
                    this.more = more;
                }

                private String device;//支付宝发单设备

                public String getCrowd() {
                    return crowd;
                }

                public void setCrowd(String crowd) {
                    this.crowd = crowd;
                }

                public String getDevice() {
                    return device;
                }

                public void setDevice(String device) {
                    this.device = device;
                }

                public String getSend_type() {
                    return send_type;
                }

                public void setSend_type(String send_type) {
                    this.send_type = send_type;
                }

                public int getBoot_time_h() {
                    return boot_time_h;
                }

                public void setBoot_time_h(int boot_time_h) {
                    this.boot_time_h = boot_time_h;
                }

                public int getOff_time_m() {
                    return off_time_m;
                }

                public void setOff_time_m(int off_time_m) {
                    this.off_time_m = off_time_m;
                }

                public int getOff_time_h() {
                    return off_time_h;
                }

                public void setOff_time_h(int off_time_h) {
                    this.off_time_h = off_time_h;
                }

                public int getBoot_time_m() {
                    return boot_time_m;
                }

                public void setBoot_time_m(int boot_time_m) {
                    this.boot_time_m = boot_time_m;
                }

                public String getMateria_url() {
                    return materia_url;
                }

                public void setMateria_url(String materia_url) {
                    this.materia_url = materia_url;
                }

                public String getDz_interval_e() {
                    return dz_interval_e;
                }

                public void setDz_interval_e(String dz_interval_e) {
                    this.dz_interval_e = dz_interval_e;
                }

                public String getDz_interval_s() {
                    return dz_interval_s;
                }

                public void setDz_interval_s(String dz_interval_s) {
                    this.dz_interval_s = dz_interval_s;
                }

                private String contact_verify_msg;//搜索加好友申请内容//可以作为通讯录加好友申请内容

                private String one_add_num_s;//通讯录加好友 开始

                private String one_add_num_e;//通讯录加好友 结束

                private String remark;//修改备注的称呼内容

                private String type;//养号互撩状态

                private int version;//版本
            private String preRemark;//修改前前缀

                public String getPreRemark() {
                    return preRemark;
                }

                public void setPreRemark(String preRemark) {
                    this.preRemark = preRemark;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                private String phoneRadio;//手机设置1是手机重启 2是手机关机 3是清理手机内存 4手机微信手机 5是结束当前任务 6是结束全部任务

                public String getPhoneRadio() {
                    return phoneRadio;
                }

                public void setPhoneRadio(String phoneRadio) {
                    this.phoneRadio = phoneRadio;
                }

                public String getDz_num_e() {
                    return dz_num_e;
                }

                public void setDz_num_e(String dz_num_e) {
                    this.dz_num_e = dz_num_e;
                }

                public String getDz_num_s() {
                    return dz_num_s;
                }

                public void setDz_num_s(String dz_num_s) {
                    this.dz_num_s = dz_num_s;
                }

                public String getWx_add_num_e() {
                    return wx_add_num_e;
                }

                public void setWx_add_num_e(String wx_add_num_e) {
                    this.wx_add_num_e = wx_add_num_e;
                }

                public String getWx_add_num_s() {
                    return wx_add_num_s;
                }

                public void setWx_add_num_s(String wx_add_num_s) {
                    this.wx_add_num_s = wx_add_num_s;
                }

                public String getContact_verify_msg() {
                    return contact_verify_msg;
                }

                public void setContact_verify_msg(String contact_verify_msg) {
                    this.contact_verify_msg = contact_verify_msg;
                }

                public String getOne_add_num_s() {
                    return one_add_num_s;
                }

                public void setOne_add_num_s(String one_add_num_s) {
                    this.one_add_num_s = one_add_num_s;
                }

                public String getOne_add_num_e() {
                    return one_add_num_e;
                }

                public void setOne_add_num_e(String one_add_num_e) {
                    this.one_add_num_e = one_add_num_e;
                }

                public String getRemark() {
                    return remark;
                }

                public void setRemark(String remark) {
                    this.remark = remark;
                }

                public String getTask_time_s() {
                    return task_time_s;
                }

                public void setTask_time_s(String task_time_s) {
                    this.task_time_s = task_time_s;
                }

                public String getRandom_time_s() {
                    return random_time_s;
                }

                public void setRandom_time_s(String random_time_s) {
                    this.random_time_s = random_time_s;
                }

                public String getRemark_interval_time_e() {
                    return remark_interval_time_e;
                }

                public void setRemark_interval_time_e(String remark_interval_time_e) {
                    this.remark_interval_time_e = remark_interval_time_e;
                }

                public String getRemark_interval_time_s() {
                    return remark_interval_time_s;
                }

                public void setRemark_interval_time_s(String remark_interval_time_s) {
                    this.remark_interval_time_s = remark_interval_time_s;
                }

                public String getAgree_interval_time_s() {
                    return agree_interval_time_s;
                }

                public void setAgree_interval_time_s(String agree_interval_time_s) {
                    this.agree_interval_time_s = agree_interval_time_s;
                }

                public String getAgree_interval_time_e() {
                    return agree_interval_time_e;
                }

                public void setAgree_interval_time_e(String agree_interval_time_e) {
                    this.agree_interval_time_e = agree_interval_time_e;
                }

                public String getTask_time_e() {
                    return task_time_e;
                }

                public void setTask_time_e(String task_time_e) {
                    this.task_time_e = task_time_e;
                }

                public String getRandom_time_e() {
                    return random_time_e;
                }

                public void setRandom_time_e(String random_time_e) {
                    this.random_time_e = random_time_e;
                }

                public String getMsg_interval_time_e() {
                    return msg_interval_time_e;
                }

                public void setMsg_interval_time_e(String msg_interval_time_e) {
                    this.msg_interval_time_e = msg_interval_time_e;
                }

                public String getMsg_interval_time_s() {
                    return msg_interval_time_s;
                }

                public void setMsg_interval_time_s(String msg_interval_time_s) {
                    this.msg_interval_time_s = msg_interval_time_s;
                }

                public String getAdd_interval_time_s() {
                    return add_interval_time_s;
                }

                public void setAdd_interval_time_s(String add_interval_time_s) {
                    this.add_interval_time_s = add_interval_time_s;
                }

                public String getAdd_interval_time_e() {
                    return add_interval_time_e;
                }

                public void setAdd_interval_time_e(String add_interval_time_e) {
                    this.add_interval_time_e = add_interval_time_e;
                }

                public String getAli_add_num() {
                    return ali_add_num;
                }

                public void setAli_add_num(String ali_add_num) {
                    this.ali_add_num = ali_add_num;
                }

                public String getDownload() {
                    return download;
                }

                public void setDownload(String download) {
                    this.download = download;
                }

                public String getInterval_time() {
                    return interval_time;
                }

                public void setInterval_time(String interval_time) {
                    this.interval_time = interval_time;
                }

                public List<String> getMateria_phone() {
                    return materia_phone;
                }

                public void setMateria_phone(List<String> materia_phone) {
                    this.materia_phone = materia_phone;
                }

                public String getMateria_msg() {
                    return materia_msg;
                }

                public void setMateria_msg(String materia_msg) {
                    this.materia_msg = materia_msg;
                }

                private String materia_ss;//图文的id

                public String getMateria_ss() {
                    return materia_ss;
                }

                public void setMateria_ss(String materia_ss) {
                    this.materia_ss = materia_ss;
                }

                public String getMateria_pic() {
                    return materia_pic;
                }

                public void setMateria_pic(String materia_pic) {
                    this.materia_pic = materia_pic;
                }

                public String getMateria_vedio() {
                    return materia_vedio;
                }

                public void setMateria_vedio(String materia_vedio) {
                    this.materia_vedio = materia_vedio;
                }

                public String getIs_mass() {
                    return is_mass;
                }

                public void setIs_mass(String is_mass) {
                    this.is_mass = is_mass;
                }

                public String getMateria_id() {
                    return materia_id;
                }

                public void setMateria_id(String materia_id) {
                    this.materia_id = materia_id;
                }

                public String getIs_remind() {
                    return is_remind;
                }

                public void setIs_remind(String is_remind) {
                    this.is_remind = is_remind;
                }

                public String getIs_protect() {
                    return is_protect;
                }

                public void setIs_protect(String is_protect) {
                    this.is_protect = is_protect;
                }

                public String getOne_add_num() {
                    return one_add_num;
                }

                public void setOne_add_num(String one_add_num) {
                    this.one_add_num = one_add_num;
                }

                public String getDay_add_num() {
                    return day_add_num;
                }

                public void setDay_add_num(String day_add_num) {
                    this.day_add_num = day_add_num;
                }

                public String getIs_verify() {
                    return is_verify;
                }

                public void setIs_verify(String is_verify) {
                    this.is_verify = is_verify;
                }

                public String getSniffing_type() {
                    return sniffing_type;
                }

                public void setSniffing_type(String sniffing_type) {
                    this.sniffing_type = sniffing_type;
                }

                public String getDz_num() {
                    return dz_num;
                }

                public void setDz_num(String dz_num) {
                    this.dz_num = dz_num;
                }

                public String getPhone_add_num() {
                    return phone_add_num;
                }

                public void setPhone_add_num(String phone_add_num) {
                    this.phone_add_num = phone_add_num;
                }

                public String getWx_add_num() {
                    return wx_add_num;
                }

                public void setWx_add_num(String wx_add_num) {
                    this.wx_add_num = wx_add_num;
                }

                private String ali_add_num_s;//支付宝开始
                private String ali_add_num_e;//支付宝结束

                public String getAli_add_num_s() {
                    return ali_add_num_s;
                }

                public void setAli_add_num_s(String ali_add_num_s) {
                    this.ali_add_num_s = ali_add_num_s;
                }

                public String getAli_add_num_e() {
                    return ali_add_num_e;
                }

                public void setAli_add_num_e(String ali_add_num_e) {
                    this.ali_add_num_e = ali_add_num_e;
                }

                public int getVersion() {
                    return version;
                }

                public void setVersion(int version) {
                    this.version = version;
                }

                public String pay_num_s;//微信发红包的最低金额（单位：分）
                public String pay_num_e;//微信发红包的最高金额（单位：分）
                public String pay_password;//支付密码

                public String shopping_s;//微信打开购物，停留的时间最小值
                public String shopping_e;//微信打开购物，停留的时间最大值

                public String game_s;//微信打开游戏，停留的时间最小值
                public String game_e;//微信打开游戏，停留的时间最大值

                public String reply_msg;//微信通讯录加好友成功之后，发送的一条消息
            }
        }
    }
}
