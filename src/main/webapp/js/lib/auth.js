// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Array of functions to execute after signing in in addition to onSignIn
 * @type {!Array<function>}
 */
const postSignInHandlers = [];

/**
 * Gets user information and populates the topbar with their image and name
 * @param {GoogleUser} googleUser the currently logged in user
 */
function onSignIn(googleUser) {
  const profile = googleUser.getBasicProfile();
  // Add user name and photo to page.
  const userInfo = document.getElementById('user-info');
  const userName = document.createElement('span');
  userName.setAttribute('class', 'mr-2 d-none d-lg-inline text-gray-600 small');
  userName.innerText = profile.getName();
  const userPhoto = document.createElement('img');
  userPhoto.setAttribute('class', 'img-profile rounded-circle profile-image');
  userPhoto.setAttribute('src', profile.getImageUrl());
  userInfo.innerText = ''
  userInfo.appendChild(userName);
  userInfo.appendChild(userPhoto);
  // Make dropdown to sign out visible.
  document.getElementById('user-dropdown').hidden = false;
  // Execute each postSignInHandler
  postSignInHandlers.forEach(func => func(googleUser));
}

/**
 * Signs out user and refreshes the page
 */
function signOut() {
  const auth2 = gapi.auth2.getAuthInstance();
  auth2.signOut().then(() => {
    location.reload();
  });
}

function registerPostSignInHandler(handler) {
  postSignInHandlers.push(handler);
}

/**
 * Set window onSignIn so the function is executed on login.
 * Exported so that the function can be used on all pages of site.
 */
window.onSignIn = onSignIn;

export const Auth = {
  onSignIn: onSignIn,
  signOut: signOut,
  registerPostSignInHandler: registerPostSignInHandler
};
