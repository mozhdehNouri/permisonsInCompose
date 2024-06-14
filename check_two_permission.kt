// extention function for checking permission is granted or not
fun Context.checkPermissionGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED


// check location permission is granted or not
fun Context.checkLocationAccess(): Boolean =
    if (checkPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) ||
        checkPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
    ) true
    else false


// if permission is not granted, send permission request
@JvmInline
value class LocationAccessRequestLauncher(private val launcher: ActivityResultLauncher<Array<String>>) {
    fun requestLocationAccess() {
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}

// make customize rememberLauncherForActivityResult base on your permission
@Composable
inline fun rememberLocationRequestLauncher(crossinline onResult: (Boolean) -> Unit):
        LocationAccessRequestLauncher {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { grantResults ->
            val locationAccess = if (grantResults[Manifest.permission.ACCESS_COARSE_LOCATION] ==
                true ||
                grantResults[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                true
            } else {
               false
            }
            onResult(locationAccess)
        },
    )                    
   return LocationAccessRequestLauncher(launcher)





   // usage in composable function 
   @Composable
   fun CheckPermission(){
    val context = LocalContext.current.applicationContext
    var locationAccess by remember { mutableStateOf(context.checkMediaAccess()) }
    val locationAccessLauncher = rememberLocationRequestLauncher { result ->
        locationAccess = result
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        mediaAccess = context.checkLocationAccess()
    }
   
    when(locationAccess)
    true->{
        // do something base on your code
    }
    false ->{
        locationAccessLauncher.requestLocationAccess()
    }
   }
