package com.u91porn.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/28
 */

public class PigAvVideo {

    /**
     * sources : [{"default":"true","label":"HD","type":"video/mp4","file":"https://video-nrt1-1.xx.fbcdn.net/v/t42.9040-2/10000000_1543865839037627_4326451394849538048_n.mp4?efg=eyJ2ZW5jb2RlX3RhZyI6InN2ZV9oZCJ9&oh=a2b32a25a2f53f9054293b054c96d2fd&oe=5A6CDC5D"},{"label":"SD","type":"video/mp4","file":"https://video-nrt1-1.xx.fbcdn.net/v/t42.9040-2/10000000_703405793199718_4307533356695814144_n.mp4?efg=eyJ2ZW5jb2RlX3RhZyI6InN2ZV9zZCJ9&oh=cd2d2ac0ae14bbac8e896e4b0dec814b&oe=5A6CDD0E"}]
     * primary : html5
     * width : 100%
     * skin : roundster
     * image : https://img.pigav.com/2017/12/iii762.jpg
     * autostart : true
     * aspectratio : 16:10
     * startparam : start
     */

    private String primary;
    private String width;
    private String skin;
    private String image;
    private String file;
    private String autostart;
    private String aspectratio;
    private String startparam;
    private boolean fallback;
    private List<SourcesBean> sources;

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAutostart() {
        return autostart;
    }

    public void setAutostart(String autostart) {
        this.autostart = autostart;
    }

    public String getAspectratio() {
        return aspectratio;
    }

    public void setAspectratio(String aspectratio) {
        this.aspectratio = aspectratio;
    }

    public String getStartparam() {
        return startparam;
    }

    public void setStartparam(String startparam) {
        this.startparam = startparam;
    }

    public List<SourcesBean> getSources() {
        return sources;
    }

    public void setSources(List<SourcesBean> sources) {
        this.sources = sources;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public boolean isFallback() {
        return fallback;
    }

    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }

    public static class SourcesBean {
        /**
         * default : true
         * label : HD
         * type : video/mp4
         * file : https://video-nrt1-1.xx.fbcdn.net/v/t42.9040-2/10000000_1543865839037627_4326451394849538048_n.mp4?efg=eyJ2ZW5jb2RlX3RhZyI6InN2ZV9oZCJ9&oh=a2b32a25a2f53f9054293b054c96d2fd&oe=5A6CDC5D
         */

        @SerializedName("default")
        private String defaultX;
        private String label;
        private String type;
        private String file;

        public String getDefaultX() {
            return defaultX;
        }

        public void setDefaultX(String defaultX) {
            this.defaultX = defaultX;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }
    }

    @Override
    public String toString() {
        return "PigAvVideo{" +
                "primary='" + primary + '\'' +
                ", width='" + width + '\'' +
                ", skin='" + skin + '\'' +
                ", image='" + image + '\'' +
                ", autostart='" + autostart + '\'' +
                ", aspectratio='" + aspectratio + '\'' +
                ", startparam='" + startparam + '\'' +
                ", file='" + file + '\'' +
                ", fallback=" + fallback +
                ", sources=" + sources +
                '}';
    }
}
