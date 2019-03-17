package build.dream.catering.controllers;

import build.dream.catering.models.table.*;
import build.dream.catering.services.TableService;
import build.dream.common.annotations.ApiRestAction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/table")
public class TableController {
    /**
     * 保存桌台区域
     *
     * @return
     */
    @RequestMapping(value = "/saveTableArea", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveTableAreaModel.class, serviceClass = TableService.class, serviceMethodName = "saveTableArea", error = "保存桌台区域失败")
    public String saveTableArea() {
        return null;
    }

    /**
     * 保存桌台
     *
     * @return
     */
    @RequestMapping(value = "/saveBranchTable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveBranchTableModel.class, serviceClass = TableService.class, serviceMethodName = "saveBranchTable", error = "保存桌台失败")
    public String saveBranchTable() {
        return null;
    }

    /**
     * 分页查询桌台
     *
     * @return
     */
    @RequestMapping(value = "/listBranchTables", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ListBranchTablesModel.class, serviceClass = TableService.class, serviceMethodName = "listBranchTables", error = "查询桌台列表失败")
    public String listBranchTables() {
        return null;
    }

    /**
     * 删除桌台区域
     *
     * @return
     */
    @RequestMapping(value = "/deleteTableArea", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = DeleteTableAreaModel.class, serviceClass = TableService.class, serviceMethodName = "deleteTableArea", error = "删除桌台区域失败")
    public String deleteTableArea() {
        return null;
    }

    @RequestMapping(value = "/deleteBranchTable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = DeleteBranchTableModel.class, serviceClass = TableService.class, serviceMethodName = "deleteBranchTable", error = "删除桌台失败")
    public String deleteBranchTable() {
        return null;
    }
}
