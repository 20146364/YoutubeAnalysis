package hungpt.deverlopment.youtubeanalysis.commons;

public class DeveloperKey {
    public static final String DEVELOPER_KEY = "AIzaSyB1X13VAx1_kfyPS2a8Do1UqC_5tTqiOkQ";
//        public static final String WEB_DEVELOPER_KEY = "AIzaSyAvpCN6eDw8baIawkTJl-GqVOrameXE2Lk";
//    public static final String WEB_DEVELOPER_KEY = "AIzaSyCxbOGlovfzWcHhsMjOk2xYgq-hi3lu0hk";
    public static final String WEB_DEVELOPER_KEY = "AIzaSyCY7OC41sQaURiIYH-EBBJYhLg9jxkzgto";
    public static final String LINK_HOT_TREND = "https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails,statistics&chart=mostPopular&maxResults=50&regionCode=VN&key=" + WEB_DEVELOPER_KEY;


    public String listSearch(String key) {
        return "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + key + "&maxResults=25&key=" + WEB_DEVELOPER_KEY;
    }

    public String listSearchByID(String key) {
        return "https://www.googleapis.com/youtube/v3/search?part=id&q=" + key + "&maxResults=50&key=" + WEB_DEVELOPER_KEY;
    }

    public String detailVideoByID(String id) {
        return "https://www.googleapis.com/youtube/v3/videos?id=" + id + "&part=snippet,contentDetails,statistics&regionCode=VN&key=" + WEB_DEVELOPER_KEY;
    }

    public String getDataChannel(String id) {
        return "https://www.googleapis.com/youtube/v3/channels?part=snippet&id=" + id + "&key=" + WEB_DEVELOPER_KEY;
    }

    public String getListCommentByID(String id, String nextPageToken) {
        String link;
        if (nextPageToken != null) {
            link = "https://www.googleapis.com/youtube/v3/commentThreads?part=snippet,replies&videoId=" + id + "&maxResults=100&nextPageToken=" + nextPageToken + "&key=" + WEB_DEVELOPER_KEY;
        } else {
            link = "https://www.googleapis.com/youtube/v3/commentThreads?part=snippet,replies&videoId=" + id + "&maxResults=100&key=" + WEB_DEVELOPER_KEY;
        }
        return link;
    }

    public String getRelated(String id) {
        return "https://www.googleapis.com/youtube/v3/search?part=id&relatedToVideoId=" + id + "&maxResults=25&type=video&key=" + WEB_DEVELOPER_KEY;
    }
}
