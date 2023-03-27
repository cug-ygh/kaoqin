package com.example.kaoqin;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.mysql.cj.protocol.Resultset;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import org.springframework.core.ReactiveAdapterRegistry;


public class input {
    public static Connection myConnection;
    //存表格信息
    public static List<Map<String,String>> list = null;
    //存人员信息
    public static List<Map> person_list = new ArrayList<Map>();
    //预定开始，预定结束和总参会时长
    public static Date start;
    public static Date end;
    public static float total_t;
    //判断的阈值，单位为分钟,默认为5
    public static int threshold=5;
    public static String day_time;
    //考勤号
    public static String kqnum;
    input(String path) throws SQLException, ParseException{
        // TODO Auto-generated method stub
        Workbook wb =null;   //工作簿
        Sheet sheet = null;  //工作表

        Row row = null;      //行

        String cellData = null;
        String filePath = path;  //腾讯会议导出表地址
        //实际上需要获取的列应该是哪些？要求：设置考勤时间
        String columns[] = {"预定开始时间","预定结束时间","会议主题",
                "会议号","用户名称","入会时间","退会时间","单次参会时长","房间类型"} ;
        wb = readExcel(filePath);  //读取Excel文件
        if(wb != null){
            //用来存放表中数据
            list = new ArrayList<Map<String,String>>();
            //获取第一个sheet
            sheet = wb.getSheetAt(0);
            //获取最大行数
            int rownum = sheet.getPhysicalNumberOfRows();
            //获取第一行
            row = sheet.getRow(0);
            //获取最大列数
            int colnum = row.getPhysicalNumberOfCells();
            // System.out.println(rownum);
            for (int i = 1; i < rownum; i++) {
                Map<String,String> map = new LinkedHashMap<String,String>();
                row = sheet.getRow(i);
                if(row !=null){
                    for (int j=0;j < colnum;j++){
                        cellData = (String) getCellFormatValue(row.getCell(j));  //第i行的第j列
                        map.put(columns[j], cellData);    //map存放的是一行，其中列名+对应数据为一个键值对(entry)
                    }
                }else{
                    break;
                }
                list.add(map);
                start=Str2Date(map.get("预定开始时间"));
                day_time=map.get("预定开始时间").split(" ")[0];
                end=Str2Date(map.get("预定结束时间"));
                total_t=(end.getTime()-start.getTime())/60000;
                kqnum=map.get("会议号");
            }
        }
        //遍历解析出来的list,得到开始时间和结束时间以及总时长
        dbcon();
        Statement stat= null;
        stat = myConnection.createStatement();
        String sql = "select*from person_info";//查询语句
        ResultSet rs=stat.executeQuery(sql);//查询
        java.sql.ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        while (rs.next()) {
            Map rowData = new HashMap();
            for (int i = 1; i <= columnCount; i++) {
                // rowData.put(md.getColumnName(i), rs.getObject(i));
                rowData.put(md.getColumnName(i), rs.getObject(i));
            }
            rowData.put("最早开始时间", "");
            rowData.put("最晚结束时间", "");
            rowData.put("总参会时长", 0);
            rowData.put("迟到",0);
            rowData.put("早退", 0);
            rowData.put("旷课", 0);
            person_list.add(rowData);
        }
        myConnection.close();
        stat.close();
        rs.close();
//        System.out.println(person_list);
    }
   // public static
    public static Date Str2Date(String str) throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.parse(str);
    }
    public static void dbcon() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String   url="jdbc:mysql://47.96.112.166:3306/kaoqin";    //获取协议、 IP  和端口等信息
        String   user="root";                                  //获取数据库用户名
        String   password="lrh213202352";                     //取数据库用户密码
        try {
            myConnection = DriverManager.getConnection ( url ,  user , password);
            System.out.println("连接数据库成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void discon() throws SQLException {
        myConnection.close();
    }
    //导入学生名单
    public static void in_stu(String filepath) throws SQLException {
        Workbook wb =null;   //工作簿
        Sheet sheet = null;  //工作表
        Row row = null;      //行
        List<Map> stu_list = new ArrayList<Map>();
        String cellData = null;
        String filePath =filepath;
        //实际上需要获取的列应该是哪些？要求：设置考勤时间
        String columns[] = {"班级","姓名","学号","性别","专业","是否重修","手机号码"};
        wb = readExcel(filePath);  //读取Excel文件
        if(wb != null){
            //获取第一个sheet
            sheet = wb.getSheetAt(0);
            //获取最大行数
            int rownum = sheet.getPhysicalNumberOfRows();
            //获取第一行
            row = sheet.getRow(0);
            //获取最大列数
            int colnum = row.getPhysicalNumberOfCells();
            //System.out.println(rownum);
            for (int i = 1; i < rownum; i++) {
                Map<String,String> map = new LinkedHashMap<String,String>();
                row = sheet.getRow(i);
                if(row !=null){
                    for (int j=0;j < colnum;j++){
                        cellData = (String) getCellFormatValue(row.getCell(j));
                        map.put(columns[j], cellData);    //map存放的是一行，其中列名+对应数据为一个键值对(entry)
                    }
                }else{
                    break;
                }
                stu_list.add(map);
            }
        }
        for(Map i:stu_list){
            System.out.println(i);
        }
        dbcon();
        Statement stat= null;
        stat = myConnection.createStatement();
        System.out.println("开始导入");
        for (Map<String,String> map : stu_list) {
            String name=map.get("姓名");
            String classname=map.get("班级");//.split("\\.")[0];
            String stunum=map.get("学号");//.split("\\.")[0];
            String sex=map.get("性别");
            //System.out.println(stunum.split("\\.")[0]);
            String telnum=map.get("手机号码");
            String sql = "insert into person_info(姓名,学工号,性别,权限,班级号,电话) VALUES ( '"+name+"','"+stunum+"','"+sex+"',0,'"+classname+"','"+telnum+"')";
            stat.execute(sql);
        }
        discon();
        stat.close();
        System.out.println("导入学生名单完成");
    }
    //上传会议记录

    //读取excel
    public static Workbook readExcel(String filePath){
        Workbook wb = null;
        if(filePath==null){
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if(".xls".equals(extString)){
                return wb = new HSSFWorkbook(is);
            }else if(".xlsx".equals(extString)){
                return wb = new XSSFWorkbook(is);
            }else{
                return wb = null;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }

    //获取单元格的内容
    public static Object getCellFormatValue(Cell cell){
        Object cellValue = null;
        if(cell!=null){
            //判断cell类型
            switch(cell.getCellType()){
                case Cell.CELL_TYPE_NUMERIC:{
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
                }
                case Cell.CELL_TYPE_FORMULA:{
                    //判断cell是否为日期格式
                    if(DateUtil.isCellDateFormatted(cell)){
                        //转换为日期格式YYYY-mm-dd
                        cellValue = cell.getDateCellValue();
                    }else{
                        //数字
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING:{
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                }
                default:
                    cellValue = "";
            }
        }else{
            cellValue = "";
        }
        return cellValue;
    }
    //获取对应得人
//    public int getname(String name) {
//    	for(int i=0;i<len(person_list);i++) {
//    		String name=(Map)person_list[i].get("姓名");
//    	}
//    }
    //得到参会时长,返回分钟
    public static float gettime(String time) {
        float res=0;
        int h=Integer.parseInt(time.split(":")[0]);
        int m=Integer.parseInt(time.split(":")[1]);
        int s=Integer.parseInt(time.split(":")[2]);
        res=h*60+m+s/60;
        //System.out.println(res);
        return res;
//    	DecimalFormat df = new DecimalFormat("#.00");
//
//    	return Float.parseFloat(df.format(res));
    }
    //对比时间,,未指定时间
    public static void judge1() throws ParseException, SQLException {
        Date starttime;
        Date endtime;
        float total;
        for (Map<String,String> map : person_list) {
            if(map.get("最早开始时间").equals("")) {
                map.put("旷课", "1");
            }else {
                starttime=Str2Date(map.get("最早开始时间"));
                //System.out.println(starttime);
                starttime=new Date(starttime.getTime()-threshold*60000);
                //System.out.println(starttime);
                endtime=Str2Date(map.get("最晚结束时间"));
                endtime=new Date(endtime.getTime()+threshold*6*60000);
                total=Float.parseFloat(map.get("总参会时长"));
                //System.out.println(starttime);
                //参会时长小于一半，最晚结束时间早于开始时间，最早开始时间晚于结束时间
                if(total*2<total_t||starttime.after(end)||endtime.before(start)) {
                    map.put("旷课", "1");
                }
                else  {
                    //判断迟到和早退
                    if(starttime.after(start)) {
                        map.put("迟到", "1");
                    }
                    if(endtime.before(end)) {
                        map.put("早退", "1");
                    }
                }
            }
            System.out.println(map);
        }
        inside();
    }
    public static String int2str(String i){
        if(i.equals("0"))
            return"否";
        if(i.equals("1")){
            return "是";
        }
        else{
            return "";
        }
    }
    //指定时间
    public static void judge2(String st,String et) throws ParseException, SQLException {
        Date starttime;
        Date endtime;
        Date start=Str2Date(day_time+" "+st);
        Date end=Str2Date(day_time+" "+et);
        float total_t=(end.getTime()-start.getTime())/60000;
        float total;
        for (Map<String,String> map : person_list) {
            if(map.get("最早开始时间").equals("")) {
                map.put("旷课", "1");
            }else {
                starttime=Str2Date(map.get("最早开始时间"));
                starttime=new Date(starttime.getTime()-threshold*60000);
                endtime=Str2Date(map.get("最晚结束时间"));
                endtime=new Date(endtime.getTime()+threshold*60000);
                total=Float.parseFloat(map.get("总参会时长"));
                //System.out.println(starttime);
                //参会时长小于一半，最晚结束时间早于开始时间，最早开始时间晚于结束时间
                if(total*2<total_t||starttime.after(end)||endtime.before(start)) {
                    map.put("旷课", "1");
                }
                else  {
                    //判断迟到和早退
                    if(starttime.after(start)) {
                        map.put("迟到", "1");
                    }
                    if(endtime.before(end)) {
                        map.put("早退", "1");
                    }
                }
            }
            //System.out.println(map);
        }
       inside();
    }
    //将结果导入数据库
    public static void inside() throws SQLException {
        dbcon();
        Statement stat= null;
        stat = myConnection.createStatement();
        for (Map<String,String> map : person_list) {
            String name=map.get("姓名");
            String stunum=map.get("学工号");
            String kq=kqnum;
            String dar_time=day_time;
            String late=int2str(String.valueOf(map.get("迟到")));
            String leave=int2str(String.valueOf(map.get("早退")));
            String  absenteeism=int2str(String.valueOf(map.get("旷课")));
            String cla=map.get("班级号");
            String time=String.format("%.1f",Float.parseFloat(String.valueOf(map.get("总参会时长")))/60);
            //System.out.println(time);
            String sql = "insert into checking_info VALUES " +
                    "( '"+name+"','"+stunum+"','"+kq+"','"+late+"','"+leave+"','"+absenteeism+"','"+cla+"',"+time+",'"+day_time+"')";
            //System.out.println(sql);
            stat.execute(sql);
            sql="select * from checking_total where name='"+name+"' and stu_num='"+stunum+"'";
            ResultSet rs = stat.executeQuery(sql);
            Boolean here=rs.first();
            if(here==false){
                sql="insert into checking_total values('"+name+"',"+String.valueOf(map.get("迟到"))+","+String.valueOf(map.get("早退"))
                        +","+String.valueOf(map.get("旷课"))+",'"+stunum+"','"+cla+"')";
                stat.execute(sql);
            }
            else {
                //System.out.println(String.valueOf(map.get("迟到"))+"  "+String.valueOf(map.get("早退"))+" "+String.valueOf(map.get("旷课")));
                sql="update  checking_total set late=late+"+String.valueOf(map.get("迟到"))+" where name='"+name+"' and stu_num='"+stunum+"'";
                stat.execute(sql);
                sql="update  checking_total set leave_early=leave_early+"+String.valueOf(map.get("早退"))+" where name='"+name+"' and stu_num='"+stunum+"'";
                stat.execute(sql);
                sql="update  checking_total set noab=noab+"+String.valueOf(map.get("旷课"))+" where name='"+name+"' and stu_num='"+stunum+"'";
                stat.execute(sql);
            }
            //System.out.println(rs.first());
        }
        stat.close();
        discon();
        System.out.println("处理完成");
    }
    //导入最早开始时间，最晚结束时间和总时长
    public static void check() {
        Date intime=new Date();
        float time=0;
        Date outtime=new Date();
        String name;
        int num=0;
        for (Map<String,String> map : list) {
//	            for (Entry<String,String> entry : map.entrySet()) {    //Entry: 键值对 对象（例如：预定开始时间:2020-05-07 21:00:00 就是一个entry）
//	                System.out.print(entry.getKey()+":"+entry.getValue()+",");
//	            }
            SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
            try {
                intime=Str2Date(map.get("入会时间"));
                outtime=Str2Date(map.get("退会时间"));
                time=gettime(map.get("单次参会时长"));
                name=map.get("用户名称");
                //System.out.println(time);
                //System.out.println(intime.toString()+outtime.toString()+time+name);
                for (Map<String,String> map2 : person_list) {
                    //是否包含人员名字
                    String name2=map2.get("姓名");
                    if(name.indexOf(name2)!=-1) {
                        if(map2.get("最早开始时间").equals("")) {
                            map2.put("最早开始时间", map.get("入会时间"));
                            map2.put("最晚结束时间", map.get("退会时间"));
                            map2.put("总参会时长", String.valueOf(time));
                            //System.out.println(String.valueOf(time));
                            num++;
                        }
                        else {
                            String starttime=map.get("入会时间");
                            String endtime=map.get("退会时间");
                            if(Str2Date(map2.get("最早开始时间")).after(Str2Date(starttime))) {
                                map2.put("最早开始时间", map.get("入会时间"));
                            }
                            if(Str2Date(map2.get("最晚结束时间")).before(Str2Date(endtime))) {
                                map2.put("最晚结束时间", map.get("退会时间"));
                            }
                            float total_time=Float.parseFloat(map2.get("总参会时长"))+time;
                            map2.put("总参会时长", String.valueOf(total_time));
                        }
                    }
                }
            } catch (ParseException e) {
                // TODO 自动生成的 catch 块
                System.out.println(e);
            }
            //System.out.println(outtime);
        }
        //System.out.println(num);
    }
    //	public static void main(String[] args) throws SQLException {
//		input a=new input();
//		a.check();
//	    }
    public static void main(String[] args) throws ParseException, SQLException, InterruptedException {
//        File file=new File("E:\\QQfile\\考勤");
//
//        File[] files = file.listFiles();
//
//        if (files != null) {
//
//            for (File f : files) {
//                System.out.println(f.getPath());
//                input a=new input(f.getPath());
//                a.check();
//                a.judge1();
//                Thread.sleep(10*1000);
//            }
//
//        }
//       input a=new input("E:\\QQfile\\考勤(1)\\2020软工综合实习课程-第11-12次-2020.05.21(14-21点).xlsx");
//        a.check();
//        a.judge1();
//    	System.out.println(a.total_t);
//    	for (Map<String,String> map : a.person_list) {
//    		System.out.println(map
//    		);
//
//    	}
        //导入学生名单
//        input.in_stu("E:\\QQfile\\2020软件工程综合实习选课名单.xls");



    }
}
