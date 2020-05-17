package cn.sciuridae.Excel;

import cn.sciuridae.DB.bean.Knife;
import cn.sciuridae.DB.sqLite.DB;
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

import static cn.sciuridae.constant.df;

public class excelWrite {

    File file;//文件
    Workbook workbook;//工作簿
    List<Sheet> sheets = new ArrayList<>();//工作簿里的一个个表
    private List<Date> dates;//日期
    private int groupId;//工会主键

    public excelWrite(String fileName, List<Date> dates, int groupId) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        workbook.write(new FileOutputStream(file));
        this.dates = dates;
        this.groupId = groupId;
    }

    public excelWrite(File file, List<Date> dates, int groupId) {
        try {
            if (!file.exists()) {
                File fileParent = file.getParentFile();
                if (!fileParent.exists()) {
                    fileParent.mkdirs();
                }
                file.createNewFile();
            }
            this.file = file;
            workbook = new HSSFWorkbook();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.dates = dates;
        this.groupId = groupId;
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
    private void readyHead(Sheet sheet) {
        sheet.setColumnWidth((short) 0, (short) (6000));//设置昵称空格长点
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("游戏ID");
        Cell cell1 = row.createCell(1);
        cell1.setCellValue("剩刀");
        row.createCell(2).setCellValue("一号刀");
        row.createCell(4).setCellValue("二号刀");
        row.createCell(6).setCellValue("三号刀");
        for (int i = 2; i < 8; i += 2) {
            CellRangeAddress region = new CellRangeAddress(0, 0, i, i + 1);
            sheet.addMergedRegion(region);
        }
        for (int i = 1; i < 21; i++) {
            for (int j = 1; j < 6; j++) {
                Cell kingskiller = row.createCell(7 + (i - 1) * 5 + j);
                kingskiller.setCellValue(i + "轮" + j + "王");
            }
        }

    }

    //填写表的数据部分
    public void writedDate() throws NullPointerException {
        for (Date date : dates) {
            int sumVoidKnife = 0;
            Sheet sheet = workbook.createSheet();
            sheets.add(sheet);
            readyHead(sheet);
            Map<String, Integer> map = DB.Instance.searchVoidKnifeByGroup(groupId, df.format(date));//空刀集
            Set<String> QQs;
            try {
                QQs = map.keySet();
            } catch (NullPointerException e) {
                throw e;
            }
            int i = 1;//这个其实代表是第几行
            //输出名字和余刀
            HashMap<String, Row> stringIntegerHashMap = new HashMap<>();
            for (String QQ : QQs) {
                Row row = sheet.createRow(i);
                row.createCell(0).setCellValue(QQ + "/" + DB.Instance.searchName(QQ));//写第一列 昵称
                sumVoidKnife += map.get(QQ);
                row.createCell(1).setCellValue(map.get(QQ));//剩余刀数
                stringIntegerHashMap.put(QQ, row);
                i++;
            }
            //输出伤害
            i = 1;
            int j = 1;//j是第几列
            Map<String, List<Knife>> map1 = DB.Instance.searchKnife(groupId, df.format(date));//刀集
            for (String QQ : QQs) {
                List<Knife> knives = map1.get(QQ);
                Row row = stringIntegerHashMap.get(QQ);
                for (Knife knife : knives) {
                    if (knife.isComplete()) {
                        row.createCell(j * 2 + 1).setCellValue(knife.getHurt());
                        j++;
                    } else {
                        row.createCell(j * 2).setCellValue(knife.getHurt());
                    }
                    System.out.println(((knife.getNo() - 10) / 10 * 5 + knife.getNo() % 10));
                    try {
                        double q = row.getCell(7 + ((knife.getNo() - 10) / 10 * 5 + knife.getNo() % 10)).getNumericCellValue();
                        row.createCell(7 + ((knife.getNo() - 10) / 10 * 5 + knife.getNo() % 10)).setCellValue(q + knife.getHurt());
                    } catch (NullPointerException e) {
                        row.createCell(7 + ((knife.getNo() - 10) / 10 * 5 + knife.getNo() % 10)).setCellValue(knife.getHurt());
                    }
                }
                j = 1;
                i++;
            }
            Row row = sheet.createRow(i);//现在这个是最后总结的行
            row.createCell(0).setCellValue("总剩刀数");
            row.createCell(1).setCellValue(sumVoidKnife);

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
