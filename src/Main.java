import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        /*if(args.length == 0) {
            System.out.println("usage : %s <courier id>");
            System.exit(1);
        }
        find_path(args[1]);*/
        find_path("wonjun");
    }

    public static void find_path(String courier) {

        ArrayList<Destination> list;
        ArrayList<Group> groups;
        String starttime = ""; //현재시간
        int i;

        //DB에서 모든 주소지 가져오기
        list = Database.takeout_all_destination(courier);
        for(i=0;i<list.size();i++) System.out.println(i+" : "+list.get(i).address);
        //구주소를 신주소로 교환  -> 신주소로 교환이 가능하지 않은 주소는 유효하지 않은 주소
        Processing.change_address(list);
        for(i=0;i<list.size();i++) System.out.println(i+" : "+list.get(i).address + " : "+list.get(i).doro_name);
        //유효하지 않은 주소 걸러서(좌표 구해주는 연산 포함) 삭제 후 DB에 가장 높은 우선순위값 부여
        //좌표를 구할 수 없는 주소는 유효하지 않은 주소
        Processing.cutout_list_by_validate(list,courier);
        for(i=0;i<list.size();i++) System.out.println(i+" : "+list.get(i).address + " : "+list.get(i).xpos+", "+list.get(i).ypos);
        System.exit(1);
        //같은 주소를 하나의 주소로 통합하는 과정 (같은 주소는 좌표가 같은 주소)
        Processing.cutout_list_by_address(list);

        //그루핑 과정을 통해 여러 그룹으로 나눔
        groups = Processing.grouping(list);
        //그룹들간의 우선 순위를 결정해준다.
        Processing.group_sequencing(groups);

        //그룹 내의 노드들 간의 우선순위를 정해준다.
        for(i=0; i<groups.size(); i++) {
            starttime = Processing.path_sequencing(groups.get(i),starttime);
        }

         //데이터베이스에 최종 적용
        Database.adapt(courier,groups);
    }
}
