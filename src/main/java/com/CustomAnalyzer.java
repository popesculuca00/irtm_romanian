package com;


import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.commons.io.FileUtils;


import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import com.ibm.icu.text.Transliterator;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
public class CustomAnalyzer extends Analyzer {
    private final CharArraySet customStopWords;

    private final Transliterator transliterator = Transliterator.getInstance("NFD; [:Nonspacing Mark:] Remove; NFC");

    public CustomAnalyzer() throws IOException{
        this("ro-stop-words.txt");
    }
    public CustomAnalyzer(String roStopWordsPath) throws IOException{

        this.customStopWords = getCustomStopWords();

    }


    @Override
    protected TokenStreamComponents createComponents(String fieldName){

        Tokenizer source = new StandardTokenizer();
        TokenStream lowerCased = new LowerCaseFilter(source);

        lowerCased = new StopFilter(lowerCased, this.customStopWords);

        TokenStream finalStream = new SnowballFilter(lowerCased, "Romanian");

        finalStream = new DiacriticRemovalFilter(finalStream);

        return new TokenStreamComponents(source, finalStream);

    }

    public static CharArraySet getCustomStopWords(){
        CharArraySet stops = new CharArraySet(Set.of(
                "a", "abia", "acea", "aceasta", "această", "aceea", "aceeasi", "aceeași", "acei", "aceia", "acel", "acela", "acelasi", "același", "acele", "acelea", "acest", "acesta", "aceste", "acestea", "acestei", "acestia", "acestui", "acești", "aceștia", "acolo", "acord", "acum", "adica", "adică",
                "ai", "aia", "aibă", "aici", "aiurea", "al", "ala", "ăla", "alaturi", "ale", "alea", "ălea", "alt", "alta", "altceva", "altcineva", "alte", "altfel", "alti", "alți", "altii", "alții", "altul", "am", "anume", "apoi", "ar", "are", "as", "aș", "asa", "așa",
                "așadar", "asemenea", "asta", "ăsta", "astazi", "astea", "ăstea", "astfel", "astăzi", "ăștia", "asupra", "atare", "atat", "atât", "atata", "atâta", "atatea", "atâtea", "atatia", "atâția", "ati", "ați", "atit", "atît", "atita", "atîta", "atitea", "atîtea", "atitia", "atîția", "atunci", "au", "avea",
                "avem", "aveți", "avut", "azi", "b", "ba", "bine", "bucur", "bună", "c", "ca", "cam", "cand", "capat", "care", "careia", "carora", "caruia", "cat", "catre", "caut", "ce", "cea", "ceea", "cei", "ceilalti", "cel", "cele", "celor", "ceva", "chiar", "ci",
                "cinci", "cind", "cine", "cineva", "cit", "cita", "cite", "citeva", "citi", "citiva", "conform", "contra", "cu", "cui", "cum", "cumva", "curând", "curînd", "când", "cât", "câte", "câtva", "câţi", "cînd", "cît", "cîte", "cîtva", "cîți", "că", "căci", "cărei", "căror",
                "cărui", "către", "d", "da", "daca", "dacă", "dar", "dat", "datorită", "dată", "dau", "de", "deasupra", "deci", "decit", "degraba", "deja", "deoarece", "departe", "desi", "despre", "deși", "din", "dinaintea", "dintr", "dintr-", "dintre", "doar", "doi", "doilea", "două", "drept",
                "dupa", "după", "dă", "e", "ea", "ei", "el", "ele", "era", "eram", "este", "eu", "exact", "ești", "f", "face", "fara", "fata", "fel", "fi", "fie", "fiecare", "fii", "fim", "fiu", "fiți", "foarte", "fost", "frumos", "fără",
                "g", "geaba", "grație", "h", "halbă", "i", "ia", "iar", "ieri", "ii", "îi", "il", "îl", "imi", "îmi", "împotriva", "in", "în", "inainte", "înainte", "inaintea", "înaintea", "inapoi", "inca", "încă", "încât", "încît", "incotro", "încotro", "insa", "însă", "intr",
                "intre", "între", "intrucat", "întrucât", "întrucît", "isi", "își", "iti", "îți", "j", "k", "l", "la", "le", "li", "lor", "lui", "lângă", "lîngă", "m", "ma", "mai", "mare", "mea", "mei", "mele", "mereu", "meu", "mi", "mie", "mine",
                "mod", "mult", "multa", "multă", "multe", "multi", "mulți", "mulțumesc", "mâine", "mîine", "mă", "n", "ne", "nevoie", "ni", "nici", "niciodata", "nicăieri", "nimeni", "nimeri", "nimic", "niste", "niște", "noastre", "noastră", "noi", "noroc", "nostri", "nostru", "nou", "noua", "nouă",
                "noștri", "nu", "numai", "o", "opt", "or", "ori", "oricare", "orice", "oricine", "oricum", "oricand", "oricând", "oricat", "oricât", "oricînd", "oricît", "oriunde", "p", "pai", "parca", "parcă", "patra", "patru", "patrulea", "pe", "pentru", "peste", "pic", "pina", "plus",
                "poate", "pot", "prea", "prima", "primul", "prin", "printr-", "putini", "puțini", "puțin", "puțina", "puțină", "până", "pînă", "r", "rog", "s", "sa", "sa-mi", "să-mi", "sa-ti", "să-ți", "sai", "sale", "sapte", "șapte", "sase", "șase", "sau", "se", "si",
                "și", "sint", "sintem", "spate", "spre", "stiu", "știu", "sub", "sunt", "suntem", "sunteți", "sus", "sută", "sînt", "sîntem", "sînteți", "să", "săi", "său", "t", "ta", "tale", "te", "ti", "ți", "tie", "ție", "timp", "tine", "toata", "toate",
                "toată", "tocmai", "tot", "toti", "totul", "totusi", "totuși", "toți", "trei", "treia", "treilea", "tu", "tuturor", "tăi", "tău", "u", "ul", "ului", "un", "una", "unde", "undeva", "unei", "uneia", "unele", "uneori", "unii", "unor", "unora", "unu", "unui",
                "unuia", "unul", "v", "va", "vi", "voastre", "voastră", "voi", "vom", "vor", "vostru", "vouă", "vostri", "voștri", "vreme", "vreo", "vreun", "vă", "x", "z", "zece", "zero", "zi", "zice")
                , true);
        return stops;
    }


    private static String ConvertToUTF8(String input) {
        byte[] originalBytes = input.getBytes(StandardCharsets.UTF_8);
        String originalString = new String(originalBytes, StandardCharsets.UTF_8);

        if (originalString.equals(input)) {
            return input;
        }

        return new String(input.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }


    private class DiacriticRemovalFilter extends TokenFilter {

        private final CharTermAttribute charTermAttribute = addAttribute(CharTermAttribute.class);

        protected DiacriticRemovalFilter(TokenStream input) {
            super(input);
        }

        @Override
        public boolean incrementToken() throws IOException {
            if (input.incrementToken()) {
                // Get the current token
                String originalToken = charTermAttribute.toString();

                // Remove diacritics using transliteration
                String tokenWithoutDiacritics = transliterator.transliterate(originalToken);

                // Update the token in the attribute
                charTermAttribute.setEmpty();
                charTermAttribute.append(tokenWithoutDiacritics);

                return true;
            } else {
                return false;
            }
        }

    }

}