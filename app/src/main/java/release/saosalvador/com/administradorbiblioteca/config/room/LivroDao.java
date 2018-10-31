/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.config.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import release.saosalvador.com.administradorbiblioteca.model.Livro;

@Dao
public interface LivroDao {

    @Query("SELECT * FROM livro")
    List<Livro> getAll();

    @Query("SELECT * FROM livro WHERE idLivro IN (:livroIds)")
    List<Livro> loadAllByIds(int[] livroIds);

    @Query("SELECT * FROM livro WHERE categoria LIKE :category")
    Livro findByCategory(String category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Livro... livros);

    @Delete
    void delete(Livro livro);
}