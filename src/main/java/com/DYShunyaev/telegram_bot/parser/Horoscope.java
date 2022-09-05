package com.DYShunyaev.telegram_bot.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Horoscope {

    public static final StringBuilder RESULT = new StringBuilder();

    public static String getUrl(String zodiacU) {

        String zodiacSign = switch (zodiacU) {
            case "Овен" -> "aries";
            case "Телец" -> "taurus";
            case "Близнецы" -> "gemini";
            case "Рак" -> "cancer";
            case "Лев" -> "leo";
            case "Дева" -> "virgo";
            case "Весы" -> "libra";
            case "Сккорпион" -> "scorpio";
            case "Стрелец" -> "sagittarius";
            case "Козерог" -> "capricorn";
            case "Водолей" -> "aquarius";
            case "Рыбы" -> "pisces";
            default -> "capricorn";
        };

        return "https://horo.mail.ru/prediction/" + zodiacSign + "/today/";
    }

    public static Document getPage(String zodiacP) throws IOException {
        return Jsoup.parse(new URL(getUrl(zodiacP)), 3000);
    }

    public static String getHoroscope(String zodiacH) throws IOException {
        Document page = getPage(zodiacH);
        Element dataCode = page.select("span[class = link__text]").first();
        Element zodiacSign = page.select("h1[class = hdr__inner]").first();
        Element horoscopeText = page.select("div[class = article__text]").first();
        Elements headLines = horoscopeText.select("div[class = article__item article__item_alignment_left article__item_html]");
        RESULT.append(dataCode.text()).append("\n").append(zodiacSign.text()).append("\n").append("\n");
        for (Element strings: headLines) {
            String horoscope = strings.select("p").text();
            RESULT.append(horoscope).append("\n");
        }

        return RESULT.toString();
    }

    public static void clearSB() {
        RESULT.delete(0,10000);
    }
}

