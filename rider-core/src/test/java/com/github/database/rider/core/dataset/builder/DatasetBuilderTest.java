package com.github.database.rider.core.dataset.builder;

import com.github.database.rider.core.dataset.writer.JSONWriter;
import com.github.database.rider.core.dataset.writer.YMLWriter;
import com.github.database.rider.core.metamodel.Contact_;
import com.github.database.rider.core.replacers.DateTimeReplacer;
import com.github.database.rider.core.util.DateUtils;
import com.github.database.rider.core.util.EntityManagerProvider;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;

@RunWith(JUnit4.class)
public class DatasetBuilderTest {

    private static final String NEW_LINE = System.getProperty("line.separator");


    @Test
    public void shouldGenerateYamlDataSet() throws DataSetException, IOException {
        DataSetBuilder builder = new DataSetBuilder();
        ColumnSpec id = ColumnSpec.of("ID");
        builder.row("USER").column("ID", 1)
                .column("NAME", "@realpestano")
                .add().row("USER")
                .column(id, 2).column("NAME", "@dbunit")
                .add().row("TWEET")
                .column("ID", "abcdef12345").column("CONTENT", "dbunit rules!")
                .column("DATE", "[DAY,NOW]")
                .add().row("FOLLOWER").column(id, 1)
                .column("USER_ID", 1).column("FOLLOWER_ID", 2)
                .add().build();

        IDataSet dataSet = builder.build();

        File datasetFile = Files.createTempFile("rider-dataset", ".yml").toFile();
        FileOutputStream fos = new FileOutputStream(datasetFile);
        new YMLWriter(fos).write(dataSet);

        assertThat(contentOf(datasetFile)).
                contains("FOLLOWER:" + NEW_LINE +
                        "  - ID: 1" + NEW_LINE +
                        "    USER_ID: 1" + NEW_LINE +
                        "    FOLLOWER_ID: 2" + NEW_LINE).
                contains("USER:" + NEW_LINE +
                "  - ID: 1" + NEW_LINE +
                "    NAME: \"@realpestano\"" + NEW_LINE +
                "  - ID: 2" + NEW_LINE +
                "    NAME: \"@dbunit\"").
                contains("TWEET:" + NEW_LINE +
                "  - ID: \"abcdef12345\"" + NEW_LINE +
                "    CONTENT: \"dbunit rules!\"" + NEW_LINE +
                "    DATE: \"[DAY,NOW]\""+ NEW_LINE );
    }

    @Test
    public void shouldGenerateJsonDataSet() throws DataSetException, IOException {
        DataSetBuilder builder = new DataSetBuilder();
        ColumnSpec id = ColumnSpec.of("ID");
        builder.row("USER").column("ID", 1)
                .column("NAME", "@realpestano")
                .add().row("USER")
                .column(id, 2).column("NAME", "@dbunit")
                .add().row("TWEET")
                .column("ID", "abcdef12345").column("CONTENT", "dbunit rules!")
                .column("DATE", "[DAY,NOW]")
                .add().row("FOLLOWER").column(id, 1)
                .column("USER_ID", 1).column("FOLLOWER_ID", 2)
                .add().build();

        IDataSet dataSet = builder.build();

        File datasetFile = Files.createTempFile("rider-dataset", ".json").toFile();
        FileOutputStream fos = new FileOutputStream(datasetFile);
        new JSONWriter(fos,dataSet).write();

        assertThat(contentOf(datasetFile)).
                contains("{"+NEW_LINE  +
                        "  \"USER\": ["+NEW_LINE  +
                        "    {"+NEW_LINE  +
                        "      \"ID\": 1,"+NEW_LINE  +
                        "      \"NAME\": \"@realpestano\""+NEW_LINE  +
                        "    },"+NEW_LINE  +
                        "    {"+NEW_LINE  +
                        "      \"ID\": 2,"+NEW_LINE  +
                        "      \"NAME\": \"@dbunit\""+NEW_LINE  +
                        "    }"+NEW_LINE  +
                        "  ],"+NEW_LINE  +
                        "  \"TWEET\": ["+NEW_LINE  +
                        "    {"+NEW_LINE  +
                        "      \"ID\": \"abcdef12345\","+NEW_LINE  +
                        "      \"CONTENT\": \"dbunit rules!\","+NEW_LINE  +
                        "      \"DATE\": \"[DAY,NOW]\""+NEW_LINE  +
                        "    }"+NEW_LINE  +
                        "  ],"+NEW_LINE  +
                        "  \"FOLLOWER\": ["+NEW_LINE  +
                        "    {"+NEW_LINE  +
                        "      \"ID\": 1,"+NEW_LINE  +
                        "      \"USER_ID\": 1,"+NEW_LINE  +
                        "      \"FOLLOWER_ID\": 2"+NEW_LINE  +
                        "    }"+NEW_LINE  +
                        "  ]"+NEW_LINE  +
                        "}");
    }

    @Test
    public void shouldGenerateFlatXmlDataSet() throws DataSetException, IOException {
        DataSetBuilder builder = new DataSetBuilder();
        ColumnSpec id = ColumnSpec.of("ID");
        builder.row("USER").column("ID", 1)
                .column("NAME", "@realpestano")
                .add().row("USER")
                .column(id, 2).column("NAME", "@dbunit")
                .add().row("TWEET")
                .column("ID", "abcdef12345").column("CONTENT", "dbunit rules!")
                .column("DATE", "[DAY,NOW]")
                .add().row("FOLLOWER").column(id, 1)
                .column("USER_ID", 1).column("FOLLOWER_ID", 2)
                .add().build();

        IDataSet dataSet = builder.build();

        File datasetFile = Files.createTempFile("rider-dataset", ".xml").toFile();
        FileOutputStream fos = new FileOutputStream(datasetFile);
        FlatXmlDataSet.write(dataSet, fos);
        assertThat(contentOf(datasetFile)).
                contains("<?xml version='1.0' encoding='UTF-8'?>"+NEW_LINE  +
                        "<dataset>"+NEW_LINE  +
                        "  <USER ID=\"1\" NAME=\"@realpestano\"/>"+NEW_LINE  +
                        "  <USER ID=\"2\" NAME=\"@dbunit\"/>"+NEW_LINE  +
                        "  <TWEET ID=\"abcdef12345\" CONTENT=\"dbunit rules!\" DATE=\"[DAY,NOW]\"/>"+NEW_LINE  +
                        "  <FOLLOWER ID=\"1\" USER_ID=\"1\" FOLLOWER_ID=\"2\"/>"+NEW_LINE  +
                        "</dataset>");

    }

    @Test
    public void shouldGenerateDataSetcolumnDateColumn() throws DataSetException, IOException {
        DataSetBuilder builder = new DataSetBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        Date date = new Date();

        IDataSet dataSet = builder.row("USER").column("ID", 1)
                .column("DATE", date)
                .column("CALENDAR", calendar)
                .add().build();


        File datasetFile = Files.createTempFile("rider-dataset", ".yml").toFile();
        FileOutputStream fos = new FileOutputStream(datasetFile);
        new YMLWriter(fos).write(dataSet);


        assertThat(contentOf(datasetFile)).
                contains("USER:" + NEW_LINE +
                        "  - ID: 1" + NEW_LINE +
                        "    DATE: \"" + DateUtils.format(date) +"\"" + NEW_LINE +
                        "    CALENDAR: \"" + DateUtils.format(calendar.getTime()) +"\"" + NEW_LINE +
                        "");
    }

    @Test
    public void shouldGenerateDataSetUsingMetaModel() throws DataSetException, IOException {

        EntityManagerProvider.instance("contactPU");
        DataSetBuilder builder = new DataSetBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        Date date = new Date();

        IDataSet dataSet = builder.row("CONTACT")
                .column(Contact_.id, 1)
                .column(Contact_.name, "dbrider")
                .column(Contact_.date,date )
                .column(Contact_.calendar, calendar)
                .add().build();


        File datasetFile = Files.createTempFile("rider-dataset", ".yml").toFile();
        FileOutputStream fos = new FileOutputStream(datasetFile);
        new YMLWriter(fos).write(dataSet);


        assertThat(contentOf(datasetFile)).
                contains("CONTACT:" + NEW_LINE +
                        "  - ID: 1" + NEW_LINE +
                        "    NAME: \"dbrider\"" + NEW_LINE +
                        "    DATE: \"" + DateUtils.format(date) +"\"" + NEW_LINE +
                        "    CALENDAR: \"" + DateUtils.format(calendar.getTime()) +"\"" + NEW_LINE +
                        "");
    }

    @Test
    public void shouldGenerateUppercaseYamlDataSet() throws IOException, DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        ColumnSpec id = ColumnSpec.of("id");
        builder.row("user").column(id, 1)
                .column("name", "@realpestano")
                .add().row("user")
                .column(id, 2).column("name", "@dbunit")
                .add().row("tweet")
                .column("id", "abcdef12345").column("content", "dbunit rules!")
                .column("date", "[DAY,NOW]")
                .add().row("follower").column(id, 1)
                .column("user_id", 1).column("follower_id", 2)
                .add().build();

        IDataSet dataSet = builder.build();

        File datasetFile = Files.createTempFile("rider-dataset", ".yml").toFile();
        FileOutputStream fos = new FileOutputStream(datasetFile);
        new YMLWriter(fos).write(dataSet);

        assertThat(contentOf(datasetFile)).
                contains("FOLLOWER:" + NEW_LINE +
                        "  - ID: 1" + NEW_LINE +
                        "    USER_ID: 1" + NEW_LINE +
                        "    FOLLOWER_ID: 2" + NEW_LINE).
                contains("USER:" + NEW_LINE +
                        "  - ID: 1" + NEW_LINE +
                        "    NAME: \"@realpestano\"" + NEW_LINE +
                        "  - ID: 2" + NEW_LINE +
                        "    NAME: \"@dbunit\"").
                contains("TWEET:" + NEW_LINE +
                        "  - ID: \"abcdef12345\"" + NEW_LINE +
                        "    CONTENT: \"dbunit rules!\"" + NEW_LINE +
                        "    DATE: \"[DAY,NOW]\""+ NEW_LINE );
    }


    @Test
    public void shouldGenerateDataSetReusingRows() throws IOException, DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        ColumnSpec id = ColumnSpec.of("id");
        builder.row("user").column(id, 1)
                .column("name", "@realpestano")
                .add().row("user")
                .column(id, 2).column("name", "@dbunit")
                .add().row("tweet")
                .column("id", "abcdef12345").column("content", "dbunit rules!")
                .column("date", "[DAY,NOW]")
                .add().row("follower").column(id, 1)
                .column("user_id", 1).column("follower_id", 2)
                .add().build();

        IDataSet dataSet = builder.build();

        File datasetFile = Files.createTempFile("rider-dataset", ".yml").toFile();
        FileOutputStream fos = new FileOutputStream(datasetFile);
        new YMLWriter(fos).write(dataSet);

        assertThat(contentOf(datasetFile)).
                contains("FOLLOWER:" + NEW_LINE +
                        "  - ID: 1" + NEW_LINE +
                        "    USER_ID: 1" + NEW_LINE +
                        "    FOLLOWER_ID: 2" + NEW_LINE).
                contains("USER:" + NEW_LINE +
                        "  - ID: 1" + NEW_LINE +
                        "    NAME: \"@realpestano\"" + NEW_LINE +
                        "  - ID: 2" + NEW_LINE +
                        "    NAME: \"@dbunit\"").
                contains("TWEET:" + NEW_LINE +
                        "  - ID: \"abcdef12345\"" + NEW_LINE +
                        "    CONTENT: \"dbunit rules!\"" + NEW_LINE +
                        "    DATE: \"[DAY,NOW]\""+ NEW_LINE );
    }

}
