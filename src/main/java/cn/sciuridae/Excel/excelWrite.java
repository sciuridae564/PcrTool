package cn.sciuridae.Excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class excelWrite {

    File file;//文件
    Workbook workbook;//工作簿
    List<Sheet> sheets;//工作簿里的一个个表

    public excelWrite(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        workbook.write(new FileOutputStream(file));
    }

    public excelWrite(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            this.file = file;
            workbook = new HSSFWorkbook();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public excelWrite() throws IOException {
        File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
        String dir = desktopDir.getAbsolutePath();
        ArrayList<String> files = new ArrayList<>();
        File[] tempList = desktopDir.listFiles();
        for (File file : tempList) {
            files.add(file.getAbsolutePath());
        }
        int i = 0;
        for (; files.contains(dir + "\\" + i); i++) ;
        File file = new File(dir + "\\" + i);
        if (!file.exists()) {
            file.createNewFile();
        }
        workbook.write(new FileOutputStream(file));
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        workbook.close();
    }

    /**
     * 写表头,表头为固定格式
     */
    public void readyHead(Sheet sheet) {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("游戏ID");
        Cell cell1 = row.createCell(1);
        cell1.setCellValue("剩刀");
        for (int i = 2; i < 8; i += 2) {
            CellRangeAddress region = new CellRangeAddress(0, 0, i, i + 1);
            sheet.addMergedRegion(region);
        }


    }


    /**
     * 刷新写入
     */
    public void reflashFile() {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
