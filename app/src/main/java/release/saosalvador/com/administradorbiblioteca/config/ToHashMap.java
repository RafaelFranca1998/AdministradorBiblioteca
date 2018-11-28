/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import release.saosalvador.com.administradorbiblioteca.model.Category;
import release.saosalvador.com.administradorbiblioteca.model.Livro;

public class ToHashMap {

    public ToHashMap() {
    }

    public static HashMap<String,String> hashmapToCategory(Category category){
        HashMap<String,String> map =  new HashMap<>();
        map.put("categoryName", category.getCategoryName());
        map.put("imgDownload", category.getImgDownload());
        return map;
    }
    public static Category hashmapToCategory(HashMap<String,String> m){
        Category object =  new Category();
        object.setCategoryName(m.get("categoryName"));
        object.setImgDownload( m.get("imgDownload"));
        return object;
    }
    public static HashMap<String,Object> livroToHashMap(Livro livro){
        HashMap<String,Object> map =  new HashMap<>();
        try {
            map.put("nome", livro.getNome());
            map.put("idLivro", livro.getIdLivro());
            map.put("editora", livro.getEditora());
            map.put("ano", livro.getAno());
            map.put("autor", livro.getAutor());
            map.put("categoria", livro.getCategoria());
            map.put("area", livro.getArea());
            map.put("linkDownload", livro.getLinkDownload());
            map.put("imgDownload", livro.getImgDownload());
            map.put("dataAdicionado", livro.getDataAdicionado());
            map.put("dataVisitado", livro.getDataVisitado());
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
            object.setAno(Objects.requireNonNull(map.get("ano")).toString());
            object.setAutor(Objects.requireNonNull(map.get("autor")).toString());
            object.setCategoria(Objects.requireNonNull(map.get("categoria")).toString());
            object.setArea(Objects.requireNonNull(map.get("area")).toString());
            object.setLinkDownload(Objects.requireNonNull(map.get("linkDownload")).toString());
            object.setImgDownload(Objects.requireNonNull(map.get("imgDownload")).toString());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return object;
    }
}
