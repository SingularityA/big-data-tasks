package com.spbu.bigdatatasks.task3;

public interface ElectionStatsCounter {
    String TABLE_NAME = "election_table";
    String REGION = "Регион";
    String TEC = "ТИК";
    String PEC = "УИК";
    String VOTERS_INCLUDED_IN_VOTER_LIST =
            "Число избирателей, включенных в список избирателей";
    String BALLOTS_RECEIVED_BY_ELECTION_COMMISSION =
            "Число избирательных бюллетеней, полученных участковой избирательной комиссией";
    String BALLOTS_ISSUED_TO_EARLY_VOTERS =
            "Число избирательных бюллетеней, выданных избирателям, проголосовавшим досрочно";
    String BALLOTS_ISSUED_AT_POLLING_STATION_ON_ELECTION_DAY =
            "Число избирательных бюллетеней, выданных в помещении для голосования в день голосования";
    String BALLOTS_ISSUED_OUTSIDE_POLLING_STATION_ON_ELECTION_DAY =
            "Число избирательных бюллетеней, выданных вне помещения для голосования в день голосования";
    String CANCELED_BALLOTS =
            "Число погашенных избирательных бюллетеней";
    String BALLOTS_IN_MOBILE_BALLOT_BOXES =
            "Число избирательных бюллетеней в переносных ящиках для голосования";
    String BALLOTS_IN_STATIONARY_BALLOT_BOXES =
            "Число бюллетеней в стационарных ящиках для голосования";
    String INVALID_BALLOTS =
            "Число недействительных избирательных бюллетеней";
    String VALID_BALLOTS =
            "Число действительных избирательных бюллетеней";
    String LOST_BALLOTS =
            "Число утраченных избирательных бюллетеней";
    String NOT_COUNTED_BALLOTS =
            "Число избирательных бюллетеней, не учтенных при получении";
    String BABURIN = "Бабурин Сергей Николаевич";
    String GRUDININ = "Грудинин Павел Николаевич";
    String ZHIRINOVSKY = "Жириновский Владимир Вольфович";
    String PUTIN = "Путин Владимир Владимирович";
    String SOBCHAK = "Собчак Ксения Анатольевна";
    String SURAIKIN = "Сурайкин Максим Александрович";
    String TITOV = "Титов Борис Юрьевич";
    String YAVLINSKY = "Явлинский Григорий Алексеевич";

    void countVoterTurnout();
    void countFavouriteCandidate();
}
