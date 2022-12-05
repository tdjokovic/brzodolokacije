package com.example.frontend.presentation.post

import Constants.Companion.POST_ID
import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.common.Resource
import com.example.frontend.domain.DataStoreManager
import com.example.frontend.domain.model.Comment
import com.example.frontend.domain.use_case.get_post.GetPostUseCase
import com.example.frontend.domain.use_case.post_comments.GetFirstCommentsUseCase
import com.example.frontend.presentation.post.components.PostCommentsState
import com.example.frontend.presentation.post.components.PostState
import com.example.frontend.presentation.posts.components.PostsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val getPostUseCase : GetPostUseCase,
    private val getFirstCommentsUseCase: GetFirstCommentsUseCase,
    savedStateHandle: SavedStateHandle,
    application: Application
) : ViewModel() {

    private val _state = mutableStateOf(PostState())
    val state : State<PostState> = _state
    val context = application.baseContext

    private val _stateGetComments = mutableStateOf(PostCommentsState())
    val stateGetComments : State<PostCommentsState> = _stateGetComments

    var access_token = "";
    var refresh_token = "";

    init {
        GlobalScope.launch(Dispatchers.Main){
            access_token = DataStoreManager.getStringValue(context, "access_token");
            refresh_token = DataStoreManager.getStringValue(context, "refresh_token");

            getPost(savedStateHandle);
        }
    }

    fun getPost(savedStateHandle : SavedStateHandle){
        var thisPostId = 0L;
        savedStateHandle.get<Long>(POST_ID)?.let { postId ->
            thisPostId = postId;
        }

        getPostUseCase("Bearer "+access_token, thisPostId).onEach { result ->
            when(result){
                is Resource.Success -> {
                    _state.value = PostState(post = result.data)
                    println(result.data)
                    getFirstCommentsByPostId(result.data!!.postId)
                }
                is Resource.Error -> {
                    _state.value = PostState(error = result.message ?:
                    "An unexpected error occured")
                }
                is Resource.Loading -> {
                    _state.value = PostState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getFirstCommentsByPostId(postId: Long)
    {
        GlobalScope.launch(Dispatchers.Main){

            var access_token =  DataStoreManager.getStringValue(context, "access_token");
            var refresh_token = DataStoreManager.getStringValue(context, "refresh_token");

            getFirstCommentsUseCase("Bearer "+access_token, postId).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        _stateGetComments.value = PostCommentsState(comments = result.data)
                        println(result.data)
                    }
                    is Resource.Error -> {
                        _stateGetComments.value = PostCommentsState(error = result.message ?:
                        "An unexpected error occured")
                    }
                    is Resource.Loading -> {
                        _stateGetComments.value = PostCommentsState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}