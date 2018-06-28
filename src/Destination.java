/*
Destination : 하나의 목적지 정보 (실체 클래스)
 */
public class Destination implements Comparable<Destination>{
    public String address;  //주소
    public String xpos; //x좌표
    public String ypos; //y좌표
    public double dist; //출발지로부터의 거리
    public int[] aid = new int[10];  //같은 주소를 가진 aid 목록
    public int num = 0;   //aid의 개수
    public String starttime;  //시작시간
    public String endtime;    //도착시간

    public String doro_name = null;  //도로명 주소
    public String build_main = null;  //건물 주번호
    public String build_sub = null;   //건물 부번호

    @Override
    public int compareTo(Destination o) {
        return Double.compare(this.dist,o.dist);
    }
}
