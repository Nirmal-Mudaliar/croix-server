package com.nirmal.routes

import com.nirmal.data.request.CreateCommentRequest
import com.nirmal.data.request.DeleteCommentRequest
import com.nirmal.data.response.BasicApiResponse
import com.nirmal.service.CommentService
import com.nirmal.service.LikeService
import com.nirmal.service.UserService
import com.nirmal.util.ApiResponseMessages
import com.nirmal.util.QueryParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createComment(
    commentService: CommentService,
    userService: UserService,
) {
    authenticate {
        post("/api/comment/create") {
            val request = call.receiveOrNull<CreateCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            ifEmailBelongsToUser(
                userId = request.userId,
                validateEmail = userService::doesEmailBelongToUserId
            ) {
                when(commentService.createComment(request)) {
                    is CommentService.ValidationEvent.ErrorEmptyField-> {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = false,
                                message = ApiResponseMessages.FIELDS_BLANK
                            )
                        )
                    }
                    is CommentService.ValidationEvent.ErrorCommentTooLong -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = false,
                                message = ApiResponseMessages.COMMENT_TOO_LONG
                            )
                        )
                    }
                    is CommentService.ValidationEvent.Success -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = true
                            )
                        )
                    }
                }
            }
        }
    }

}

fun Route.getCommentForPost(
    commentService: CommentService,
) {
    authenticate {
        get("/api/comment/get") {
            val postId = call.parameters[QueryParams.PARAM_POST_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val comments = commentService.getCommentsForPost(postId)
            call.respond(
                HttpStatusCode.OK,
                comments
            )
        }
    }
}

fun Route.deleteComment(
    commentService: CommentService,
    likeService: LikeService,
    userService: UserService
) {
    authenticate {
        delete("/api/comment/delete") {
            val request = call.receiveOrNull<DeleteCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            ifEmailBelongsToUser(
                userId = request.userId,
                validateEmail = userService::doesEmailBelongToUserId
            ) {
                val deleted = commentService.deleteComment(request.commentId)
                if (deleted) {
                    likeService.deleteLikesForParent(request.commentId)
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(successful = true)
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        BasicApiResponse(successful = false)
                    )
                }
            }
        }
    }
}












