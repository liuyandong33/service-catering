package build.dream.erp.controllers;

import build.dream.erp.utils.GsonUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/demo")
public class DemoController {
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("demo/index");
        return modelAndView;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String list() {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("id", 100);
        row.put("name", "aa");
        row.put("email", "554030404@qq.com");
        rows.add(row);
        rows.add(row);
        rows.add(row);
        rows.add(row);
        rows.add(row);
        rows.add(row);
        rows.add(row);
        rows.add(row);
        rows.add(row);
        rows.add(row);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("rows", rows);
        data.put("total", 10);
        return GsonUtils.toJson(data);
    }
}
