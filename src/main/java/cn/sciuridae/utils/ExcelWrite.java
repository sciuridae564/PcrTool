package cn.sciuridae.utils;

import cn.sciuridae.dataBase.bean.KnifeList;
import cn.sciuridae.dataBase.bean.TeamMember;
import cn.sciuridae.dataBase.service.KnifeListService;
import cn.sciuridae.dataBase.service.TeamMemberService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static cn.sciuridae.utils.timeUtil.localDateTolocalDateTimes;

public class ExcelWrite {

    @Autowired
    TeamMemberService teamMemberServiceImpl;
    @Autowired
    KnifeListService knifeListServiceImpl;
    private File file;//文件
    private Workbook workbook = new HSSFWorkbook();//工作簿
    private List<Sheet> sheets = new ArrayList<>();//工作簿里的一个个表
    private List<LocalDate> dates;//日期
    private long groupQQ;//工会主键

    public ExcelWrite(String fileName, List<LocalDate> dates, long groupId) throws IOException {
        file = new File(fileName);
        System.out.println(fileName);
        if (!file.exists() || !file.isFile()) {
            file.createNewFile();
        }
        this.dates = dates;
        this.groupQQ = groupId;
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
        row.createCell(8).setCellValue("总伤");
        for (int i = 1; i < 21; i++) {
            for (int j = 1; j < 6; j++) {
                Cell kingskiller = row.createCell(8 + (i - 1) * 5 + j);
                kingskiller.setCellValue(i + "轮" + j + "王");
            }
        }

    }

    //填写表的数据部分
    public void writedDate() throws NullPointerException {
        for (LocalDate date : dates) {
            int sumVoidKnife = 0;
            Sheet sheet = workbook.createSheet();
            sheets.add(sheet);
            readyHead(sheet);//写表头
            LocalDateTime[] localDateTimes = localDateTolocalDateTimes(date);

            //这个工会所有的成员
            List<TeamMember> list = teamMemberServiceImpl.getTeamMemberByGroup(groupQQ);
            long hurtSum = 0;
            int i = 1;//这个其实代表是第几行
            int j = 1;//j是第几列
            //输出名字和余刀
            HashMap<String, Row> stringIntegerHashMap = new HashMap<>();
            //工会人员循环
            for (TeamMember teamMember : list) {
                List<KnifeList> knives = knifeListServiceImpl.getKnife(teamMember.getUserQQ(), localDateTimes[0], localDateTimes[1]);
                int voidKnife = 3;
                Row row = sheet.createRow(i);
                //各个人员出刀信息的循环
                for (KnifeList knife : knives) {
                    if (knife.getComplete()) {
                        voidKnife--;
                    }//统计空刀数
                    hurtSum += knife.getHurt();//统计总伤害
                    //输出伤害
                    if (knife.getComplete()) {
                        row.createCell(j * 2 + 1).setCellValue(knife.getHurt());
                        j++;
                    } else {
                        row.createCell(j * 2).setCellValue(knife.getHurt());
                    }
                    //输出每个王的伤害统计总额
                    try {
                        double q = row.getCell(8 + (knife.getLoop() * 5 + knife.getPosition())).getNumericCellValue();
                        row.createCell(8 + (knife.getLoop() * 5 + knife.getPosition())).setCellValue(q + knife.getHurt());
                    } catch (NullPointerException e) {
                        row.createCell(8 + (knife.getLoop() * 5 + knife.getPosition())).setCellValue(knife.getHurt());
                    }
                    row.createCell(0).setCellValue(teamMember.getUserQQ() + "/" + teamMember.getName());//写第一列 昵称
                    row.createCell(1).setCellValue(voidKnife);//剩余刀数
                }
                row.createCell(8).setCellValue(hurtSum);//总伤害写入
                hurtSum = 0;
                j = 1;
                i++;
            }
            Row row = sheet.createRow(i);//现在这个是最后总结的行
            row.createCell(0).setCellValue("总剩刀数");
            row.createCell(1).setCellValue(sumVoidKnife);
            //写入磁盘中
            reflashFile();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
