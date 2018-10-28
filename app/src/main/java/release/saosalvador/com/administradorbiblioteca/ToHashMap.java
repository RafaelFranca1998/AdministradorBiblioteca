package release.saosalvador.com.administradorbiblioteca;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import release.saosalvador.com.administradorbiblioteca.model.Category;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class ToHashMap {

    public ToHashMap() {
    }

    public static HashMap<String,String> categoryToHashMap(Category object){
        HashMap<String,String> map =  new HashMap<>();
        map.put("categoryName", object.getCategoryName());
        map.put("imgDownload", object.getImgDownload());
        return map;
    }
    public static Category categoryToHashMap(HashMap<String,String> m){
        Category object =  new Category();
        object.setCategoryName(m.get("categoryName"));
        object.setImgDownload( m.get("imgDownload"));
        return object;
    }
    public static HashMap<String,String> livroToHashMap(Livro object){
        HashMap<String,String> map =  new HashMap<>();
        try {
            map.put("nome", object.getNome());
            map.put("idLivro", object.getIdLivro());
            map.put("editora", object.getEditora());
        //  map.put("edicao", object.getEdicao());
            map.put("ano", object.getAno());
            map.put("autor", object.getAutor());
            map.put("categoria", object.getCategoria());
            map.put("area", object.getArea());
            map.put("linkDownload", object.getLinkDownload());
            map.put("imgDownload", object.getImgDownload());
        //  map.put("endereçoLocal", object.getEndereçoLocal());
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }
    public static Livro hashMapToLivro(Map<String, Object> map){
        Livro  object =  new Livro();
        try {
            object.setNome(Objects.requireNonNull(map.get("nome")).toString());
            object.setIdLivro(Objects.requireNonNull(map.get("idLivro")).toString());
            object.setEditora(Objects.requireNonNull(map.get("editora")).toString());
            //object.setEdicao(map.get("edicao").toString());
            object.setAno(Objects.requireNonNull(map.get("ano")).toString());
            object.setAutor(Objects.requireNonNull(map.get("autor")).toString());
            object.setCategoria(Objects.requireNonNull(map.get("categoria")).toString());
            object.setArea(Objects.requireNonNull(map.get("area")).toString());
            object.setLinkDownload(Objects.requireNonNull(map.get("linkDownload")).toString());
            object.setImgDownload(Objects.requireNonNull(map.get("imgDownload")).toString());
           // object.setEndereçoLocal(map.get("endereçoLocal").toString());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return object;
    }
}
