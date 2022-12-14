package com.nirmal.plugins

import com.nirmal.routes.*
import com.nirmal.service.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val userService: UserService by inject()

    val followService: FollowService by inject()

    val postService: PostService by inject()

    val likeService: LikeService by inject()

    val commentService: CommentService by inject()

    val activityService: ActivityService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()


    routing {
        // User routes
        createUser(userService)
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )
        searchUser(userService)
        getUserProfile(userService)
        getPostsForProfile(postService)
        updateUserProfile(userService)

        // Following
        followUser(followService, activityService)
        unfollowUser(followService)

        // Posts
        createPost(postService)
        getPostByFollows(postService)
        deletePost(postService, commentService, likeService)

        // likes
        likeParent(likeService = likeService, activityService = activityService)
        unLikeParent(likeService = likeService)
        getLikesForParent(likeService)

        // Comments
        createComment(commentService, activityService)
        deleteComment(commentService, likeService)
        getCommentForPost(commentService)

        // Activities
        getActivities(activityService)

        static {
            resources("static")
        }

    }
}
