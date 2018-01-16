package build.dream.catering.controllers;

import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.LogUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

@Controller
@RequestMapping(value = "/calculate")
public class CalculateController {
    @RequestMapping(value = "/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("calculate/index");
        return modelAndView;
    }

    @RequestMapping(value = "/export")
    @ResponseBody
    public String export() throws IOException {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        LogUtils.info(requestParameters.toString());
        Integer startMuPrice = Integer.valueOf(requestParameters.get("startMuPrice"));
        Integer endMuPrice = Integer.valueOf(requestParameters.get("endMuPrice"));
        Integer rowNumber = endMuPrice - startMuPrice + 1;
        // 土地规模
        Integer landScale = Integer.valueOf(requestParameters.get("landScale"));
        //原始土地总价
        Double originalTotalLandPrice = Double.valueOf(requestParameters.get("originalTotalLandPrice"));

        // 原始土地单价
        Double originalLandPrice = originalTotalLandPrice / landScale;
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(1000);
        Sheet sheet = sxssfWorkbook.createSheet("土地增值税合计");
        Row titleRow = sheet.createRow(0);
        String[] titles = {"亩单价", "土地规模", "原土地总价", "原土地单价", "土地作价总价", "增值额", "增值率", "适应税率", "速算扣除系数", "土地增值税"};
        for (int index = 0; index < titles.length; index++) {
            Cell cell = titleRow.createCell(index);
            cell.setCellValue(titles[index]);
        }
        for (int rowIndex = 1; rowIndex <= rowNumber; rowIndex++) {
            Row row = sheet.createRow(rowIndex);
            int muPrice = startMuPrice + rowIndex - 1;
            Cell muPriceCell = row.createCell(0);
            muPriceCell.setCellValue(muPrice);

            Cell landScaleCell = row.createCell(1);
            landScaleCell.setCellValue(landScale);

            Cell originalTotalLandPriceCell = row.createCell(2);
            originalTotalLandPriceCell.setCellValue(originalTotalLandPrice.intValue());

            Cell originalLandPriceCell = row.createCell(3);
            originalLandPriceCell.setCellValue(Double.valueOf(String.format("%.2f", originalLandPrice)));

            double totalLandPrice = muPrice * landScale;
            Cell totalLandPriceCell = row.createCell(4);
            totalLandPriceCell.setCellValue(totalLandPrice);

            double valueAdded = totalLandPrice - originalTotalLandPrice;
            Cell valueAddedCell = row.createCell(5);
            valueAddedCell.setCellValue(valueAdded);

            double valueAddedRate = valueAdded / originalTotalLandPrice;
            Cell valueAddedRateCell = row.createCell(6);
            valueAddedRateCell.setCellValue(Double.valueOf(String.format("%.2f", valueAddedRate)));

            double adaptiveTaxRate = 0;
            double deductionCoefficient = 0;
            if (valueAddedRate <= 0.5) {
                adaptiveTaxRate = 0.3;
                deductionCoefficient = 0;
            } else if (valueAddedRate <= 1) {
                adaptiveTaxRate = 0.4;
                deductionCoefficient = 0.05;
            } else if (valueAddedRate <= 2) {
                adaptiveTaxRate = 0.5;
                deductionCoefficient = 0.15;
            } else {
                adaptiveTaxRate = 0.6;
                deductionCoefficient = 0.35;
            }

            Cell adaptiveTaxRateCell = row.createCell(7);
            adaptiveTaxRateCell.setCellValue(adaptiveTaxRate);

            Cell deductionCoefficientCell = row.createCell(8);
            deductionCoefficientCell.setCellValue(deductionCoefficient);

            double landValueIncrementTax = Double.valueOf(String.format("%.2f", valueAdded * adaptiveTaxRate - originalTotalLandPrice * deductionCoefficient));
            Cell landValueIncrementTaxCell = row.createCell(9);
            landValueIncrementTaxCell.setCellValue(landValueIncrementTax);

        }

        String filename = URLEncoder.encode("收益计算曲线.xlsx","UTF-8");

        HttpServletResponse httpServletResponse = ApplicationHandler.getHttpServletResponse();
        httpServletResponse.addHeader("Content-Disposition", "attachment;filename=" + filename);
        sxssfWorkbook.write(httpServletResponse.getOutputStream());
        return null;
    }
}
