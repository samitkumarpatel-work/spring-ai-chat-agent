package net.samitkumar.spring_ai_chat_agent;

import com.poiji.annotation.ExcelCellName;
import com.poiji.bind.Poiji;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.Arrays;

@Configuration
public class VectorConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(VectorConfiguration.class);


    @Bean
    VectorStore vectorStore(AzureOpenAiEmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel)
                .build();
    }

    @Bean
    CommandLineRunner init(
            @Value("classpath*:/biodata/*.pdf") Resource[] resources,
            @Value("classpath:about/jsonplaceholder_user_details.xlsx") Resource xlsxResource,
            VectorStore vectorStore) {
        return args -> {

            //Load all pdf's
            /*Arrays.stream(resources).toList().forEach(resource -> {
                logger.info("File Name : {}", resource.getFilename());
                var documents = new PagePdfDocumentReader(
                        resource,
                        PdfDocumentReaderConfig.builder()
                                .withPageTopMargin(0)
                                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                        .withNumberOfTopTextLinesToDelete(0)
                                        .build())
                                .withPagesPerDocument(1)
                                .build()
                ).read();

                vectorStore.add(documents);
            });*/

            //Load xlsx file
            logger.info("File Name : {}", xlsxResource.getFilename());
            var excelData = Poiji.fromExcel(xlsxResource.getFile(), ExcelData.class);
            var ds = excelData
                    .stream()
                    .map(d -> Document.builder().id(d.getId()).metadata("id", d.getId()).metadata("username", d.getUsername()).metadata("fullname", d.getFullname()).text(d.getAbout()).build())
                    .toList();
            vectorStore.add(ds);

            //finding documents Test
            vectorStore.similaritySearch("Patricia")
                    .stream()
                    .filter(document -> {
                        var dInS = (String)document.getMetadata().get("fullname");
                        return dInS.contains("Patricia");
                    })
                    .forEach(document -> logger.info("### Document: Score= {} , data= {}", document.getScore(), document.getText()));


        };
    }
}

class ExcelData {
    @ExcelCellName("id") String id;
    @ExcelCellName("fullname") String fullname;
    @ExcelCellName("username") String username;
    @ExcelCellName("about") String about;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}