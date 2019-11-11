package com.example.medjournal.models

/**
 *  A class that contains basic user info
 *  @param uid user id
 *  @param username user name
 *  @param profileImageUrl url of user profile picture in Firebase Storage
 */
class User(val uid: String, val username: String, val profileImageUrl: String) {
    constructor() : this("", "", "")
}