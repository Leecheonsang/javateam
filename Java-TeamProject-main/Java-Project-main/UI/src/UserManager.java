import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class UserManager {
    private final Map<String, User> userDatabase = new HashMap<>();
    private static final String SSN_PATTERN = "\\d{6}-\\d{7}";
    private User loggedInUser = null; // 현재 로그인한 사용자

    // 사용자 등록 메서드
    public void registerUser(String id, String password, String name, String ssn, String address) {
        if (Pattern.matches(SSN_PATTERN, ssn)) {
            User user = new User(id, password, name, ssn, address);
            saveUserToDatabase(user);
            System.out.println("회원가입이 완료되었습니다.");
        } else {
            System.out.println("<html><body><h2>잘못된 주민번호 형식입니다.</h2></body></html>");
        }
    }

    // 사용자 정보 데이터베이스에 저장
    private void saveUserToDatabase(User user) {
        userDatabase.put(user.id, user);
    }

    // 사용자 정보 데이터베이스에서 가져오기
    public User retrieveUserFromDatabase(String id) {
        return userDatabase.get(id);
    }

    // 사용자 인증 메서드
    public boolean authenticateUser(String id, String password) {
        User user = retrieveUserFromDatabase(id);
        if (user != null && user.password.equals(password)) {
            loggedInUser = user; // 로그인 성공 시 현재 사용자 저장
            return true;
        }
        return false;
    }

    // 현재 로그인한 사용자 반환
    public User getLoggedInUser() {
        return loggedInUser;
    }

    // 로그아웃 메서드
    public void logoutUser() {
        loggedInUser = null; // 로그아웃 시 현재 사용자 초기화
    }

    // 사용자 이름 중복 확인 메서드
    public boolean isUsernameTaken(String id) {
        return retrieveUserFromDatabase(id) != null;
    }

    // 사용자 정보 저장 클래스
    public static class User {
        String id;
        String password;
        String name;
        String ssn;
        String address;

        public User(String id, String password, String name, String ssn, String address) {
            this.id = id;
            this.password = password;
            this.name = name;
            this.ssn = ssn;
            this.address = address;
        }
    }
}
