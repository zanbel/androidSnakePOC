package com.example.zorg.androidsnakepoc;

/**
 * Created by roy on 26-Dec-14.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import com.example.zorg.androidsnakepoc.SnakeApp;

public class GameAnalytics {

    private int speed;
    private Date startDate;
    private Date endDate;
    private int score;
    private ReasonOfDeath eReasonOfDeath;
    private int userId;
    private int duration;
    public static List<GameAnalytics> games;
    private static final String PATH = Environment
            .getExternalStorageDirectory().toString() + "/Snake/Games.xml";
    private final SimpleDateFormat FORMAT = new SimpleDateFormat(
            "dd-MM-yyyy HH:mm:ss");

    public enum ReasonOfDeath {

        leftWall, rightWall, upWall, downWall, selfinflict;
    }

    public GameAnalytics() {

        this.userId = SnakeApp.getUserId();
        speed = SnakeApp.getSpeed();
        startDate = new Date(System.currentTimeMillis());
        endDate = new Date();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public List<GameAnalytics> getGames() {

        if (games == null)
            games = getGamesFromXML();

        return games;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ReasonOfDeath geteReasonOfDeath() {
        return eReasonOfDeath;
    }

    public void seteReasonOfDeath(ReasonOfDeath eReasonOfDeath) {
        this.eReasonOfDeath = eReasonOfDeath;
    }

    public void seteReasonOfDeathFromInt(int reasonOfDeath) {

        switch (reasonOfDeath) {
            case 0:
                eReasonOfDeath = ReasonOfDeath.leftWall;
                break;
            case 1:
                eReasonOfDeath = ReasonOfDeath.rightWall;
                break;
            case 2:
                eReasonOfDeath = ReasonOfDeath.upWall;
                break;
            case 3:
                eReasonOfDeath = ReasonOfDeath.downWall;
                break;
            case 4:
                eReasonOfDeath = ReasonOfDeath.selfinflict;
                break;
        }
    }

    public void seteReasonOfDeathFromString(String reasonOfDeath) {

        if (reasonOfDeath.equalsIgnoreCase("leftWall"))
            eReasonOfDeath = ReasonOfDeath.leftWall;
        else if (reasonOfDeath.equalsIgnoreCase("rightWall"))
            eReasonOfDeath = ReasonOfDeath.rightWall;
        else if (reasonOfDeath.equalsIgnoreCase("upWall"))
            eReasonOfDeath = ReasonOfDeath.upWall;
        else if (reasonOfDeath.equalsIgnoreCase("downWall"))
            eReasonOfDeath = ReasonOfDeath.downWall;
        else if (reasonOfDeath.equalsIgnoreCase("selfinflict"))
            eReasonOfDeath = ReasonOfDeath.selfinflict;

    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    private String getStringDuration() {

		/*
		 * int hour = endTime.hour - startTime.hour; int minute = endTime.minute
		 * - startTime.minute; int sec = endTime.second - startTime.second;
		 * duration = (hour * 3600) + (minute * 60) + sec; return
		 * String.valueOf(duration);
		 */
        int hour = this.getEndDate().getHours()
                - this.getStartDate().getHours();
        int minute = this.getEndDate().getMinutes()
                - this.getStartDate().getMinutes();
        int sec = this.getEndDate().getSeconds()
                - this.getStartDate().getSeconds();

        duration = (hour * 3600) + (minute * 60) + sec;
        return String.valueOf(duration);
    }

    public void writeGamesToXML(List<GameAnalytics> games) {

        FileOutputStream outStream = null;
        OutputStreamWriter osw = null;
        StringBuffer sb = null;
        BufferedWriter bw = null;

        try {

            // checking if xml file exist and creating if not
            File gamesFile = new File(PATH);
            gamesFile.createNewFile();
            outStream = new FileOutputStream(gamesFile);
            osw = new OutputStreamWriter(outStream);

            sb = new StringBuffer(getXMLString());
            bw = new BufferedWriter(osw);
            //outStream.write(getXMLString().getBytes());
            bw.write(sb.toString());

            bw.flush();
            osw.flush();
            bw.close();
            outStream.close();

        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException: " + e.getMessage());

        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());

        }
    }

    private String getXMLString() {

        XmlSerializer xmlSerializer = null;
        StringWriter writer = null;

        // start Document
        try {

            xmlSerializer = Xml.newSerializer();
            writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag("", "Games");


            for (Iterator<GameAnalytics> i = games.iterator(); i.hasNext();) {
                GameAnalytics game = i.next();

                xmlSerializer.startTag("", "Game");

                xmlSerializer.startTag("", "UserId");
                xmlSerializer.text(String.valueOf(game.getUserId()));
                xmlSerializer.endTag("", "UserId");

                xmlSerializer.startTag("", "Score");
                xmlSerializer.text(String.valueOf(game.getScore()));
                xmlSerializer.endTag("", "Score");

                xmlSerializer.startTag("", "Speed");
                xmlSerializer.text(String.valueOf(game.getSpeed()));
                xmlSerializer.endTag("", "Speed");

                xmlSerializer.startTag("", "ReasonOfDeath");
                xmlSerializer.text(String.valueOf(game.geteReasonOfDeath()
                        .toString()));
                xmlSerializer.endTag("", "ReasonOfDeath");

                xmlSerializer.startTag("", "Duration");
                xmlSerializer.text(game.getStringDuration());
                xmlSerializer.endTag("", "Duration");

                xmlSerializer.startTag("", "StartDate");
                xmlSerializer.text(FORMAT.format(game.getStartDate()));
                xmlSerializer.endTag("", "StartDate");

                xmlSerializer.startTag("", "EndDate");
                xmlSerializer.text(FORMAT.format(game.getEndDate()));
                xmlSerializer.endTag("", "EndDate");

                xmlSerializer.endTag("", "Game");

            }

            xmlSerializer.endTag("", "Games");
            xmlSerializer.endDocument();
            xmlSerializer.flush();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return writer.toString();

    }

    private List<GameAnalytics> getGamesFromXML() {

        List<GameAnalytics> result = new ArrayList<GameAnalytics>();
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

        try {

            SAXParser saxParser = saxParserFactory.newSAXParser();
            SAXHandler handler = new SAXHandler();
            saxParser.parse(new File(PATH), handler);
            // get games list
            result = handler.getGames();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public class SAXHandler extends DefaultHandler {

        // List to hold Employees object
        private List<GameAnalytics> saxgames = null;
        private GameAnalytics saxgame = null;

        // getter method for employee list
        public List<GameAnalytics> getGames() {
            return saxgames;
        }

        boolean bUserId = false;
        boolean bSpeed = false;
        boolean bScore = false;
        boolean bStartDate = false;
        boolean bEndDate = false;
        boolean bDuration = false;
        boolean bReasonOfDeath = false;

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {

            if (qName.equalsIgnoreCase("GAME")) {
                saxgame = new GameAnalytics();
                if (saxgames == null)
                    saxgames = new ArrayList<GameAnalytics>();
            } else if (qName.equalsIgnoreCase("USERID")) {
                bSpeed = true;
            } else if (qName.equalsIgnoreCase("SPEED")) {
                bSpeed = true;
            } else if (qName.equalsIgnoreCase("SCORE")) {
                bScore = true;
            } else if (qName.equalsIgnoreCase("STARTDATE")) {
                bStartDate = true;
            } else if (qName.equalsIgnoreCase("ENDDATE")) {
                bEndDate = true;
            } else if (qName.equalsIgnoreCase("DURATION")) {
                bDuration = true;
            } else if (qName.equalsIgnoreCase("REASONOFDEATH")) {
                bReasonOfDeath = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {

            try {

                if (qName.equalsIgnoreCase("GAME"))
                    saxgames.add(saxgame);

            } catch (Exception e) {
                Log.w("", e.toString());
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void characters(char ch[], int start, int length)
                throws SAXException {

            try {

                if (bUserId) {
                    saxgame.setUserId(Integer.parseInt(new String(ch, start,
                            length)));
                    bUserId = false;
                } else if (bSpeed) {
                    saxgame.setSpeed(Integer.parseInt(new String(ch, start,
                            length)));
                    bSpeed = false;
                } else if (bScore) {
                    saxgame.setScore(Integer.parseInt(new String(ch, start,
                            length)));
                    bScore = false;
                } else if (bDuration) {
                    saxgame.setDuration(Integer.parseInt(new String(ch, start,
                            length)));
                    bDuration = false;
                } else if (bReasonOfDeath) {
                    saxgame.seteReasonOfDeathFromString(new String(ch, start,
                            length));
                    bReasonOfDeath = false;
                } else if (bStartDate) {
                    Date startDate = (Date) saxgame.FORMAT.parse(new String(ch,
                            start, length));
                    saxgame.setStartDate(startDate);
                    bStartDate = false;
                } else if (bEndDate) {
                    Date endDate = (Date) saxgame.FORMAT.parse(new String(ch,
                            start, length));
                    saxgame.setEndDate(endDate);
                    bEndDate = false;
                }
            } catch (Exception e) {
                Log.w("", e.toString());
            }
        }
    }
}

