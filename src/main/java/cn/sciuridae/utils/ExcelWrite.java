package cn.sciuridae.utils;

import cn.sciuridae.dataBase.bean.KnifeList;
import cn.sciuridae.dataBase.bean.TeamMember;
import cn.sciuridae.dataBase.service.KnifeListService;
import cn.sciuridae.dataBase.service.TeamMemberService;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static cn.sciuridae.utils.timeUtil.localDateTolocalDateTimes;

public class ExcelWrite {

    TeamMemberService teamMemberServiceImpl;
    KnifeListService knifeListServiceImpl;
    private File file;//文件
    private Workbook workbook = new HSSFWorkbook();//工作簿
    private List<Sheet> sheets = new ArrayList<>();//工作簿里的一个个表
    private List<LocalDate> dates;//日期
    private long groupQQ;//工会主键


    public ExcelWrite(String fileName, List<LocalDate> dates, long groupId, TeamMemberService teamMemberServiceImpl, KnifeListService knifeListServiceImpl) throws IOException {
        file = new File(fileName);
        System.out.println(fileName);
        if (!file.exists() || !file.isFile()) {
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            file.createNewFile();
        }
        this.dates = dates;
        this.groupQQ = groupId;
        this.knifeListServiceImpl = knifeListServiceImpl;
        this.teamMemberServiceImpl = teamMemberServiceImpl;
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
            CellStyle style1 = workbook.createCellStyle();
            style1.setFillForegroundColor(IndexedColors.RED.getIndex());
            style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle style2 = workbook.createCellStyle();
            style2.setFillForegroundColor(IndexedColors.GOLD.getIndex());
            style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle style3 = workbook.createCellStyle();
            style3.setFillForegroundColor(IndexedColors.VIOLET.getIndex());
            style3.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            //这个工会所有的成员
            List<TeamMember> list = teamMemberServiceImpl.getTeamMemberByGroup(groupQQ);
            long hurtSum = 0;
            int i = 1;//这个其实代表是第几行
            int j = 1;//j是第几列
            //工会人员循环
            for (TeamMember teamMember : list) {
                List<KnifeList> knives = knifeListServiceImpl.getKnife(teamMember.getUserQQ(), localDateTimes[0], localDateTimes[1]);
                int voidKnife = 3;
                Row row = sheet.createRow(i);
                //各个人员出刀信息的循环
                if (knives.size() == 0) {
                    row.createCell(0).setCellValue(teamMember.getUserQQ() + "/" + teamMember.getName());//写第一列 昵称
                    row.createCell(1).setCellValue(3);//剩余刀数
                    sumVoidKnife += 3;
                } else {
                    for (KnifeList knife : knives) {
                        if (knife.getComplete()) {
                            voidKnife--;
                        }//统计空刀数
                        hurtSum += knife.getHurt();//统计总伤害
                        //输出伤害
                        Cell cell;
                        if (knife.getComplete()) {
                            cell=row.createCell(j * 2 + 1);
                            j++;
                        } else {
                            cell=row.createCell(j * 2);
                        }
                        switch (knife.getList()){
                            case 1:cell.setCellStyle(style1);break;
                            case 2:cell.setCellStyle(style2);break;
                            case 3:cell.setCellStyle(style3);break;
                        }
                        cell.setCellValue(knife.getHurt());
                        //输出这刀对这个王的伤害总额
                        try {
                            double q = row.getCell(8 + (knife.getLoop() * 5 - 5 + knife.getPosition())).getNumericCellValue();
                            row.createCell(8 + (knife.getLoop() * 5 - 5 + knife.getPosition())).setCellValue(q + knife.getHurt());
                        } catch (NullPointerException e) {
                            row.createCell(8 + (knife.getLoop() * 5 - 5 + knife.getPosition())).setCellValue(knife.getHurt());
                        }
                        sumVoidKnife += voidKnife;
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

            Row row1 = sheet.createRow(i+3);
            Cell cell= row.createCell(0);
            cell.setCellValue("一队刀");cell.setCellStyle(style1);
            cell= row.createCell(1);
            cell.setCellValue("二队刀");cell.setCellStyle(style2);
            cell= row.createCell(2);
            cell.setCellValue("三队刀");cell.setCellStyle(style3);
            cell= row.createCell(3);
            cell.setCellValue("旧版本出刀可能没有区分dd刀和大刀的数据，所以统一就只有红色了");


            //拜托，你们工会真的很弱哎（笑
            if (sumVoidKnife > list.size() * 1.5) {
                BufferedImage bufferImg ;
                try {
                    ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                    bufferImg = ImageIO.read(ExcelWrite.class.getResourceAsStream("/image/xun.jpg"));
                    ImageIO.write(bufferImg, "jpg", byteArrayOut);

                    //画图的顶级管理器，一个sheet只能获取一个
                    Drawing<?> patriarch = sheet.createDrawingPatriarch();
                    //anchor主要用于设置图片的属性
                    HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 255, 255, (short) 7, 5, (short) 10, 15);
                    anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                    //插入图片
                    patriarch.createPicture(anchor, workbook.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


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
