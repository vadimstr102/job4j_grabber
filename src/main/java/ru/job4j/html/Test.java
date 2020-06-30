package ru.job4j.html;

public class Test {
    public static void main(String[] args) {
        int numPage = 5;
        for (int i = 1; i <= numPage; i++) {
            System.out.printf("https://www.sql.ru/forum/job-offers/%s\r\n", i);
        }
    }
}
