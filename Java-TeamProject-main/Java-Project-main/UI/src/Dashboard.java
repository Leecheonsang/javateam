import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Dashboard {
    private static final String BASE_PATH = "C:/Users/dlcjs/OneDrive/바탕 화면/Java-TeamProject-main/Java-TeamProject-main/Java-Project-main/UI/src/templates/";
    private static final UserManager userManager = new UserManager();

    public static class DashboardHandler implements HttpHandler {
        private final UserManager.User loggedInUser;

        public DashboardHandler(UserManager.User loggedInUser) {
            this.loggedInUser = loggedInUser;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (loggedInUser != null) {
                String dashboardHtml = loadHtml("dashboard.html")
                        .replace("{{username}}", loggedInUser.id)
                        .replace("{{name}}", loggedInUser.name)
                        .replace("{{address}}", loggedInUser.address);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, dashboardHtml.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(dashboardHtml.getBytes(StandardCharsets.UTF_8));
                }
            } else {
                // 로그인 안된 상태에서 대시보드 접근 시 login.html로 리다이렉트
                exchange.getResponseHeaders().set("Location", "/login");
                exchange.sendResponseHeaders(302, -1);
            }
        }

        // HTML 파일 로드 메서드
        private String loadHtml(String fileName) throws IOException {
            return new String(Files.readAllBytes(Paths.get(BASE_PATH + fileName)), StandardCharsets.UTF_8);
        }
    }

    // CSS 파일 핸들러
    public static class CssHandler implements HttpHandler {
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
}

