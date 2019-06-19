package com.spbu.bigdatatasks.task3;

import org.junit.Ignore;
import org.junit.Test;

public class ElectionStatsTests {

    private final String url = "http://www.vybory.izbirkom.ru/region/region/izbirkom" +
            "?action=show&root=1&tvd=100100084849066&vrn=100100084849062&region=0&global=1&sub_region=0&prver=0" +
            "&pronetvd=null&vibid=100100084849066&type=227";
    private final String path = System.getProperty("user.dir")+ "\\src\\main\\resources\\task3\\table.cvs";
    private final ElectionStatsCounter counter = new SparkElectionStatsCounter(path);

    /* takes 20 minutes to complete */
    @Ignore
    @Test
    public void parseSites() {
        final PageParser pageParser = new PageParser();
        pageParser.parse(url, path);
    }

    @Test
    public void task1() {
        counter.countVoterTurnout();
        /*
                       Регион|electors| blanks|       percentage|
        +--------------------+--------+-------+-----------------+
        |Чеченская Республика|  547208| 525339| 96.0035306501367|
        |     Республика Тыва|  175102| 162780|92.96295873262441|
        |Кабардино-Балкарс...|  528431| 483932|91.57903302417913|
        ...
        */
    }

    @Test
    public void task2() {
        counter.countFavouriteCandidate();
        /*
         Число избирательных бюллетеней, полученных участковой избирательной комиссией|      УИК|Путин Владимир Владимирович|
        +-----------------------------------------------------------------------------+---------+---------------------------+
        |                                                                         3710| УИК №902|                       3397|
        ...
        */
    }
}
