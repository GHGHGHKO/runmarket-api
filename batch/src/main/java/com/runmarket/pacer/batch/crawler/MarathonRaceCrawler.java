package com.runmarket.pacer.batch.crawler;

import com.runmarket.pacer.domain.port.in.race.SaveRaceCommand;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class MarathonRaceCrawler {

    private static final String LIST_URL = "/list.php";
    private static final String DETAIL_URL = "/view.php";
    private static final Charset EUC_KR = Charset.forName("EUC-KR");
    private static final Pattern RACE_NO_PATTERN = Pattern.compile("open_window\\([^)]*'view\\.php\\?no=(\\d+)'");
    private static final Pattern KOREAN_DATE_PATTERN = Pattern.compile("(\\d{4})년\\s*(\\d{1,2})월\\s*(\\d{1,2})일");
    private static final Pattern NUMERIC_DATE_PATTERN = Pattern.compile("(\\d{4})[.\\-/](\\d{1,2})[.\\-/](\\d{1,2})");
    private static final Pattern TIME_PATTERN = Pattern.compile("(오전|오후)\\s*(\\d{1,2})시\\s*(?:(\\d{2})분)?");
    private static final Pattern HM_PATTERN = Pattern.compile("(\\d{2}):(\\d{2})");
    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+");

    @Value("${marathon-race.timeout-ms:15000}")
    private int timeoutMs;

    @Value("${marathon-race.url}")
    private String url;

    public List<Integer> fetchRaceNos() {
        LinkedHashSet<Integer> all = new LinkedHashSet<>();
        int currentYear = Year.now().getValue();

        for (int year : new int[]{currentYear, currentYear + 1}) {
            try {
                Connection.Response response = Jsoup.connect(url + LIST_URL)
                        .method(Connection.Method.POST)
                        .data("syear_key", String.valueOf(year))
                        .data("course1_key", "").data("syoil_key", "")
                        .data("take_key", "").data("area_key", "")
                        .data("smonth_key", "").data("search_f", "").data("search_k", "")
                        .timeout(timeoutMs)
                        .ignoreContentType(true)
                        .execute();

                String html = new String(response.bodyAsBytes(), EUC_KR);
                Matcher m = RACE_NO_PATTERN.matcher(html);
                int before = all.size();
                while (m.find()) all.add(Integer.parseInt(m.group(1)));
                log.info("{}년: {}개 수집 (누적 {}개)", year, all.size() - before, all.size());
            } catch (IOException e) {
                log.error("{}년 목록 조회 실패: {}", year, e.getMessage());
            }
        }

        log.info("총 {}개 대회 번호 수집 완료", all.size());
        return new ArrayList<>(all);
    }

    public Optional<SaveRaceCommand> fetchRaceDetail(int no) {
        try {
            Connection.Response response = Jsoup.connect(url + DETAIL_URL)
                    .data("no", String.valueOf(no))
                    .timeout(timeoutMs)
                    .ignoreContentType(true)
                    .execute();

            if (response.statusCode() != 200) {
                log.warn("no={} 상세 페이지 응답 오류: {}", no, response.statusCode());
                return Optional.empty();
            }

            Document doc = Jsoup.parse(new String(response.bodyAsBytes(), EUC_KR));
            Map<String, String> lm = buildLabelMap(doc);

            String name = lm.getOrDefault("대회명", "").strip();
            String rawDatetime = lm.getOrDefault("대회일시", "");
            LocalDate date = parseKoreanDate(rawDatetime);

            if (name.isEmpty() || date == null) {
                log.warn("no={} 필수 필드 누락 (name={}, date={}), 건너뜁니다", no, name, date);
                return Optional.empty();
            }

            String[] regDates = parseRegDates(lm.getOrDefault("접수기간", ""));

            return Optional.of(new SaveRaceCommand(
                    no,
                    name,
                    parseCourses(lm.getOrDefault("대회종목", "")),
                    date,
                    parseStartTime(rawDatetime),
                    nullIfBlank(lm.get("대회장소")),
                    null,
                    nullIfBlank(lm.get("대회지역")),
                    nullIfBlank(lm.get("주최단체")),
                    nullIfBlank(lm.get("대표자명")),
                    nullIfBlank(lm.get("전화번호")),
                    nullIfBlank(lm.getOrDefault("E-mail", lm.get("이메일"))),
                    regDates[0] != null ? LocalDate.parse(regDates[0]) : null,
                    regDates[1] != null ? LocalDate.parse(regDates[1]) : null,
                    extractHomepageUrl(doc, lm.getOrDefault("홈페이지", "")),
                    null,
                    null,
                    nullIfBlank(lm.get("기타사항"))
            ));
        } catch (IOException e) {
            log.error("no={} 상세 페이지 조회 실패: {}", no, e.getMessage());
            return Optional.empty();
        }
    }

    private Map<String, String> buildLabelMap(Document doc) {
        Map<String, String> map = new java.util.LinkedHashMap<>();
        for (Element tr : doc.select("tr")) {
            Elements tds = tr.select("td");
            for (int i = 0; i + 1 < tds.size(); i += 2) {
                String label = tds.get(i).text().strip();
                String value = tds.get(i + 1).text().strip();
                if (!label.isEmpty()) map.put(label, value);
            }
            if (tds.size() == 2) {
                String label = tds.get(0).text().strip();
                if (!label.isEmpty()) map.putIfAbsent(label, tds.get(1).text().strip());
            }
        }
        return map;
    }

    private LocalDate parseKoreanDate(String text) {
        Matcher m = KOREAN_DATE_PATTERN.matcher(text);
        if (m.find()) {
            return LocalDate.of(
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2)),
                    Integer.parseInt(m.group(3)));
        }
        m = NUMERIC_DATE_PATTERN.matcher(text);
        if (m.find()) {
            return LocalDate.of(
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2)),
                    Integer.parseInt(m.group(3)));
        }
        return null;
    }

    private String parseStartTime(String text) {
        Matcher m = TIME_PATTERN.matcher(text);
        if (m.find()) {
            String meridiem = m.group(1);
            int hour = Integer.parseInt(m.group(2));
            int minute = m.group(3) != null ? Integer.parseInt(m.group(3)) : 0;
            if ("오후".equals(meridiem) && hour != 12) hour += 12;
            else if ("오전".equals(meridiem) && hour == 12) hour = 0;
            return String.format("%02d:%02d", hour, minute);
        }
        m = HM_PATTERN.matcher(text);
        return m.find() ? m.group(0) : null;
    }

    private String[] parseRegDates(String text) {
        List<String> dates = new ArrayList<>();
        Matcher km = KOREAN_DATE_PATTERN.matcher(text);
        while (km.find()) {
            dates.add(String.format("%s-%02d-%02d",
                    km.group(1),
                    Integer.parseInt(km.group(2)),
                    Integer.parseInt(km.group(3))));
        }
        if (dates.size() < 2) {
            Matcher nm = NUMERIC_DATE_PATTERN.matcher(text);
            while (nm.find() && dates.size() < 2) {
                dates.add(String.format("%s-%02d-%02d",
                        nm.group(1),
                        Integer.parseInt(nm.group(2)),
                        Integer.parseInt(nm.group(3))));
            }
        }
        return new String[]{
                !dates.isEmpty() ? dates.get(0) : null,
                dates.size() > 1 ? dates.get(1) : null
        };
    }

    private List<String> parseCourses(String text) {
        if (text == null || text.isBlank()) return List.of();
        return List.of(text.split("[,/·|]")).stream()
                .map(String::strip)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private String extractHomepageUrl(Document doc, String rawValue) {
        for (Element tr : doc.select("tr")) {
            Elements tds = tr.select("td");
            for (int i = 0; i < tds.size() - 1; i++) {
                if (tds.get(i).text().contains("홈페이지")) {
                    Element valueTd = tds.get(i + 1);
                    Element a = valueTd.selectFirst("a[href]");
                    if (a != null) return a.attr("href");
                    Matcher m = URL_PATTERN.matcher(valueTd.text());
                    if (m.find()) return m.group(0);
                }
            }
        }
        Matcher m = URL_PATTERN.matcher(rawValue);
        return m.find() ? m.group(0) : nullIfBlank(rawValue);
    }

    private String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
