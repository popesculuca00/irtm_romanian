package com;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import java.io.File;


//TODO: make romanian analyzer work
//TODO: Stem, remove diacritics and clean stopwords
//TODO: Add a GenerateIndex method
//TODO: Make a cli (in main??)
public class Indexer {

    public void CreateIndex() throws IOException, ParseException {
        CreateIndex("dataset");
    }
    public void CreateIndex(String path) throws IOException, ParseException {
        File directory = new File(path);

        if(!directory.isDirectory()){
            System.out.println("Directory " + directory + " is not a valid directory");
            return;
        }

        System.out.println("Directory is valid, indexing files");
        File[] files = directory.listFiles();

        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);

        for(File file : files){
            AddDocumentToIndex(w, file);
        }

    }

    private static void AddDocumentToIndex(IndexWriter w, File file){
        System.out.println("Adding file " + file + " to index from private method.");
        String x = ReadDocument(file);
        System.out.println("Document Content:");
        System.out.println(x.toString().substring(0, 100).replace("\n", " "));
        System.out.println("\n\n");

    }
    @SuppressWarnings({
            "serial", "deprecation",
            "rawtypes", "unchecked"})
    private static String ReadDocument(File file){
        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        try (FileInputStream inputStream = new FileInputStream(file)) {
            // Parse the DOCX document
            parser.parse(inputStream, handler, metadata, context);
        } catch (IOException | TikaException | SAXException e) {
            if (!e.toString().contains("org.apache.tika.parser.pdf.PDFParser")) {
                e.printStackTrace();
            }
        }

        return handler.toString();
    }

}
