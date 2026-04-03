package br.edu.utfpr.td.tsi;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;

import br.edu.utfpr.td.tsi.model.Publications;
import jakarta.annotation.PostConstruct;

@Component
public class Scraper {
    private Logger logger = LoggerFactory.getLogger(Scraper.class);

    @PostConstruct 
    public void scrapPublications() {
        Document doc = null;
        List<Publications> publicationsList = new ArrayList<>();

        WebDriver driver = new ChromeDriver();
        driver.get("https://ivansalvadori.github.io/publications.html");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));  
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("pub-ul")));        
        doc = Jsoup.parse(driver.getPageSource());
        driver.quit();

        Elements publications = doc.select("#pub-ul li");
        logger.atLevel(Level.INFO).log("Scraping completed, starting data processing...");
        for (Element publication : publications) {
            String pub = publication.select("li").text();
            String autor = pub.split("\\.")[0].trim();
            String ano = pub.split("\\.")[1].trim().replace("(", "").replace(")", "");
            String titulo = pub.split("\\.")[2].trim();
            String local = pub.split("\\.")[3].trim();
            String pages = pub.split("\\.")[4].trim();
            String url = publication.select("a").attr("href");

            publicationsList.add(new Publications(autor, titulo, ano, local, pages, url));
        }
        logger.atLevel(Level.INFO).log("Escrita do arquivo JSON concluída.");
        JsonFileRecorder record = new JsonFileRecorder();   
        record.gravarArquivo(publicationsList);
    }
}
