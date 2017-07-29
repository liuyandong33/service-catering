package build.dream.erp.chain.tags;

import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * Created by liuyandong on 2017/6/6.
 */
public class ScriptTag extends SimpleTagSupport {
    private String type = null;
    private String dir = null;
    private String file = null;
    private String base = null;

    public void setType(String type) {
        this.type = type;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setBase(String base) {
        this.base = base;
    }

    @Override
    public void doTag() throws JspException, IOException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<script").append(" type=\"").append(type).append("\"").append(" src=\"");
        if (StringUtils.isNotBlank(base)) {
            stringBuffer.append(base).append("/");
        }
        stringBuffer.append(dir).append("/").append(file).append("\"").append("></script>");
        getJspContext().getOut().write(stringBuffer.toString());
    }
}
