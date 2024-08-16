package com.example.mysqlAndJpa.runner;

import com.example.mysqlAndJpa.model.Member;
import com.example.mysqlAndJpa.service.MemberService;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MysqlAndJpaRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(MysqlAndJpaRunner.class);
    @Value("${report.file.dir}")
    private String fileDir;
    @Value("#{'${report.sheet.title}'.split(',')}")
    private List<String> titleList;
    @Autowired
    private MemberService memberService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("-----MysqlAndJpaRunner Start-----");
        try {
            List<Member> memberList = memberService.findAll();
            generateExcel(memberList);

        }catch (Exception e){
            logger.error("產生Excel檔發生錯誤", e);
        }
        logger.info("-----MysqlAndJpaRunner End-----");
    }

    private void generateExcel(List<Member> memberList) throws Exception {
        // 開始創建xlsx檔案
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("會員資料");
        XSSFRow row = null;
        XSSFCell cell = null;

        // 設定Style
        XSSFFont bold = workbook.createFont();
        bold.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.cloneStyleFrom(style);
        titleStyle.setFont(bold);

        // 開始塞入內容
        int rowNum = 0;
        row = sheet.createRow(rowNum);

        for(int i=0; i<titleList.size(); i++){
            cell = row.createCell(i);
            cell.setCellStyle(titleStyle);
            cell.setCellValue(titleList.get(i));
        }

        if(!memberList.isEmpty()){
            for(Member member :memberList){
                int cellNum = 0;
                rowNum++;
                row = sheet.createRow(rowNum);

                cell = row.createCell(cellNum);
                cell.setCellStyle(style);
                cell.setCellValue(member.getMemberId());
                cellNum++;

                cell = row.createCell(cellNum);
                cell.setCellStyle(style);
                cell.setCellValue(member.getEmail());
                cellNum++;

                cell = row.createCell(cellNum);
                cell.setCellStyle(style);
                cell.setCellValue(member.getName());
                cellNum++;

                cell = row.createCell(cellNum);
                cell.setCellStyle(style);
                cell.setCellValue(member.getAge());
            }
        }else {
            logger.info("資料庫內無資料");
        }

        // 凍結窗格
        sheet.createFreezePane(titleList.size(), 1);

        // 儲存格根據內容自適應寬度
        for(int cellLen=0; cellLen < titleList.size(); cellLen++){
            int maxLen = 0;
            int currentLen = 0;

            for(int rowLen=0; rowLen <= memberList.size(); rowLen++){
                Cell currentCell = sheet.getRow(rowLen).getCell(cellLen);

                // 根據字符串的字節長度計算列寬
                if(currentCell.getCellTypeEnum() == CellType.STRING){
                    String value = currentCell.getStringCellValue();
                    currentLen = (value.getBytes().length + 3) * 256;

                }else if (currentCell.getCellTypeEnum() == CellType.NUMERIC){
                    double numericValue = currentCell.getNumericCellValue();
                    String value = String.valueOf(numericValue);
                    currentLen = (value.getBytes().length + 3) * 256;
                }

                if(currentLen > maxLen){
                    maxLen = currentLen;
                }
            }
            sheet.setColumnWidth(cellLen, maxLen); //以最長的字符串長度設置為列寬 (必須連最長的內容都要顯示出來)
        }

        // 創建檔案路徑
        File file = new File(fileDir);
        if(!file.exists()){
            file.mkdirs();
        }
        logger.info("fileDir: {}", fileDir);
        String fileName = "Member_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(fileDir + fileName);
            workbook.write(output);
            output.flush();
            workbook.close();
        } catch (Exception e) {
            logger.error("write file fail", e);
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(output);
        }

    }
}
