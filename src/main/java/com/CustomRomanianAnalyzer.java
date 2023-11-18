package com;

import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.analysis.RomanianNormalizationFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.sis.io.wkt.Transliterator;
import org.tartarus.snowball.ext.RomanianStemmer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;


import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.tartarus.snowball.ext.RomanianStemmer;

import java.io.Reader;

public class CustomRomanianAnalyzer extends Analyzer{
    private final CharArraySet stopWords;

    private final RomanianAnalyzer inbuiltAnalyzer;
    public CustomRomanianAnalyzer(){
        this.inbuiltAnalyzer = new RomanianAnalyzer();
        this.stopWords = this.inbuiltAnalyzer.getDefaultStopSet();
    }

//    @Override
//    protected TokenStreamComponents createComponents(String fieldName, Reader reader){
//        TokenStream tokenizer = this.inbuiltAnalyzer.normalize(fieldName, reader);
//        tokenizer = new ASCIIFoldingFilter(tokenizer);
//        tokenizer = new StopFilter(tokenizer, stopWords);
//        tokenizer = new RomanianStemmer()
//        //new RomanianNormalizationFilter(new RomanianAnalyzer().tokenStream(fieldName, reader));
//    }
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new StandardTokenizer();
//        TokenStream result = new StandardFilter(source);
        TokenStream result = new LowerCaseFilter(source);

        result = new StopFilter(result, this.stopWords);

//        result = new ICUTransformFilter(result, Transliterator.getInstance("Latin-ASCII"));
//        if(!stemExclusionSet.isEmpty())
//            result = new SetKeywordMarkerFilter(result, stemExclusionSet);
//        result = new SnowballFilter(result, new RomanianStemmer());
        return new TokenStreamComponents(source, result);
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
        TokenStream result = new LowerCaseFilter(in);
        result = new LowerCaseFilter(result);
        return result;
    }

}
