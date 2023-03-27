package com.example.kaoqin;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.example.kaoqin.DBconnect.RS2List;

@RestController
public class Contrller2 {
    @RequestMapping("/hello")
    public String say(){
        return "index";
    }
    @RequestMapping ("check")
    public List check(String check_num) throws SQLException {
        DBconnect db=new DBconnect();
        db.select(check_num);
        return RS2List(db.select(check_num));
    }

    @RequestMapping("/insert")
     public static Boolean insert(String name,String kq_num,String reason) throws SQLException {
        DBconnect db=new DBconnect();
        Boolean res=db.insert(kq_num,name,reason);
        return res;
    }
    @RequestMapping(value = "/handle", method = RequestMethod.POST)
    @ResponseBody
    public Boolean handle(@RequestParam("handle")String handle,
                          @RequestParam("kq_num")String kq_num,@RequestParam("name")String name){
        DBconnect db=new DBconnect();
        Boolean res=db.handle(handle,kq_num,name);
        return res;
    }
    @PostMapping("/check_apply")
    public List check_appply() throws SQLException {
        DBconnect db=new DBconnect();
        return RS2List(db.check_apply());
    }
    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestParam("username")String username,@RequestParam("password")String password) throws SQLException {
        DBconnect db=new DBconnect();
        return db.login(username,password);
    }
    @PostMapping("/check_total")
    public Map<String,String> check_total() throws SQLException {
        DBconnect db=new DBconnect();
        return db.check_total();
    }
    @RequestMapping("/check_kq")
    public int check_kq(String name, String kq_num) throws SQLException {
        System.out.println(name);
        System.out.println(kq_num);
        DBconnect db=new DBconnect();
//        db.insert("512","yfah","122");
        int i=db.check_kq(name,kq_num);
        //System.out.println(i);
        return i;
    }
//    @PostMapping("/fileUpload")
//    public String uploadMusicFile(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws IOException {
//        String fileName = file.getOriginalFilename();
//        // 获取文件后缀
//        String prefix = fileName.substring(fileName.lastIndexOf("."));
//        // 保存文件名
//        String saveFileName = String.valueOf(System.currentTimeMillis()) + prefix;
//        // 保存路径
//        String saveFilePath = "E:\\";
//        // 保存文件
//        File saveFile = new File(saveFilePath + saveFileName);
//
//        file.transferTo(saveFile);
//
//        return"";
//    }

}
