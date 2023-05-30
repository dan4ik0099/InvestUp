package com.example.investup.other

import com.example.investup.retrofit.dataClass.*
import com.example.investup.retrofit.dataClass.Tag
import com.example.investup.retrofit.requestModel.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @POST("auth/login")
    suspend fun requestAuth(@Body userAuthRequest: UserAuthRequest): Response<UserToken>

    @POST("auth/registration")
    suspend fun requestRegister(@Body userRegisterRequest: UserRegisterRequest): Response<UserToken>

    @Headers("Content-Type: application/json")
    @GET("users/me")
    suspend fun requestInfoMe(@Header("Authorization") accessToken: String): Response<User>

    @Headers("Content-Type: application/json")
    @GET("/users/{id}")
    suspend fun requestUserInfo(
        @Path("id") id: String,
        @Header("Authorization") accessToken: String
    ): Response<User>


    @Headers("Content-Type: application/json")
    @GET("/posts")
    suspend fun requestAllPosts(@Header("Authorization") accessToken: String): Response<ArrayList<Post>>

    @Headers("Content-Type: application/json")
    @GET("/posts/favorites")
    suspend fun requestFavoritePosts(@Header("Authorization") accessToken: String): Response<ArrayList<Post>>

    @Headers("Content-Type: application/json")
    @GET("/posts/me")
    suspend fun requestMyPosts(@Header("Authorization") accessToken: String): Response<ArrayList<Post>>

    @Headers("Content-Type: application/json")
    @GET("/posts/{id}")
    suspend fun requestPostById(
        @Path("id") id: String,
        @Header("Authorization") accessToken: String
    ): Response<Post>


    @Headers("Content-Type: application/json")
    @PUT("/posts/{id}")
    suspend fun changePostById(
        @Path("id") id: String,
        @Body postEditRequest: PostEditRequest,
        @Header("Authorization") accessToken: String

    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @DELETE("/posts/{id}")
    suspend fun deletePostById(
        @Path("id") id: String,
        @Header("Authorization") accessToken: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("/posts/user/{id}")
    suspend fun requestPostsByUserId(
        @Path("id") id: String,
        @Header("Authorization") accessToken: String
    ): Response<ArrayList<Post>>

    @Headers("Content-Type: application/json")
    @POST("/dialogs/create")
    suspend fun createDialog(
        @Header("Authorization") accessToken: String,
        @Body dialogCreateRequest: DialogCreateRequest
    ): Response<DialogInfo>



    @Headers("Content-Type: application/json")
    @POST("/complaints/user")
    suspend fun createReportUser(
        @Header("Authorization") accessToken: String,
        @Body userReportRequest: UserReportRequest
    ): Response<ResponseBody>
    @Headers("Content-Type: application/json")
    @POST("/complaints/post")
    suspend fun createReportPost(
        @Header("Authorization") accessToken: String,
        @Body postReportRequest: PostReportRequest
    ): Response<ResponseBody>



    @Headers("Content-Type: application/json")
    @GET("/dialogs")
    suspend fun requestAllDialogs(
        @Header("Authorization") accessToken: String
    ): Response<ArrayList<Dialog>>


    @Headers("Content-Type: application/json")
    @GET("/dialogs/{id}")
    suspend fun requestDialogById(
        @Path("id") id: String,
        @Header("Authorization") accessToken: String
    ): Response<DialogInfo>



    @Headers("Content-Type: application/json")
    @DELETE("/posts/comment/{id}")
    suspend fun deleteCommentById(
        @Path("id") id: String,
        @Header("Authorization") accessToken: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("/posts/favorite/{id}")
    suspend fun addToFavoritePostById(
        @Path("id") id: String,
        @Header("Authorization") accessToken: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("/posts/unfavorite/{id}")
    suspend fun deleteFromFavoritePostById(
        @Path("id") id: String,
        @Header("Authorization") accessToken: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("tags")
    suspend fun requestTags(@Header("Authorization") accessToken: String): Response<ArrayList<Tag>>

    @Headers("Content-Type: application/json")
    @POST("/users/change/info")
    suspend fun requestChangeNameAndLastName(
        @Header("Authorization") accessToken: String,
        @Body userChangeNameRequest: UserChangeNameRequest
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("/posts/favorites")
    suspend fun requestFavoritePostsBySearch(
        @Query("search", encoded = true) search: String?,
        @Query("tags[]", encoded = true) tags: ArrayList<String>?,
        @Query("sort") sort: String?,
        @Query("sortValue") sortValue: String?,
        @Header("Authorization") accessToken: String
    ): Response<ArrayList<Post>>


    @Headers("Content-Type: application/json")
    @GET("/posts")
    suspend fun requestPostsBySearch(
        @Query("search", encoded = true) search: String?,
        @Query("tags[]", encoded = true) tags: ArrayList<String>?,
        @Query("sort") sort: String?,
        @Query("sortValue") sortValue: String?,
        @Header("Authorization") accessToken: String
    ): Response<ArrayList<Post>>

    @Headers("Content-Type: application/json")
    @GET("/posts/me")
    suspend fun requestMyPostsBySearch(
        @Query("search", encoded = true) search: String?,
        @Query("tags[]", encoded = true) tags: ArrayList<String>?,
        @Query("sort") sort: String?,
        @Query("sortValue") sortValue: String?,
        @Header("Authorization") accessToken: String
    ): Response<ArrayList<Post>>


    @Headers("Content-Type: application/json")
    @GET("/posts/user/{id}")
    suspend fun requestUserPostsBySearch(
        @Path("id") id: String,
        @Query("search", encoded = true) search: String?,
        @Query("tags[]", encoded = true) tags: ArrayList<String>?,
        @Query("sort") sort: String?,
        @Query("sortValue") sortValue: String?,
        @Header("Authorization") accessToken: String
    ): Response<ArrayList<Post>>


    @POST("/users/change/password")
    suspend fun requestChangePassword(
        @Header("Authorization") accessToken: String,
        @Body userChangeNameRequest: UserChangePasswordRequest
    ): Response<ResponseBody>

    @POST("/posts/comment")
    suspend fun uploadComment(
        @Header("Authorization") accessToken: String,
        @Body uploadCommentRequest: UploadCommentRequest
    ): Response<Comment>

    @Headers("Content-Type: application/json")
    @GET("/users/sessions")
    suspend fun session(@Header("Authorization") accessToken: String
    ): Response<ResponseBody>
    @Multipart
    @POST("/users/avatar")
    suspend fun uploadFile(
        @Header("Authorization") accessToken: String,
        @Part photo: MultipartBody.Part
    ): Response<ResponseBody>

    @Multipart
    @POST("/posts/create")
    suspend fun uploadPost(
        @Header("Authorization") accessToken: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("shortDescription") shortDescription: RequestBody,
        @Part("tags") tags: RequestBody,
        @Part video: MultipartBody.Part?
    ): Response<ResponseBody>






}

