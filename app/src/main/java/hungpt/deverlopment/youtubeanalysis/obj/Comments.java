package hungpt.deverlopment.youtubeanalysis.obj;

public class Comments {
    private String authorDisplayName;
    private String authorProfileImageUrl;
    private String textDisplay;
    private String likeCount;
    private String publishedAt;

    public Comments(String authorDisplayName, String authorProfileImageUrl, String textDisplay, String likeCount, String publishedAt) {
        this.authorDisplayName = authorDisplayName;
        this.authorProfileImageUrl = authorProfileImageUrl;
        this.textDisplay = textDisplay;
        this.likeCount = likeCount;
        this.publishedAt = publishedAt;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }

    public String getAuthorProfileImageUrl() {
        return authorProfileImageUrl;
    }

    public void setAuthorProfileImageUrl(String authorProfileImageUrl) {
        this.authorProfileImageUrl = authorProfileImageUrl;
    }

    public String getTextDisplay() {
        return textDisplay;
    }

    public void setTextDisplay(String textDisplay) {
        this.textDisplay = textDisplay;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}
