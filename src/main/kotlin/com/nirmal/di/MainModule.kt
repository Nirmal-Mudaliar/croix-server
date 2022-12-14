package com.nirmal.di

import com.google.gson.Gson
import com.nirmal.data.repository.activity.ActivityRepository
import com.nirmal.data.repository.activity.ActivityRepositoryImpl
import com.nirmal.data.repository.comment.CommentRepository
import com.nirmal.data.repository.comment.CommentRepositoryImpl
import com.nirmal.data.repository.follow.FollowRepository
import com.nirmal.data.repository.follow.FollowRepositoryImpl
import com.nirmal.data.repository.likes.LikeRepository
import com.nirmal.data.repository.likes.LikeRepositoryImpl
import com.nirmal.data.repository.post.PostRepository
import com.nirmal.data.repository.post.PostRepositoryImpl
import com.nirmal.data.repository.user.UserRepository
import com.nirmal.data.repository.user.UserRepositoryImpl
import com.nirmal.service.*
import com.nirmal.util.Constants
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import kotlin.math.sin


val mainModule = module {
    single {
        val client = KMongo.createClient().coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }

    // User
    single<UserRepository> {
        UserRepositoryImpl(get())
    }
    single {
        UserService(get(), get())
    }

    // Follow
    single<FollowRepository> {
        FollowRepositoryImpl(get())
    }
    single {
        FollowService(get())
    }

    // Post
    single<PostRepository> {
        PostRepositoryImpl(get())
    }
    single {
        PostService(get())
    }

    // Like
    single<LikeRepository> {
        LikeRepositoryImpl(get())
    }
    single {
        LikeService(get(), get(), get())
    }

    // Comment
    single<CommentRepository> {
        CommentRepositoryImpl(get())
    }
    single {
        CommentService(get())
    }

    // Activity
    single<ActivityRepository> {
        ActivityRepositoryImpl(get())
    }
    single {
        ActivityService(get(), get(), get())
    }

    single {
        Gson()
    }

}