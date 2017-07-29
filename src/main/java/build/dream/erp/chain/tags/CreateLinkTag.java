package build.dream.erp.chain.tags;

import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * Created by liuyandong on 2017/6/6.
 */
public class CreateLinkTag extends SimpleTagSupport {
    private String controller = null;
    private String action = null;
    private String base = null;

    public void setController(String controller) {
        this.controller = controller;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setBase(String base) {
        this.base = base;
    }

    @Override
    public void doTag() throws JspException, IOException {
        StringBuffer link = new StringBuffer();
        if (StringUtils.isNotBlank(base)) {
            link.append(base).append("/");
        }
        link.append(controller).append("/").append(action);
        getJspContext().getOut().write(link.toString());
    }
}
