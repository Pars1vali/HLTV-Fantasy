import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class algorithm {
    static ArrayList<String> teamLinks = new ArrayList<>();
    static  ArrayList<Player> playersList = new ArrayList<>();

    public static void main(String[] args) throws  InterruptedException {

        String url1 = "https://www.hltv.org/fantasy/310/league/140986?offset=";
        String url2 = "";

        HandlerProcess1 process1 = new HandlerProcess1(url1, 0,50);
        HandlerProcess2 process2 = new HandlerProcess2(url1, 50, 100);
        ParserPlayersLinks1 parserPlayersLinks1process = new ParserPlayersLinks1(algorithm.teamLinks);
        SortPlayers sortPlayers = new SortPlayers(playersList);
        process1.start();
        process2.start();
        process1.join();
        process2.join();
        parserPlayersLinks1process.start();
        parserPlayersLinks1process.join();
        sortPlayers.start();
        sortPlayers.join();
        TopPlayersInCategories topCreateProcess = new TopPlayersInCategories(playersList, playersList.size(), "D:\\Java\\HlTV_Algorithm\\src\\main\\resources\\result.txt","Defender");
        topCreateProcess.start();
        topCreateProcess.join();


    }
}



    class HandlerProcess1 extends Thread{
        private WebDriver driver = new ChromeDriver();
        private String url;
        private int offsetStart;
        private int offsetEnd;

        public HandlerProcess1(String url,int offsetStart, int offsetEnd){
            this.url = url;
            this.offsetStart = offsetStart;
            this.offsetEnd = offsetEnd;
        }

        @Override
        public void run() {
            for (int i = offsetStart; i < offsetEnd; i+=10) {
                driver.get(url+i);
                if(i==offsetStart){
                    WebElement coke = driver.findElement(By.xpath("//*[@id=\"CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll\"]"));
                    coke.click();
                }
                String code = driver.getPageSource();
                Document page = Jsoup.parse(code);
                Elements links = page.getElementsByAttributeValue("class", "tr-wrapper");
                for (Element elem : links) {
                    algorithm.teamLinks.add("https://www.hltv.org" + elem.attr("href"));

                }

            }
            driver.close();
        }
    }

    class HandlerProcess2 extends Thread{
    private WebDriver driver = new ChromeDriver();
    private String url;
    private int offsetStart;
    private int offsetEnd;

    public HandlerProcess2(String url,int offsetStart, int offsetEnd){
        this.url = url;
        this.offsetStart = offsetStart;
        this.offsetEnd = offsetEnd;
    }
    @Override
    public void run() {
        for (int i = offsetStart; i < offsetEnd; i+=10) {
            driver.get(url+i);
            if(i==offsetStart){
                WebElement coke = driver.findElement(By.xpath("//*[@id=\"CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll\"]"));
                coke.click();
            }
            String code = driver.getPageSource();
            Document page = Jsoup.parse(code);
            Elements links = page.getElementsByAttributeValue("class", "tr-wrapper");
            for (Element elem : links) {
                algorithm.teamLinks.add("https://www.hltv.org" + elem.attr("href"));

            }
        }
        driver.close();

    }

}

    class ParserPlayersLinks1 extends Thread {

        private WebDriver driver = new ChromeDriver();
        private String code;
        private ArrayList<String> teamLinks;

        public ParserPlayersLinks1(ArrayList<String> teamLinks){
            this.teamLinks = teamLinks;
        }

        @Override
        public void run() {
            for (int i = 0; i < teamLinks.size(); i++) {

                driver.get(teamLinks.get(i));
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                code = driver.getPageSource();
                if(i==0) {
                    WebElement coke = driver.findElement(By.xpath("//*[@id=\"CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll\"]"));
                    coke.click();
                }
                Document page = Jsoup.parse(code);
                Elements players = page.getElementsByAttributeValue("class","player-block player-not-in-match");
                for(Element player : players){
                    String name = player.getElementsByAttributeValue("class","text-ellipsis").first().text();
                    String role = player.getElementsByAttributeValue("class","assign-role-text").first().text();
                    String[] array = (player.getElementsByAttributeValue("class","points").first().text()).split(" ");
                    int point = Integer.parseInt(array[0]);
                    int countUse = 1;
                    algorithm.playersList.add(new Player(name,role,point,countUse,false,0));

                }
            }
            driver.close();
        }
    }

    class SortPlayers extends  Thread{
    private ArrayList<Player> playerList;
    private ArrayList<Player> playersListToRemove = new ArrayList<>();
    public SortPlayers(ArrayList<Player> playerList){
        this.playerList = playerList;
    }

        @Override
        public void run() {
            for (Player player : playerList) {
                player.setActive(true);
                for (Player playerFromList : playerList) {
                    if(playerFromList.isActive()==false){
                        if(player.getName().equals(playerFromList.getName()) && player.getRole().equals(playerFromList.getRole())){
                            player.setPoint(player.getPoint()+playerFromList.getPoint());
                            player.setCountUse(player.getCountUse()+1);
                            playerFromList.setActive(true);
                            playersListToRemove.add(playerFromList);
                        }
                    }
                }
            }
            for (int i = 0; i < playersListToRemove.size(); i++) {
                playerList.remove(playersListToRemove.get(i));
            }
            for (int i = 0; i < playerList.size(); i++) {
                playerList.get(i).setAverageDamage(playerList.get(i).getPoint()/playerList.get(i).getCountUse());
            }
            System.out.println();

        }
    }

    class TopPlayersInCategories extends  Thread{
        private ArrayList<Player> playersList;
        private int topN;
        private String attribute;
        private String filepath;

        public TopPlayersInCategories(ArrayList<Player> playersList, int topN, String filepath){
            this.playersList = playersList;
            this.topN = topN;
            this.filepath = filepath;
        }

        public TopPlayersInCategories(ArrayList<Player> playersList, int topN, String filepath, String attribute){
            this.playersList = playersList;
            this.topN = topN;
            this.filepath = filepath;
            this.attribute = attribute;
        }

        @Override
        public void run() {
            try {
                FileWriter fileWriterClearFile = new FileWriter(filepath);
                fileWriterClearFile.write("");
                fileWriterClearFile.close();
                FileWriter fileWriter = new FileWriter(filepath,false);
                Collections.sort(playersList);
                if(attribute==null){
                    for (int i = 0; i < topN; i++) {
                        Player player = playersList.get(i);
                        System.out.println(player.getName() + " " + player.getRole() + " " + player.getAverageDamage());
                        fileWriter.write(player.getName() + " " + player.getRole() + " " + player.getAverageDamage() + "\n");
                        fileWriter.flush();
                    }
                }else{
                    for (int i = 0; i < topN; i++) {
                        Player player = playersList.get(i);
                        fileWriter.write(player.getName() + " " + player.getRole() + " " + player.getAverageDamage() + "\n");
                        fileWriter.flush();

                        if(attribute.equals(player.getName()) || attribute.equals(player.getRole())){
                            System.out.println(player.getName() + " " + player.getRole() + " " + player.getAverageDamage());
                        }
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }










