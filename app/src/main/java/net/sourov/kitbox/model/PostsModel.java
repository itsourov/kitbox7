package net.sourov.kitbox.model;

public class PostsModel {

    String title, downloadLink, imageLink, description;

    public PostsModel() {
    }

    public PostsModel(String title, String downloadLink, String imageLink, String description) {
        this.title = title;
        this.downloadLink = downloadLink;
        this.imageLink = imageLink;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
