/*
Database : 데이터베이스를 열고 데이터를 가져오는 연산 등 데이터베이스에
접근하는 비즈니스 로직들을 모두 정의(제어클래스)
 */

import java.util.ArrayList;


public class Database {
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
