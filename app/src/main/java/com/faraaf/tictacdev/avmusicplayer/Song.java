package com.faraaf.tictacdev.avmusicplayer;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "songs_table")
public class Song implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;

    private String artist;

    private String thumbnail;

    private String songLink;

    public Song() {
    }

    public Song(long songID, String songTitle, String songArtist, String thumbNail, String link) {
        id = songID;
        title = songTitle;
        artist = songArtist;
        thumbnail = thumbNail;
        songLink = link;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getSongLink() {
        return songLink;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }
}
