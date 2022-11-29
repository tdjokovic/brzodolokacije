package com.example.frontend.presentation.profile_settings

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.frontend.common.Resource
import com.example.frontend.common.navigation.Screen
import com.example.frontend.domain.DataStoreManager
import com.example.frontend.domain.use_case.get_user.GetMyProfilePictureUseCase
import com.example.frontend.domain.use_case.get_user.GetUserInfoUseCase
import com.example.frontend.domain.use_case.profile_settings.ChangeEmailUseCase
import com.example.frontend.domain.use_case.profile_settings.ChangePasswordUseCase
import com.example.frontend.domain.use_case.profile_settings.ChangeProfilePictureUseCase
import com.example.frontend.presentation.profile_settings.components.ChangeProfilePictureState
import com.example.frontend.presentation.profile_settings.components.ProfilePictureState
import com.example.frontend.presentation.profile_settings.components.UserInfoChangeState
import com.example.frontend.presentation.profile_settings.components.ProfileSettingsUserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getMyProfilePictureUseCase: GetMyProfilePictureUseCase,
    private val changeEmailUseCase: ChangeEmailUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val changeProfilePictureUseCase: ChangeProfilePictureUseCase,
    savedStateHandle: SavedStateHandle,
    application: Application
): ViewModel() {

    private val _state = mutableStateOf(ProfileSettingsUserState())
    val state : State<ProfileSettingsUserState> = _state
    val context = application.baseContext

    private val _stateGetMyProfilePicture = mutableStateOf(ProfilePictureState())
    val stateGetMyProfilePicture : State<ProfilePictureState> = _stateGetMyProfilePicture

    private val _stateEmailChange = mutableStateOf(UserInfoChangeState())
    val stateEmailChange : State<UserInfoChangeState> = _stateEmailChange
    var currentEmail: String = "";

    private val _stateChangeProfilePicture = mutableStateOf(ChangeProfilePictureState())
    val stateChangeProfilePicture : State<ChangeProfilePictureState> = _stateChangeProfilePicture
    var changePictureEnabled: Boolean = false;
    var currentPicture: String = "";


    /*private val _statePasswordChange = mutableStateOf(ProfileSettingsUserState())
    val statePasswordChange : State<ProfileSettingsUserState> = _statePasswordChange
    private val _stateProfilePictureChange = mutableStateOf(ProfileSettingsUserState())
    val stateProfilePictureChange : State<ProfileSettingsUserState> = _stateProfilePictureChange*/

    var access_token  = "";
    var refresh_token = "";
    var username = "";
    var loginUserId = 0L;

    init {
        GlobalScope.launch(Dispatchers.Main){
            access_token =  DataStoreManager.getStringValue(context, "access_token");
            refresh_token = DataStoreManager.getStringValue(context, "refresh_token");
            loginUserId = DataStoreManager.getLongValue(context, "userId");

            getMyData();
            getMyProfilePicture()
        }
    }

    fun getMyData()
    {
        GlobalScope.launch(Dispatchers.Main){

            getUserInfoUseCase("Bearer "+ access_token).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        _state.value = ProfileSettingsUserState(user = result.data)
                        println("DAJ PODATKE" + result.data)
                        currentEmail = result.data!!.email
                    }
                    is Resource.Error -> {
                        _state.value = ProfileSettingsUserState(error = result.message ?:"An unexpected error occured")
                    }
                    is Resource.Loading -> {
                        _state.value = ProfileSettingsUserState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getMyProfilePicture()
    {
        GlobalScope.launch(Dispatchers.Main){

            getMyProfilePictureUseCase("Bearer "+ access_token).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        println("****////////********GETMYPROFILEPICTURE SUCCESS ")
                        _stateGetMyProfilePicture.value = ProfilePictureState(picture = result.data!!)
                        currentPicture = result.data!!
                    }
                    is Resource.Error -> {
                        println("GETMYPROFILEPICTURE ERROR" + result.message)
                        _stateGetMyProfilePicture.value = ProfilePictureState(error = result.message ?:"An unexpected error occured")
                    }
                    is Resource.Loading -> {
                        _stateGetMyProfilePicture.value = ProfilePictureState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun changeProfilePicture(navController: NavController)
    {
        GlobalScope.launch(Dispatchers.IO){
            var access_token = DataStoreManager.getStringValue(context, "access_token")
            var refresh_token = DataStoreManager.getStringValue(context, "refresh_token")
            val path = context.getExternalFilesDir(null)!!.absolutePath
            val tempFile = File(path, "tempFileName.jpg")
            val fOut = FileOutputStream(tempFile)
            stateChangeProfilePicture.value.picture!!.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
            val file = MultipartBody.Part.createFormData(
                "profile_image", tempFile.name,
                RequestBody.create(MediaType.parse("image/*"), tempFile)
            )

            changeProfilePictureUseCase("Bearer "+ access_token, file).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        println("****////////********CHANGEMYPROFILEPICTURE SUCCESS ")
                        tempFile.delete()
                        navController.navigate(Screen.ProfileSettingsScreen.route)
                    }
                    is Resource.Error -> {
                        println("CHANGEMYPROFILEPICTURE ERROR" + result.message)
                        _stateChangeProfilePicture.value = ChangeProfilePictureState(error = result.message ?:"An unexpected error occured")
                        tempFile.delete()
                    }
                    is Resource.Loading -> {
                        _stateChangeProfilePicture.value = ChangeProfilePictureState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun parsePhoto(photo: Bitmap) {
        _stateChangeProfilePicture.value = ChangeProfilePictureState(picture = photo)
        changePictureEnabled = true;
    }

    fun changeEmail(navController: NavController, newEmail: String)
    {
        GlobalScope.launch(Dispatchers.Main){
            changeEmailUseCase("Bearer "+ access_token, newEmail).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        _stateEmailChange.value = UserInfoChangeState(message = result.data ?: "")
                        navController.navigate(Screen.ProfileSettingsScreen.route)
                    }
                    is Resource.Error -> {
                        _stateEmailChange.value = UserInfoChangeState(error = result.message ?:"An unexpected error occured")
                    }
                    is Resource.Loading -> {
                        _stateEmailChange.value = UserInfoChangeState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}