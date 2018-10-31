/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package release.saosalvador.com.administradorbiblioteca.config.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

import release.saosalvador.com.administradorbiblioteca.model.Livro;

@Entity(tableName = "my_table")
public class MyData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "ListData")
    @TypeConverters(DataTypeConverter.class)
    private List<Livro> mList = null;
    @Embedded
    private Livro mUser;
    public MyData() {
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Livro> getList() {
        return mList;
    }

    public void setList(List<Livro> list) {
        this.mList = list;
    }

    public Livro getUser() {
        return mUser;
    }

    public void setUser(Livro user) {
        this.mUser = user;
    }
}
