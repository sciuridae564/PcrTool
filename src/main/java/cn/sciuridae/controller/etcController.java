package cn.sciuridae.controller;

import cn.sciuridae.dataBase.bean.PcrUnion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.List;

import static cn.sciuridae.utils.stringTool.getExcelDirName;

@Controller
public class etcController {

    @GetMapping("/excel/List")
    @ResponseBody
    public String[] getExcelList(HttpSession session) {
        PcrUnion group = (PcrUnion) session.getAttribute("group");
        String dirName = getExcelDirName(String.valueOf(group.getGroupQQ()));
        File dir = new File(dirName);
        String[] fileNames = dir.list();
        return fileNames;
    }

    @RequestMapping("/downloadFile")
    @ResponseBody
    private String downloadFile(String fileName, HttpServletResponse response, HttpSession session) {
        PcrUnion group = (PcrUnion) session.getAttribute("group");
        String dirName = getExcelDirName(String.valueOf(group.getGroupQQ()));
        File file = new File(dirName + fileName);
        //使用流的形式下载文件
        try {
            //加载文件
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "文件下载出错";
        }
    }


    public class ReportDataBean {
        private List<String> fileAbsolutePaths;
        private String[] fileNames;

        public ReportDataBean(List<String> categories, String[] data) {
            this.fileAbsolutePaths = categories;
            this.fileNames = data;
        }

        public List<String> getFileAbsolutePaths() {
            return fileAbsolutePaths;
        }

        public void setFileAbsolutePaths(List<String> fileAbsolutePaths) {
            this.fileAbsolutePaths = fileAbsolutePaths;
        }

        public String[] getFileNames() {
            return fileNames;
        }

        public void setFileNames(String[] fileNames) {
            this.fileNames = fileNames;
        }
    }
}
