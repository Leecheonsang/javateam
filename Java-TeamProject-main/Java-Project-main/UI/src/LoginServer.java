//서버를 실행시키기 위한 자바 파일입니다.
//현재 로그인은 서버가 켜져있는동안 배열에 임시적으로 저장하고 따로 데이터베이스에 저장하지는 않습니다.
//이후에 브라우저를 킨뒤에 localhost:5050/signup을 치시면 회원가입창으로 접속이 가능합니다.(5050은 포트번호입니다.)
//서버 종료방법은 터미널에서 ctrl+c를 입력하시면 종료가 됩니다.
// LoginServer.java - 서버 실행 및 회원가입/로그인/대시보드 기능 담당
// LoginServer.java - 서버 실행 및 회원가입/로그인/대시보드 기능 담당

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class LoginServer {
    private static final UserManager userManager = new UserManager();
    private static final String BASE_PATH = "src/templates/";

    // 서버 시작 메서드
    public static void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 5051), 0);
        server.createContext("/signup", new SignupHandler());
        server.createContext("/login", new LoginHandler());
        server.createContext("/dashboard", new DashboardHandler());
        server.createContext("/style.css", new CssHandler());
        server.createContext("/dashboardcss.css", new DashboardCssHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("서버가 localhost에서 실행 중입니다.");
    }

    // CSS 파일 핸들러
    static class CssHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] css = Files.readAllBytes(Paths.get(BASE_PATH + "style.css"));
            exchange.getResponseHeaders().set("Content-Type", "text/css; charset=UTF-8");
            exchange.sendResponseHeaders(200, css.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(css);
            }
        }
    }
        // 대시보드 전용 CSS 파일 핸들러
        static class DashboardCssHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                byte[] css = Files.readAllBytes(Paths.get(BASE_PATH + "dashboardcss.css"));
                exchange.getResponseHeaders().set("Content-Type", "text/css; charset=UTF-8");
                exchange.sendResponseHeaders(200, css.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(css);
                }
            }
        }

    // 회원가입 핸들러 
    static class SignupHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // 기존 회원가입 로직 처리
            } else {
                // GET 요청일 때 HTML을 로드하여 반환
                sendResponse(exchange, loadHtml("signup.html"));
            }
        }
    }

    // 로그인 핸들러 (자동 로그인 이동 기능 추가)
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String formData = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseFormData(formData);

                String id = params.get("username");
                String password = params.get("password");

                if (userManager.authenticateUser(id, password)) {
                    exchange.getResponseHeaders().set("Location", "/dashboard");
                    exchange.sendResponseHeaders(302, -1);
                } else {
                    sendResponse(exchange, "<html><body><h2>로그인 실패: 잘못된 ID 또는 비밀번호입니다.</h2></body></html>");
                }
            } else {
                sendResponse(exchange, loadHtml("login.html"));
            }
        }
    }

    // 대시보드 핸들러 (자동 로그인 이동, 사용자 정보 표시, 검색 기능 추가)
    static class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (userManager.getLoggedInUser() == null) {
                // 로그인하지 않은 사용자는 로그인 페이지로 리디렉션
                exchange.getResponseHeaders().set("Location", "/login");
                exchange.sendResponseHeaders(302, -1);
                return;
            }

            String userInfo = "<h2>환영합니다, 사용자 정보:</h2><ul>";
            UserManager.User user = userManager.getLoggedInUser();
            if (user != null) {
                userInfo += "<li>ID: " + user.id + "</li>";
                userInfo += "<li>이름: " + user.name + "</li>";
                userInfo += "<li>주소: " + user.address + "</li>";
            }
            userInfo += "</ul>";

            // 검색기능을 위한 form 데이터로 수정
            String dashboardHtml = loadHtml("dashboard.html")
                    .replace("{{userInfo}}", userInfo)
                    .replace("{{searchResults}}", "");

            sendResponse(exchange, dashboardHtml);
        }
    }

    // 폼 데이터 파싱 메서드
    private static Map<String, String> parseFormData(String formData) {
        Map<String, String> params = new HashMap<>();
        for (String pair : formData.split("&")) {
            String[] keyValue = pair.split("=");
            if (keyValue.length > 1) {
                String key = keyValue[0];
                String value = java.net.URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    // 응답 전송 메서드
    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    // HTML 파일 로드 메서드
    private static String loadHtml(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(BASE_PATH + fileName)), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws IOException {
        startServer();
    }
}
