/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca;

import java.util.HashMap;
import java.util.Map;

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
    public static Category categoryToHashMap(HashMap m){
        Category object =  new Category();
        HashMap<String,String > map = m;
        object.setCategoryName(map.get("categoryName"));
        object.setImgDownload(map.get("imgDownload"));
        return object;
    }
    public static HashMap<String,String> livroToHashMap(Livro object){
        HashMap<String,String> map =  new HashMap<>();
        try {
            map.put("nome", object.getNome());
            map.put("idLivro", object.getIdLivro());
            map.put("editora", object.getEditora());
            map.put("edicao", object.getEdicao());
            map.put("ano", object.getAno());
            map.put("autor", object.getAutor());
            map.put("categoria", object.getCategoria());
            map.put("area", object.getArea());
            map.put("linkDownload", object.getLinkDownload());
            map.put("imgDownload", object.getImgDownload());
            map.put("endereçoLocal", object.getEndereçoLocal());
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }
    public static Livro hashMapToLivro(Map<String, Object> hashMap){
        Livro  object =  new Livro();
        Map<String,Object> map =  hashMap;
        try {
            object.setNome(map.get("nome").toString());
            object.setIdLivro(map.get("idLivro").toString());
            object.setEditora(map.get("editora").toString());
            object.setEdicao(map.get("edicao").toString());
            object.setAno(map.get("ano").toString());
            object.setAutor(map.get("autor").toString());
            object.setCategoria(map.get("categoria").toString());
            object.setArea(map.get("area").toString());
            object.setLinkDownload(map.get("linkDownload").toString());
            object.setImgDownload(map.get("imgDownload").toString());
            object.setEndereçoLocal(map.get("endereçoLocal").toString());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return object;
    }
}
