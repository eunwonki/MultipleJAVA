import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    public void find_path(String courier) {
        ArrayList<Destination> list;
        ArrayList<Group> groups;

        //DB에서 모든 주소지 가져오기
        list = Database.takeout_all_destination(courier);
        //유효하지 않은 주소 걸러서(위도 경도 구해주는 연산 포함) 삭제 후 DB에 가장 높은 우선순위값 부여
        Processing.cutout_list_by_validate(list,courier);
        //같은 주소를 하나의 주소로 통합하는 과정(위도,경도가 같은 경우)
        Processing.cutout_list_by_address(list);

        //그루핑 과정을 통해 여러 그룹으로 나눔
        groups = Processing.grouping(list);
        //그룹들간의 우선 순위를 결정해준다.
        Processing.group_sequencing(groups);


    }
}
