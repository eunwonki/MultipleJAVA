/*
Database : 데이터베이스를 열고 데이터를 가져오는 연산 등 데이터베이스에
접근하는 비즈니스 로직들을 모두 정의(제어클래스)
 */

import java.sql.*;
import java.util.ArrayList;


public class Database {
    public Connection con;
    public Statement stmt;
    /*
   connectDB : DB에 연결
    */
    public void connectDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //MySQL 서버를 설정합니다.
            con = DriverManager.getConnection("jdbc:mysql://multipledestination.online:3306/doroname_data?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8", "super", "Abcd23@#");
            System.out.println("데이터 베이스 접속이 성공했습니다.");
            stmt = con.createStatement();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    /*
  closeDB : DB에 연결 해제
   */
    public void closeDB() {
        try {
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    takeout_all_destination : 택배 기사 이름으로 된 데이터베이스에서 모든 주소지 리스트를 가져옴
     */
    public static ArrayList<Destination> takeout_all_destination(String courier){
        ArrayList<Destination> list = new ArrayList<Destination>();
        return list;
    }

    /*
   cutoutlist_store : 유효하지 않은 주소로 판단된 것들을 DB에 적용
    */
    public static void cutoutlist_store(String courier,int[] aid){
    }
    /*
    adapt : 모든 사항을 DB에 적용
   */
    public static void adapt(String courier,ArrayList<Group> groups){
    }
}
