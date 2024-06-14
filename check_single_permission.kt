// extension function  for checking permission is granted or not
fun Context.checkPermissionGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED


// check storage permission is granted or not
fun Context.checkReadExternalStorageAccess(): Boolean =
    checkPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)


// if permission is not granted, send permission request
@JvmInline
value class ReadExternalStorageAccessRequestLauncher(private val launcher: ActivityResultLauncher<String>) {
    fun readExternalStorageAccess() {
        launcher.launch(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}

// make customize rememberLauncherForActivityResult base on your permission
@Composable
inline fun rememberReadExternalStorageRequestLauncher(crossinline onResult: (Boolean) -> Unit): ReadExternalStorageAccessRequestLauncher {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { grantResults ->
            onResult(grantResults)
        },
    )
    return ReadExternalStorageAccessRequestLauncher(launcher)
}
