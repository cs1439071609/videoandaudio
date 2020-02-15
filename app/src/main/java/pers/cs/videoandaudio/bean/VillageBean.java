package pers.cs.videoandaudio.bean;

import java.util.List;

/**
 * @author chensen
 * @time 2020/2/13  11:29
 * @desc
 */
public class VillageBean {

    /**
     * info : {"count":0,"np":1581544202}
     * list : [{"comment":"10","rating":"4","top_comments":[],"text":"这是一只啥啊\u2026\u2026好凶","down":4,"smartapp_keyword":"皮皮搞笑,内涵段子,皮皮虾app,gif制作,蟹直播,麻花,极速版,章鱼输入法,装逼神器bili哔哩,毒斗图表情包,笑话交友今日头条tt语音绿洲,抖音人人番茄西瓜影音大全看点tv美女b站P图半二次元音","id":"30214256","cate":"萌","bookmark":0,"is_best":0,"smartapp_intro":"百思不得姐是一个温暖的搞笑创意型内容聚合平台，你可以在这里看到内涵段子，冷笑话、搞笑图片、搞笑视频以及搞笑的神最右等等、也可以在这里了解到最新发生的实时事件。","share_url":"http://a.f.budejie.com/share/30214256.html?wx.qq.com","gif":{},"forward":0,"type":"gif","status":4,"tags":[],"is_bookmark":0,"video_signs":0,"smartapp_title":"这是一只啥啊\u2026\u2026好凶 百思不得姐","up":84,"u":{},"passtime":"2020-02-13 10:36:02"},{"comment":"8","rating":"4","text":"在座的人有没有这样的女朋友","down":6,"video":{},"id":"30210313","cate":"搞笑","bookmark":0,"is_best":0,"smartapp_intro":"百思不得姐是一个温暖的搞笑创意型内容聚合平台，你可以在这里看到内涵段子，冷笑话、搞笑图片、搞笑视频以及搞笑的神最右等等、也可以在这里了解到最新发生的实时事件。","share_url":"http://a.f.budejie.com/share/30210313.html?wx.qq.com","forward":1,"type":"video","status":4,"tags":[],"is_bookmark":0,"video_signs":0,"smartapp_title":"在座的人有没有这样的女朋友 百思不得姐","up":102,"u":{},"passtime":"2020-02-13 10:11:01","smartapp_keyword":"皮皮搞笑,内涵段子,皮皮虾app,gif制作,蟹直播,麻花,极速版,章鱼输入法,装逼神器bili哔哩,毒斗图表情包,笑话交友今日头条tt语音绿洲,抖音人人番茄西瓜影音大全看点tv美女b站P图半二次元音"},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}]
     */

    //定义名须与字段名相同
    private InfoBean info;
    private List<VillageItem> list;

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public List<VillageItem> getList() {
        return list;
    }

    public void setList(List<VillageItem> list) {
        this.list = list;
    }

    public static class InfoBean {
        /**
         * count : 0
         * np : 1581544202
         */

        private int count;
        private int np;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getNp() {
            return np;
        }

        public void setNp(int np) {
            this.np = np;
        }
    }

    public static class VillageItem {
        /**
         * comment : 10
         * rating : 4
         * top_comments : [{"voicetime":0,"status":0,"hate_count":0,"cmt_type":"text","precid":0,"content":"赶紧带回去吧，遛饿就麻烦了","like_count":17,"u":{"header":["http://wimg.spriteapp.cn/profile/large/2019/10/24/5db127181d60b_mini.jpg","http://dimg.spriteapp.cn/profile/large/2019/10/24/5db127181d60b_mini.jpg"],"uid":"23043330","is_vip":false,"room_url":"","sex":"m","room_name":"","room_role":"","room_icon":"","name":"听风喃"},"preuid":0,"passtime":"2020-02-12 17:15:44","voiceuri":"","id":18885203}]
         * text : 迪拜人饲养的宠物确实不一样
         * down : 4
         * smartapp_keyword : 皮皮搞笑,内涵段子,皮皮虾app,gif制作,蟹直播,麻花,极速版,章鱼输入法,装逼神器bili哔哩,毒斗图表情包,笑话交友今日头条tt语音绿洲,抖音人人番茄西瓜影音大全看点tv美女b站P图半二次元音
         * id : 30211905
         * cate : 牛人
         * bookmark : 0
         * is_best : 0
         * smartapp_intro : 百思不得姐是一个温暖的搞笑创意型内容聚合平台，你可以在这里看到内涵段子，冷笑话、搞笑图片、搞笑视频以及搞笑的神最右等等、也可以在这里了解到最新发生的实时事件。
         * share_url : http://a.f.budejie.com/share/30211905.html?wx.qq.com
         * forward : 1
         * type : video
         * status : 4
         * tags : [{"post_number":81485,"image_list":"http://img.spriteapp.cn/ugc/2017/06/59bb322e5a4711e794d0842b2b4c75ab.jpg","forum_sort":0,"forum_status":2,"id":124,"info":"萌宠集中营，萌的一脸血；\n\n版主申请加微信：L1391446139","name":"萌宠","colum_set":1,"tail":"名铲屎官","sub_number":383793,"display_level":0}]
         * is_bookmark : 0
         * video_signs : 0
         * smartapp_title : 迪拜人饲养的宠物确实不一样 百思不得姐
         * up : 104
         * u : {"header":["http://wimg.spriteapp.cn/profile/large/2019/12/13/5df312d6019f6_mini.png","http://dimg.spriteapp.cn/profile/large/2019/12/13/5df312d6019f6_mini.png"],"relationship":0,"uid":"22659899","is_vip":false,"is_v":false,"room_url":"","room_name":"","room_role":"","room_icon":"","name":"盛世烟火"}
         * passtime : 2020-02-13 05:50:02
         * video : {"thumbnail_height":640,"long_picture":0,"thumbnail_link":["http://wimg.spriteapp.cn/cropx/352x640/picture/2020/0212/5e434f17bb4e4_wpd.jpg","http://dimg.spriteapp.cn/cropx/352x640/picture/2020/0212/5e434f17bb4e4_wpd.jpg"],"playfcount":271,"thumbnail_width":352,"height":640,"width":352,"video":["http://uvideo.spriteapp.cn/video/2020/0212/5e434f17bb4e4_wpd.mp4","http://tvideo.spriteapp.cn/video/2020/0212/5e434f17bb4e4_wpd.mp4"],"download":["http://uvideo.spriteapp.cn/video/2020/0212/5e434f17bb4e4_wpdm.mp4","http://tvideo.spriteapp.cn/video/2020/0212/5e434f17bb4e4_wpdm.mp4"],"duration":19,"playcount":1556,"thumbnail":["http://wimg.spriteapp.cn/picture/2020/0212/5e434f17bb4e4_wpd.jpg","http://dimg.spriteapp.cn/picture/2020/0212/5e434f17bb4e4_wpd.jpg"],"thumbnail_small":["http://wimg.spriteapp.cn/cropx/150x150/picture/2020/0212/5e434f17bb4e4_wpd.jpg","http://dimg.spriteapp.cn/cropx/150x150/picture/2020/0212/5e434f17bb4e4_wpd.jpg"]}
         */

        //评论数
        private String comment;
        //评分数
        private String rating;
        //热门评论（type：video）
        private List<TopCommentsBean> top_comments;

        private ImageBean image;
        //文本
        private String text;
        //下载数
        private int down;

        private String smartapp_keyword;
        private String id;
        private String cate;
        private int bookmark;
        private int is_best;
        private String smartapp_intro;
        //分享地址
        private String share_url;


        private GifBean gif;

        private int forward;
        //类型
        private String type;
        private int status;

        private List<TagsBean> tags;

        private int is_bookmark;
        private int video_signs;
        private String smartapp_title;
        private int up;
        private UBean u;
        //时间
        private String passtime;

        private VideoBean video;


        public GifBean getGif() {
            return gif;
        }

        public void setGif(GifBean gif) {
            this.gif = gif;
        }

        public ImageBean getImage() {
            return image;
        }

        public void setImage(ImageBean image) {
            this.image = image;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getDown() {
            return down;
        }

        public void setDown(int down) {
            this.down = down;
        }

        public String getSmartapp_keyword() {
            return smartapp_keyword;
        }

        public void setSmartapp_keyword(String smartapp_keyword) {
            this.smartapp_keyword = smartapp_keyword;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCate() {
            return cate;
        }

        public void setCate(String cate) {
            this.cate = cate;
        }

        public int getBookmark() {
            return bookmark;
        }

        public void setBookmark(int bookmark) {
            this.bookmark = bookmark;
        }

        public int getIs_best() {
            return is_best;
        }

        public void setIs_best(int is_best) {
            this.is_best = is_best;
        }

        public String getSmartapp_intro() {
            return smartapp_intro;
        }

        public void setSmartapp_intro(String smartapp_intro) {
            this.smartapp_intro = smartapp_intro;
        }

        public String getShare_url() {
            return share_url;
        }

        public void setShare_url(String share_url) {
            this.share_url = share_url;
        }

        public int getForward() {
            return forward;
        }

        public void setForward(int forward) {
            this.forward = forward;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getIs_bookmark() {
            return is_bookmark;
        }

        public void setIs_bookmark(int is_bookmark) {
            this.is_bookmark = is_bookmark;
        }

        public int getVideo_signs() {
            return video_signs;
        }

        public void setVideo_signs(int video_signs) {
            this.video_signs = video_signs;
        }

        public String getSmartapp_title() {
            return smartapp_title;
        }

        public void setSmartapp_title(String smartapp_title) {
            this.smartapp_title = smartapp_title;
        }

        public int getUp() {
            return up;
        }

        public void setUp(int up) {
            this.up = up;
        }

        public UBean getU() {
            return u;
        }

        public void setU(UBean u) {
            this.u = u;
        }

        public String getPasstime() {
            return passtime;
        }

        public void setPasstime(String passtime) {
            this.passtime = passtime;
        }

        public VideoBean getVideo() {
            return video;
        }

        public void setVideo(VideoBean video) {
            this.video = video;
        }

        public List<TopCommentsBean> getTop_comments() {
            return top_comments;
        }

        public void setTop_comments(List<TopCommentsBean> top_comments) {
            this.top_comments = top_comments;
        }

        public List<TagsBean> getTags() {
            return tags;
        }

        public void setTags(List<TagsBean> tags) {
            this.tags = tags;
        }

        public static class UBean {
            /**
             * header : ["http://wimg.spriteapp.cn/profile/large/2019/12/13/5df312d6019f6_mini.png","http://dimg.spriteapp.cn/profile/large/2019/12/13/5df312d6019f6_mini.png"]
             * relationship : 0
             * uid : 22659899
             * is_vip : false
             * is_v : false
             * room_url :
             * room_name :
             * room_role :
             * room_icon :
             * name : 盛世烟火
             */
            //头像地址
            private List<String> header;
            //关系
            private int relationship;

            private String uid;
            private boolean is_vip;
            private boolean is_v;
            private String room_url;
            private String sex;
            private String room_name;
            private String room_role;
            private String room_icon;
            //用户名
            private String name;

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public int getRelationship() {
                return relationship;
            }

            public void setRelationship(int relationship) {
                this.relationship = relationship;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public boolean isIs_vip() {
                return is_vip;
            }

            public void setIs_vip(boolean is_vip) {
                this.is_vip = is_vip;
            }

            public boolean isIs_v() {
                return is_v;
            }

            public void setIs_v(boolean is_v) {
                this.is_v = is_v;
            }

            public String getRoom_url() {
                return room_url;
            }

            public void setRoom_url(String room_url) {
                this.room_url = room_url;
            }

            public String getRoom_name() {
                return room_name;
            }

            public void setRoom_name(String room_name) {
                this.room_name = room_name;
            }

            public String getRoom_role() {
                return room_role;
            }

            public void setRoom_role(String room_role) {
                this.room_role = room_role;
            }

            public String getRoom_icon() {
                return room_icon;
            }

            public void setRoom_icon(String room_icon) {
                this.room_icon = room_icon;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<String> getHeader() {
                return header;
            }

            public void setHeader(List<String> header) {
                this.header = header;
            }
        }

        public static class VideoBean {
            /**
             * thumbnail_height : 640
             * long_picture : 0
             * thumbnail_link : ["http://wimg.spriteapp.cn/cropx/352x640/picture/2020/0212/5e434f17bb4e4_wpd.jpg","http://dimg.spriteapp.cn/cropx/352x640/picture/2020/0212/5e434f17bb4e4_wpd.jpg"]
             * playfcount : 271
             * thumbnail_width : 352
             * height : 640
             * width : 352
             * video : ["http://uvideo.spriteapp.cn/video/2020/0212/5e434f17bb4e4_wpd.mp4","http://tvideo.spriteapp.cn/video/2020/0212/5e434f17bb4e4_wpd.mp4"]
             * download : ["http://uvideo.spriteapp.cn/video/2020/0212/5e434f17bb4e4_wpdm.mp4","http://tvideo.spriteapp.cn/video/2020/0212/5e434f17bb4e4_wpdm.mp4"]
             * duration : 19
             * playcount : 1556
             * thumbnail : ["http://wimg.spriteapp.cn/picture/2020/0212/5e434f17bb4e4_wpd.jpg","http://dimg.spriteapp.cn/picture/2020/0212/5e434f17bb4e4_wpd.jpg"]
             * thumbnail_small : ["http://wimg.spriteapp.cn/cropx/150x150/picture/2020/0212/5e434f17bb4e4_wpd.jpg","http://dimg.spriteapp.cn/cropx/150x150/picture/2020/0212/5e434f17bb4e4_wpd.jpg"]
             */
            //缩略图高度
            private int thumbnail_height;
            private int long_picture;
            //缩略图链接
            private List<String> thumbnail_link;
            private int playfcount;
            //缩略图宽度
            private int thumbnail_width;
            private int height;
            private int width;

            private List<String> video;
            private List<String> download;

            private int duration;
            private int playcount;


            private List<String> thumbnail;
            private List<String> thumbnail_small;

            public int getThumbnail_height() {
                return thumbnail_height;
            }

            public void setThumbnail_height(int thumbnail_height) {
                this.thumbnail_height = thumbnail_height;
            }

            public int getLong_picture() {
                return long_picture;
            }

            public void setLong_picture(int long_picture) {
                this.long_picture = long_picture;
            }

            public int getPlayfcount() {
                return playfcount;
            }

            public void setPlayfcount(int playfcount) {
                this.playfcount = playfcount;
            }

            public int getThumbnail_width() {
                return thumbnail_width;
            }

            public void setThumbnail_width(int thumbnail_width) {
                this.thumbnail_width = thumbnail_width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public int getPlaycount() {
                return playcount;
            }

            public void setPlaycount(int playcount) {
                this.playcount = playcount;
            }

            public List<String> getThumbnail_link() {
                return thumbnail_link;
            }

            public void setThumbnail_link(List<String> thumbnail_link) {
                this.thumbnail_link = thumbnail_link;
            }

            public List<String> getVideo() {
                return video;
            }

            public void setVideo(List<String> video) {
                this.video = video;
            }

            public List<String> getDownload() {
                return download;
            }

            public void setDownload(List<String> download) {
                this.download = download;
            }

            public List<String> getThumbnail() {
                return thumbnail;
            }

            public void setThumbnail(List<String> thumbnail) {
                this.thumbnail = thumbnail;
            }

            public List<String> getThumbnail_small() {
                return thumbnail_small;
            }

            public void setThumbnail_small(List<String> thumbnail_small) {
                this.thumbnail_small = thumbnail_small;
            }
        }

        public static class TagsBean {
            /**
             * post_number : 81485
             * image_list : http://img.spriteapp.cn/ugc/2017/06/59bb322e5a4711e794d0842b2b4c75ab.jpg
             * forum_sort : 0
             * forum_status : 2
             * id : 124
             * info : 萌宠集中营，萌的一脸血；

             版主申请加微信：L1391446139
             * name : 萌宠
             * colum_set : 1
             * tail : 名铲屎官
             * sub_number : 383793
             * display_level : 0
             */

            private int post_number;
            private String image_list;
            private int forum_sort;
            private int forum_status;
            private int id;
            private String info;
            private String name;
            private int colum_set;
            private String tail;
            private int sub_number;
            private int display_level;

            public int getPost_number() {
                return post_number;
            }

            public void setPost_number(int post_number) {
                this.post_number = post_number;
            }

            public String getImage_list() {
                return image_list;
            }

            public void setImage_list(String image_list) {
                this.image_list = image_list;
            }

            public int getForum_sort() {
                return forum_sort;
            }

            public void setForum_sort(int forum_sort) {
                this.forum_sort = forum_sort;
            }

            public int getForum_status() {
                return forum_status;
            }

            public void setForum_status(int forum_status) {
                this.forum_status = forum_status;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getInfo() {
                return info;
            }

            public void setInfo(String info) {
                this.info = info;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getColum_set() {
                return colum_set;
            }

            public void setColum_set(int colum_set) {
                this.colum_set = colum_set;
            }

            public String getTail() {
                return tail;
            }

            public void setTail(String tail) {
                this.tail = tail;
            }

            public int getSub_number() {
                return sub_number;
            }

            public void setSub_number(int sub_number) {
                this.sub_number = sub_number;
            }

            public int getDisplay_level() {
                return display_level;
            }

            public void setDisplay_level(int display_level) {
                this.display_level = display_level;
            }
        }

        //热门评论
        public static class TopCommentsBean {
            /**
             * voicetime : 0
             * status : 0
             * hate_count : 0
             * cmt_type : text
             * precid : 0
             * content : 赶紧带回去吧，遛饿就麻烦了
             * like_count : 17
             * u : {"header":["http://wimg.spriteapp.cn/profile/large/2019/10/24/5db127181d60b_mini.jpg","http://dimg.spriteapp.cn/profile/large/2019/10/24/5db127181d60b_mini.jpg"],"uid":"23043330","is_vip":false,"room_url":"","sex":"m","room_name":"","room_role":"","room_icon":"","name":"听风喃"}
             * preuid : 0
             * passtime : 2020-02-12 17:15:44
             * voiceuri :
             * id : 18885203
             */

            private int voicetime;
            private int status;
            private int hate_count;
            //评论类型
            private String cmt_type;
            private int precid;
            //评论内容
            private String content;
            private int like_count;
            //评论人
            private UBean u;
            private int preuid;
            //评论时间
            private String passtime;
            private String voiceuri;
            private int id;

            public int getVoicetime() {
                return voicetime;
            }

            public void setVoicetime(int voicetime) {
                this.voicetime = voicetime;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getHate_count() {
                return hate_count;
            }

            public void setHate_count(int hate_count) {
                this.hate_count = hate_count;
            }

            public String getCmt_type() {
                return cmt_type;
            }

            public void setCmt_type(String cmt_type) {
                this.cmt_type = cmt_type;
            }

            public int getPrecid() {
                return precid;
            }

            public void setPrecid(int precid) {
                this.precid = precid;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getLike_count() {
                return like_count;
            }

            public void setLike_count(int like_count) {
                this.like_count = like_count;
            }

            public UBean getU() {
                return u;
            }

            public void setU(UBean u) {
                this.u = u;
            }

            public int getPreuid() {
                return preuid;
            }

            public void setPreuid(int preuid) {
                this.preuid = preuid;
            }

            public String getPasstime() {
                return passtime;
            }

            public void setPasstime(String passtime) {
                this.passtime = passtime;
            }

            public String getVoiceuri() {
                return voiceuri;
            }

            public void setVoiceuri(String voiceuri) {
                this.voiceuri = voiceuri;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }


        }

        public static class ImageBean{

            /**
             * medium : []
             * long_picture : 0
             * thumbnail_link : ["http://wimg.spriteapp.cn/cropx/1040x780/ugc/2020/01/24/5e2ad5d79bf15.jpg","http://dimg.spriteapp.cn/cropx/1040x780/ugc/2020/01/24/5e2ad5d79bf15.jpg"]
             * big : ["http://wimg.spriteapp.cn/ugc/2020/01/24/5e2ad5d79bf15_1.jpg","http://dimg.spriteapp.cn/ugc/2020/01/24/5e2ad5d79bf15_1.jpg"]
             * download_url : ["http://wimg.spriteapp.cn/ugc/2020/01/24/5e2ad5d79bf15_d.jpg","http://dimg.spriteapp.cn/ugc/2020/01/24/5e2ad5d79bf15_d.jpg","http://wimg.spriteapp.cn/ugc/2020/01/24/5e2ad5d79bf15.jpg","http://dimg.spriteapp.cn/ugc/2020/01/24/5e2ad5d79bf15.jpg"]
             * height : 3024
             * width : 4032
             * thumbnail_width : 4032
             * small : []
             * thumbnail_height : 3024
             * thumbnail_small : ["http://wimg.spriteapp.cn/cropx/150x150/ugc/2020/01/24/5e2ad5d79bf15.jpg","http://dimg.spriteapp.cn/cropx/150x150/ugc/2020/01/24/5e2ad5d79bf15.jpg"]
             */
            private List<?> medium;
            private int long_picture;
            private List<String> thumbnail_link;
            private List<String> big;
            private List<String> download_url;
            private int height;
            private int width;
            private int thumbnail_width;
            private int thumbnail_height;
            private List<?> small;
            private List<String> thumbnail_small;

            public int getLong_picture() {
                return long_picture;
            }

            public void setLong_picture(int long_picture) {
                this.long_picture = long_picture;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getThumbnail_width() {
                return thumbnail_width;
            }

            public void setThumbnail_width(int thumbnail_width) {
                this.thumbnail_width = thumbnail_width;
            }

            public int getThumbnail_height() {
                return thumbnail_height;
            }

            public void setThumbnail_height(int thumbnail_height) {
                this.thumbnail_height = thumbnail_height;
            }

            public List<?> getMedium() {
                return medium;
            }

            public void setMedium(List<?> medium) {
                this.medium = medium;
            }

            public List<String> getThumbnail_link() {
                return thumbnail_link;
            }

            public void setThumbnail_link(List<String> thumbnail_link) {
                this.thumbnail_link = thumbnail_link;
            }

            public List<String> getBig() {
                return big;
            }

            public void setBig(List<String> big) {
                this.big = big;
            }

            public List<String> getDownload_url() {
                return download_url;
            }

            public void setDownload_url(List<String> download_url) {
                this.download_url = download_url;
            }

            public List<?> getSmall() {
                return small;
            }

            public void setSmall(List<?> small) {
                this.small = small;
            }

            public List<String> getThumbnail_small() {
                return thumbnail_small;
            }

            public void setThumbnail_small(List<String> thumbnail_small) {
                this.thumbnail_small = thumbnail_small;
            }
        }

        public static class GifBean{

            /**
             * long_picture : 0
             * gif_thumbnail : ["http://wimg.spriteapp.cn/ugc/2020/02/13/5e44ec91a4a94_a_1.jpg","http://dimg.spriteapp.cn/ugc/2020/02/13/5e44ec91a4a94_a_1.jpg"]
             * thumbnail_link : ["http://wimg.spriteapp.cn/cropx/180x165/ugc/2020/02/13/5e44ec91a4a94_a_1.jpg","http://dimg.spriteapp.cn/cropx/180x165/ugc/2020/02/13/5e44ec91a4a94_a_1.jpg"]
             * download_url : ["http://wimg.spriteapp.cn/ugc/2020/02/13/5e44ec91a4a94_d.jpg","http://dimg.spriteapp.cn/ugc/2020/02/13/5e44ec91a4a94_d.jpg","http://wimg.spriteapp.cn/ugc/2020/02/13/5e44ec91a4a94_a_1.jpg","http://dimg.spriteapp.cn/ugc/2020/02/13/5e44ec91a4a94_a_1.jpg"]
             * height : 165
             * width : 180
             * thumbnail_width : 180
             * images : ["http://wimg.spriteapp.cn/ugc/2020/02/13/5e44ec91a4a94.gif","http://dimg.spriteapp.cn/ugc/2020/02/13/5e44ec91a4a94.gif"]
             * thumbnail_height : 165
             */

            private int long_picture;

            private List<String> gif_thumbnail;
            private List<String> thumbnail_link;
            private List<String> download_url;

            private int height;
            private int width;
            private int thumbnail_width;

            private List<String> images;

            private int thumbnail_height;



            public int getLong_picture() {
                return long_picture;
            }

            public void setLong_picture(int long_picture) {
                this.long_picture = long_picture;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getThumbnail_width() {
                return thumbnail_width;
            }

            public void setThumbnail_width(int thumbnail_width) {
                this.thumbnail_width = thumbnail_width;
            }

            public int getThumbnail_height() {
                return thumbnail_height;
            }

            public void setThumbnail_height(int thumbnail_height) {
                this.thumbnail_height = thumbnail_height;
            }

            public List<String> getGif_thumbnail() {
                return gif_thumbnail;
            }

            public void setGif_thumbnail(List<String> gif_thumbnail) {
                this.gif_thumbnail = gif_thumbnail;
            }

            public List<String> getThumbnail_link() {
                return thumbnail_link;
            }

            public void setThumbnail_link(List<String> thumbnail_link) {
                this.thumbnail_link = thumbnail_link;
            }

            public List<String> getDownload_url() {
                return download_url;
            }

            public void setDownload_url(List<String> download_url) {
                this.download_url = download_url;
            }

            public List<String> getImages() {
                return images;
            }

            public void setImages(List<String> images) {
                this.images = images;
            }
        }




    }




}
