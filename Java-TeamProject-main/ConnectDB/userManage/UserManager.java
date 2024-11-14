package userManage;

import java.util.*;
import java.sql.*;
import java.util.regex.Pattern;

public class UserManager {
    // 사용자 정보를 저장할 해시맵, 아이디를 키로, 유저 객체를 값으로 저장
    private static HashMap<String, User> userDatabase = new HashMap<>();
    // 사용자 입력을 받기 위한 스캐너 객체
    private static Scanner scanner = new Scanner(System.in);
    // 주민번호 형식 검사용 정규식 패턴
    private static final String SSN_PATTERN = "\\d{6}-\\d{7}";

    // 유저 객체
    static class User {
        String id;
        String password;
        String name;
        String ssn;
        String address;
        // 유저 객체 생성자 아이디, 비밀번호, 이름, 주민번호 설정
        public User(String id, String password, String name, String ssn, String address) {
            this.id = id;
            this.password = password;
            this.name = name;
            this.ssn = ssn;
            this.address = address;
        }
    }

    // 회원가입 메서드
    public static void register(Connection conn) {
        try {
            String ssn;
            String id;
            // 유효한 주민번호를 받을 때까지 반복
            while (true) {
                System.out.print("주민번호 입력 (XXXXXX-XXXXXXX 형식): ");
                ssn = scanner.nextLine();

                // 주민번호 형식 검사
                if (Pattern.matches(SSN_PATTERN, ssn)) {
                    String CheckRRN = "SELECT userid FROM user WHERE RRN = ?";
                    PreparedStatement pstmt = conn.prepareStatement(CheckRRN);
                    pstmt.setString(1, ssn);
                    ResultSet rs = pstmt.executeQuery();
                    if(rs.next()){
                        String findID = rs.getString("userid");
                        if(findID != null){
                            System.out.println("이미 같은 명의로 회원가입이 되어있습니다.");
                            System.out.printf("회원 아이디는 %s 입니다.\n", findID);
                            mainMenu(conn);
                        }
                    }
                    break; // 형식이 올바르면 반복 종료
                } else {
                    System.out.println("올바른 형식의 주민번호를 입력해 주세요.");
                }
            }

            // 유효한 ID를 받을 때까지 반복
            while (true) {
                System.out.print("ID 입력: ");
                id = scanner.nextLine();
                String CheckID = "SELECT COUNT(*) FROM user WHERE userid = ?";
                PreparedStatement pstmt = conn.prepareStatement(CheckID);
                pstmt.setString(1, id);
                ResultSet rs = pstmt.executeQuery();
                // ID 중복 확인
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        System.out.println("이미 존재하는 ID입니다. 다른 ID를 사용하세요.");
                    }
                    else break;
                }
            }
            System.out.print("비밀번호 입력: ");
            String password = scanner.nextLine();
            System.out.print("이름 입력: ");
            String name = scanner.nextLine();
            System.out.print("주소 입력 : ");
            String address = scanner.nextLine();
            // 새로운 유저 객체 생성 후 데이터베이스에 추가
            String Register = "INSERT INTO user(userid, passwd, username, RRN, address) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(Register);
            pstmt.setString(1, id);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setString(4, ssn);
            pstmt.setString(5, address);
            pstmt.executeUpdate();
            System.out.println("회원가입이 완료되었습니다.");
        }
        catch (Exception e){System.out.println(e);}
    }
    // 로그인 메서드
    public static void login(Connection conn) {
        try {
            String CheckID = "SELECT COUNT(*) FROM user WHERE userid = ?";
            PreparedStatement pstmt = conn.prepareStatement(CheckID);
            System.out.print("아이디 입력: ");
            String id = scanner.nextLine();
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            // 아이디가 데이터베이스에 있는지 확인
            if (rs.next()){
                if(rs.getInt(1) != 0) { // ID 존재 시
                    System.out.print("비밀번호 입력: ");
                    String password = scanner.nextLine();
                    String CheckPW = "SELECT passwd FROM user WHERE userid = ?";
                    pstmt = conn.prepareStatement(CheckPW);
                    pstmt.setString(1, id);
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        String resultPW = rs.getString("passwd");
                        if (resultPW.equals(password)) {
                            System.out.println("로그인 성공! 사용자 메뉴로 이동합니다.");
                            userMenu();
                        } else {
                            System.out.println("비밀번호가 틀립니다. 메인 메뉴로 이동합니다.");
                            return;
                        }
                    }
                }
                else{ //ID 없음
                    System.out.println("존재하지 않는 아이디입니다. 메인 메뉴로 이동합니다.");
                    return;
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("입력 오류가 발생했습니다. 다시 시도해주세요.");
        } catch (NullPointerException e) {
            System.out.println("사용자 정보를 찾을 수 없습니다.");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // 회원탈퇴 메서드
    public static void deleteAccount(Connection conn) {
        try {
            System.out.print("아이디 입력: ");
            String id = scanner.nextLine();
            String CheckID = "SELECT COUNT(*) FROM user WHERE userid = ?";
            PreparedStatement pstmt = conn.prepareStatement(CheckID);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            // 아이디가 데이터베이스에 있는지 확인
            if (rs.next()) { // 아이디 없을 때
                int count = rs.getInt(1);
                if (count == 0) {
                    System.out.println("존재하지 않는 아이디입니다. 메인 메뉴로 이동합니다.");
                    return;
                }
            }
            System.out.print("비밀번호 입력: ");
            String password = scanner.nextLine();
            String CheckPW = "SELECT passwd FROM user WHERE userid = ?";
            pstmt = conn.prepareStatement(CheckPW);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            // 비밀번호 일치 여부 확인 후 삭제
            if(rs.next() && rs.getString(1) .equals(password)) {
                String findPW = rs.getString(1);
                System.out.print("회원탈퇴 하시겠습니까? [Y] or [N] >> ");
                String answer = scanner.nextLine();
                if (answer.equals("Y") || answer.equals("y")) {
                    String DeleteSQL = "DELETE FROM user WHERE userid = ?";
                    pstmt = conn.prepareStatement(DeleteSQL);
                    pstmt.setString(1, id);
                    pstmt.executeUpdate();
                    System.out.println("회원탈퇴가 완료되었습니다.");
                    mainMenu(conn);
                } else {
                    System.out.println("회원탈퇴가 취소되었습니다.");
                    mainMenu(conn);
                }
            }
            else {
                System.out.println("비밀번호가 틀립니다. 메인 메뉴로 이동합니다.");
                mainMenu(conn);
            }
        } catch (Exception e) {
           System.out.println(e);
        }
    }

    // 프로그램 종료 메서드
    public static void exit(Connection conn) {
        System.out.println("프로그램을 종료합니다.");
        scanner.close();
        System.exit(0);
    }

    // 메인 메뉴
    public static void mainMenu(Connection conn) {
        while (true) {
            System.out.println("\n===== 메인 메뉴 =====");
            System.out.println("1. 회원가입");
            System.out.println("2. 로그인");
            System.out.println("3. 회원탈퇴");
            System.out.println("4. 종료");
            System.out.print("선택: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        register(conn);
                        break;
                    case 2:
                        login(conn);
                        break;
                    case 3:
                        deleteAccount(conn);
                        break;
                    case 4:
                        exit(conn);
                        break;
                    default:
                        System.out.println("올바른 번호를 입력해주세요.");
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해 주세요.");
            } catch (InputMismatchException e) {
                System.out.println("올바른 입력 형식을 사용해 주세요.");
                scanner.next(); // 버퍼 정리
            } catch (Exception e) {
                System.out.println("예기치 않은 오류가 발생했습니다.");
            }
        }
    }

    // 사용자 메뉴 (추가 기능 필요 시 구현)
    public static void userMenu() {
        // Placeholder for user-specific menu options
        System.out.println("사용자 메뉴가 추가될 수 있습니다.");
    }
}
