package com.example.finalproject.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.crocodic.core.data.CoreDao

@Dao
abstract class UserDao : CoreDao<UserEntity>{
    @Query("SELECT * FROM UserEntity WHERE idRoom = 1")
    abstract fun getUser(): LiveData<UserEntity?>

    @Query("DELETE FROM UserEntity")
    abstract suspend fun deleteAll()

    @Query("SELECT EXISTS (SELECT 1 FROM UserEntity WHERE idRoom = 1)")
    abstract fun isLogin(): Boolean

    @Query("SELECT id From UserEntity WHERE idRoom = 1")
    abstract fun getUserId(): Int

    //coba coba
    @Insert
    abstract suspend fun insertUser(userEntity: UserEntity)

    @Update
    abstract fun updateUser(userEntity: UserEntity)

    @Query("SELECT * FROM UserEntity WHERE name||school LIKE :searchQ")
    abstract fun searchFriend(searchQ: String): List<UserEntity>

}