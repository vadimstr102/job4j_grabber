package ru.job4j.grabber;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class GrabberHttpHandler implements HttpHandler {
    private Store store;

    public GrabberHttpHandler(Store store) {
        this.store = store;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        StringBuilder builder = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH);

        builder.append("<h1>").append("Вакансии: ").append("</h1>");
        builder.append("<table border=\"1\" cellpadding=\"10\">");
        builder.append("<tr>")
                .append("<th>").append("Название").append("</th>")
                .append("<th>").append("Дата создания").append("</th>")
                .append("<th>").append("Описание").append("</th>")
                .append("</tr>");

        for (Post post : store.getAll()) {
            builder.append("<tr>")
                    .append("<td valign=\"top\">")
                    .append(String.format("<a href=\"%s\">%s</a> ", post.getLink(), post.getName()))
                    .append("</td>")
                    .append("<td valign=\"top\" align=\"center\">")
                    .append(sdf.format(post.getCreated()))
                    .append("</td>")
                    .append("<td>")
                    .append(post.getText())
                    .append("</td>")
                    .append("</tr>");
        }

        builder.append("</table>");

        byte[] bytes = builder.toString().getBytes();
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);

        OutputStream out = exchange.getResponseBody();
        out.write(bytes);
        out.close();
    }
}
