package com;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import java.io.File;
import java.nio.file.Paths;
import org.apache.lucene.store.FSDirectory;


//TODO: make romanian analyzer work
//TODO: Stem, remove diacritics and clean stopwords
//TODO: Add a GenerateIndex method
//TODO: Make a cli (in main??)
@SuppressWarnings({
        "serial", "deprecation",
        "rawtypes", "unchecked"})
public class Indexer {

    public static void CreateIndex() throws IOException, ParseException {
        CreateIndex("dataset", "index");
    }
    public static void CreateIndex(String path, String index_path) throws IOException, ParseException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog","fatal");

        File directory_src = new File(path);
        Directory idx_directory = FSDirectory.open(Paths.get(index_path));

        if(!directory_src.isDirectory()){
            System.out.println("Directory " + directory_src + " is not a valid directory");
        }

        System.out.println("Directory is valid, indexing files");
        File[] files = directory_src.listFiles();

        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(idx_directory, config);

        for(File file : files){
            w = AddDocumentToIndex(w, file);
        }
        w.commit();
        w.close();

    }

    private static IndexWriter AddDocumentToIndex(IndexWriter w, File file) throws IOException {
        System.out.println("Adding file " + file + " to index from private method.");
        String x = ReadDocument(file);

        Document doc = new Document();

        doc.add(new TextField("original_content",  x, Field.Store.YES));
        w.addDocument(doc);
        return w;
    }

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
