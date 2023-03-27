package com.example.kaoqin;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBconnect {
    public static Connection myConnection;
    DBconnect()  {
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
    //RS2LIST
    public static List RS2List(ResultSet rs) throws SQLException {
        List list = new ArrayList();
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        while (rs.next()) {
            Map rowData = new HashMap();
            for (int i = 1; i <= columnCount; i++) {
               // rowData.put(md.getColumnName(i), rs.getObject(i));
                rowData.put(md.getColumnName(i), rs.getObject(i));
            }
            list.add(rowData);
        }
        return list;
    }
    //根据考勤号
    public static ResultSet select(String number) throws SQLException {
        Statement stat= null;
        stat = myConnection.createStatement();
        String sql = "select*from checking_info where "+"kq_num="+number;//查询语句
        ResultSet rs=stat.executeQuery(sql);//查询
        return  rs;
    }
    //提交考勤申请
    public static  Boolean insert(String kq_num,String name,String reason) throws SQLException {
        try {
            String sql;
            Statement stat = null;
            stat = myConnection.createStatement();
            sql="select * from checking_info where kq_num='"+kq_num+"' and name='"+name+"'";
            ResultSet rs=stat.executeQuery(sql);
            if(rs.first()){
            sql = "insert into allowance VALUES ('" + kq_num + "','" + name + "','" + reason + "'," + 0 + ")";
            stat.execute(sql);//查询
            return true;}
            else return false;
        }
        catch(Exception e){
            return false;
        }

    }
    //处理考勤申请
    public static Boolean handle(String handle,String kq_num,String name){
        try {
            Statement stat = null;
            stat = myConnection.createStatement();
            String sql="";
            if(handle.equals("true")) {
                sql = "update allowance set pass=1 where kq_num='"+kq_num+"' and name='"+name+"'";
                stat.execute(sql);//查询
                sql="update checking_info set late='否' where kq_num='"+kq_num+"' and name='"+name+"'";
                stat.execute(sql);
//                sql="update checking_info set leave='否' where kq_num='"+kq_num+"' and name='"+"'";
//                stat.execute(sql);
                sql="update checking_info set noab='否' where kq_num='"+kq_num+"' and name='"+name+"'";
                stat.execute(sql);
                System.out.println("sda");
            }
            else{
                sql = "update allowance set pass=2 where kq_num='"+kq_num+"' and name='"+name+"'";
                stat.execute(sql);//查询
            }


            return true;
        }
        catch(Exception e){
            //System.out.println("fsa");
            return false;
        }
    }
    //查询考勤申请
    public static ResultSet check_apply() throws SQLException {
        Statement stat= null;
        stat = myConnection.createStatement();
        String sql = "select*from allowance where pass=0";//查询语句
        ResultSet rs=stat.executeQuery(sql);//查询
        return  rs;
    }
    //查询考勤申请结果
    public  static int check_kq(String name,String kq_num) throws SQLException {
        Statement stat= null;
        stat = myConnection.createStatement();
        String sql = "select pass from allowance where name='"+name+"' and kq_num='"+kq_num+"'";//查询语句
        ResultSet rs=stat.executeQuery(sql);//查询
        List list = new ArrayList();
        list=RS2List(rs);
        System.out.println(list);
        if(null == list || list.size() ==0 ){
            return -1;
        }
        else {
//        System.out.println(list);
            Map a= (Map) list.get(0);
            System.out.println(a);
//            System.out.println(m.get("通过"));
            return 1;
           // return Integer.parseInt(m.get("通过").toString());
        }
    }
    public Map<String,String> check_total() throws SQLException {
        Statement stat= null;
        stat = myConnection.createStatement();
        Map<String,String> map = new HashMap<>();
        ResultSet rs;
        String sql="";
        String late_total = null;
        String leave_total = null;
        String noab_total = null;
        String kq_total = null;
        String people_total = null;
        //次数
        int count;
        sql="select sum(late) from checking_total";
        rs=stat.executeQuery(sql);
        if(rs.next()){
          late_total=rs.getString(1);

        }
        sql="select sum(leave_early) from checking_total";
        rs=stat.executeQuery(sql);
        if(rs.next()){
            leave_total=rs.getString(1);
        }
        sql="select sum(noab) from checking_total";
        rs=stat.executeQuery(sql);
        if(rs.next()){
            noab_total=rs.getString(1);
        }
        sql="SELECT COUNT(*) FROM checking_info";
        rs=stat.executeQuery(sql);
        if(rs.next()){
            kq_total=rs.getString(1);
        }
        sql="SELECT COUNT(*) FROM checking_total";
        rs=stat.executeQuery(sql);
        if(rs.next()){
            people_total=rs.getString(1);
        }
//        System.out.println(Integer.parseInt(kq_total));
//        System.out.println(Integer.parseInt(people_total));
        count=Integer.parseInt(kq_total)/Integer.parseInt(people_total);
        System.out.println(count);
        map.put("late_total",late_total);
        map.put("leave_total",leave_total);
        map.put("noab_total",noab_total);
        map.put("late_per",String.valueOf(Integer.parseInt(late_total)/count));
        map.put("leave_per",String.valueOf(Integer.parseInt(leave_total)/count));
        map.put("noab_per",String.valueOf(Integer.parseInt(noab_total)/count));
        System.out.println(map);
        return map;
    }
    public static void main(String args[]) throws SQLException {
//    DBconnect db=new DBconnect();
//    ResultSet rs=db.select("01");
//        List list = new ArrayList();
//        list=RS2List(rs);
//        Map m=(Map)list.get(0);
//        System.out.println(m.get("姓名"));
        DBconnect db=new DBconnect();
//        db.insert("512","yfah","122");
//        ;
//        db.check_total();
        db.check_kq("殷广豪","685819221");

    }


    public String login(String name,String pass) throws SQLException {
        String res="false";
        Statement stat= null;
        stat = myConnection.createStatement();
        String sql="select * from manage where username='"+name+"' and password='"+pass+"'";
        ResultSet rs=stat.executeQuery(sql);
        if(rs.first()){
            res="true";
        }
        return res;
    }
}

