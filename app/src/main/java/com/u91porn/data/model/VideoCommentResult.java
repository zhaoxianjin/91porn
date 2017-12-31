package com.u91porn.data.model;

import java.util.List;

/**
 * 评论接口回调
 *
 * @author flymegoc
 * @date 2017/12/28
 */

public class VideoCommentResult {
    /**
     * 留言已经提交，审核后通过
     */
    public static final int COMMENT_SUCCESS=1;
    /**
     * 你已经在这个视频下留言过.
     */
    public static final int COMMENT_ALLREADY=2;
    /**
     * 不允许留言!
     */
    public static final int COMMENT_NO_PERMISION=3;
    /**
     * attributes : {"id":""}
     * a : [{"attributes":{"id":""},"data":1}]
     * data :
     */

    private AttributesBean attributes;
    private String data;
    private List<ABean> a;

    public AttributesBean getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributesBean attributes) {
        this.attributes = attributes;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<ABean> getA() {
        return a;
    }

    public void setA(List<ABean> a) {
        this.a = a;
    }

    public static class AttributesBean {
        /**
         * id :
         */

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class ABean {
        /**
         * attributes : {"id":""}
         * data : 1
         */

        private AttributesBeanX attributes;
        private int data;

        public AttributesBeanX getAttributes() {
            return attributes;
        }

        public void setAttributes(AttributesBeanX attributes) {
            this.attributes = attributes;
        }

        public int getData() {
            return data;
        }

        public void setData(int data) {
            this.data = data;
        }

        public static class AttributesBeanX {
            /**
             * id :
             */

            private String id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }
}
