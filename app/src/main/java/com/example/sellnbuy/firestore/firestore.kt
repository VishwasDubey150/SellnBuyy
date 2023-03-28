package com.example.sellnbuy.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.load.ImageHeaderParser.ImageType
import com.example.sellnbuy.*
import com.example.sellnbuy.model.Product
import com.example.sellnbuy.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class firestore:baseActivity() {
    private val mfirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: sign_up, userInfo: User) {
        mfirestore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hidePB()
                Log.e(activity.javaClass.simpleName, "Error while registering the user.", e)
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }


    fun getUserDetails(activity: Activity) {
        mfirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)!!

                when (activity) {
                    is login -> {
                        activity.userLoggedInSuccess(user)
                    }
                }

                val sharedPreferences = activity.getSharedPreferences(
                    Constants.Sellnbuy_pref,
                    Context.MODE_PRIVATE)

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.Loggedin_un,
                    "${user.Username}"
                )
                editor.apply()

                when (activity) {
                    is login -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }

            }
            .addOnFailureListener { e ->

                when (activity) {
                    is login -> {
                        activity.hidePB()
                    }
                    is SettingActivity -> {
                        activity.hidePB()
                    }
                }
            }
    }
    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {

        mfirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {

                // Notify the success result.
                when (activity) {
                    is profile -> {
                        activity.userProfileUpdateSuccess()
                    }

                    is SettingActivity ->{

                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is profile -> {
                        activity.hidePB()
                        Toast.makeText(this, "Something went worng", Toast.LENGTH_SHORT).show()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.", e
                )
            }
    }
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

            //getting the storage reference
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                imageType + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(
                    activity,
                    imageFileURI
                )
            )

            //adding the file to reference
            sRef.putFile(imageFileURI!!)
                .addOnSuccessListener { taskSnapshot ->
                    // The image upload is success
                    Log.e(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())

                            // Here call a function of base activity for transferring the result to it.
                            when (activity) {
                                is profile -> {
                                    activity.imageUploadSuccess(uri.toString())
                                }

                                is AddProductActivity->{
                                    activity.imageUploadSuccess(uri.toString())

                                }
                            }
                        }
                }
                .addOnFailureListener { exception ->

                    // Hide the progress dialog if there is any error. And print the error in log.
                    when (activity) {
                        is profile -> {
                            activity.hidePB()
                        }
                        is AddProductActivity ->{
                            hidePB()
                        }
                    }



                    Log.e(
                        activity.javaClass.simpleName,
                        exception.message,
                        exception
                    )
                }
        }

    fun uploadProductDetails(activity: AddProductActivity,productInfo: Product)
    {
        mfirestore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener {
                e ->
                activity.hidePB()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.", e
                )
            }
    }
}






