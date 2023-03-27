package com.example.kaoqin;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@org.springframework.stereotype.Controller
@RequestMapping("/file")
public class Controller {
    public static String filepath = "";
    @RequestMapping("/hello")
    public String say() {
        return "index";
    }
    @RequestMapping("/inside")
    @ResponseBody
    public Map<String, String> inside(String starttime_h,  String starttime_m,
                                      String endtime_h, String endtime_m
            ,String choose) {
        Map<String, String> result = new HashMap<>();
        String path=filepath;
        System.out.println(path);
        if (path.equals("")){
            result.put("status", "no_file");
            return result;
        }
        path="//"+path.split("/")[1]+"//"+path.split("/")[2];
        System.out.println(path);
        if (choose.equals("false")) {
            try {
                input a = new input(path);
                a.check();
                a.judge1();
                result.put("status", "ok");
                result.put("kq",a.kqnum);
            } catch (Exception e) {
                e.printStackTrace();
                result.put("status", "fail");
            }
        } else if (choose.equals("true")) {
            try {
                input a = new input(path);
                a.check();
                a.judge2(starttime_h + ":" + starttime_m + ":00", endtime_h + ":" + endtime_m + ":00");
                result.put("status", "ok");
                result.put("kq",a.kqnum);
            } catch (Exception e) {
                e.printStackTrace();
                result.put("status", "fail");
            }
        }
        filepath = "";
        return result;
    }

//    @PostMapping("/check_total")
//    @ResponseBody

    @PostMapping("/getpath")
    @ResponseBody
    public String getpath(@RequestParam("filepath") String path, HttpServletRequest request)  {
       String res="";
        filepath=path;
        System.out.println("getpath:"+filepath);
        return res;
    }

    @PostMapping("/upload1")
    @ResponseBody
    public Map<String, Object> upload1(@RequestParam("file") MultipartFile file, HttpServletRequest request)  {

        //log.info("系统路径={}",request.getSession().getServletContext().getRealPath(""));
//        String path ="E:\\";
        String path ="/upload/";
        Map<String, Object> result = new HashMap<>();
        new File(path).mkdirs();
        try {
            //System.out.println(file.getOriginalFilename());
            File file1 = new File(path + file.getOriginalFilename());
            if (file1.exists()){
                System.out.println("文件已经存在");
                result.put("status", "false");
                return result;
            }
            file.transferTo(file1);

            result.put("status", "true");
            result.put("path", path + file.getOriginalFilename());
            result.put("data", null);
        } catch (IOException e) {
            result.put("status", "error");
            e.printStackTrace();
        }
        return result;
    }

}
