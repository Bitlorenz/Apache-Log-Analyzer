package unimore.t4.Heimdall.PreProcessing;

import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import io.krakens.grok.api.Match;

import java.io.IOException;
import java.util.Map;

/**
 *Classe che utilizza la libreria grok per fare il parsing della linea di log
 *Tiene il riferimento del file writer per scrivere il log su file txt
 */
public class LogParser {

    /**
     * oggetto che si occupa della funzione di parsing*/
    private Grok grok;
    private LogWriter logWriter;
    private JsonWriter jsonWriter;
    /**
     * stringa che indica il patern usato per il parsing dall'oggetto grok */
    private static final String pattern = "%{COMBINEDAPACHELOG}";
    /**
     * Pattern utilizzato per parsare gli errori
     */
    private static final String patter_err= "\\[(?<timestamp>%{DAY:day} %{MONTH:month} %{MONTHDAY} %{TIME} %{YEAR})\\]\\s+\\[:%{LOGLEVEL:loglevel}\\]\\s+\\[pid %{NUMBER:pid}]\\s+\\[client %{IP:clientip}:%{NUMBER:port}\\]\\s+\\[client %{IP:clientip2}.*?\\]\\s+ModSecurity:\\s+%{GREEDYDATA:error}\\s+\\[file\\s%{QS:path_file}\\]?(?:\\s+\\[line %{QS:line}])?(?:\\s+\\[id %{QS:id}\\])?(?:\\s+\\[msg %{QS:message}\\])?(?:\\s+\\[data %{QS:data}\\])?(?:\\s+\\[severity %{QS:severity}\\])?(?:\\s+\\[ver %{QS:ver}\\])?(?:\\s+\\[tag %{QS:tag}\\])?(?:\\s+\\[tag %{QS:tag2}\\])?(?:\\s+\\[tag %{QS:tag3}\\])?(?:\\s+\\[tag %{QS:tag4}\\])?(?:\\s+\\[tag %{QS:tag5}\\])?.*?\\[hostname %{QS:hostname}\\]\\s+\\[uri %{QS:uri}\\]\\s+\\[unique_id %{QS:unique_id}\\](?:%{GREEDYDATA:referer})";
    /**
     * Costruttore della classe che inzializza gli oggetti di libreria
     */
    public LogParser(String dirDstLogName, String dirDstJsonName){
        if(dirDstLogName.equals("File_output") && dirDstJsonName.equals("File_Json")) {
            GrokCompiler grokCompiler = GrokCompiler.newInstance();
            grokCompiler.registerDefaultPatterns();
            grok = grokCompiler.compile(pattern);
            jsonWriter = new JsonWriter(dirDstJsonName);
            logWriter = new LogWriter(dirDstLogName);
        }

        if(dirDstLogName.equals("File_output_err") && dirDstJsonName.equals("File_Json_err")){
            GrokCompiler grokCompiler= GrokCompiler.newInstance();
            grokCompiler.registerDefaultPatterns();
            grok=grokCompiler.compile(patter_err);
            jsonWriter= new JsonWriter(dirDstJsonName);
            logWriter= new LogWriter(dirDstLogName);
        }

    }

    /**
     * Metodo che prende una stringa ed applica su essa la logica di parsing
     * Dopodichè chiama il metodo writeLog tramite logWriter per scriverlo su
     * file
     * @param logLine linea di log grezza
     * @param name nome del file di log sorgente
     */
    public void matchLogMakeMap(String logLine, String name)
            throws IOException {
        Match gm = grok.match(logLine);
        Map<String, Object> captureMap = gm.capture();
        logWriter.writeLog(captureMap, name);
        jsonWriter.writeOnJson(captureMap, name);
    }
}
