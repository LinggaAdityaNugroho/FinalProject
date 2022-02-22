package com.example.finalproject.edit

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.tos
import com.crocodic.core.helper.DateTimeHelper
import com.example.finalproject.R
import com.example.finalproject.activity.BaseActivity
import com.example.finalproject.databinding.ActivityEditUserBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

@AndroidEntryPoint
class EditUserActivity : BaseActivity<ActivityEditUserBinding, EditViewModel>() {

    private var filePhoto: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutRes(R.layout.activity_edit_user)

        //toolbar
        setSupportActionBar(binding.toolbarEditUser)

        observe()
        setViewText()

    }

    private fun setViewText() {
        viewModel.user.observe(this) {
            binding.edNameSave.setText(it?.name)
            binding.edtDescriptionUpdate.setText(it?.description)

            // TODO: set gambar disini
            Glide
                .with(this@EditUserActivity)
                .load(it?.photo)
                .override(322, 322)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.btnUploadFoto)
        }
    }

    private fun observe() {
        viewModel.apiResponse.observe(this) {
            when (it.status) {
                ApiStatus.LOADING -> {
                    it.message?.let { msg -> loadingDialog.show(msg) }
                }
                ApiStatus.SUCCESS -> {
                    loadingDialog.dismiss()
                    finish()
                }
                ApiStatus.WRONG -> {
                    it.message?.let { msg -> loadingDialog.setResponse(msg) }
                }
                else -> {
                    loadingDialog.dismiss()
                    tos(R.string.error)
                }
            }
        }
    }

    private fun validateForm() {
        if (listOf(binding.edNameSave, binding.edtDescriptionUpdate)
                .isEmptyRequired(R.string.form_tidak_boleh_kosong)
        ) else {
            filePhoto?.let { photoBang ->
                if (filePhoto != null) {
                    viewModel.user.observe(this) {
                        viewModel.updateAdaGambare(it?.name, it?.school, it?.description, photoBang)
                    }
                } else {
                    viewModel.user.observe(this) {
                        viewModel.updateNoImage(it?.name, it?.school, it?.description)
                    }
                }
            }
        }

    }

    private var activityLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let {
                generateFileImage(it)
                /*try {
                    val bitmap = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    }
                    binding.btnUploadFoto.setImageBitmap(bitmap)
                    //fotoProfil = BitmapConverter().bitmapToString(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }*/
            }
        }

    private fun checkPermissionGallery(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityLauncherGallery.launch(galleryIntent)
    }

    private fun requestPermissionGallery() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            110
        )
    }

    private fun generateFileImage(uri: Uri) {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()

            val orientation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getOrientation2(uri)
            } else {
                getOrientation(uri)
            }

            val file = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                createImageFile()
            } else {
                //File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}" + File.separator + "BurgerBangor", getNewFileName())
                File(externalCacheDir?.absolutePath, getNewFileName())
            }

            val fos = FileOutputStream(file)
            var bitmap = image

            if (orientation != -1 && orientation != 0) {

                val matrix = Matrix()
                when (orientation) {
                    6 -> matrix.postRotate(90f)
                    3 -> matrix.postRotate(180f)
                    8 -> matrix.postRotate(270f)
                    else -> matrix.postRotate(orientation.toFloat())
                }
                bitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            binding.btnUploadFoto.setImageBitmap(bitmap)
            filePhoto = file

        } catch (e: Exception) {
            e.printStackTrace()
            binding.root.snacked("File ini tidak dapat digunakan")
        }
    }

    @SuppressLint("Range")
    private fun getOrientation(shareUri: Uri): Int {
        val orientationColumn = arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cur = contentResolver.query(
            shareUri,
            orientationColumn,
            null,
            null,
            null
        )
        var orientation = -1
        if (cur != null && cur.moveToFirst()) {
            if (cur.columnCount > 0) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
            }
            cur.close()
        }
        return orientation
    }

    @SuppressLint("NewApi")
    private fun getOrientation2(shareUri: Uri): Int {
        val inputStream = contentResolver.openInputStream(shareUri)
        return getOrientation3(inputStream)
    }

    @SuppressLint("NewApi")
    private fun getOrientation3(inputStream: InputStream?): Int {
        val exif: ExifInterface
        var orientation = -1
        inputStream?.let {
            try {
                exif = ExifInterface(it)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return orientation
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = DateTimeHelper().createAtLong().toString()
        val storageDir =
            getAppSpecificAlbumStorageDir(Environment.DIRECTORY_DOCUMENTS, "Attachment")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private fun getNewFileName(isPdf: Boolean = false): String {
        val timeStamp = DateTimeHelper().createAtLong().toString()
        return if (isPdf) "PDF_${timeStamp}_.pdf" else "JPEG_${timeStamp}_.jpg"
    }

    private fun getAppSpecificAlbumStorageDir(albumName: String, subAlbumName: String): File {
        // Get the pictures directory that's inside the app-specific directory on
        // external storage.
        val file = File(getExternalFilesDir(albumName), subAlbumName)
        if (!file.mkdirs()) {
            //Log.e("fssfsf", "Directory not created")
        }
        return file
    }

    override fun onClick(v: View?) {

        when (v) {
            binding.btnSaveEdit -> validateForm()
            binding.btnUploadFoto -> {
                if (checkPermissionGallery()) {
                    openGallery()
                } else {
                    requestPermissionGallery()
                }
            }
        }

        super.onClick(v)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
