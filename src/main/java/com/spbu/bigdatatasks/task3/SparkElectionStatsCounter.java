package com.spbu.bigdatatasks.task3;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import java.util.*;
import java.util.function.UnaryOperator;
import static org.apache.spark.sql.functions.*;

public class SparkElectionStatsCounter implements ElectionStatsCounter {

    private final String path;
    public SparkElectionStatsCounter(String path) {
        this.path = path;
    }

    // lambdas - mini tasks
    private UnaryOperator<Dataset<Row>> voterTurnout = dataset -> {
        final String electors = "electors_number";
        final String blanks = "blanks_number";
        final String percentage = "percentage";

        return dataset.groupBy(REGION)
                .agg(
                        sum(VOTERS_INCLUDED_IN_VOTER_LIST).as(electors),
                        sum(VALID_BALLOTS).as(blanks)
                ).withColumn(
                        percentage,
                        col(blanks).divide(col(electors)).multiply(100)
                ).orderBy(col(percentage).desc());
    };

    private UnaryOperator<Dataset<Row>> favouriteCandidate = dataset -> {
        Dataset<Row> result = dataset.agg(
                sum(BABURIN).as(BABURIN),
                sum(GRUDININ).as(GRUDININ),
                sum(ZHIRINOVSKY).as(ZHIRINOVSKY),
                sum(PUTIN).as(PUTIN),
                sum(SOBCHAK).as(SOBCHAK),
                sum(SURAIKIN).as(SURAIKIN),
                sum(TITOV).as(TITOV),
                sum(YAVLINSKY).as(YAVLINSKY)
        );
        result.show();

        String[] colNames = result.columns();
        List<Long> values = new ArrayList<>();
        for (String colName: colNames)
            values.add(result.first().getAs(colName));
        String favouriteCandidate = colNames[values.indexOf(Collections.max(values))];

        System.out.println("FAVOURITE: " + favouriteCandidate); // Путин

        result = dataset
                .filter(col(BALLOTS_RECEIVED_BY_ELECTION_COMMISSION).$greater(300))
                .select(col(PEC),
                        col(favouriteCandidate),
                        col(BALLOTS_RECEIVED_BY_ELECTION_COMMISSION))
                .orderBy(col(favouriteCandidate).desc()).limit(1);

        return result;
    };

    private UnaryOperator<Dataset<Row>> maxDifference = dataset -> {
        final String sum1 = "sum: " + VALID_BALLOTS;
        final String sum2 = "sum: " + VOTERS_INCLUDED_IN_VOTER_LIST;
        final String percentage = "percentage";
        final String maxPercentage = "max_percentage";
        final String minPercentage = "min_percentage";
        final String difference = "difference";

        return dataset.select(REGION, TEC, VALID_BALLOTS, VOTERS_INCLUDED_IN_VOTER_LIST)
                .groupBy(TEC).agg(
                        first(col(REGION)).as(REGION),
                        col(TEC),
                        sum(VALID_BALLOTS).as(sum1),
                        sum(VOTERS_INCLUDED_IN_VOTER_LIST).as(sum2))
                .withColumn(percentage, col(sum1).divide(col(sum2)).multiply(100))
                .groupBy(REGION).agg(
                        min(percentage).as(minPercentage),
                        max(percentage).as(maxPercentage)
                ).withColumn(difference, col(maxPercentage).minus(col(minPercentage)))
                .orderBy(col(difference).desc())
                .limit(1);
    };

    private UnaryOperator<Dataset<Row>> variance = dataset -> {
        final String percentage = "percentage";
        final String variance = "variance";

        return dataset
                .select(REGION, VOTERS_INCLUDED_IN_VOTER_LIST, VALID_BALLOTS)
                .withColumn(percentage, col(VALID_BALLOTS).divide(col(VOTERS_INCLUDED_IN_VOTER_LIST)).multiply(100))
                .groupBy(REGION)
                .agg(var_samp(percentage).as(variance))
                .orderBy(REGION);
    };

    private UnaryOperator<Dataset<Row>> summaryTable = dataset -> {
        Long totalSum;
        String percents;
        Dataset<Row> result;

        for (String name: Arrays.asList(BABURIN ,GRUDININ, ZHIRINOVSKY, PUTIN, SOBCHAK, SURAIKIN, TITOV, YAVLINSKY)) {
            percents = "percents_of " + name;
            totalSum = dataset.select(name).agg(sum(name)).first().getLong(0);

            result = dataset.select(REGION, name)
                    .groupBy(REGION).agg(bround(sum(name).divide(totalSum).multiply(100)).as(percents))
                    .groupBy(percents).agg(
                            count(percents).as("number_of_regions_with_this_result")
                    ).orderBy(col(percents).desc());
            result.show();
        }
        return dataset;
    };

    // ? somehow fields are read like strings not integers ?
    private UnaryOperator<Dataset<Row>> sqlExample = dataset -> {
        String query = "SELECT '" + REGION + "', '" + TEC + "', '" + PEC + "', " +
                "SUM('" + VALID_BALLOTS + "'), SUM('" + VOTERS_INCLUDED_IN_VOTER_LIST + "')" +
                "FROM " + TABLE_NAME + " " +
                "GROUP BY '" + TEC + "'";
        Dataset<Row> result = dataset.sparkSession().sql(query);
        result.show();
        return result;
    };

    // task1
    @Override
    public void countVoterTurnout() {
        runTask(voterTurnout);
    }

    // task2
    @Override
    public void countFavouriteCandidate() {
        runTask(favouriteCandidate);
    }

    // task3
    @Override
    public void countMaxDifference() {
        runTask(maxDifference);
    }

    // task4
    @Override
    public void countVariance() {
        runTask(variance);
    }

    @Override
    public void countSummaryTables() {
        runTask(summaryTable);
    }

    // helper function
    public void runTask(UnaryOperator<Dataset<Row>> task) {
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("Spark Elections Example")
                .getOrCreate();
        spark.sparkContext().setLogLevel("OFF");

        Dataset<Row> dataset = prepareData(spark.read().csv(path));
        Dataset<Row> result  = task.apply(dataset);

        // dataset.show();
        result.show();

        spark.close();
    }

    private Dataset<Row> prepareData(Dataset<Row> dataset) {
        Dataset<Row> newDataset = dataset
                .withColumn(REGION, dataset.col("_c0"))
                .withColumn(TEC, dataset.col("_c1"))
                .withColumn(PEC, dataset.col("_c2"))
                .withColumn(VOTERS_INCLUDED_IN_VOTER_LIST,
                        dataset.col("_c3").cast(DataTypes.IntegerType))
                .withColumn(BALLOTS_RECEIVED_BY_ELECTION_COMMISSION,
                        dataset.col("_c4").cast(DataTypes.IntegerType))
                .withColumn(BALLOTS_ISSUED_TO_EARLY_VOTERS,
                        dataset.col("_c5").cast(DataTypes.IntegerType))
                .withColumn(BALLOTS_ISSUED_AT_POLLING_STATION_ON_ELECTION_DAY,
                        dataset.col("_c6").cast(DataTypes.IntegerType))
                .withColumn(BALLOTS_ISSUED_OUTSIDE_POLLING_STATION_ON_ELECTION_DAY,
                        dataset.col("_c7").cast(DataTypes.IntegerType))
                .withColumn(CANCELED_BALLOTS,
                        dataset.col("_c8").cast(DataTypes.IntegerType))
                .withColumn(BALLOTS_IN_MOBILE_BALLOT_BOXES,
                        dataset.col("_c9").cast(DataTypes.IntegerType))
                .withColumn(BALLOTS_IN_STATIONARY_BALLOT_BOXES,
                        dataset.col("_c10").cast(DataTypes.IntegerType))
                .withColumn(INVALID_BALLOTS,
                        dataset.col("_c11").cast(DataTypes.IntegerType))
                .withColumn(VALID_BALLOTS,
                        dataset.col("_c12").cast(DataTypes.IntegerType))
                .withColumn(LOST_BALLOTS,
                        dataset.col("_c13").cast(DataTypes.IntegerType))
                .withColumn(NOT_COUNTED_BALLOTS,
                        dataset.col("_c14").cast(DataTypes.IntegerType))
                .withColumn(BABURIN,
                        dataset.col("_c15").cast(DataTypes.IntegerType))
                .withColumn(GRUDININ,
                        dataset.col("_c16").cast(DataTypes.IntegerType))
                .withColumn(ZHIRINOVSKY,
                        dataset.col("_c17").cast(DataTypes.IntegerType))
                .withColumn(PUTIN,
                        dataset.col("_c18").cast(DataTypes.IntegerType))
                .withColumn(SOBCHAK,
                        dataset.col("_c19").cast(DataTypes.IntegerType))
                .withColumn(SURAIKIN,
                        dataset.col("_c20").cast(DataTypes.IntegerType))
                .withColumn(TITOV,
                        dataset.col("_c21").cast(DataTypes.IntegerType))
                .withColumn(YAVLINSKY,
                        dataset.col("_c22").cast(DataTypes.IntegerType))
                .select(REGION,
                        TEC,
                        PEC,
                        VOTERS_INCLUDED_IN_VOTER_LIST,
                        BALLOTS_RECEIVED_BY_ELECTION_COMMISSION,
                        BALLOTS_ISSUED_TO_EARLY_VOTERS,
                        BALLOTS_ISSUED_AT_POLLING_STATION_ON_ELECTION_DAY,
                        BALLOTS_ISSUED_OUTSIDE_POLLING_STATION_ON_ELECTION_DAY,
                        CANCELED_BALLOTS,
                        BALLOTS_IN_MOBILE_BALLOT_BOXES,
                        BALLOTS_IN_STATIONARY_BALLOT_BOXES,
                        INVALID_BALLOTS,
                        VALID_BALLOTS,
                        LOST_BALLOTS,
                        NOT_COUNTED_BALLOTS,
                        BABURIN,
                        GRUDININ,
                        ZHIRINOVSKY,
                        PUTIN,
                        SOBCHAK,
                        SURAIKIN,
                        TITOV,
                        YAVLINSKY
                );
        newDataset.createOrReplaceTempView(TABLE_NAME);
        return newDataset;
    }
}
