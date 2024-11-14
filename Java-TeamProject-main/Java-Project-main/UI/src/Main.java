import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // LoginServer 시작
            LoginServer.startServer();
        } catch (IOException e) {
            System.out.println("서버 시작 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
