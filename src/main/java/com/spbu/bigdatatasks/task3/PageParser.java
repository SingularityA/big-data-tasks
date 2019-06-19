package com.spbu.bigdatatasks.task3;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageParser {

    public void parse(String url, String to) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(to))) {
            Document mainPage = Jsoup.connect(url).get();
            System.out.println("Connected to " + mainPage.title());

            for (Element region: mainPage.select("a[style]")) {
                String regionName = region.text();
                Document regionPage = Jsoup.connect(region.attr("href")).get();
                System.out.println("Connected to " + regionName);

                for (Element tik: regionPage.select("a[style]")) {
                    String tikName = tik.text();
                    Document tikPage = Jsoup.connect(tik.attr("href")).get();
                    System.out.println("Connected to " + tikName);

                    String uikHref = tikPage.select("a[href]:contains(сайт избирательной комиссии)").attr("href");

                    Document uikPage = Jsoup.connect(uikHref).get();
                    System.out.println("Parsing the table for " + tikName);

                    for (StringBuilder line: parseTable(uikPage)) {
                        writer.write(regionName + ",");
                        writer.write(tikName + ",");
                        writer.write(line.toString() + "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<StringBuilder> parseTable(Document tablePage) {
        Elements trs = tablePage.select("tbody").last().select("tr");
        trs.remove(13); // break row always

        int rows = trs.size();
        int cols = trs.first().select("td nobr").size();

        List<StringBuilder> lines = new ArrayList<>(cols);
        for (int i = 0; i < cols; i++)
            lines.add(new StringBuilder());

        List<Element> tds;
        for (int i = 0; i < rows - 1; i++) {
            tds = trs.get(i).select("td nobr");
            for (int j = 0; j < cols; j++)
                lines.get(j).append(tds.get(j).text()).append(",");
        }
        tds = trs.last().select("td nobr");
        for (int j = 0; j < cols; j++)
            lines.get(j).append(tds.get(j).text());

        return lines;
    }
}
