package AdminMenu;

import java.util.*;
import java.sql.*;

public class BookManage {
    public static void InsertBook(Connection conn) {
        Scanner scanner = new Scanner(System.in);
        try {
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            int bookid1 = FindBookID(conn); // 원하는 도서가 등록되어 있는지 확인
            int finalbookid = findfinalBookdID(conn); // 새로운 도서 추가 시 마지막 bookid 알려줌
            int menuNum = 0;
            if (bookid1 != 0) {
                System.out.println("추가하고자 하는 도서가 이미 존재합니다."); // 온라인 or 중고 선택 -> 온라인이면 재고만 변경 , 중고일 경우 가격, 재고 변경
                System.out.printf("bookid는 %d입니다\n========================================\n", bookid1);
                System.out.println("1: 온라인 재고\n2: 중고 재고");
                System.out.print("메뉴 선택 >> ");
                menuNum = scanner.nextInt();
                scanner.nextLine();
                switch (menuNum) {
                    case 1: // 기존 + 온라인 -> 재고 수 변경
                        String changeInv = "SELECT onlineinv from book where bookid = ?";
                        pstmt = conn.prepareStatement(changeInv);
                        pstmt.setInt(1, bookid1);
                        rs = pstmt.executeQuery();
                        if (rs.next()) {
                            int curronlineinv = rs.getInt(1);
                            System.out.printf("현재 재고량은 %d개입니다.\n", curronlineinv);
                            System.out.print("추가할 재고량을 입력하세요 >> ");
                            int plusNum = scanner.nextInt();
                            scanner.nextLine();
                            String plusInv = "UPDATE book SET onlineinv = ? + ? WHERE bookid = ?";
                            pstmt = conn.prepareStatement(plusInv);
                            pstmt.setInt(1, curronlineinv);
                            pstmt.setInt(2, plusNum);
                            pstmt.setInt(3, bookid1);
                            int result = pstmt.executeUpdate();
                            if(result > 0){
                                System.out.println("재고가 추가되었습니다.");
                                String resultSQL = "SELECT * FROM book WHERE bookid = ?";
                                pstmt = conn.prepareStatement(resultSQL);
                                pstmt.setInt(1,bookid1);
                                rs = pstmt.executeQuery();
                                if(rs.next()){
                                    System.out.printf("도서 이름 : %s, 출판사 : %s, 재고량 : %d\n", rs.getString("bookname"), rs.getString("publisher"), rs.getInt("onlineinv"));
                                }
                            }
                        }
                        break;
                    case 2: // 기존 + 중고 -> 판매자는 같은 책을 여러권 판매할 수 없음, 출판사와 책 이름이 같은 도서를 동일한 판매자가 판매 -> 실패
                        System.out.print("판매자 아이디를 입력해주세요 >> ");
                        String seller = scanner.nextLine();
                        String checkSQL = "SELECT * FROM book WHERE seller = ?";
                        pstmt = conn.prepareStatement(checkSQL);
                        pstmt.setString(1,seller);
                        rs = pstmt.executeQuery();
                        if(rs.next()){

                        }
                        break;
                }
            } else {
                System.out.println("처음 추가하는 도서입니다."); // 온라인 or 중고 -> 온라인이면 가격, 재고 변경 , 중고일 경우 가격, 재고 변경
                System.out.printf("마지막 bookid는 %d입니다\n========================================\n", finalbookid);
                System.out.println("1: 온라인 재고\n2: 중고 재고\n3. 종료");
                System.out.print("메뉴 선택 >> ");
                menuNum = scanner.nextInt();
                scanner.nextLine();

            }
            /*System.out.println("1: 온라인 재고\n2: 중고 재고\n3. 종료");
            System.out.print("메뉴 선택 >> ");
            int menuNum = scanner.nextInt();
            scanner.nextLine();
            switch (menuNum) {
                case 1: // 온라인 재고 추가
                    System.out.println("bookname, publisher, onlineprice, onlineinv 순서대로 입력");
                    String string = scanner.nextLine();
                    String[] parts = string.split(",");
                    if(parts.length != 4){
                        System.out.println("입력이 잘못되었습니다.");
                        return;
                    }
                    String newBook = "INSERT INTO book(bookname, publisher, onlineprice, onlineinv) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(newBook);
                    pstmt.setInt(1, Integer.parseInt(parts[0].trim()));
                    pstmt.setString(2, parts[1].trim());
                    pstmt.setString(3, parts[2].trim());
                    pstmt.setInt(4, Integer.parseInt(parts[3].trim()));
                    pstmt.setInt(5, Integer.parseInt(parts[4].trim()));
                    pstmt.executeUpdate();
                    break;
                case 2: // 중고 재고 추가
                    String oldBook = "";
                    break;
                case 3:
                    break;
            }*/
        }
        catch (Exception e) {
        }
    }

    public static void DeleteBook(Connection conn) {
        Scanner scanner = new Scanner(System.in);
    }

    public static int FindBookID(Connection conn) {
        Scanner scanner = new Scanner(System.in);
        int bookid = 0;
        try {
            System.out.print("책 이름 >> ");
            String bookname = scanner.nextLine();
            System.out.print("출판사 >> ");
            String publisher = scanner.nextLine();
            String SQL = "SELECT bookid FROM book WHERE bookname = ? AND publisher = ?";
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, bookname);
            pstmt.setString(2, publisher);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                bookid = rs.getInt("bookid");
            }
        } catch (Exception e) {
        }
        return bookid;
    }

    public static int findfinalBookdID(Connection conn) {
        int finalbookid = 0;
        try {
            String SQL = "SELECT MAX(bookid) FROM book";
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                finalbookid = rs.getInt(1);
            }
        } catch (Exception e) {
        }
        return finalbookid;
    }

    public static void menu(Connection conn) {
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                System.out.println("====================재고 관리====================");
                System.out.println("1. 재고 추가\n2. 재고 삭제\n3. 메뉴로 이동");
                System.out.print("메뉴 선택 >> ");
                int menuNum = scanner.nextInt();
                scanner.nextLine();  // 숫자 뒤의 Enter를 처리하기 위해 사용
                switch (menuNum) {
                    case 1:
                        BookManage.InsertBook(conn); // 재고 추가
                        break;
                    case 2:
                        BookManage.DeleteBook(conn); // 재고 삭제
                        break;
                    case 3:
                        AdminMenu.menu.Adminmenu(conn);
                        break;
                    default:
                        System.out.println("잘못된 선택입니다. 다시 시도하세요.");
                        break;
                }
            }
        } catch (Exception e) {
        }
    }
}