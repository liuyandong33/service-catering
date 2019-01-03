package build.dream.catering.models.weixin;

import build.dream.common.models.BasicModel;
import build.dream.common.models.CateringBasicModel;

import java.math.BigInteger;
import java.util.List;

public class SaveWeiXinMenuModel extends CateringBasicModel {
    private Button first;
    private Button second;
    private Button third;

    public Button getFirst() {
        return first;
    }

    public void setFirst(Button first) {
        this.first = first;
    }

    public Button getSecond() {
        return second;
    }

    public void setSecond(Button second) {
        this.second = second;
    }

    public Button getThird() {
        return third;
    }

    public void setThird(Button third) {
        this.third = third;
    }

    public static class Button extends BasicModel {
        private List<SubButton> subButtons;

        private BigInteger id;

        private String type;

        private String name;

        private String messageContent;

        private String url;

        private String mediaId;

        private String miniProgramAppId;

        private String pagePath;

        public List<SubButton> getSubButtons() {
            return subButtons;
        }

        public void setSubButtons(List<SubButton> subButtons) {
            this.subButtons = subButtons;
        }

        public BigInteger getId() {
            return id;
        }

        public void setId(BigInteger id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessageContent() {
            return messageContent;
        }

        public void setMessageContent(String messageContent) {
            this.messageContent = messageContent;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public String getMiniProgramAppId() {
            return miniProgramAppId;
        }

        public void setMiniProgramAppId(String miniProgramAppId) {
            this.miniProgramAppId = miniProgramAppId;
        }

        public String getPagePath() {
            return pagePath;
        }

        public void setPagePath(String pagePath) {
            this.pagePath = pagePath;
        }
    }

    public static class SubButton extends BasicModel {
        private BigInteger id;

        private String type;

        private String name;

        private String messageContent;

        private String url;

        private String mediaId;

        private String miniProgramAppId;

        private String pagePath;

        public BigInteger getId() {
            return id;
        }

        public void setId(BigInteger id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessageContent() {
            return messageContent;
        }

        public void setMessageContent(String messageContent) {
            this.messageContent = messageContent;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public String getMiniProgramAppId() {
            return miniProgramAppId;
        }

        public void setMiniProgramAppId(String miniProgramAppId) {
            this.miniProgramAppId = miniProgramAppId;
        }

        public String getPagePath() {
            return pagePath;
        }

        public void setPagePath(String pagePath) {
            this.pagePath = pagePath;
        }
    }
}
