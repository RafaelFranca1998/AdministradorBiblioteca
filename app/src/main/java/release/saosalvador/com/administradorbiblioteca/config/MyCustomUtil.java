package release.saosalvador.com.administradorbiblioteca.config;

import android.util.Base64;

import java.text.Normalizer;

/**
 * metodos uteis para o funcionamento do programa.
 *
 */
public class MyCustomUtil {
    /**
     * Codifica um texto.
     * @param texto
     * @return
     */
    public static String codeBase64(String texto){
        String code = Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("([\\n\\r])","");
        code = code.replace("=","");
        return code;
    }

    /**
     * Decodifica um texto.
     * @param text
     * @return
     */
    public static String decodeBase64(String text){
        return new String( Base64.decode(text, Base64.DEFAULT));
    }

    /**
     * Remove a url de um texto especifico.
     * @param textoUrl
     * @return
     */
    public static String removeUrl(String textoUrl){
        return textoUrl.replaceAll("https://ecossocial-2c0dc.firebaseio.com/events/","");
    }

    /**
     * remove espaços e insere traços.
     * @param textoUrl
     * @return
     */
    public static String removeSpaces(String textoUrl){
        String text = textoUrl;
        text = text.replaceAll("\\s+","-");
        text = text.replaceAll("_","-");
        return text;
    }

    /**
     * Remove traços e insere espaços.
     * @param text
     * @return
     */
    public static String removeLines(String text){
        String toRemove = text;
        toRemove = toRemove.replaceAll("-"," ");
        toRemove = toRemove.replaceAll("_"," ");
        toRemove = toRemove.replaceAll("\\s+"," ");
        return toRemove;
    }

    /**
     * Remove caracteres especiais.
     * @param src
     * @return
     */
    public static String unaccent(String src) {
        return Normalizer
                .normalize(src, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }
}
