/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.config.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import release.saosalvador.com.administradorbiblioteca.model.Livro;

@Database(entities = {Livro.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LivroDao userDao();
}
