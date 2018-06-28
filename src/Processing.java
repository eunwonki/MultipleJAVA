/*
Database : 주소지를 처리하는 비즈니스 로직을 정의(제어클래스)
 */
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.sql.*;

public class Processing {

    public static void calculate_distance(ArrayList<Destination> list,String xpos_s,String ypos_s) {
        System.out.println("정렬중입니다.");

        double xpos = Double.valueOf(xpos_s);
        double ypos = Double.valueOf(ypos_s);

        for(int i=0;i<list.size();i++)
        {
            double cur_x = Double.valueOf(list.get(i).xpos);
            double cur_y = Double.valueOf(list.get(i).ypos);
            double dist =  Math.sqrt(Math.pow(cur_x - xpos, 2) + Math.pow(cur_y - ypos, 2));
            list.get(i).dist = dist;
        }

        Collections.sort(list);
    }

    /*
    cutout_list_by_validate : 주소지 리스트의 위도 경도를 구해주고 구할 수 없는 주소들은
    Database에 높은 우선순위값을 적용하고 고려 리스트에서 제외
     */
    public static void cutout_list_by_validate(ArrayList<Destination> list, String courier){
        int[] cutout_aid = new int[5];

        Database.cutoutlist_store(courier,cutout_aid); //DB에 적용
        boolean isHyphen = false;
        Database db = new Database();
        db.connectDB("doroname_data");
        String[] addr_token = new String[20];
        ResultSet rs = null;

        for (int j = 0; j < list.size(); j++) {
            int i = 0;
            String addr = new String(list.get(j).address);
            if (addr.contains("-")) {
                addr = addr.replace("-", " ");
                isHyphen = true;
            }
            StringTokenizer stringTokenizer = new StringTokenizer(addr, " ");
            while (stringTokenizer.hasMoreTokens()) {
                String str = stringTokenizer.nextToken();
                if (str.matches(".*동")) continue;
//                System.out.println(str);
                if (str.matches(".*로")) {
                    String temp_str = stringTokenizer.nextToken();
                    if (temp_str.matches(".*길")) {
                        str += temp_str;
                    } else {
                        addr_token[i++] = str;
                        addr_token[i++] = temp_str;
//                        System.out.println(i - 2 + " : " + addr_token[i - 2]);
//                        System.out.println(i - 1 + " : " + addr_token[i - 1]);
                        continue;
                    }
                }
                addr_token[i++] = str;
//                System.out.println(i - 1 + " : " + addr_token[i - 1]);
            }

            try {
                if (isHyphen) {
                    rs = db.searchPointByAddress(getCityName(addr_token[0]), addr_token[2], addr_token[3], addr_token[4]);
                } else {
                    rs = db.searchPointByAddress(getCityName(addr_token[0]), addr_token[2], addr_token[3]);
                }
                isHyphen = false;
                if(rs.next()) {
                    //System.out.println(addr + " : " + rs.getString("xPoint") + ", " + rs.getString("yPoint"));
                    list.get(j).xpos = rs.getString("xPoint");list.get(j).ypos = rs.getString("yPoint");
                }
                else {
                    System.out.println(j);
                    list.remove(j);j--;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        db.closeDB();
    }

    static String getCityName(String city) {
        final String[] admin_names = {"seoul", "incheon", "busan", "chungbuk", "chungnam", "daegu", "daejeon", "gangwon", "gwangju",
                "gyeongbuk", "gyeongnam", "gyunggi", "jeju", "jeonbuk", "jeonnam", "sejong", "ulsan"};

        if (city.contains("서울")) return "seoul";
        else if (city.contains("인천")) return "incheon";
        else if (city.contains("부산")) return "busan";
        else if (city.contains("충청북도")) return "chungbuk";
        else if (city.contains("충북")) return "chungbuk";
        else if (city.contains("충청남도")) return "chungnam";
        else if (city.contains("충남")) return "chungnam";
        else if (city.contains("대구")) return "daegu";
        else if (city.contains("대전")) return "daejeon";
        else if (city.contains("강원도")) return "gangwon";
        else if (city.contains("광주")) return "gwangju";
        else if (city.contains("경상북도")) return "gyeongbuk";
        else if (city.contains("경북")) return "gyeongbuk";
        else if (city.contains("경상남도")) return "gyeongnam";
        else if (city.contains("경남")) return "gyeongnam";
        else if (city.contains("경기")) return "gyunggi";
        else if (city.contains("제주")) return "jeju";
        else if (city.contains("전라북도")) return "jeonbuk";
        else if (city.contains("전북")) return "jeonbuk";
        else if (city.contains("전라남도")) return "jeonnam";
        else if (city.contains("전남")) return "jeonnam";
        else if (city.contains("세종")) return "sejong";
        else if (city.contains("울산")) return "ulsan";
        return city + "라는 도시는 없습니다.";
    }


    /*
   cutout_list_by_address : 주소지 리스트의 위도 경도를 비교하여 같은 주소는 하나로 통합하여 그 주소지의 aid 배열에 들어가게 만듬
    */
    public static void cutout_list_by_address(ArrayList<Destination> list) {

    }
    /*
  change_address : 구주소를 신주소로 바꿈
   */
    public static void change_address(ArrayList<Destination> list){

        Database db = new Database();
        db.connectDB("doroname_data");

        for(int i=0;i<list.size();i++) {
            String address = new String(list.get(i).address);

            StringTokenizer st = new StringTokenizer(address, " ");

            String si = new String("");    //시,도
            String gu = new String("");    //시,군,구
            String dong = new String("");  //읍,면,동
            String ri = new String("");
            String ji = new String("");
            String ji_main = new String(""); //지번 번지
            String ji_sub = new String(""); //지번 호
            String extra = new String("");
            int opt = 1;  //0일떄 도로명주소  1일때  지번주소 변화가 필요

            while (st.hasMoreTokens()) {
                String str = new String(st.nextToken());
                switch (str.charAt(str.length() - 1)) {
                    case '로': case '길':
                        if(list.get(i).doro_name == null)
                            list.get(i).doro_name = new String(str);
                        else
                            list.get(i).doro_name = list.get(i).doro_name + new String(str);
                        opt = 0;
                        break;
                    case '도':
                        si = str;
                        break;
                    case '시':
                        if (getCityName(str).equals(str+"라는 도시는 없습니다."))
                            gu = str;
                        else si = str;
                        break;
                    case '군': case '구':
                        gu = str;
                        break;
                    case '읍': case '면': case '동':
                        dong = str;
                        break;
                    case '리':
                        ri = str;
                        break;
                    default:
                        if ('0' <= str.charAt(str.length() - 1) && str.charAt(str.length() - 1) <= '9') {
                            ji = str;
                            StringTokenizer st2 = new StringTokenizer(ji, "-");
                            ji_main = new String(st2.nextToken());
                            if(str.contains("-")) ji_sub = new String(st2.nextToken());
                        } else extra = new String(str);

                        if (st.hasMoreTokens()) {
                            extra.concat(" ");
                            extra.concat(new String(st.nextToken("")));
                        }
                        break;
                }
            }
            if (opt == 1) {
                String table = new String(getCityName(si)) + "_build";

                String sql = "select name_doro, build_main, build_sub from " + table +
                        " where name_sigun='"+gu+"'"; //도,시 시,군,구는 필수요소
                if (!dong.equals("")) sql += " and name_eup='" + dong + "'";
                if (!ri.equals("")) sql += " and name_ri='" + ri + "'";
                if (!ji_main.equals("")) sql += " and ji_main='" + ji_main + "'";
                if (!ji_sub.equals("")) sql += " and ji_sub='" + ji_sub + "'";

                try {
                    ResultSet rs = db.stmt.executeQuery(sql);
                    if(rs.next()) {
                        list.get(i).doro_name = new String(rs.getString("name_doro"));
                        list.get(i).build_main = new String(rs.getString("build_main"));
                        list.get(i).build_sub = new String(rs.getString("build_sub"));

                        String new_address = new String("");
                        if (!si.equals("")) new_address += si;
                        new_address += " " + gu;
                        if (!dong.equals("")) new_address += " " + dong;
                        new_address += " " + list.get(i).doro_name;
                        if(!list.get(i).build_main.equals("0")) new_address += " " + list.get(i).build_main;
                        if(!list.get(i).build_sub.equals("0")) new_address += "-" + list.get(i).build_sub;
                        if (!extra.equals("")) new_address += " " + extra;
                        list.get(i).address = new String(new_address);
                    }
                    else{
                        System.out.println(address +" : " + sql);
                        list.remove(i);
                        i--;
                    }
                } catch (SQLException e) {
                    System.out.println("쿼리문이 실행되지 않음");
                }
            }
        }
        db.closeDB();
    }
    /*
   grouping : 주소지 리스트를 특정 기준을 통해 그룹으로 묶는다.
    */
    public static ArrayList<Group> grouping(ArrayList<Destination> list) {
        ArrayList<Group> groups = new ArrayList<Group>();

        return groups;
    }
    /*
  group sequencing : 그룹 간의 우선 순위를 정한다.
   */
    public static void group_sequencing(ArrayList<Group> groups) {
    }
    /*
  path sequencing : 그룹 내의 우선 순위를 정한다.
   */
    public static String path_sequencing(Group groups, String starttime) {
        String endtime = "";
        return endtime;
    }
}
