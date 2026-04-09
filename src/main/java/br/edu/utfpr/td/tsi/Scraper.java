package br.edu.utfpr.td.tsi;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
    final Logger logger = LoggerFactory.getLogger(Scraper.class);

    @PostConstruct 
    public void scrapPublications() {
        List<Publications> publicationsList = new ArrayList<>();
        
        WebDriver driver = new ChromeDriver();
        driver.get("https://ivansalvadori.github.io/publications.html");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));  
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("pub-ul")));        
        Document doc = Jsoup.parse(driver.getPageSource());
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
            String compAbstract = "";
            String keyWords = "";
            if (url.isEmpty() || url.equals("https://doi.org/10.13140/RG.2.1.4068.8803")) {
                logger.atLevel(Level.WARN).setMessage("URL vazia para a publicação: " + titulo).log();
            } else if (url.contains("COMPSAC") || url.contains("AICCSA") || url.contains("ICWS") || url.contains("ISCC") || url.contains("SCC")){
                driver = new ChromeDriver();
                driver.get(url);
                wait = new WebDriverWait(driver, Duration.ofSeconds(15));  
                wait.until(ExpectedConditions.presenceOfElementLocated(By.className("abstract-text-content")));
                WebElement btnKeywords = driver.findElement(By.id("keywords"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnKeywords);
                Document compDoc = Jsoup.parse(driver.getPageSource());
                driver.quit();
                compAbstract = compDoc.select(".abstract-text-content").text();
                Elements compkeywords = compDoc.select("ul.u-mt-1 a");
                for (Element keyword : compkeywords) {
                    keyWords = keyWords + keyword.select("a").text() + "; ";
                }
            } else if (url.contains("sbbd")){
                Document compDoc = null;
                try {
                    compDoc = Jsoup.connect(url).get();
                } catch (IOException e) {
                    logger.atLevel(Level.ERROR).setCause(e).setMessage("Erro ao conectar ao site").log();
                    throw new RuntimeException("Erro ao conectar ao site: " + e.getMessage());
                }  
                compAbstract = compDoc.selectFirst("div.item.abstract").text().replace("Resumo ", "");         
                Element keyWordsDiv = compDoc.selectFirst("div.item.keywords");
                keyWords = keyWordsDiv.select("span.value").text();
            } else if (url.equals("https://doi.org/10.1145/3330204.3330258") || url.equals("https://doi.org/10.1145/3011141.3011155") || url.equals("https://doi.org/10.1145/3151759.3151793") || url.equals("https://doi.org/10.1145/3151759.3151783") || url.equals("https://doi.org/10.1145/3011141.3011179") ){
                driver = new ChromeDriver();
                driver.get(url);
                Document compDoc = Jsoup.parse(driver.getPageSource());
                driver.quit();
                Element abstElement = compDoc.selectFirst("section#abstract");
                compAbstract = abstElement.select("div[role=paragraph]").text();
                Elements keyWordsDiv = compDoc.select("div.article__index-terms a");
                for (Element keyword : keyWordsDiv) {
                    keyWords = keyWords + keyword.select("a").text() + "; ";
                }
            } else if (url.contains("IJWIS")) {
                driver = new ChromeDriver();
                driver.get(url);
                Document compDoc = Jsoup.parse(driver.getPageSource());
                driver.quit();
                Elements keyWordsDiv = compDoc.select("a.content-metadata--item");
                for (Element keyword : keyWordsDiv) {
                    keyWords = keyWords + keyword.select("a").text() + "; ";
                }
            }

            publicationsList.add(new Publications(autor, titulo, ano, local, pages, url, compAbstract, keyWords));
        }
        
        
        logger.atLevel(Level.INFO).log("Escrita do arquivo JSON concluída.");
        JsonFileRecorder record = new JsonFileRecorder();   
        record.gravarArquivo(publicationsList);
    }
}
