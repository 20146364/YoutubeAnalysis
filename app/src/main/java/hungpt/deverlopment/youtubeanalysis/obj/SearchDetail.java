package hungpt.deverlopment.youtubeanalysis.obj;

public class SearchDetail {
    private String id;
    private String title;
    private String channelId;

    public SearchDetail(String id, String title, String channelId) {
        this.id = id;
        this.title = title;
        this.channelId = channelId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
