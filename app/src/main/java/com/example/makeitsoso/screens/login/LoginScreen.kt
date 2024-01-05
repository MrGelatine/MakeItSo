/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.makeitsoso.screens.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.makeitsoso.R
import com.example.makeitsoso.R.string as AppText
import com.example.makeitsoso.common.composable.*
import com.example.makeitsoso.common.ext.basicButton
import com.example.makeitsoso.common.ext.fieldModifier
import com.example.makeitsoso.common.ext.textButton
import com.example.makeitsoso.theme.MakeItSoTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
  openAndPopUp: (String, String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LoginViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState

  val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestIdToken(LocalContext.current.resources.getString(R.string.default_web_client_id))
    .requestEmail()
    .build()
  val mGoogleSignInClient = GoogleSignIn.getClient(LocalContext.current, gso)
  val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    Log.d("myTag", "This is my message")
    val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
    try {
      // Google Sign In was successful, authenticate with Firebase
      val account = task.getResult(ApiException::class.java)
      viewModel.onGoogleSignInClick(openAndPopUp, account.idToken!!)
    } catch (e: ApiException) {
      // Google Sign In failed, update UI appropriately
    }
  }

  BasicToolbar(AppText.login_details)

  Column(
    modifier = modifier.fillMaxWidth().fillMaxHeight().verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    EmailField(uiState.email, viewModel::onEmailChange, Modifier.fieldModifier())
    PasswordField(uiState.password, viewModel::onPasswordChange, Modifier.fieldModifier())

    BasicButton(AppText.sign_in, Modifier.basicButton()) { viewModel.onSignInClick(openAndPopUp) }
    Button(
      onClick = {
        val signInIntent = mGoogleSignInClient.signInIntent
        launcher.launch(signInIntent)
      },

      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      //.height(100.dp)
      //.padding(start = 16.dp, end = 16.dp),
      shape = RoundedCornerShape(6.dp),
      colors = ButtonDefaults.buttonColors(
        backgroundColor = Color.Black,
        contentColor = Color.White
      )
    ) {
      Text(text = "Sign in with Google", modifier = Modifier.padding(6.dp))
    }
    BasicTextButton(AppText.forgot_password, Modifier.textButton()) {
      viewModel.onForgotPasswordClick()
    }
  }
}
