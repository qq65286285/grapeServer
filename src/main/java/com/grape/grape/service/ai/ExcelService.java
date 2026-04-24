package com.grape.grape.service.ai;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    public Workbook readExcel(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Excel file not found: " + filePath);
        }
        FileInputStream fis = new FileInputStream(file);
        return new XSSFWorkbook(fis);
    }

    public List<List<String>> parseExcel(Workbook workbook) {
        List<List<String>> data = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(0); // 读取第一个sheet
        
        // 遍历所有行
        for (Row row : sheet) {
            List<String> rowData = new ArrayList<>();
            // 遍历所有列
            for (Cell cell : row) {
                String cellValue = getCellValue(cell);
                rowData.add(cellValue);
            }
            if (!rowData.isEmpty()) {
                data.add(rowData);
            }
        }
        
        return data;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    public void closeWorkbook(Workbook workbook) {
        if (workbook != null) {
            try {
                workbook.close();
            } catch (IOException e) {
                System.out.println("Error closing workbook: " + e.getMessage());
            }
        }
    }
}
