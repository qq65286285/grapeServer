// package com.grape.grape;

// import org.apache.poi.ss.usermodel.*;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// import org.junit.jupiter.api.Test;

// import java.io.File;
// import java.io.FileInputStream;
// import java.io.IOException;
// import java.io.PrintStream;
// import java.io.UnsupportedEncodingException;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// public class ExcelToTestScenarioTest {

//     @Test
//     public void testExcelToTestScenario() throws IOException {
//         // 设置控制台输出为UTF-8编码
//         try {
//             System.setOut(new PrintStream(System.out, true, "UTF-8"));
//         } catch (UnsupportedEncodingException e) {
//             e.printStackTrace();
//         }

//         // Excel文件路径
//         String excelPath = "src/main/resources/excelTemplate/caseExcel/-导入验证.xlsx";
//         File excelFile = new File(excelPath);

//         // 检查文件是否存在
//         if (!excelFile.exists()) {
//             System.out.println("Excel文件不存在: " + excelFile.getAbsolutePath());
//             return;
//         }

//         // 创建映射来分组场景和类型
//         Map<String, Map<String, List<String>>> scenarioMap = new HashMap<>();

//         try (FileInputStream fis = new FileInputStream(excelFile);
//              Workbook workbook = new XSSFWorkbook(fis)) {

//             // 获取第一个工作表
//             Sheet sheet = workbook.getSheetAt(0);

//             // 从第二行开始读取数据（第一行是标题）
//             for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
//                 Row row = sheet.getRow(rowIndex);
//                 if (row == null) continue;

//                 // 获取单元格值
//                 String scenario = getCellValue(row.getCell(0));
//                 String type = getCellValue(row.getCell(1));
//                 String content = getCellValue(row.getCell(2));

//                 // 跳过空行
//                 if (scenario.isEmpty() && type.isEmpty() && content.isEmpty()) {
//                     continue;
//                 }

//                 // 分组数据
//                 scenarioMap.computeIfAbsent(scenario, k -> new HashMap<>())
//                         .computeIfAbsent(type, k -> new ArrayList<>())
//                         .add(content);
//             }
//         }

//         // 处理分组数据并打印结果
//         System.out.println("===== Excel转换为测试场景结果 =====");
//         for (Map.Entry<String, Map<String, List<String>>> scenarioEntry : scenarioMap.entrySet()) {
//             String scenarioName = scenarioEntry.getKey();
//             System.out.println("\n场景: " + scenarioName);

//             Map<String, List<String>> typeMap = scenarioEntry.getValue();
//             for (Map.Entry<String, List<String>> typeEntry : typeMap.entrySet()) {
//                 String typeName = typeEntry.getKey();
//                 List<String> contents = typeEntry.getValue();

//                 System.out.println("  类型: " + typeName);
//                 System.out.println("  内容列表:");
//                 for (int i = 0; i < contents.size(); i++) {
//                     System.out.println("    " + (i + 1) + ". " + contents.get(i));
//                 }
//             }
//         }
//         System.out.println("\n===== 转换完成 =====");
//     }

//     /**
//      * 获取单元格值
//      */
//     private String getCellValue(Cell cell) {
//         if (cell == null) {
//             return "";
//         }

//         String value;
//         switch (cell.getCellType()) {
//             case STRING:
//                 value = cell.getStringCellValue();
//                 break;
//             case NUMERIC:
//                 if (DateUtil.isCellDateFormatted(cell)) {
//                     value = cell.getDateCellValue().toString();
//                 } else {
//                     value = String.valueOf(cell.getNumericCellValue());
//                 }
//                 break;
//             case BOOLEAN:
//                 value = String.valueOf(cell.getBooleanCellValue());
//                 break;
//             case FORMULA:
//                 value = cell.getCellFormula();
//                 break;
//             default:
//                 value = "";
//         }

//         return value.trim();
//     }
// }