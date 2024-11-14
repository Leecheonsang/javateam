package AdminMenu;
import java.util.*;
import java.sql.*;

public class menu {
    public static void Adminmenu(Connection conn){
        Scanner scanner = new Scanner(System.in);
        try{
        while(true){
            System.out.println("=====관리자 메뉴=====");
            System.out.println("1. 도서 재고 관리\n2. 온라인 배송\n3. 문의 사항\n4. 로그아웃");
            System.out.print("메뉴 선택 >> ");
            int menuNum = scanner.nextInt();
            scanner.nextLine();
            switch(menuNum){
                case 1: BookManage.menu(conn);
                break;
                case 2: OnlineDelivery(conn);
                break;
                case 3: Message(conn);
                break;
                //case 4: Mainmenu(conn);
                //break;
            }
        }
    }
        catch(Exception e){}
}
    public static void OnlineDelivery(Connection conn){

    }
    public static void Message(Connection conn){

    }
    public static void login(Connection conn){
        Scanner scanner = new Scanner(System.in);
        System.out.print("관리자 ID >> ");
        String adminID = scanner.next();
        scanner.nextLine();
        if(adminID.equals("root")){
            System.out.print("관리자 비밀번호 >> ");
            String adminPW = scanner.next();
            scanner.nextLine();
            if(adminPW.equals("root")){
                System.out.println("로그인 되었습니다.");
                menu.Adminmenu(conn);
            }
            else {
                System.out.println("관리자 비밀번호가 잘못 입력되었습니다.");
            }
        }
        else { // 관리자 ID 틀림
            System.out.println("관리자 ID가 잘못 입력되었습니다.");
            //mainmenu(conn); 메인메뉴로 이동
        }
    }
}