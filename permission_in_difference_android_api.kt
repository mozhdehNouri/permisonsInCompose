// extention function for checking permission is granted or not
fun Context.checkPermissionGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED


// you can define enum class 
enum class ImageAccess {
    Granted,
    Denied
}

// check permission is granted or not
fun Context.checkImageAccess(): ImageAccess =
 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (checkPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES)) {
            ImageAccess.Granted
        } else {
            ImageAccess.Denied
        }
    } else {
        if (checkPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ImageAccess.Granted
        } else {
            ImageAccess.Denied
        }
    }

 // if permission is not granted, send permission request
@JvmInline
value class ImageAccessRequestLauncher(private val launcher: ActivityResultLauncher<String>) {
    fun requestImageAccess() {
        launcher.launch(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else  {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        )
    }
}

// make customize rememberLauncherForActivityResult base on your permission
@Composable
inline fun rememberImageAccessRequestLauncher(crossinline onResult: (ImageAccess) -> Unit): ImageAccessRequestLauncher {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { grantResults ->
            val ImageAccess =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (grantResults) {
                    ImageAccess.Granted
                } else {
                    ImageAccess.Denied
                }
            } else {
                if (grantResults) {
                    ImageAccess.Granted
                } else {
                    ImageAccess.Denied
                }
            }
            onResult(ImageAccess)
        },
    )
    return ImageAccessRequestLauncher(launcher)
}



   // usage in composable function 
   @Composable
   fun CheckPermission(){
    val context = LocalContext.current.applicationContext
    var imageAccess by remember { mutableStateOf(context.checkImageAccess()) }
    val imageAccessLauncher = rememberImageAccessRequestLauncher { result ->
        imageAccess = result
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        imageAccess = context.checkImageAccess()
    }
   
    when(imageAccess)
    Granted->{
        // do something base on your need
    }
    Denied ->{
        imageAccessLauncher.requestImageAccess()
    }
   }
