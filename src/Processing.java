/*
Database : 주소지를 처리하는 비즈니스 로직을 정의(제어클래스)
 */
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.sql.*;

public class Processing {

    /*
    cutout_list_by_validate : 주소지 리스트의 위도 경도를 구해주고 구할 수 없는 주소들은
    Database에 높은 우선순위값을 적용하고 고려 리스트에서 제외
     */
    public static void cutout_list_by_validate(ArrayList<Destination> list, String courier){
        int[] cutout_aid = new int[5];

        Database.cutoutlist_store(courier,cutout_aid); //DB에 적용
        boolean isHyphen = false;
        Database db = new Database();
        db.connectDB();
        String[] addr_token = new String[20];
        ResultSet rs = null;
        String[] ex_addr = {"서울특별시 종로구 자하문로 26길 1-3 청운빌라 102동 202호",
                "인천광역시 연수구 송도동 해돋이로 120번길 16 풍림아이원 205동 1301호",
                "서울특별시 동작구 상도로61길 64 모닝캐슬 103호",
                "인천광역시 연수구 해돋이로 248 연송초등학교 교장실"};

        for (String addr : ex_addr) {
            int i = 0;
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
                rs.next();
                System.out.println(addr + " : " + rs.getString("xPoint") + ", " + rs.getString("yPoint"));
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        db.closeDB();
        try {
            rs.close();
        }catch (SQLException e) {
            System.out.println(e);
        }
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
   cutout_list_by_validate : 주소지 리스트의 위도 경도를 비교하여 같은 주소는 하나로 통합하여 그 주소지의 aid 배열에 들어가게 만듬
    */
    public static void cutout_list_by_address(ArrayList<Destination> list) {

    }
    /*
  change_address : 구주소를 신주소로 바꿈
   */
    public static void change_address(ArrayList<Destination> list){

        Database db = new Database();
        db.connectDB();
        String address = "서울특별시 동작구 상도동 506-5 우천빌라 302호";
       // String address = "경기도 안성시 일죽면 송천리 반석아파트 104동 1005호";
        //String address = "경기도 안성시 서동대로 7416-10";
        StringTokenizer st = new StringTokenizer(address," ");

        String si = new String("");    //시,도
        String gu = new String("");    //시,군,구
        String dong = new String("");  //읍,면,동
        String ri = new String("");
        String ji = new String("");
        String ji_main = new String(""); //지번 번지
        String ji_sub = new String(""); //지번 호
        String extra = new String("");
        int opt = 0;  //1일떄 도로명주소  //2일때  지번주소 변화가 필요

        while(opt == 0)
        {
            String str = new String(st.nextToken());
            switch(str.charAt(str.length()-1))
            {
                case '길' : case '로':
                    opt = 1;
                    break;
                case '도':
                    si = str;
                    break;
                case '시':
                    if(str.length()<3) gu = str;
                    else if((str.charAt(str.length()-3) == '특' && str.charAt(str.length()-2) == '별') ||
                            (str.charAt(str.length()-3) == '광' && str.charAt(str.length()-2) == '역'))
                        si = str;
                    else gu = str;
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
                    if('0' <= str.charAt(str.length()-1) && str.charAt(str.length()-1) <= '9')
                    {
                        ji = str;
                        StringTokenizer st2 = new StringTokenizer(ji, "-");
                        ji_main = new String(st2.nextToken());
                        ji_sub = new String(st2.nextToken());
                    }
                    else extra = str;

                    if(st.hasMoreTokens()) {
                        extra = extra.concat(new String(st.nextToken("")));
                    }
                    opt = 2;
                    break;
            }
        }
        if(opt != 1)
        {
            String doro = "";
            String build_main = "";
            String build_sub = "";

            String sql = "select name_doro, build_main, build_sub from seoul_build where";
            sql += " name_sigun='" + gu+"'";  //시군구는 필수요소
            if(!si.equals("")) sql += " and name_sido='" + si+"'";
            if(!dong.equals("")) sql += " and name_eup='" + dong+"'";
            if(!ri.equals("")) sql += " and name_ri='" +ri+"'";
            if(!ji_main.equals("")) sql+= " and ji_main='" +ji_main+"'";
            if(!ji_sub.equals("")) sql+= " and ji_sub='" +ji_sub+"'";
            //System.out.println(sql);
            try {
                ResultSet rs = db.stmt.executeQuery(sql);
                rs.next();
                doro = rs.getString("name_doro");
                build_main = rs.getString("build_main");
                build_sub = rs.getString("build_sub");

                System.out.println("지번주소 : "+address);
                String new_address = new String("");
                if(!si.equals("")) new_address += si;
                new_address += " " + gu;
                if(!dong.equals("")) new_address += " "+dong;
                new_address += " " + doro;
                if(!build_main.equals("0")) new_address += " " + build_main;
                if(!build_sub.equals("0")) new_address += " " + build_sub;
                if(!extra.equals("")) new_address += " " + extra;
                System.out.println("도로명주소 : "+new_address);
            }
            catch (SQLException e){
                System.out.println("쿼리문이 실행되지 않음");
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
