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
    fun requestReadExternalStorageAccess() {
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



// usage in compose
// usage in composable function 
   @Composable
   fun CheckPermission(){
    val context = LocalContext.current.applicationContext
    var storageAccess by remember { mutableStateOf(context.checkReadExternalStorageAccess()) }
    val storageAccessLauncher = rememberReadExternalStorageRequestLauncher { result ->
        storageAccess = result
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        storageAccess = context.checkReadExternalStorageAccess()
    }
   
    when(storageAccess){
    true->{
        // do something base on your need
    }
    false ->{
        storageAccessLauncher.requestReadExternalStorageAccess()
    }
   }
   }
