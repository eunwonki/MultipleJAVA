/*
Destination : 하나의 목적지 정보 (실체 클래스)
 */
public class Destination {
    private String address;  //주소
    private float latitude; //위도
    private float longtitude; //경도
    private int[] aid;  //같은 주소를 가진 aid 목록
    private int num;   //aid의 개수
    private String starttime;  //시작시간
    private String endtime;    //도착시간
}
