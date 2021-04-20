# HUE 22

## Features

* Synchronization using Timestamps:
  * During synchronization the timestamps are compared and the newer one is used
  * If the timestamp (from the server) is invalid it is assumed that the client is more up-to-date
  * If the entity doesn't exist on the server, it is created
* Login and Registration in App
* Multiple Todo Lists in the Navigation Drawer
  * New ones can be created using the overflow menu resulting in a Dialog
  * Todo List name is shown in the Activity Title
  * Last opened list is remembered
  * Drawer is disabled in Register and Login
* Animations using Material Motion
  * Login to Register
  * List change
  * Show details
  * Open Editor (from FAB)
* Drawer Header showing current username
* Logout Button in overflow menu
  * User data is removed on logout so that it doesn't mix with the next users data
* Swipe to Refresh on list view

## Internal Features

* Android Jetpack Navigation Component is used
* API Calls via Retrofit 2
* Database or API operations are performed asynchronously using Kotlin Coroutines
* Modular Architecture
  * Event handlers are in their respective fragment/activity
* Extensive use of `LiveData`
* Usage of Room with SQLite as data storage as replacement for the old `Serializer`s and `Storage`s
* Continuous Integration using GitHub Actions
* `ViewModel`s in Login/Register
* `BindingAdapter`s so that Data Bindings can be used everywhere where it makes sense
* Error handling for Login
* Input validation in editor (using animated hiding of FAB)

## Improvements

* Extract Activities into Fragments to achieve Single Activity Architecture
* Migration from JCenter to Maven Central
* `data-lib` is now a real Android Library instead of just a Kotlin Module
* Various dependency updates
* FAB moves out of the way if a `Snackbar` appears using `CoordinatorLayout`

## Bug Fixes

* Task Checking in the Details View works now

# HUE 19

* Add option to tick tasks
  * Add checkboxes
  * Update serializers
  * Change all UI occurrences of Note to Task
* Add app preferences with the following settings
  * Task storage location
  * Task serialization format
  * Task storage file name
* Implement external storage as a storage target
  * Add preference option
* Implement GSON serializer
  * Add GSON option to serializer preference
* Implement and extract our proprietary file format into it's own sub-project
  * Our binary based file format is the most storage-efficient file format possible for storing your tasks
  * Add gradle tasks for automatic creation of artifacts
    * Source code ZIP
    * Documentation ZIP
    * Compiled jar file
* Use Hilt/Dagger for dependency injection leading to better code modularity
  * Implement providers that provide the storage or serializer corresponding to the preference
* Minor improvements
  * Add FAB for creating new tasks to MainActivity
  * Change overdue icon
  * Change Date/Time picker color
  * Generally overhaul color scheme
  * Use Data Bindings exclusively
  * Switch from deprecated startActivityForResult to new type-safe API
  * Dependency updates
  * Add new feature ideas to list
* Bug fixes
  * Fix off-by-one error in date picker
  * Found new bugs