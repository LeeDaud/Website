-- LeeDaud涓汉缃戠珯鏁版嵁搴?-- 鍖呭惈鍥涗釜缃戠珯鐨勫悗绔暟鎹簱
-- 涓婚〉home.LeeDaud.cc
-- 绠＄悊admin.LeeDaud.cc
-- 绠€鍘哻v.LeeDaud.cc
-- 鍗氬blog.LeeDaud.cc

drop database if exists LeeDaud;
create database LeeDaud DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

use LeeDaud;
set names utf8mb4;

-- ===========绠＄悊绔?admin.LeeDaud.cc)鐩稿叧琛?===================
-- 绠＄悊鍛樿〃
create table admin(
    id int primary key auto_increment,
    username varchar(20) not null comment '鐢ㄦ埛鍚?,
    password varchar(255) not null comment '鍔犲瘑鍚庣殑瀵嗙爜',
    salt varchar(50) not null comment '鐩愬€?,
    nickname varchar(20) comment '鏄电О',
    email varchar(50) comment '鐢靛瓙閭',
    role tinyint comment '瑙掕壊,1-绠＄悊鍛?0-娓稿',
    create_time datetime comment '鍒涘缓鏃堕棿',
    update_time datetime comment '鏇存柊鏃堕棿'
) comment '绠＄悊鍛樿〃';

-- 鎿嶄綔鏃ュ織琛?create table operation_logs(
    id int primary key auto_increment,
    admin_id int comment '绠＄悊鍛業D',
    operation_type varchar(20) comment '鎿嶄綔绫诲瀷',
    operation_target varchar(100) comment '鎿嶄綔鐩爣(妯″潡)',
    target_id int comment '鐩爣ID',
    operate_data JSON comment '鎿嶄綔鏁版嵁',
    result tinyint comment '鎿嶄綔缁撴灉锛?-澶辫触锛?-鎴愬姛',
    error_message text comment '閿欒淇℃伅',
    operation_time datetime comment '鎿嶄綔鏃堕棿',
    index idx_admin_time(admin_id,operation_time desc),
    index idx_type_time (operation_type, operation_time desc)
)comment '鎿嶄綔鏃ュ織琛?;

-- 绯荤粺閰嶇疆琛?create table system_config(
    id int primary key auto_increment,
    config_key varchar(50) unique not null comment '閰嶇疆閿?,
    config_value text comment '閰嶇疆鍊?,
    config_type varchar(20) comment '閰嶇疆绫诲瀷,string,number,boolean,json,date',
    description varchar(255) comment '閰嶇疆鎻忚堪',
    create_time datetime comment '鍒涘缓鏃堕棿',
    update_time datetime comment '鏇存柊鏃堕棿'
) comment '绯荤粺閰嶇疆琛?;
-- ==========================================================

-- ================涓婚〉(home.LeeDaud.cc)鐩稿叧琛?===============
-- 涓汉淇℃伅琛?create table personal_info(
    id int primary key auto_increment,
    nickname varchar(20) not null comment '鏄电О',
    tag varchar(30) not null comment '鏍囩',
    description varchar(50) comment '涓汉绠€浠?,
    avatar varchar(255) comment '澶村儚url',
    website varchar(100) comment '涓汉缃戠珯',
    email varchar(50) comment '鐢靛瓙閭',
    github varchar(100) comment 'GitHub',
    location varchar(50) comment '鎵€鍦ㄥ湴',
    create_time datetime comment '鍒涘缓鏃堕棿',
    update_time datetime comment '鏇存柊鏃堕棿'
) comment '涓汉淇℃伅琛?;

--  绀句氦濯掍綋琛?create table social_media(
    id int primary key auto_increment,
    name varchar(20) not null comment '鍚嶇О',
    icon varchar(50) comment '鍥炬爣绫诲悕',
    link varchar(100) comment '閾炬帴',
    sort int comment '鎺掑簭锛岃秺灏忚秺闈犲墠',
    is_visible tinyint default 1 comment '鏄惁鍙',
    create_time datetime comment '鍒涘缓鏃堕棿',
    update_time datetime comment '鏇存柊鏃堕棿'
) comment '绀句氦濯掍綋琛?;
-- ==========================================================


-- ====================绠€鍘?cv.LeeDaud.cc)鐩稿叧琛?==============
-- 缁忓巻琛?create table experiences(
    id int primary key auto_increment,
    type tinyint not null comment '绫诲瀷锛?-鏁欒偛缁忓巻锛?-瀹炰範鍙婂伐浣滅粡鍘?2-椤圭洰缁忓巻',

    --  鍐呭鍩烘湰淇℃伅
    title varchar(50) not null comment '鏍囬,鍏徃鍚?瀛︽牎鍚?椤圭洰鍚?,
    subtitle varchar(100) comment '鍓爣棰?鑱屼綅/涓撲笟/椤圭洰瑙掕壊',
    logo_url varchar(255) comment 'logo/鏍″窘',
    content text not null comment '鍐呭',
    project_link varchar(255) comment '椤圭洰閾炬帴',
    start_date DATE NOT NULL comment '寮€濮嬫椂闂?,
    end_date DATE comment '缁撴潫鏃堕棿',

    is_visible tinyint default 1 comment '鏄惁鍙',

    create_time datetime comment '鍒涘缓鏃堕棿',
    update_time datetime comment '鏇存柊鏃堕棿',

    index idx_time(start_date desc)
) comment '缁忓巻琛?;

-- 鎶€鑳借〃
create table skills(
    id int primary key auto_increment,
    name varchar(20) not null comment '鎶€鑳藉悕绉?,
    description varchar(255) comment '鎶€鑳芥弿杩?,
    icon varchar(255) comment '鍥炬爣url',
    sort int comment '鎺掑簭锛岃秺灏忚秺闈犲墠',
    is_visible tinyint default 1 comment '鏄惁鍙',
    create_time datetime comment '鍒涘缓鏃堕棿',
    update_time datetime comment '鏇存柊鏃堕棿'
) comment '鎶€鑳借〃';
-- ==========================================================

-- ================鍗氬(blog.LeeDaud.cc)鐩稿叧琛?================
-- 璁垮琛?create table visitors(
    id int primary key auto_increment comment '璁垮ID',
    fingerprint varchar(150) not null comment '璁垮鎸囩汗,鐢ㄤ簬鍞竴鏍囪瘑璁垮',
    session_id varchar(100) comment '浼氳瘽ID(褰撳墠娴忚鍣ㄤ細璇?',
    ip varchar(45) not null comment 'IP鍦板潃',
    user_agent text comment '鐢ㄦ埛浠ｇ悊',
    country varchar(25) comment '鍥藉',
    province varchar(25) comment '鐪佷唤',
    city varchar(25) comment '鍩庡競',
    longitude varchar(50) comment '缁忓害',
    latitude varchar(50) comment '绾害',
    -- 璁块棶淇℃伅
    first_visit_time datetime comment '棣栨璁块棶鏃堕棿',
    last_visit_time datetime comment '鏈€鍚庤闂椂闂?,
    total_views int comment '璁块棶娆℃暟',
    is_blocked tinyint comment '鏄惁琚皝绂?0-鍚︼紝1-鏄?,
    expires_at datetime comment '灏佺缁撴潫鏃堕棿',
    create_time datetime comment '鍒涘缓鏃堕棿',
    update_time datetime comment '鏇存柊鏃堕棿',

    unique index uk_visitor_fingerprint(fingerprint),
    index idx_session_id(session_id),
    index idx_last_visit(last_visit_time desc)
) comment '璁垮琛?;

-- 娴忚琛?create table views(
    id int primary key auto_increment,
    visitor_id int comment '璁垮ID',
    page_path varchar(100) comment '椤甸潰璺緞',
    referer varchar(255) comment '鏉ユ簮URL',
    page_title varchar(100) comment '椤甸潰鏍囬',
    ip_address varchar(45) comment 'IP鍦板潃',
    user_agent text comment '鐢ㄦ埛浠ｇ悊',
    view_time datetime comment '璁块棶鏃堕棿',

    index idx_view_time(view_time desc),
    index idx_visitor_time(visitor_id,view_time desc),
    index idx_page_date(page_path(50),view_time)
) comment '娴忚琛?;

-- 鏂囩珷鍒嗙被琛?create table article_categories(
    id int primary key auto_increment,
    name varchar(20) not null comment '鍒嗙被鍚嶇О,濡傦細鏃ュ父,蹇冨緱,骞村害鎬荤粨,缂栫▼,闈㈢粡',
    slug varchar(20) not null comment 'URL鏍囪瘑锛屽锛歞aily, thinking, year-summary, programming, interview',
    description varchar(100) comment '鍒嗙被鎻忚堪',
    sort int default 0 comment '鎺掑簭锛岃秺灏忚秺闈犲墠',
    create_time datetime comment '鍒涘缓鏃堕棿',
    update_time datetime comment '鏇存柊鏃堕棿'
) comment '鏂囩珷鍒嗙被琛?;

-- 鏂囩珷琛?create table articles(
    id int primary key auto_increment,

    -- 鍩虹淇℃伅
    title varchar(50) not null comment '鏂囩珷鏍囬',
    slug varchar(50) unique not null comment 'URL鏍囪瘑锛屽锛歸hat-is-slug-field',
    summary text comment '鏂囩珷鎽樿',
    cover_image varchar(255) comment '灏侀潰鍥剧墖url',

    -- 鍐呭
    content_markdown longtext not null comment 'Markdown鍐呭',
    content_html longtext not null comment '杞崲鍚庣殑HTML鍐呭',

    -- 鍒嗙被
    category_id int comment '鍒嗙被ID',

    -- 缁熻淇℃伅
    view_count int default 0 comment '娴忚娆℃暟',
    like_count int default 0 comment '鐐硅禐娆℃暟',
    comment_count int default 0 comment '璇勮鏁?,
    word_count int default 0 comment '瀛楁暟缁熻',
    reading_time int default 0 comment '棰勮闃呰鏃堕棿锛屽崟浣嶏細鍒嗛挓',

    -- 鍙戝竷淇℃伅
    is_published tinyint default 0 comment '鏄惁鍙戝竷,0-鍚︼紝1-鏄?,
    is_top tinyint default 0 comment '鏄惁缃《,0-鍚︼紝1-鏄?,
    publish_time datetime comment '鍙戝竷鏃堕棿',

    -- 褰掓。
    publish_year INT GENERATED ALWAYS AS (IFNULL(YEAR(publish_time), 0)) STORED COMMENT '鍙戝竷骞翠唤',
    publish_month INT GENERATED ALWAYS AS (IFNULL(MONTH(publish_time), 0)) STORED COMMENT '鍙戝竷鏈堜唤',
    publish_day INT GENERATED ALWAYS AS (IFNULL(DAY(publish_time), 0)) STORED COMMENT '鍙戝竷鏃ユ湡',
    publish_date DATE GENERATED ALWAYS AS (IFNULL(DATE(publish_time), NULL)) STORED COMMENT '鍙戝竷鏃ユ湡锛堝幓鎺夋椂闂达級',

    create_time datetime comment '鍒涘缓鏃堕棿',
    update_time datetime comment '鏇存柊鏃堕棿',

    index idx_published_time (is_published, publish_time desc),
    index idx_publish_date (publish_date desc),
    index idx_category_status (category_id, is_published, publish_time desc),
    index idx_slug(slug),
    index idx_view_count (view_count desc),
    -- 鍏ㄦ枃绱㈠紩锛岀敤浜庢悳绱?    fulltext idx_fulltext(title,summary,content_markdown(500))
) comment '鏂囩珷琛?;

-- 鏂囩珷鏍囩琛?create table article_tags(
    id int primary key auto_increment,
    name varchar(20) not null comment '鏍囩鍚嶇О',
    slug varchar(30) not null comment 'URL鏍囪瘑',
    create_time datetime comment '鍒涘缓鏃堕棿',
    update_time datetime comment '鏇存柊鏃堕棿',
    unique index uk_tag_name(name),
    unique index uk_tag_slug(slug)
) comment '鏂囩珷鏍囩琛?;

-- 鏂囩珷-鏍囩鍏宠仈琛?create table article_tag_relations(
    id int primary key auto_increment,
    article_id int not null comment '鏂囩珷ID',
    tag_id int not null comment '鏍囩ID',
    unique index uk_article_tag(article_id, tag_id),
    index idx_tag_id(tag_id)
) comment '鏂囩珷鏍囩鍏宠仈琛?;

-- 鏂囩珷璇勮琛?create table article_comments(
    id int primary key auto_increment,

    -- 璇勮淇℃伅
    article_id int not null comment '鏂囩珷ID',
    root_id int comment '鏍硅瘎璁篒D,null鏄竴绾ц瘎璁?,
    parent_id int comment '鐖惰瘎璁篒D,null鏄竴绾ц瘎璁?,
    parent_nickname varchar(15) comment '鐖惰瘎璁烘樀绉?,
    content text not null comment '璇勮鍐呭',
    content_html text not null comment '杞崲鍚庣殑HTML鍐呭,(濡傛灉鏄痬arkdown)',

    -- 璇勮鑰呬俊鎭?    visitor_id int comment '璁垮ID',
    nickname varchar(15) comment '鏄电О',
    email_or_qq varchar(50) comment '閭鎴杚q',
    location varchar(30) comment '鍦板潃',
    user_agent_os varchar(20) comment '鎿嶄綔绯荤粺鍚嶇О',
    user_agent_browser varchar(20) comment '娴忚鍣ㄥ悕绉?,

    -- 鐘舵€佷俊鎭?    is_approved tinyint default 0 comment '鏄惁瀹℃牳閫氳繃锛?-鍚︼紝1-鏄?,
    is_markdown tinyint default 0 comment '鏄惁浣跨敤markdown锛?-鍚︼紝1-鏄?,
    is_secret tinyint default 0 comment '鏄惁鍖垮悕锛?-鍚︼紝1-鏄?,
    is_notice tinyint default 0 comment '鏈夊洖澶嶆槸鍚﹂€氱煡锛?-鍚︼紝1-鏄?,
    is_edited tinyint default 0 comment '鏄惁缂栬緫杩囷紝0-鍚︼紝1-鏄?,
    is_admin_reply tinyint default 0 comment '鏄惁涓虹鐞嗗憳鍥炲锛?-鍚︼紝1-鏄?,

    create_time datetime comment '鍒涘缓鏃堕棿',
    update_time datetime comment '鏇存柊鏃堕棿',

    -- 鏂囩珷璇勮鍒楄〃
    index idx_article_status (article_id, is_approved, create_time desc),
    -- 鍥炲鍒楄〃
    index idx_parent (parent_id desc),
    index idx_root (root_id desc),
    -- 瀹℃牳鍒楄〃
    index idx_approved (is_approved, create_time desc),
    -- 鐢ㄦ埛璇嗗埆
    index idx_fingerprint (visitor_id)
) comment '鏂囩珷璇勮琛?;

-- 鏂囩珷鐐硅禐琛?create table article_likes(
    id int primary key auto_increment,
    article_id int not null comment '鏂囩珷ID',
    visitor_id int not null comment '璁垮ID',
    like_time datetime comment '鐐硅禐鏃堕棿',
    unique index uk_article_visitor (article_id, visitor_id),
    index idx_article (article_id, like_time desc)
) comment '鏂囩珷鐐硅禐琛?;

-- Rss璁㈤槄璁板綍琛?create table rss_subscriptions(
    id int primary key auto_increment,
    visitor_id int not null comment '璁垮ID',
    nickname varchar(15) not null comment '鏄电О',
    email varchar(50) not null comment '閭',
    is_active tinyint default 1 comment '鏄惁婵€娲伙紝0-鍚︼紝1-鏄?,
    subscribe_time datetime comment '璁㈤槄鏃堕棿',
    un_subscribe_time datetime comment '鍙栨秷璁㈤槄鏃堕棿',
    index idx_email(email),
    index idx_visitor_id(visitor_id)
) comment 'Rss璁㈤槄璁板綍琛?;

-- 鍙嬮摼琛?create table friend_links(
    id int primary key auto_increment,
    name varchar(20) not null comment '缃戠珯鍚嶇О',
    url varchar(100) not null comment '缃戠珯鍦板潃',
    avatar_url varchar(255) comment '澶村儚url',
    description varchar(255) comment '缃戠珯鎻忚堪',
    sort int default 0 comment '鎺掑簭锛岃秺灏忚秺闈犲墠',
    is_visible tinyint default 1 comment '鏄惁鍙',
    create_time datetime comment '鍒涘缓鏃堕棿',
    update_time datetime comment '鏇存柊鏃堕棿'
) comment '鍙嬫儏閾炬帴琛?;

-- 鐣欒█琛?create table messages(
    id int primary key auto_increment,

    -- 鐣欒█淇℃伅
    content text not null comment '鐣欒█鍐呭',
    content_html text not null comment '杞崲鍚庣殑HTML鍐呭,(濡傛灉鏄痬arkdown)',
    root_id int comment '鏍圭暀瑷€ID,null鏄竴绾х暀瑷€',
    parent_id int comment '鐖剁暀瑷€ID,null鏄竴绾х暀瑷€',
    parent_nickname varchar(15) comment '鐖剁暀瑷€鏄电О',

    -- 鐣欒█鑰呬俊鎭?    visitor_id int comment '璁垮ID',
    nickname varchar(15) comment '鏄电О',
    email_or_qq varchar(50) comment '閭鎴杚q',
    location varchar(30) comment '鍦板潃',
    user_agent_os varchar(20) comment '鎿嶄綔绯荤粺鍚嶇О',
    user_agent_browser varchar(20) comment '娴忚鍣ㄥ悕绉?,

    -- 鐘舵€佷俊鎭?    is_approved tinyint default 0 comment '鏄惁瀹℃牳閫氳繃锛?-鍚︼紝1-鏄?,
    is_markdown tinyint default 0 comment '鏄惁浣跨敤markdown锛?-鍚︼紝1-鏄?,
    is_secret tinyint default 0 comment '鏄惁鍖垮悕锛?-鍚︼紝1-鏄?,
    is_notice tinyint default 0 comment '鏈夊洖澶嶆槸鍚﹂€氱煡锛?-鍚︼紝1-鏄?,
    is_edited tinyint default 0 comment '鏄惁缂栬緫杩囷紝0-鍚︼紝1-鏄?,
    is_admin_reply tinyint default 0 comment '鏄惁涓虹鐞嗗憳鍥炲锛?-鍚︼紝1-鏄?,

    create_time datetime comment '鍒涘缓鏃堕棿',
    update_time datetime comment '鏇存柊鏃堕棿'
) comment '鐣欒█琛?;

-- 闊充箰琛?create table music(
    id int primary key auto_increment,
    title varchar(50) not null comment '闊充箰鏍囬',
    artist varchar(50) comment '浣滆€?,
    duration int comment '鏃堕暱锛屽崟浣嶏細绉?,
    -- 鏂囦欢淇℃伅url
    cover_image varchar(255) comment '灏侀潰鍥剧墖url',
    music_url varchar(255) not null comment '闊抽鏂囦欢url',
    lyric_url varchar(255) comment '姝岃瘝鏂囦欢url',

    has_lyric tinyint default 0 comment '鏄惁鏈夋瓕璇嶏紝0-鍚︼紝1-鏄?,
    lyric_type varchar(10) comment '姝岃瘝绫诲瀷,lrc,json,txt',
    sort int comment '鎺掑簭锛岃秺灏忚秺闈犲墠',
    is_visible tinyint default 1 comment '鏄惁鍙',

    create_time datetime comment '鍒涘缓鏃堕棿',
    update_time datetime comment '鏇存柊鏃堕棿',

    index idx_sort_visible (sort, is_visible,id desc)
) comment '闊充箰琛?;
-- ==========================================================

-- 鎻掑叆蹇呰鐨勫垵濮嬫暟鎹?-- xxx涓洪渶瑕佹浛鎹㈢殑鍐呭

-- 绠＄悊鍛樿处鍙穟sername鏄櫥褰曟椂鐢ㄧ殑锛宯ickname鏄湪绠＄悊绔櫥褰曞悗鏄剧ず鐨勬樀绉帮紝鐩愬€肩敤闅忔満瀛楃涓插氨琛岋紝涓嶈澶畝鍗曪紝杩欓噷鐨勫瘑鐮佽鎻掑叆鏍规嵁鐩愬€煎姞瀵嗗悗鐨勫瘑鐮?-- 瀵嗙爜闇€瑕佸埌娴嬭瘯绫讳腑鐢熸垚锛屽湪/src/test/java/cc/LeeDaud/LeeDaudBackendApplicationTests.java
-- 杩愯娴嬭瘯鏂规硶浼氬湪鎺у埗鍙版墦鍗板嚭鍔犲瘑鍚庣殑瀵嗙爜锛屽皢鍔犲瘑鍚庣殑瀵嗙爜鍜岀洂鍊煎～鍏ヤ互涓嬫彃鍏ヨ鍙?-- 閭濉啓鑷繁鐨勯偖绠憋紝浣滀负绠＄悊绔櫥褰曢獙璇佺爜鐨勬帴鍙楅偖绠?insert into admin(username, password, salt, nickname, email, role, create_time, update_time)
values('LeeDaud', 'a2a8037261b12f9feb97ec017a76be5422b5e398401c18b5443e4b0845e94fb1', 'LeeDaud@2026', 'LeeDaud', '1015976714@qq.com', 1, now(), now());

-- 娓稿璐﹀彿锛堝彲閫夛級
-- 娓稿鐨勮处鍙峰悕鍜岀洂鍊艰嚜宸辨潵瀹氾紝瀵嗙爜鍚屾牱鍘绘祴璇曠被鐢熸垚锛岄偖绠变笉鐢ㄥ～锛屽洜涓洪厤缃枃浠堕噷鍙互鍥哄畾娓稿濉殑閭楠岃瘉鐮?insert into admin(username, password, salt, nickname, email, role, create_time, update_time)
values('xxx', 'xxx', 'xxx', '娓稿', '',0, now(), now());

-- 绯荤粺閰嶇疆椤?-- 璇蜂笉瑕佹敼鍙樹笅鍒楅厤缃殑閿紝璺熷墠绔湁瀵瑰簲鐨勩€傞渶瑕佽嚜琛屽～鍏ュ€硷紝缃戠珯寤虹珛鏃堕棿璇风敤2025-10-31杩欑鏍煎紡
-- icp澶囨淇℃伅
insert into system_config(config_key, config_value, config_type, description, create_time, update_time)
values ('icp-beian','xxx','string','icp澶囨',now(),now());
-- 鍏畨澶囨淇℃伅
insert into system_config(config_key, config_value, config_type, description, create_time, update_time)
values ('gongan-beian','xxx','string','鍏畨澶囨',now(),now());
-- 缃戠珯寤虹珛鏃堕棿
insert into system_config(config_key, config_value, config_type, description, create_time, update_time)
values ('start-time','xxxx-xx-xx','string','缃戠珯寤虹珛鏃堕棿',now(),now());

