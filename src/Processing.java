/*
Database : 주소지를 처리하는 비즈니스 로직을 정의(제어클래스)
 */
import java.util.ArrayList;

public class Processing {
    /*
    cutout_list_by_validate : 주소지 리스트의 위도 경도를 구해주고 구할 수 없는 주소들은
    Database에 높은 우선순위값을 적용하고 고려 리스트에서 제외
    Database에 높은 우선순위값을 적용하고 고려 리스트에서 제외
     */
    public static void cutout_list_by_validate(ArrayList<Destination> list,String courier){
        int[] cutout_aid = new int[5];

        Database.cutoutlist_store(courier,cutout_aid); //DB에 적용
    }

    /*
   cutout_list_by_validate : 주소지 리스트의 위도 경도를 비교하여 같은 주소는 하나로 통합하여 그 주소지의 aid 배열에 들어가게 만듬
    */
    public static void cutout_list_by_address(ArrayList<Destination> list) {

    }
    /*
  change_address : 구주소를 신주소로 바꿈
   */
    public static void change_address(ArrayList<Destination> list) {

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
